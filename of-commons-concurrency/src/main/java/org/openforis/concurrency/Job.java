package org.openforis.concurrency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Synchronously executes a series of Tasks in order.
 * 
 * @author M. Togna
 * @author S. Ricci
 */
public abstract class Job extends Worker {
	
	private transient JobManager jobManager;

	private List<Worker> tasks = new ArrayList<Worker>();
	private int currentTaskIndex = -1;
	
	/**
	 * Builds all the tasks. Each task will be initialized before running it.
	 * @throws Throwable 
	 */
	@Override
	protected void initializeInternalVariables() throws Throwable {
		super.initializeInternalVariables();
		buildTasks();
	}
	
	@Override
	public int getProgressPercent() {
		switch ( getStatus() ) {
		case COMPLETED:
			return 100;
		case PENDING:
			return 0;
		default:
			Worker cTask = getCurrentTask();
			if ( cTask == null ) {
				return 0;
			} else {
				int totalWeight = 0;
				double weightedProgress = 0;
				for (Worker t : tasks) {
					totalWeight += t.getWeight();
					weightedProgress += t.getProgressPercent() * t.getWeight();
				}
				double result = weightedProgress / totalWeight;
				//round result to integer
				return Double.valueOf(Math.floor(result)).intValue();
			}
		}
	}
	
	@Override
	public void abort() {
		super.abort();
		//abort current task
		Worker currentTask = getCurrentTask();
		if (currentTask != null) {
			currentTask.abort();
		}
	}
	
	/**
	 * Runs each contained task in order.
	 * 
	 * @throws Exception
	 */
	@Override
	protected void execute() throws Throwable {
		while ( hasTaskToRun() ) {
			Worker task = nextTask();
			
			initializeTask(task);
			
			switch (task.getStatus()) {
			case PENDING:
				runTask(task);
				break;
			case FAILED:
				onTaskFailed(task);
				break;
			case ABORTED:
				abort();
				break;
			default:
			}
		}
	}

	protected void runTask(Worker task) throws Throwable {
		try {
			task.run();

			switch ( task.getStatus() ) {
			case COMPLETED:
				onTaskCompleted(task);
				break;
			case FAILED:
				onTaskFailed(task);
				break;
			case ABORTED:
				abort();
				break;
			default:
			}
		} finally {
			onTaskEnd(task);
		}
	}

	/**
	 * Creates and adds tasks to this job.
	 * @throws Throwable
	 */
	protected abstract void buildTasks() throws Throwable;

	protected <T extends Worker> T createTask(Class<T> type) {
		T task = jobManager.createWorker(type);
		return task;
	}
	
	protected boolean hasTaskToRun() {
		return isRunning() && currentTaskIndex + 1 < tasks.size();
	}

	protected Worker nextTask() {
		this.currentTaskIndex ++;
		return tasks.get(currentTaskIndex);
	}

	/**
	 * Creates and adds a task of the specified type.
	 * @param type
	 * @return
	 */
	protected <T extends Worker> T addTask(Class<T> type) {
		T task = createTask(type);
		addTask(task);
		return task;
	}
	
	/**
	 * Throws IllegalStateException if invoked after run() is called
	 * 
	 * @param task
	 */
	protected <T extends Worker> void addTask(T task) {
		if ( !isPending() ) {
			throw new IllegalStateException("Cannot add tasks to a job once started");
		}
		tasks.add(task);
	}

	protected <C extends Collection<? extends Worker>> void addTasks(C tasks) {
		for (Worker task : tasks) {
			addTask(task);
		}
	}

	/**
	 * Called when the task ends its execution. The status can be {@link Status#COMPLETED}, {@link Status#FAILED}, {@link Status#ABORTED}
	 * @param task
	 */
	protected void onTaskEnd(Worker task) {
		
	}

	/**
	 * Called when the task ends its execution with the status {@link Status#COMPLETED}
	 * @param task
	 */
	protected void onTaskCompleted(Worker task) {
	}
	
	protected void onTaskFailed(Worker task) throws Throwable {
		if (task.getLastException() != null) {
			throw task.getLastException();
		} else {
			setErrorMessage(task.getErrorMessage());
			changeStatus(Status.FAILED);
		}
	}

	/**
	 * Called before task execution.
	 * @param task
	 */
	protected void initializeTask(Worker task) {
		task.initialize();
	}
	
	@Override
	public void release() {
		super.release();
		for (Worker t: tasks) {
			t.release();
		}
	}
	
	public List<Worker> getTasks() {
		return Collections.unmodifiableList(tasks);
	}

	public int getCurrentTaskIndex() {
		return this.currentTaskIndex;
	}

	public Worker getCurrentTask() {
		return currentTaskIndex >= 0 ? tasks.get(currentTaskIndex) : null;
	}
	
	public JobManager getJobManager() {
		return jobManager;
	}
	
	public void setJobManager(JobManager jobManager) {
		this.jobManager = jobManager;
	}
	
}