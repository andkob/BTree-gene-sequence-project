package cs321.btree;

import java.nio.ByteBuffer;

/**
 * Represents a node in a B-Tree structure. Each node can have multiple keys and children,
 * depending on the degree of the B-Tree.
 * 
 * @author Andrew Kobus
 */
public class BTreeNode {
    // This order will be the order of the node's metadata on the disk. DO NOT change this order.
    public int numKeys;
    public boolean isLeaf;
    private long parentPointer;
    private long location; // the byte offset in the file
    public TreeObject[] keys;
    public long[] children; // child pointers

    /**
     * Basic constructor for a BTree Node
     */
    public BTreeNode(int degree) {
        numKeys = 0;
        isLeaf = true;
        parentPointer = 0;
        location = 0;
        keys = new TreeObject[2 * degree - 1];
        children = new long[2 * degree];
    }

    /**
     * Returns the size of this node in bytes
     * @return the size of this node in bytes
     */
    public int getNodeSize() {
        // keysSize + childrenSize + parentPointerSize + locationSize + isLeafSize + numKeysSize
        return TreeObject.SIZE * keys.length + Long.BYTES * children.length
                + Long.BYTES + Long.BYTES + 1 + Integer.BYTES;
    }

    /**
     * Returns the location of this node in terms of Byte offset
     * @return the Byte offset of this node on the disk
     */
    public long getLocation() {
        return location;
    }

    /**
     * Sets the Byte offset in the random access file of this node
     * @param location the location of this node in the random access file (Byte offset)
     */
    public void setLocation(long location) {
        this.location = location;
    }

    /**
     * Returns the pointer to this node's parent
     * @return the pointer to this node's parent
     */
    public long getParentPointer() {
        return parentPointer;
    }

    /**
     * Sets the parent pointer of this node
     * @param parentPointer the pointer to this node's parent
     */
    public void setParent(long parentPointer) {
        this.parentPointer = parentPointer;
    }
}
