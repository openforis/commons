package org.openforis.concurrency;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 
 * @author S. Ricci
 *
 */
public class SimpleJobManager implements JobManager {
	
	private Collection<String> locks;
	private Map<String, Job> jobByLockId;
	
	private Executor jobExecutor;
	
	public SimpleJobManager() {
		jobByLockId = new HashMap<String, Job>();
		locks = new HashSet<String>();
		jobExecutor = Executors.newCachedThreadPool();
	}
	
	@Override
	public <J extends Job> J createJob(Class<J> type) {
		try {
			J job = type.newInstance();
			job.setJobManager(this);
			return job;
		} catch (Exception e) {
			throw new RuntimeException("Error instanciating job of type " + type.getName());
		}
	}

	@Override
	public <T extends Task> T createTask(Class<T> type) {
		try {
			T task = type.newInstance();
			return task;
		} catch (Exception e) {
			throw new RuntimeException("Error instanciating job of type " + type.getName());
		}
	}
	
	/**
	 * Executes a job in the background
	 * @throws Throwable 
	 */
	public <J extends Job> void start(J job) {
		start(job, true);
	}
	
	@Override
	public <J extends Job> void start(J job, boolean async) {
		start(job, null, async);
	}

	synchronized public <J extends Job> void start(final J job, final String lockId) {
		start(job, lockId, true);
	}
	
	synchronized public <J extends Job> void start(final J job, final String lockId, boolean async) {
		job.init();
		
		if ( job.isPending() ) {
			if ( lockId != null ) {
				lock(job, lockId);
			}
			Runnable jobRunnable = new Runnable() {
				@Override
				public void run() {
					try {
						job.run();
					} finally {
						if ( lockId != null ) {
							release(lockId);
						}
					}
				}

			};
			if ( async ) {
				jobExecutor.execute(jobRunnable);
			} else {
				jobRunnable.run();
			}
		}
	}

	protected <J extends Job> void lock(final J job, final String lockId) {
		if ( locks.contains(lockId) ) {
			throw new RuntimeException("Another job is runnign for the same locking group: " + lockId);
		} else {
			locks.add(lockId);
			jobByLockId.put(lockId, job);
		}
	}

	protected synchronized void release(String lockId) {
		locks.remove(lockId);
	}
	
	protected <J extends Job> void runJob(final J job, final String lockId) {
		try {
			job.run();
		} finally {
			if ( lockId != null ) {
				locks.remove(lockId);
			}
		}
	}
	
	public Executor getJobExecutor() {
		return jobExecutor;
	}
	
	protected void setJobExecutor(Executor jobExecutor) {
		this.jobExecutor = jobExecutor;
	}
}
