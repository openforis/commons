package org.openforis.concurrency.spring;

import java.util.concurrent.Executor;

import org.openforis.concurrency.Job;
import org.openforis.concurrency.SimpleJobManager;
import org.openforis.concurrency.Worker;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 
 * @author S. Ricci
 *
 */
@Component
public class SpringJobManager extends SimpleJobManager implements BeanFactoryAware {
	
	private BeanFactory beanFactory;

	@Override
	protected <T extends Worker> T createInstance(Class<T> type)
			throws InstantiationException, IllegalAccessException {
		return beanFactory.getBean(type);
	}

	protected <T extends Worker> T createInstance(String name, Class<T> type) {
		return beanFactory.getBean(name, type);
	}
	
	public <J extends Job> J createJob(String name, Class<J> type) {
		J job = createInstance(name, type);
		job.setJobManager(this);
		return job;
	}
	
	//Autowire jobExecutor from application context
	@Autowired
	@Override
	protected void setJobExecutor(Executor jobExecutor) {
		super.setJobExecutor(jobExecutor);
	}
	
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
}
