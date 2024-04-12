package cs321.btree;

import java.nio.ByteBuffer;

/**
 * Represents a node in a B-Tree structure. Each node can have multiple keys and children,
 * depending on the degree of the B-Tree.
 */
public class BTreeNode {
    int n;
    long[] keys;
    long[] children;
    boolean isLeaf;
    long diskAddress;
    private long pointer; // where we can start reading this node in the file
    static public int NODE_SIZE; // node size in bytes TODO

    /**
     * Constructs a BTreeNode with a specified degree.
     * Initializes arrays for keys and children based on the degree.
     *
     * @param t the degree of the B-Tree, which dictates the number of maximum children and keys
     */
    public BTreeNode(int t) {
        keys = new long[2 * t - 1];  // max keys
        children = new long[2 * t];  // max children
        isLeaf = true;
        n = 0;
        diskAddress = -1;  // will be set when writing to disk
    }
    
    /**
     * Gets the pointer to this node's location in the file.
     *
     * @return the pointer to where this node is stored in the file
     */
    public long getPointer() {
        return pointer;
    }

    /**
     * Converts a ByteBuffer into a BTreeNode object. This method should read
     * the ByteBuffer and extract the necessary information to create a populated BTreeNode.
     * 
     * @param buffer the ByteBuffer containing the node's serialized data
     * @return a new BTreeNode object constructed from the data in the buffer
     */
    public static BTreeNode fromByteBuffer(ByteBuffer buffer) {
        throw new UnsupportedOperationException("Unimplemented method 'fromByteBuffer'");
    }
}
