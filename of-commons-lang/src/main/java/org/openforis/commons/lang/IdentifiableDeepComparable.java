package org.openforis.commons.lang;

/**
 * 
 * @author S. Ricci
 *
 */
public interface IdentifiableDeepComparable extends DeepComparable {

	boolean deepEquals(Object o, boolean ignoerId);
	
}
