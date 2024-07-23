/**
 * 
 */
package org.openforis.commons.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.openforis.commons.lang.DeepComparable;
import org.openforis.commons.lang.Objects;

/**
 * @author M. Togna
 * @author S. Ricci
 * 
 */
public abstract class CollectionUtils {

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
	
	public static <T> Collection<T> unmodifiableCollection(Collection<? extends T> collection) {
		if ( collection == null ) {
			return Collections.emptyList();
		} else {
			return Collections.unmodifiableCollection(collection);
		}
	}
	
	/**
	 * Shifts the item to the specified index.
	 */
	public static <T> void shiftItem(List<T> list, T item, int toIndex) {
		int oldIndex = list.indexOf(item);
		if ( oldIndex < 0 ) {
			throw new IllegalArgumentException("Item not found");
		}
		if ( toIndex >= 0 && toIndex < list.size() ) {
			list.remove(oldIndex);
			list.add(toIndex, item);
		} else {
			throw new IndexOutOfBoundsException("Index out of bounds: " + toIndex + " (list size = " + list.size() + ")");
		}
	}
	
	public static boolean isNotEmpty(Collection<?> collection) {
		return collection != null && !collection.isEmpty();
	}
	
	public static <T, C extends Collection<T>> void filter(C collection, Predicate<T> predicate) {
	    if (collection != null && predicate != null) {
	    	Iterator<T> it = collection.iterator();
	        while(it.hasNext()) {
	            T item = it.next();
				if (!predicate.evaluate(item)) {
	                it.remove();
	            }
	        }
	    }
	}
	
	public static <T, C extends Collection<T>>  List<T> filterIntoList(C collection, Predicate<T> predicate) {
		List<T> result = new ArrayList<T>();
		if (collection != null && predicate != null) {
			for (T item : collection) {
				if (predicate.evaluate(item)) {
					result.add(item);
				}
			}
		}
		return result;
	}
	
	public static <T> T find (Collection<T> items, Predicate<T> predicate) {
		for (T item : items) {
			if (predicate.evaluate(item)) {
				return item;
			}
		}
		return null;
	}
	
	public static <T> T findItem(Collection<T> items, Object key) {
		return findItem(items, key, "id");
	}
	
	public static <T> T findItem(Collection<T> items, Object key, String keyPropertyName) {
		for (T item : items) {
			Object keyValue = Objects.getPropertyValue(item, keyPropertyName);
			if (key.equals(keyValue)) {
				return item;
			}
		}
		return null;
	}
	
	public static <T> Collection<T> addIgnoreNull(Collection<T> items, T item) {
		if (item != null ) {
			items.add(item);
		}
		return items;
	}
	
	public static <I, T> List<I> project(Collection<T> items, String propertyName) {
		List<I> result = new ArrayList<I>(items.size());
		for (T item : items) {
			I keyValue = Objects.getPropertyValue(item, propertyName);
			result.add(keyValue);
		}
		return result;
	}

	public static <T extends DeepComparable> boolean deepEquals(Collection<T> coll1, Collection<T> coll2) {
		return deepEquals(coll1, coll2, false);
	}
	
	public static <T extends DeepComparable> boolean deepEquals(Collection<T> coll1, Collection<T> coll2, boolean ignoreId) {
		if (coll1 == coll2)
            return true;

		Iterator<T> e1 = coll1.iterator();
        Iterator<T> e2 = coll2.iterator();
        while (e1.hasNext() && e2.hasNext()) {
        	DeepComparable o1 = e1.next();
        	DeepComparable o2 = e2.next();
            if (! Objects.deepEquals(o1, o2, ignoreId))
                return false;
        }
        return !(e1.hasNext() || e2.hasNext());
	}
	
	/**
	 * Creates a clone of the specified list (NOT a DEEP CLONE of the objects inside the list)
	 * 
	 * @param list
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T extends Object> List<T> clone(List<T> list) {
		if (list == null) {
			return null;
		}
		if (list instanceof ArrayList) {
			return (List<T>) ((ArrayList) list).clone();
		} else {
			List<T> result = new ArrayList<T>(list.size());
			for (T item : list) {
				result.add(item);
			}
			return result;
		}
	}
	
	public static <K extends Object, V extends Object> void cloneValuesInto(Map<K, V> fromMap, Map<K, V> intoMap) {
		for (Entry<K, V> entry : fromMap.entrySet()) {
			intoMap.put(ObjectUtils.cloneIfPossible(entry.getKey()), ObjectUtils.cloneIfPossible(entry.getValue()));
		}
	}
	
	public static <T extends Object> List<T> copyAndFillWithNulls(List<T> list, int newSize) {
		return copyAndFillWith(list, newSize, null);
	}
	
	public static <T extends Object> List<T> copyAndFillWith(List<T> list, int newSize, T fillValue) {
		if (list.size() > newSize) {
			throw new IllegalArgumentException(String.format("Original collection size (%d) must not exceed the new collection size: %d", list.size(), newSize));
		}
		ArrayList<T> result = new ArrayList<T>(newSize);
		result.addAll(list);
		result.addAll(Collections.nCopies(newSize - list.size(), fillValue));
		return result;
	}
	
	public static <T extends Object> Set<T> intersect(Collection<T> listA, Collection<T> listB) {
		Set<T> result = new LinkedHashSet<T>();
		for (T item : listA) {
			if (listB.contains(item)) {
				result.add(item);
			}
		}
		return result;
	}
	
	public static <T extends Object> List<Integer> indexOfItems(List<T> list, Collection<T> items) {
		List<Integer> result = new ArrayList<Integer>();
		for (T item : items) {
			int index = list.indexOf(item);
			if (index >= 0) {
				result.add(index);
			}
		}
		return result;
	}
	
	public static <T extends Object> List<T> sublistByIndexes(List<T> source, List<Integer> indexes) {
		List<T> result = new ArrayList<T>();
		for (Integer index : indexes) {
			result.add(source.get(index));
		}
		return result;
	}
	
	public static <T> List<T> map(Collection<T> collection, Transformer<T> transformer) {
		List<T> result = new ArrayList<T>();
		if (collection == null || transformer == null) return result;
		for (T item : collection) {
			T transformedItem = transformer.transform(item);
			result.add(transformedItem);
		}
		return result;
	}
	
}
