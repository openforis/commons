package org.openforis.concurrency.spring;

import java.util.concurrent.Executor;

import org.openforis.concurrency.SimpleJobManager;
import org.openforis.concurrency.Worker;
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
	protected <T extends Worker> T createInstance(Class<T> type)
			throws InstantiationException, IllegalAccessException {
		return beanFactory.getBean(type);
	}
	
	//Autowire jobExecutor from application context
	@Autowired
	@Override
	protected void setJobExecutor(Executor jobExecutor) {
		super.setJobExecutor(jobExecutor);
	}
	
}
