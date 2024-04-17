package cs321.btree;

public class TreeObject
{
    private long DNA; // sequence of dna
    private int frequency;
    public static final int SIZE = Long.BYTES + Integer.BYTES;

    public TreeObject(long key) {
        DNA = key;
        frequency = 1;
    }

    /**
     * Returns the DNA sequence as the key for this object
     * @return this object's DNA sequence
     */
    public long getKey() {
        return DNA;
    }
}
