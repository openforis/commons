/**
 * 
 */
package org.openforis.commons.collection;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author M. Togna
 * 
 */
public class CollectionUtils {

	/**
	 * 
	 * Returns an unmodifiable view of the specified list. <br/>
	 * This method makes use of the method unmodifiableList of java.util.Collections and returns an empty list if the provided list is null.
	 * 
	 * @param list
	 * @return
	 * @see java.util.Collections.unmodifiableList
	 */
	public static <T> List<T> unmodifiableList(List<? extends T> list) {
		if ( list == null ) {
			return Collections.emptyList();
		} else {
			return Collections.unmodifiableList(list);
		}
	}

	public static <T> Set<T> unmodifiableSet(Set<? extends T> set) {
		if ( set == null ) {
			return Collections.emptySet();
		} else {
			return Collections.unmodifiableSet(set);
		}
	}

	public static <K, V> Map<K, V> unmodifiableMap(Map<? extends K, ? extends V> map) {
		if ( map == null ) {
			return Collections.emptyMap();
		} else {
			return Collections.unmodifiableMap(map);
		}
	}
}
