package org.openforis.concurrency;

public class JobConfig {

	private boolean async = true;
	private String lockId;
	private boolean transientJob;

	public JobConfig() {}

	public JobConfig(boolean async) {
		this(async, null);
	}

	public JobConfig(boolean async, String lockId) {
		this(async, lockId, false);
	}
	
	public JobConfig(boolean async, String lockId, boolean transientJob) {
		super();
		this.async = async;
		this.lockId = lockId;
		this.transientJob = transientJob;
	}

	public boolean isAsync() {
		return async;
	}

	public void setAsync(boolean async) {
		this.async = async;
	}

	public String getLockId() {
		return lockId;
	}

	public void setLockId(String lockId) {
		this.lockId = lockId;
	}

	public boolean isTransientJob() {
		return transientJob;
	}

	public void setTransientJob(boolean transientJob) {
		this.transientJob = transientJob;
	}

}
