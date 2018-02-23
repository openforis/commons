package org.openforis.concurrency;

/**
 * 
 * @author S. Ricci
 */
public interface Processor<T> {

	void process(T item);
	
}
