package org.openforis.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base class for asynchronous
 * 
 * @author M. Togna
 * @author S. Ricci
 * 
 */
public abstract class Worker {

	private UUID id;
	private long startTime;
	private long endTime;
	private Status status;
	
	private transient Throwable lastException;
	private transient Log log;
	private transient List<WorkerStatusChangeListener> statusChangeListeners;

	public enum Status {
		PENDING, RUNNING, COMPLETED, FAILED, ABORTED;
	}

	public Worker() {
		this.startTime = -1;
		this.endTime = -1;
		this.lastException = null;
		this.log = LogFactory.getLog(getClass());
		this.id = UUID.randomUUID();
		this.statusChangeListeners = new ArrayList<WorkerStatusChangeListener>();
		this.status = Status.PENDING;
	}

	synchronized public void init() {
	}

	protected abstract void execute() throws Throwable;
	
	public void addStatusChangeListener(WorkerStatusChangeListener listener) {
		this.statusChangeListeners.add(listener);
	}
	
	public void removeStatusChangeListener(WorkerStatusChangeListener listener) {
		this.statusChangeListeners.remove(listener);
	}
	
	public String getName() {
		return getClass().getSimpleName();
	}

	public synchronized void run() {
		if (!isPending()) {
			throw new IllegalStateException("Already run");
		}
		try {
			changeStatus(Status.RUNNING);
			this.startTime = System.currentTimeMillis();
			execute();
			changeStatus(Status.COMPLETED);
		} catch (Throwable t) {
			changeStatus(Status.FAILED);
			this.lastException = t;
			log.warn("Task failed");
			t.printStackTrace();
		} finally {
			this.endTime = System.currentTimeMillis();
			notifyAll();
		}
	}

	protected void changeStatus(Status newStatus) {
		Status oldStatus = this.status;
		WorkerStatusChangeEvent event = new WorkerStatusChangeEvent(this, oldStatus, newStatus);
		this.status = newStatus;
		notifyAllStatusChangeListeners(event);
	}
	
	protected void notifyAllStatusChangeListeners(WorkerStatusChangeEvent event) {
		for (WorkerStatusChangeListener listener : statusChangeListeners) {
			listener.statusChanged(event);
		}
	}

	public final long getDuration() {
		switch (status) {
		case PENDING:
			return -1;
		case RUNNING:
			return System.currentTimeMillis() - startTime;
		default:
			return endTime - startTime;
		}
	}

	public final boolean isPending() {
		return status == Status.PENDING;
	}

	public final boolean isRunning() {
		return status == Status.RUNNING;
	}

	public final boolean isFailed() {
		return status == Status.FAILED;
	}

	public final boolean isAborted() {
		return status == Status.ABORTED;
	}

	public final boolean isCompleted() {
		return status == Status.COMPLETED;
	}

	public abstract int getProgressPercent();
	
	/**
	 * If task was run and finished, aborted or failed
	 * 
	 * @return
	 */
	public final boolean isEnded() {
		return status != Status.PENDING && status != Status.RUNNING;
	}

	public final Status getStatus() {
		return this.status;
	}

	public final long getStartTime() {
		return this.startTime;
	}

	public final long getEndTime() {
		return this.endTime;
	}

	public final Throwable getLastException() {
		return this.lastException;
	}

	public void abort() {
		changeStatus(Status.ABORTED);
	}
	
	public UUID getId() {
		return id;
	}

	protected final Log log() {
		return this.log;
	}

	public synchronized boolean waitFor(int timeoutMillis) {
		long start = System.currentTimeMillis();
		while (! isEnded() && System.currentTimeMillis() - start < timeoutMillis) {
			try {
				wait(timeoutMillis);
			} catch (InterruptedException e) {
			}
		}
		return isCompleted();
	}
	
}