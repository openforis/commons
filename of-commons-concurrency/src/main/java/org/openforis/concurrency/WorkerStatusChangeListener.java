package org.openforis.concurrency;


/**
 * 
 * @author R. Ricci
 *
 */
public interface WorkerStatusChangeListener {

	void statusChanged(WorkerStatusChangeEvent event);
	
}
