package org.openforis.concurrency;

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

	public void destroy() {
		jobInfoUpdateTimer.cancel();
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
