package cs321.btree;

import java.nio.ByteBuffer;

public class BTreeNode {
    // This order will be the order of the node's metadata on the disk. DO NOT change this order.
    public int numKeys;
    public boolean isLeaf;
    private long parentPointer;
    private int location; // the byte offset in the file
    public TreeObject[] objects;
    public long[] children; // child pointers

    /**
     * Basic constructor for a BTree Node
     */
    public BTreeNode(int degree) {
        objects = new TreeObject[2 * degree - 1];
        children = new long[2 * degree];
        isLeaf = true;
        numKeys = 0;
    }

    /**
     * Returns the size of this node in bytes
     * @return the size of this node in bytes
     */
    public int getNodeSize() {
        // objectsSize + childrenSIze + parentPointerSize + locationSize + isLeafSize + numKeysSize
        return TreeObject.SIZE * objects.length + Long.BYTES * children.length
                + Long.BYTES + Integer.BYTES + 1 + Integer.BYTES;
    }

    /**
     * Returns the location of this node in terms of Byte offset
     * @return the Byte offset of this node on the disk
     */
    public int getLocation() {
        return location;
    }

    /**
     * Sets the Byte offset in the random access file of this node
     * @param location the location of this node in the random access file (Byte offset)
     */
    public void setLocation(int location) {
        this.location = location;
    }

    /**
     * Sets the parent pointer of this node
     * @param parentPointer the pointer to this node's parent
     */
    public void setParent(long parentPointer) {
        this.parentPointer = parentPointer;
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
