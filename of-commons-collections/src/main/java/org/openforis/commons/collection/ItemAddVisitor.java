package org.openforis.commons.collection;

import java.util.Collection;

/**
 * 
 * @author S. Ricci
 *
 */
public class ItemAddVisitor<I> implements Visitor<I> {

	private Collection<I> collection;
	
	public ItemAddVisitor(Collection<I> collection) {
		super();
		this.collection = collection;
	}

	@Override
	public void visit(I item) {
		collection.add(item);
	}

}
