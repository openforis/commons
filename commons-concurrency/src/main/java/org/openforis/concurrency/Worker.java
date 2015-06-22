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
	private String errorMessage;
	
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
		this.errorMessage = null;
		this.log = LogFactory.getLog(getClass());
		this.id = UUID.randomUUID();
		this.statusChangeListeners = new ArrayList<WorkerStatusChangeListener>();
		this.status = Status.PENDING;
	}
	
	protected final void initialize() {
		log().debug("Initializing...");
		try {
			initalizeInternalVariables();
		} catch ( Throwable t ) {
			handleException(t);
		}
	}

	protected void initalizeInternalVariables() throws Throwable {}
	
	protected final void beforeExecute() {
		log().debug("Before executing...");
		try {
			beforeExecuteInternal();
		} catch ( Throwable t ) {
			handleException(t);
		}
	}
	
	protected void beforeExecuteInternal() throws Throwable {}

	protected abstract void execute() throws Throwable;
	
	protected final void afterExecute() {
		log().debug("After executing...");
		try {
			afterExecuteInternal();
			if ( isRunning() ) {
				changeStatus(Status.COMPLETED);
			}
		} catch ( Throwable t ) {
			handleException(t);
		}
	}
	
	protected void afterExecuteInternal() {}

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
		if (! isPending()) {
			throw new IllegalStateException("Already run");
		}
		try {
			changeStatus(Status.RUNNING);
			this.startTime = System.currentTimeMillis();
			
			beforeExecute();
			
			execute();
			
			afterExecute();
		} catch (Throwable t) {
			handleException(t);
		} finally {
			this.endTime = System.currentTimeMillis();
			notifyAll();
			log().debug(String.format("Finished in %.1f sec", getDuration() / 1000f));
			onEnd();
		}
	}

	public void abort() {
		changeStatus(Status.ABORTED);
	}
	
	protected void changeStatus(Status newStatus) {
		Status oldStatus = this.status;
		WorkerStatusChangeEvent event = new WorkerStatusChangeEvent(this, oldStatus, newStatus);
		this.status = newStatus;
		notifyAllStatusChangeListeners(event);
		switch ( newStatus ) {
		case COMPLETED:
			onCompleted();
			break;
		case FAILED:
			onFailed();
			break;
		case ABORTED:
			onAborted();
			break;
		default:
			break;
		}
	}
	
	protected void onEnd() {
	}
	
	protected void onCompleted() {
	}

	protected void onFailed() {
	}

	protected void onAborted() {
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

	private void handleException(Throwable t) {
		log().error(String.format("Error running worker (status: %s): %s", status.name(), t.getMessage()), t);
		lastException = t;
		errorMessage = t.getMessage();
		changeStatus(Status.FAILED);
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

	public UUID getId() {
		return id;
	}

	protected final Log log() {
		return this.log;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	protected void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	protected void setLastException(Throwable lastException) {
		this.lastException = lastException;
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