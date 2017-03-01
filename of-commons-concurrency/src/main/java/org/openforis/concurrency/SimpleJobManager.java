package org.openforis.concurrency;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 
 * @author S. Ricci
 *
 */
public class SimpleJobManager implements JobManager {
	
	protected static final long MAX_JOB_IDLE_MILLIS = 30 * 60 * 1000; //30 minutes
	private static final long JOB_INFO_UPDATE_PERIOD_MILLIS = 60 * 1000; //1 minute
	
	private Map<String, Job> jobByLockId;
	private Map<String, JobInfo> jobInfoById;
	
	private Executor jobExecutor;
	
	private Timer jobInfoUpdateTimer;
	
	public SimpleJobManager() {
		jobByLockId = new HashMap<String, Job>();
		jobInfoById = new HashMap<String, JobInfo>();
		jobExecutor = Executors.newCachedThreadPool();
		initJobInfoUpdateTimer();
	}

	public synchronized void destroy() {
		jobInfoUpdateTimer.cancel();
		abortRunningJobs();
	}

	private void abortRunningJobs() {
		Collection<Job> jobs = jobByLockId.values();
		for (Job job : jobs) {
			if (job.isRunning()) {
				job.abort();
			}
		}
	}
	
	private void initJobInfoUpdateTimer() {
		jobInfoUpdateTimer = new Timer();
		jobInfoUpdateTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				pruneIdleJobs();
			}
		}, JOB_INFO_UPDATE_PERIOD_MILLIS, JOB_INFO_UPDATE_PERIOD_MILLIS);
	}
	
	@Override
	public <J extends Job> J createJob(Class<J> type) {
		return createWorker(type);
	}

	@Override
	public <T extends Worker> T createWorker(Class<T> type) {
		try {
			T task = createInstance(type);
			if (task instanceof Job) {
				((Job) task).setJobManager(this);
			}
			return task;
		} catch (Exception e) {
			throw new RuntimeException("Error instanciating worker of type " + type.getName(), e);
		}
	}

	protected <T extends Worker> T createInstance(Class<T> type)
			throws InstantiationException, IllegalAccessException {
		return type.newInstance();
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
		jobInfoById.put(job.getId().toString(), new JobInfo(job));
		
		job.initialize();
		
		if ( job.isPending() ) {
			if ( lockId != null ) {
				lock(job, lockId);
			}
			Runnable jobRunnable = new Runnable() {
				@Override
				public void run() {
					try {
						job.run();
					} catch (Exception e) {
						//do nothing, exceptions thrown to rollback transaction
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
		if ( jobByLockId.containsKey(lockId) && jobByLockId.get(lockId).isRunning() ) {
			throw new RuntimeException("Another job is runnign for the same locking group: " + lockId);
		} else {
			jobByLockId.put(lockId, job);
		}
	}

	public Job getJob(String jobId) {
		JobInfo jobInfo = jobInfoById.get(jobId);
		return jobInfo == null ? null : jobInfo.getJob();
	}

	public Job getLockingJob(String lockId) {
		return jobByLockId.get(lockId);
	}
	
	protected synchronized void release(String lockId) {
		jobByLockId.remove(lockId);
	}
	
	protected <J extends Job> void runJob(final J job, final String lockId) {
		try {
			job.run();
		} finally {
			if ( lockId != null ) {
				release(lockId);
			}
		}
	}
	
	public Executor getJobExecutor() {
		return jobExecutor;
	}
	
	protected void setJobExecutor(Executor jobExecutor) {
		this.jobExecutor = jobExecutor;
	}
	
	private synchronized void pruneIdleJobs() {
		Set<Entry<String,JobInfo>> entrySet = jobInfoById.entrySet();
		Iterator<Entry<String, JobInfo>> iterator = entrySet.iterator();
		while (iterator.hasNext()) {
			Entry<String, JobInfo> entry = (Entry<String, JobInfo>) iterator.next();
			JobInfo jobInfo = entry.getValue();
			Job job = jobInfo.getJob();
			if (! job.isRunning() && ! job.isPending()) {
				jobInfo.incrementIdleMillis(JOB_INFO_UPDATE_PERIOD_MILLIS);
				if (jobInfo.getIdleMillis() > MAX_JOB_IDLE_MILLIS) {
					iterator.remove();
				}
			}
		}
	}
	
	private class JobInfo {
		
		private Job job;
		private long idleMillis;
		
		public JobInfo(Job job) {
			super();
			this.job = job;
			this.idleMillis = 0;
		}

		public Job getJob() {
			return job;
		}
		
		public long getIdleMillis() {
			return idleMillis;
		}
		
		public void incrementIdleMillis(long amount) {
			idleMillis += amount;
		}
		
	}
	
}
