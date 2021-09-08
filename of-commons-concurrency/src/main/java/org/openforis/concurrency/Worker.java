package org.openforis.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base class for asynchronous
 * 
 * @author M. Togna
 * @author S. Ricci
 * 
 */
public abstract class Worker {

	private UUID id = UUID.randomUUID();
	private long startTime = -1;
	private long endTime = -1;
	private Status status = Status.PENDING;
	private String errorMessage;
	private String [] errorMessageArgs;
	private int weight = 1; //helps to better estimate Job progress percent
	private transient Throwable lastException;
	private transient Logger log = Logger.getLogger(getClass().getName());
	private transient List<WorkerStatusChangeListener> statusChangeListeners = new ArrayList<WorkerStatusChangeListener>();

	public enum Status {
		PENDING, RUNNING, COMPLETED, FAILED, ABORTED;
	}

	public void initialize() {
		logDebug("Initializing...");
		
		try {
			validateInput();
			if (isPending()) {
				createInternalVariables();
				if (isPending()) {
					initializeInternalVariables();
				}
			}
		} catch ( Throwable t ) {
			handleException(t);
		}
	}

	protected void validateInput() throws Throwable {}

	protected void createInternalVariables() throws Throwable {}
	
	protected void initializeInternalVariables() throws Throwable {}
	
	protected void beforeExecute() {
		logDebug("Before executing...");
		try {
			this.startTime = System.currentTimeMillis();
			beforeExecuteInternal();
			changeStatus(Status.RUNNING);
		} catch ( Throwable t ) {
			handleException(t);
		}
	}
	
	protected void beforeExecuteInternal() throws Throwable {}

	protected abstract void execute() throws Throwable;
	
	protected void afterExecute() {
		logDebug("After executing...");
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

	protected synchronized void run() {
		if (! isPending()) {
			throw new IllegalStateException("Already run");
		}
		try {
			beforeExecute();

			execute();
			
			afterExecute();
		} catch (Throwable t) {
			handleException(t);
		} finally {
			this.endTime = System.currentTimeMillis();
			notifyAll();
			logDebug(String.format("Finished in %.1f sec", getDuration() / 1000f));
			onEnd();
		}
	}

	public void abort() {
		changeStatus(Status.ABORTED);
		release();
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
	
	protected void onEnd() {}
	
	protected void onCompleted() {}

	protected void onFailed() {}

	protected void onAborted() {}
	
	public void destroy() {
		if (isRunning()) {
			abort();
		}
		release();
	}
	
	/**
	 * Releases the resource used during the execution
	 */
	protected void release() {}
	
	protected void notifyAllStatusChangeListeners(WorkerStatusChangeEvent event) {
		for (WorkerStatusChangeListener listener : statusChangeListeners) {
			listener.statusChanged(event);
		}
	}

	public long getDuration() {
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
		logError(String.format("Error running worker (status: %s): %s", status.name(), t.getMessage()), t);
		lastException = t;
		errorMessage = t.getMessage();
		changeStatus(Status.FAILED);
	}
	
	public boolean isPending() {
		return status == null || status == Status.PENDING;
	}

	public boolean isRunning() {
		return status == Status.RUNNING;
	}

	public boolean isFailed() {
		return status == Status.FAILED;
	}

	public boolean isAborted() {
		return status == Status.ABORTED;
	}

	public boolean isCompleted() {
		return status == Status.COMPLETED;
	}

	public abstract int getProgressPercent();
	
	/**
	 * If task was run and finished, aborted or failed
	 * 
	 * @return
	 */
	public boolean isEnded() {
		return status != Status.PENDING && status != Status.RUNNING;
	}

	public Status getStatus() {
		return this.status;
	}

	public long getStartTime() {
		return this.startTime;
	}

	public long getEndTime() {
		return this.endTime;
	}

	public Throwable getLastException() {
		return this.lastException;
	}

	public UUID getId() {
		return this.id;
	}

	protected Logger log() {
		if (this.log == null) {
			this.log = Logger.getLogger(getClass().getName());
		}
		return this.log;
	}
	
	protected void logDebug(String message) {
		log().log(Level.FINE, message);
	}
	
	protected void logError(String message, Throwable throwable) {
		log().log(Level.SEVERE, message, throwable);
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	protected void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public String[] getErrorMessageArgs() {
		return errorMessageArgs;
	}
	
	public int getWeight() {
		return weight;
	}
	
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	protected void setErrorMessageArgs(String[] errorMessageArgs) {
		this.errorMessageArgs = errorMessageArgs;
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