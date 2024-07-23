
package org.openforis.commons.collection;

/**
 * 
 * @author S. Ricci
 * @author A. Sanchez-Paus Diaz
 *
 * @param <T>
 */
public interface Transformer<T> {

	T transform(T item);
	
}
