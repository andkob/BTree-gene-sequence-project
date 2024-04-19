package cs321.btree;

import java.nio.ByteBuffer;

/**
 * Represents a node in a B-Tree structure. Each node can have multiple keys and children,
 * depending on the degree of the B-Tree.
 */
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
        // objectsSize + childrenSize + parentPointerSize + locationSize + isLeafSize + numKeysSize
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

    /**
     * Converts a ByteBuffer into a BTreeNode object. This method should read
     * the ByteBuffer and extract the necessary information to create a populated BTreeNode.
     * 
     * @param buffer the ByteBuffer containing the node's serialized data
     * @return a new BTreeNode object constructed from the data in the buffer
     */
    public static BTreeNode fromByteBuffer(ByteBuffer buffer, int degree) {
        BTreeNode newNode = new BTreeNode(degree);
        int numKeys = buffer.getInt(); // Read the number of keys 'n'
        newNode.numKeys = numKeys;
        newNode.isLeaf = buffer.get() == 1; // Read the isLeaf flag
        newNode.setParent(buffer.getLong()); // Read the parent pointer
        newNode.setLocation(buffer.getInt());

        for (int i = 0; i < numKeys; i++) {
            long key = buffer.getLong(); // Read the key
            newNode.objects[i] = new TreeObject(key); // Create a TreeObject and store the key
        }

        if (!newNode.isLeaf) {
            for (int i = 0; i <= numKeys; i++) {
                newNode.children[i] = buffer.getLong(); // Read the child pointer
            }
        }

        return newNode;
    }
}
