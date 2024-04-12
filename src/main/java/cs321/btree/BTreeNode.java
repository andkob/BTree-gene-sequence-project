package cs321.btree;

import java.nio.ByteBuffer;

public class BTreeNode {
    int n;
    long[] keys;
    long[] children;
    boolean isLeaf;
    long diskAddress;
    private long pointer; // where we can start reading this node in the file
    static public int NODE_SIZE; // node size in bytes TODO

    public BTreeNode(int t) {
        keys = new long[2 * t - 1];  // max keys
        children = new long[2 * t];  // max children
        isLeaf = true;
        n = 0;
        diskAddress = -1;  // will be set when writing to disk
    }
    
    public long getPointer() {
        return pointer;
    }

    /**
     * TODO read the relevant data from the ByteBuffer and use it to construct a BTreeNode object.
     * @param buffer
     * @return
     */
    public static BTreeNode fromByteBuffer(ByteBuffer buffer) {
        throw new UnsupportedOperationException("Unimplemented method 'fromByteBuffer'");
    }
}
