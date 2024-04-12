package cs321.btree;

import java.nio.ByteBuffer;

/**
 * BTreeNode
 */
public class BTreeNode {

    private long pointer; // where we can start reading this node in the file
    static public int NODE_SIZE; // node size in bytes TODO

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