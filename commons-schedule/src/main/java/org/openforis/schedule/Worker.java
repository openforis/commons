package org.openforis.schedule;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base class for asynchronous
 * 
 * @author G. Miceli
 * 
 */
public abstract class Worker {

	private Status status;
	private UUID id;
	private long startTime;
	private long endTime;
	
	//  deserializing it into json might cause problems
	private transient Throwable lastException;
	private transient Log log;

	public enum Status {
		PENDING, RUNNING, COMPLETED, FAILED, ABORTED;
	}

	public Worker() {
		this.status = Status.PENDING;
		this.startTime = -1;
		this.endTime = -1;
		this.lastException = null;
		this.log = LogFactory.getLog(getClass());
		this.id = UUID.randomUUID();
	}

	synchronized public void init() {
	}

	protected abstract void execute() throws Throwable;

	public String getName() {
		return getClass().getSimpleName();
	}

	public synchronized void run() {
		if (!isPending()) {
			throw new IllegalStateException("Already run");
		}
		try {
			this.status = Status.RUNNING;
			this.startTime = System.currentTimeMillis();
			execute();
			onBeforeCompleted();
			this.status = Status.COMPLETED;
		} catch (Throwable t) {
			this.status = Status.FAILED;
			this.lastException = t;
			log.warn("Task failed");
			t.printStackTrace();
		} finally {
			this.endTime = System.currentTimeMillis();
			notifyAll();
			onEnd();
		}
	}

	/**
	 * Called just before the execution ends and before the status changes into {@link Status#COMPLETED}
	 */
	protected void onBeforeCompleted() {
	}

	/**
	 * Called when the process finishes (the status will be {@link Status#COMPLETED}, {@link Worker.Status#FAILED} or {@link Worker.Status#ABORTED}
	 */
	protected void onEnd() {
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
		status = Status.ABORTED;
	}
	
	public UUID getId() {
		return id;
	}

	protected final Log log() {
		return this.log;
	}

	public synchronized boolean waitFor(int timeoutMillis) {
		long start = System.currentTimeMillis();
		while (!isEnded() && System.currentTimeMillis() - start < timeoutMillis) {
			try {
				wait(timeoutMillis);
			} catch (InterruptedException e) {
			}
		}
		return isCompleted();
	}
	
}