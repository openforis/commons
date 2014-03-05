package org.openforis.concurrency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Synchronously executes a series of Tasks in order.
 * 
 * @author M. Togna
 * @author S. Ricci
 */
public class Job extends Worker implements Iterable<Task> {
	
	@Autowired
	private transient BeanFactory beanFactory;

	private int currentTaskIndex;

	private List<Task> tasks;
	
	protected Job() {
		this.currentTaskIndex = -1;
		this.tasks = new ArrayList<Task>();
	}

	/**
	 * Initializes each contained task in order. Called after all tasks have been added 
	 * (i.e. not in constructor!)
	 * @throws Throwable 
	 */
	public void init() {
		super.init();
		log().debug("Initializing");
		for (Task task : tasks) {
			task.init();
			if ( task.isFailed() ) {
				//stop initialization if a task fails the initialization
				setErrorMessage(task.getErrorMessage());
				changeStatus(Status.FAILED);
				break;
			}
		}
	}

	@Override
	public int getProgressPercent() {
		if ( getStatus() == Status.RUNNING ) {
			int currentTaskProgress = getCurrentTask().getProgressPercent();
			return 100 * ( currentTaskIndex + currentTaskProgress  / 100 ) / tasks.size();
		} else {
			return 0;
		}
	}
	
	@Override
	public synchronized void run() {
		log().debug("Starting job");
		super.run();
		log().debug(String.format("Finished in %.1f sec", getDuration() / 1000f));
	}
	
	/**
	 * Runs each contained task in order.
	 * 
	 * @throws Exception
	 */
	protected void execute() throws Throwable {
		this.currentTaskIndex = -1;
		while ( hasTaskToRun() ) {
			Task task = nextTask();
			
			task.run();
			
			switch ( task.getStatus() ) {
			case FAILED:
				if ( task.getLastException() != null ) {
					throw task.getLastException();
				} else {
					this.changeStatus(Status.FAILED);
				}
				break;
			case ABORTED:
				abort();
				break;
			default:
			}
		}
	}
	
	protected <T extends Task> T createTask(Class<T> type) {
		T task = beanFactory.getBean(type);
		return task;
	}
	
	protected boolean hasTaskToRun() {
		return isRunning() && currentTaskIndex + 1 < tasks.size();
	}

	protected Task nextTask() {
		this.currentTaskIndex ++;
		return tasks.get(currentTaskIndex);
	}

	/**
	 * Throws IllegalStateException if invoked after run() is called
	 * 
	 * @param task
	 */
	public <T extends Task> void addTask(T task) {
		if ( !isPending() ) {
			throw new IllegalStateException("Cannot add tasks to a job once started");
		}
		tasks.add(task);
	}

	public <C extends Collection<? extends Task>> void addTasks(C tasks) {
		for (Task task : tasks) {
			addTask(task);
		}
	}

	public List<Task> getTasks() {
		return Collections.unmodifiableList(tasks);
	}

	public int getCurrentTaskIndex() {
		return this.currentTaskIndex;
	}

	public Task getCurrentTask() {
		return currentTaskIndex >= 0 ? tasks.get(currentTaskIndex) : null;
	}
	
	@Override
	public Iterator<Task> iterator() {
		return getTasks().iterator();
	}	

	public Task getTask(UUID taskId) {
		for (Task task : tasks) {
			if ( task.getId().equals(taskId) ) {
				return task;
			}
		}
		return null;
	}

}