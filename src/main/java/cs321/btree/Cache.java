package cs321.btree;

import java.util.Iterator;
import java.util.LinkedList;
/**
 * 
 * @author mrbka
 *
 */
public class Cache<E> {

	private final int cacheSize;//Maximum cache capacity
	private LinkedList<E> cache;
	
	/** Constructor instantiates int cacheSize and LinkedList<E> cache
	 * @param size
	 */
	public Cache(int size) {
		cacheSize=size;
		cache = new LinkedList<E>();
	}
	
	/** Iterates over cache, removing and returning object if found
	 * @param object
	 * @return retVal or null
	 */
	public E getObject(E object) {
		Iterator<E> cacheIter = cache.iterator();
		E retVal = null;
		while(cacheIter.hasNext()) {
			retVal = cacheIter.next();
			if(object.equals(retVal)) {
				cacheIter.remove();
				return retVal;
			}
		}
		return null;		
	}
	
	/** Adds object to first slot of cache, removing last object first if list full
	 * @param object
	 */
	public void addObject(E object) {
		if(cache.size()==cacheSize) {
			cache.removeLast();
		}
		cache.addFirst(object);
	}
	
	/** Removes first occurrence of object in cache if object is found
	 * @param object
	 */
	public void removeObject(E object) {
		cache.remove(object);
	}
	
	/** Removes last object in cache
	 */
	public void removeLastObject() {
		cache.removeLast();
	}
	
	/** Returns cacheSize which represents maximum size of cache upon instantiation
	 * @return cacheSize
	 */
	public int getSize() {
		return cacheSize;
	}
	
	/** Removes objects from cache until empty
	 */
	@SuppressWarnings("unused")
	private void clearCache() {
		while(!cache.isEmpty()) {
			cache.removeFirst();
		}
	}
	
	/**
	 * Added method for BTree implementaion (Read)
	 * @return
	 */
	public LinkedList<E> getCacheLinkedList() {
		return cache;
	}
	
}