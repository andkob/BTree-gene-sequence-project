package cs321.btree;

import java.util.HashMap;

/**
 * The Cache class contains the means of storing items in a single level cache
 * Utilizes a hashmap for constant time lookup along with a linked list to keep track of
 * most used and least used nodes.
 * 
 * @author Andrew Kobus
 */
public class Cache<T> {
    
    private HashMap<Long, CacheNode<T>> cacheMap;
    private int maxSize;
	private CacheNode<T> head;
	private CacheNode<T> tail;

    /**
     * Initializes a single level
     * cache with specified length
     * 
     * @param maxSize1 maximum amount
     * of entries for cache
     */
    public Cache(int maxSize){
        cacheMap = new HashMap<>();
		this.maxSize = maxSize;
    }

	private static class CacheNode<T> {
		long key;
		T BTreeNode;
		CacheNode<T> prev;
		CacheNode<T> next;

		CacheNode(long key, T value) {
			this.key = key;
			this.BTreeNode = value;
		}
	}

    /** Returns cacheSize which represents maximum size of cache upon instantiation
	 * @return cacheSize
	 */
	public int getSize() {
        return maxSize;
	}

	public HashMap<Long, CacheNode<T>> getCache() {
		return cacheMap;
	}

    /**
     * Gets the object or value stored
     * at location in cache
     * 
     * @param index index to retrieve
     * object data
     * @return element at index
     */
    public T getObject(long position) {
        CacheNode<T> node = cacheMap.get(position);
		if (node != null) {
			moveToHead(node);
			return node.BTreeNode;
		}
		return null;
    }

    /**
     * Adds an object to the cache
     * 
     * @param element value to be 
     * added to the front of the cache
     */
    public void addObject(long key, T value) {
        if (cacheMap.containsKey(key)) {
			CacheNode<T> node = cacheMap.get(key);
			node.BTreeNode = value;
			moveToHead(node);
		} else {
			CacheNode<T> newNode = new CacheNode<>(key, value);
			cacheMap.put(key, newNode);
			addNode(newNode);
			if (cacheMap.size() > maxSize) {
				removeTail();
			}
		}
    }

    public void moveToHead(CacheNode<T> node) {
        if (node == head) {
			return;
		}
		removeNode(node);
		addNode(node);
    }

	private void addNode(CacheNode<T> node) {
		node.next = head;
		node.prev = null;
		if (head != null) {
			head.prev = node;
		}
		head = node;

		if (tail == null) { // if list is empty
			tail = node;
		}
	}

	private void removeNode(CacheNode<T> node) {
		if (node.prev != null) {
			node.prev.next = node.next;
		} else {
			head = node.next;
		}
		if (node.next != null) {
			node.next.prev = node.prev;
		} else { // if at the end
			tail = node.prev;
		}
	}

	private void removeTail() {
		if (tail != null) {
			cacheMap.remove(tail.key);
			if (tail.prev != null) {
				tail.prev.next = null;
			} else {
				head = null; // list is now empty
			}
			tail = tail.prev;
		}
	}
}