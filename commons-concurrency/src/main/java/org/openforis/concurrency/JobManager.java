package org.openforis.concurrency;


/**
 * 
 * @author S. Ricci
 *
 */
public interface JobManager {
	
	<J extends Job> J createJob(Class<J> type);

	<T extends Worker> T createWorker(Class<T> type);

	<J extends Job> void start(J job);

	<J extends Job> void start(J job, boolean async);

	<J extends Job> void start(J job, String lockId);

	<J extends Job> void start(J job, String lockId, boolean async);
}
