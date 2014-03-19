package org.openforis.concurrency.spring;

import java.util.concurrent.Executor;

import org.openforis.concurrency.Job;
import org.openforis.concurrency.SimpleJobManager;
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
public class SpringJobManager extends SimpleJobManager {
	
	@Autowired
	private BeanFactory beanFactory;

	@Autowired
	private Executor jobExecutor;
	
	@Override
	public <J extends Job> J createJob(Class<J> type) {
		J job = beanFactory.getBean(type);
		job.setJobManager(this);
		return job;
	}

	@Override
	public <T extends Task> T createTask(Class<T> type) {
		T task = beanFactory.getBean(type);
		return task;
	}
	
	//Autowire jobExecutor from application context
	@Autowired
	@Override
	protected void setJobExecutor(Executor jobExecutor) {
		super.setJobExecutor(jobExecutor);
	}
	
}
