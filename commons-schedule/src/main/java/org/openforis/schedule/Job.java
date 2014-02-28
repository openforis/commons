package org.openforis.schedule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Synchronously executes a series of Tasks in order.
 * 
 * @author M. Togna
 * @author S. Ricci
 */
public class Job<J extends Job<J>> extends Worker implements Iterable<Task<J>> {
	
	private int currentTaskIndex;

	private List<Task<J>> tasks;
	
	protected Job() {
		this.currentTaskIndex = -1;
		this.tasks = new ArrayList<Task<J>>();
	}

	/**
	 * Initializes each contained task in order. Called after all tasks have been added 
	 * (i.e. not in constructor!)
	 */
	public void init() {
		super.init();
		log().debug("Initializing");
		for (Task<J> task : tasks) {
			task.init();
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
	protected final void execute() throws Throwable {
		this.currentTaskIndex = -1;
		while ( hasTaskToRun() ) {
			Task<J> task = nextTask();
			
			task.run();
			
			switch ( task.getStatus() ) {
			case FAILED:
				throw task.getLastException();
			case ABORTED:
				abort();
			default:
			}
		}
	}
	
	protected boolean hasTaskToRun() {
		return currentTaskIndex + 1 < tasks.size();
	}

	protected Task<J> nextTask() {
		this.currentTaskIndex ++;
		return tasks.get(currentTaskIndex);
	}

	/**
	 * Throws IllegalStateException if invoked after run() is called
	 * 
	 * @param task
	 */
	@SuppressWarnings("unchecked")
	public void addTask(Task<J> task) {
		if ( !isPending() ) {
			throw new IllegalStateException("Cannot add tasks to a job once started");
		}
		task.setJob((J) this);
		tasks.add(task);
	}

	public <T extends Collection<? extends Task<J>>> void addTasks(T tasks) {
		for (Task<J> task : tasks) {
			addTask(task);
		}
	}

//	/**
//	 * Adds a task to the Job
//	 * @param task The Class of the task we want to add to the Job
//	 * @return The added Task instance
//	 */
//	@SuppressWarnings("unchecked")
//	public <T extends Task> T addTask(Class<T> task) {
//		Task newTask = taskManager.createTask(task);
//		addTask(newTask);
//		return (T) newTask;
//	}

	public List<Task<J>> getTasks() {
		return Collections.unmodifiableList(tasks);
	}

	public int getCurrentTaskIndex() {
		return this.currentTaskIndex;
	}

	public Task<J> getCurrentTask() {
		return currentTaskIndex >= 0 ? tasks.get(currentTaskIndex) : null;
	}
	
	@Override
	public Iterator<Task<J>> iterator() {
		return getTasks().iterator();
	}	

	public Worker getTask(UUID taskId) {
		for (Worker task : tasks) {
			if ( task.getId().equals(taskId) ) {
				return task;
			}
		}
		return null;
	}

}