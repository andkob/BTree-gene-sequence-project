package cs321.btree;

/**
 * Represents a segment of DNA sequence in a B-tree.
 * Each TreeObject contains a DNA sequence segment and its frequency count.
 * 
 * @author Andrew Kobus
 */
public class TreeObject implements Comparable<TreeObject> {
    private long DNA; // dna sequence segment
    private int frequency;
    public static final int SIZE = Long.BYTES + Integer.BYTES;

    /**
     * Constructs a TreeObject with the given DNA sequence segment and a default frequency of 1.
     * @param key The DNA sequence segment to store in this object
     */
    public TreeObject(long key) {
        this(key, 1);
    }

    /**
     * Constructs a TreeObject with the given DNA sequence segment and frequency count.
     * @param key    The DNA sequence segment to store in this object
     * @param count  The frequency count of the DNA sequence segment
     */
    public TreeObject(long key, int count) {
        DNA = key;
        frequency = count;
    }

    /**
     * Returns the DNA sequence as the key for this object
     * @return this object's DNA sequence
     */
    public long getKey() {
        return DNA;
    }

    /**
     * Returns the frequency of this object's sequence segment in the BTree
     * @return The frequency of this object's DNA sequence segment
     */
    public int getCount() {
        return frequency;
    }

    /**
     * Increments this object's frequency by 1
     */
    public void incrementFrequency() {
        frequency++;
    }

    /**
     * Compares this TreeObject with another based on DNA sequence segments.
     * @param other The other TreeObject to compare
     * @return    -1 if the value of this object is than the other, 1 if greater, 0 if equal
     */
    @Override
    public int compareTo(TreeObject other) {
        if (this.DNA < other.getKey()) {
            return -1;
        } else if (this.DNA > other.getKey()) {
            return 1;
        } else {
            return 0;
        }
    }
}
