package org.openforis.concurrency.spring;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Executor;

import org.openforis.concurrency.Job;
import org.openforis.concurrency.JobManager;
import org.openforis.concurrency.Task;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 
 * @author S. Ricci
 *
 */
@Component
public class JobManagerImpl implements JobManager {
	
//	private Log LOG = LogFactory.getLog(JobManagerImpl.class);
	
	@Autowired
	private BeanFactory beanFactory;

	@Autowired
	private Executor jobExecutor;
	
	private Collection<String> locks;

	private Map<String, Job> jobByLockId;
	
	public JobManagerImpl() {
		jobByLockId = new HashMap<String, Job>();
		locks = new HashSet<String>();
	}
	
	@Override
	public <J extends Job> J createJob(Class<J> type) {
		J job = beanFactory.getBean(type);
		job.setJobManager(this);
		return job;
	}

	@Override
	public <T extends Task<?>> T createTask(Class<T> type) {
		T task = beanFactory.getBean(type);
		return task;
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
}
