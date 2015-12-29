package org.openforis.commons.collection;

/**
 * 
 * @author S. Ricci
 *
 * @param <I>
 */
public interface Visitor<I> {
	
	void visit(I item);
}