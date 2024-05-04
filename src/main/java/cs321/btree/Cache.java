package cs321.btree;

import java.util.LinkedList;

/**
 * The Cache class contains the means of
 * storing items in a single or double
 * level cache
 */
public class Cache<T> {
    
    private LinkedList<T> cache1, cache2;
    private int maxSize1, maxSize2;
    //counters for number of references and hits
    private double NR1, NH1, NR2, NH2;
    private boolean twoLevel;

    /**
     * Initializes a single level
     * cache with specified length
     * 
     * @param maxSize1 maximum amount
     * of entries for cache
     */
    public Cache(int maxSize1){
        cache1 = new LinkedList<>();
        this.maxSize1 = maxSize1;
        twoLevel = false;
    }

    /**
     * Initializes a double level
     * cache with specific lengths
     * 
     * @param maxSize1 maximum amount of 
     * entries for the 1st level cache
     * @param maxSize2 maximum amount of
     * entries for the 2nd level cache
     */
    public Cache(int maxSize1, int maxSize2){
        cache1 = new LinkedList<>();
        cache2 = new LinkedList<>();
        this.maxSize1 = maxSize1;
        this.maxSize2 = maxSize2;
        twoLevel = true;
    }

    /** Returns cacheSize which represents maximum size of cache upon instantiation
	 * @return cacheSize
	 */
	public int getSize() {
            return maxSize1;
	}


    /**
     * Gets the object or value stored
     * at location in cache
     * 
     * @param index index to retrieve
     * object data
     * @return element at index
     */
    public T getObject(int index){
        T element = null;
        if(index >= 0 && index < cache1.size()){
            element = cache1.get(index);
        } else if (twoLevel) {
            if (index >= 0 && index < cache2.size()){
                element = cache2.get(index);
            }
        } 
        return element;
    }

    /**
     * Adds an object to the cache
     * 
     * @param element value to be 
     * added to the front of the cache
     */
    public void addObject(T element){
        if(maxSize1 > 0){
            //ensures that the cache doesn't exceed maxSize
            if(cache1.size() == maxSize1){
                cache1.removeLast();
            }
            cache1.addFirst(element);
            //adds to the front of the second cash if two-level
            if(twoLevel && maxSize2 > 0){
                if(cache2.size() == maxSize2){
                    cache2.removeLast();
                }
                cache2.addFirst(element);
            }
        }   
    }

    /**
     * removes object in cache 
     * at specified index
     * 
     * @param index index to remove
     * @return removed element
     */
    public T removeObject(int index){
        T element = null;
        if(index >= 0 && index < cache1.size()){
            element = cache1.remove(index);
        }
        //removes from second level as well
        if(twoLevel){
            if(index >= 0 && index < cache2.size()){
               element = cache2.remove(index);
            }
        }
        return element;
    }

    /**
     * Empties both the first
     * and the second level caches 
     */
    public void clearCache(){
        while (!cache1.isEmpty()){
            cache1.remove(0);
        }
        if (twoLevel){
            while (!cache2.isEmpty()){
                cache2.remove(0);
            }
        }
    }

    public void moveToTop(T element) {
        cache1.remove(element);
        cache1.addFirst(element);
    }


    /**
     * Searches caches for an element and
     * if found moves to top of caches. If
     * element isn't found, it's added to caches
     * 
     * @param element value to search for in caches
     */
    public void search(T element){
        //all hits/references tracked during search
        NR1++;
        //searches for index of element, adds if not found
        if(cache1.indexOf(element) >= 0){
            NH1++;
            removeObject(cache1.indexOf(element));
            addObject(element);

        //before adding element, checks second cache
        } else if(twoLevel){
            NR2++;
            if(cache2.indexOf(element) >= 0){
                NH2++;
                removeObject(cache2.indexOf(element));
                addObject(element);
            } else {
                //adds object if not already in either cache
                addObject(element);
            }
        } else {
            addObject(element);
        }
    }

    /**
     * Visual outout and calculation of 
     * references, hits, and hit ratios
     * 
     * @return formatted String of cache results
     */
    public String results(){
        String output = ".........................";
        output += "\nGlobal references: " + (int)(NR1);
        output += "\nGlobal cache hits: " + (int)(NH1 + NH2);
        output += "\nGlobal hit ratio: " + ((NH1 + NH2)/(NR1));

        if(twoLevel){
            output += "\n\n1st-level references: " + (int)(NR1);
            output += "\n1st-level cache hits: " + (int)(NH1);
            output += "\n1st-level hit ratio: " + ((NH1/NR1));

            output += "\n\n2nd-level references: " + (int)(NR2);
            output += "\n2nd-level cache hits: " + (int)(NH2);
            output += "\n2nd-level hit ratio: " + ((NH2/NR2));
        }

        return output;
    }

    /**
	 * Added method for BTree implementaion (Read)
	 * @return
	 */
	public LinkedList<T> getCacheLinkedList() {
		return cache1;
	}


    /**
     * Displays cache contents in a
     * (somewhat) readable format
     */
    public String toString(){
        String output = "Cache 1: [";
        for(int i = 0; i < cache1.size(); i++){
            output += cache1.get(i) + ",";
        }
        output += "]\nReferences: " + (int)NR1 + " Hits: " + (int)NH1;
        if(twoLevel){
            output += "\nCache 2: [";
            for(int i = 0; i < cache2.size(); i++){
                output += cache2.get(i) + ",";
            }
            output += "]\nReferences: " + (int)NR2 + " Hits: " + (int)NH2 + "\n";
        }
        return output;
    }

}