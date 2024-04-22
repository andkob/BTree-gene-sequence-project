package cs321.btree;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;

/**
 * Represents a B-Tree structure that provides efficient data insertion, deletion,
 * and lookup. This implementation specifically supports disk-based operations
 * to accommodate large data sets that do not fit into main memory.
 * 
 * @author Andrew Kobus
 * @author Caleb Tolman
 */
public class BTree implements BTreeInterface {
	
    static private final int OPTIMAL_DEGREE = 85; // calculated optimal degree t // TODO recalculate with BTreeNode +4 bytes

    private long size; // BTree size in Bytes
    private int height;
    private int degree;
    private int nodeCount;
    private BTreeNode root;
    private FileChannel disk; // named this 'disk' for simplicity. All I/O operations should be done through this channel.

    /**
     * Constructs a BTree using the default degree and initializes it from a file
     * if it exists, or creates a new one if it does not.
     *
     * @param filePath the path to the file that stores the B-Tree on disk
     */
    public BTree(String filePath) throws IOException {
        this(OPTIMAL_DEGREE, filePath);
    }

    /**
     * Constructs a BTree from a specified file path and degree.
     * If the file exists, it reads the BTree metadata and root from the file.
     * Otherwise, it initializes a new empty BTree with a specified degree.
     *
     * @param degree   the minimum degree of the BTree
     * @param filePath the path of the file to store the BTree
     */
    @SuppressWarnings("resource")
    public BTree(int degree, String filePath) throws IOException {
        this.size = 0;
        this.height = 0;
        this.degree = degree;
        this.nodeCount = 1;

        //TODO TESTING VARIABLE - delete this later
        boolean forceElse = false; // forces the constructor into the else block if false

        File file = new File(filePath);
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        if (file.exists() && forceElse) {
            disk = raf.getChannel(); // initialize file channel with the unique FileChannel associated with the RAF
            
            // Read metadata from the file
            ByteBuffer metadataBuffer = ByteBuffer.allocate(getMetaDataSize());
            disk.read(metadataBuffer);

            // Parse metadata | DO NOT change the read order
            metadataBuffer.flip(); // prepare for reading
            size = metadataBuffer.getLong();
            long rootPointer = metadataBuffer.getLong();
            degree = metadataBuffer.getInt();
            height = metadataBuffer.getInt();
            nodeCount = metadataBuffer.getInt();

            // Read root node from disk based on the rootPointer
            BTreeNode dummyRoot = new BTreeNode(degree);
            ByteBuffer rootBuffer = ByteBuffer.allocate(dummyRoot.getNodeSize());
            disk.read(rootBuffer, rootPointer);
            rootBuffer.flip();

            // Construct the root node from the buffer
            root = BTreeNode.fromByteBuffer(rootBuffer, degree);
        } else {
            file.createNewFile();
            disk = raf.getChannel();
            this.root = new BTreeNode(degree);
            writeMetaData(); // write meta data to file
            root.setLocation(disk.size()); // set the location of the root node based on current file size TODO returning a large number
            diskWrite(root); // write the root node to the disk
        }
    }
  
    /**
     * Reads the data of a BTreeNode from disk at a specified position.
     *
     * @param nodePointer   The pointer to the node's position in the file
     * @return BTreeNode    The node at the specified pointer
     * @throws IOException  if there is an error during reading
     */
    public BTreeNode diskRead(long nodePointer) throws IOException {
        BTreeNode dummyNode = new BTreeNode(degree);
        // Determine the size of the node
        int nodeSize = dummyNode.getNodeSize();
        // Calculate the position of the node in the file based on the nodePointer
        long position = getMetaDataSize() + nodePointer;

        // Read the node from the file
        ByteBuffer nodeBuffer = ByteBuffer.allocate(nodeSize);
        disk.read(nodeBuffer, position);
        nodeBuffer.flip();

        // Create a new BTreeNode object to hold the read data
        BTreeNode node = new BTreeNode(degree);

        // Read the node's metadata from the buffer
        node.numKeys = nodeBuffer.getInt(); // Read the number of keys 'n'
        node.isLeaf = nodeBuffer.get() == 1; // Read the isLeaf flag
        node.setParent(nodeBuffer.getLong()); // Read the parent pointer
        node.setLocation(nodeBuffer.getLong()); // Read the location

        // Read Objects
        for (int i = 0; i < node.numKeys; i++) {
            long key = nodeBuffer.getLong(); // Read the key
            node.keys[i] = new TreeObject(key); // Create a TreeObject and store the key
        }

        // Read child pointers if the node is not a leaf
        if (!node.isLeaf) {
            for (int i = 0; i <= node.numKeys; i++) {
                node.children[i] = nodeBuffer.getLong(); // Read the child pointer
            }
        }

        return node;
    }
  
    /**
     * Writes a BTreeNode's data to disk at the node's specified disk address.
     *
     * @param node         The BTreeNode to write to disk
     * @throws IOException if there is an error during writing
     */
    public void diskWrite(BTreeNode node) throws IOException {
        // Create a byte buffer with capacity: nodeSize
        ByteBuffer buffer = ByteBuffer.allocate(node.getNodeSize());

        // 
        
        // write node metadata to the buffer
        buffer.putInt(node.numKeys); // write amount of keys
        buffer.put((byte) (node.isLeaf ? 1 : 0)); // Write the leaf status as a byte (1 for true, 0 for false)
        buffer.putLong(node.getParentPointer()); // write the parent pointer
        buffer.putLong(node.getLocation()); // write the byte offset

        // Write keys
        for (int i = 0; i < node.numKeys; i++) {
            buffer.putLong(node.keys[i].getKey());
        }

        // If not a leaf, write children addresses
        if (!node.isLeaf) {
            for (int i = 0; i <= node.numKeys; i++) {  // note that there are n+1 children
                buffer.putLong(node.children[i]);
            }
        }
        
        // Prepare the buffer to be written by setting the position back to the start
        buffer.flip();
        // Write the buffer to the file at the current position
        disk.write(buffer, node.getLocation());
	}

    @Override
    public long getSize() {
        return (long) nodeCount * (2 * degree - 1); // each node has 2t - 1 keys
    }

    @Override
    public int getDegree() {
        return degree;
    }

    @Override
    public int getNumberOfNodes() {
        return nodeCount;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void delete(long key) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void insert(TreeObject obj) throws IOException {
        if (root.numKeys >= (2 * degree - 1)) { // if the root node is full
            BTreeNode newNode = new BTreeNode(degree);
            newNode.isLeaf = false;
            newNode.children[0] = root.getLocation(); // set first child to the old root

            splitChild(newNode, 0, root);
            root = newNode; // update root reference
            insertNonFull(newNode, obj);
        } else {
            insertNonFull(root, obj);
        }
    }

    private void insertNonFull(BTreeNode targetNode, TreeObject key) throws IOException {
        int i = targetNode.numKeys - 1; // Initialize an insertion index
        if (targetNode.isLeaf) {
            while (i >= 0 && targetNode.keys[i].compareTo(key) > 0) {
                i--;
            }
            // Insert the key into the node if there are no duplicates, otherwise increment the key's frequency
            if (targetNode.keys[i].compareTo(key) != 0) {
                targetNode.keys[i + 1] = key;
                targetNode.numKeys++;
                diskWrite(targetNode);
            } else {
                targetNode.keys[i].incrementFrequency();
            }
        } else {
            while (i >= 0 && targetNode.keys[i].compareTo(key) > 0) {
                i--;
            }
            i++; // Increment 'i' by 1 to move to the next child pointer
            BTreeNode nextTarget = diskRead(targetNode.children[i]);
            // Split the node if it's full
            if (nextTarget.numKeys == 2 * degree - 1) {
                splitChild(targetNode, i, nextTarget);
            }
            // increment 'i' once more if key > the median key in the new node
            if (i < targetNode.numKeys && targetNode.keys[i].compareTo(key) < 0) {
                i++;
                nextTarget = diskRead(targetNode.children[i]);
            }

            insertNonFull(nextTarget, key);
        }
    }

    /**
     * Splits a child of a B-tree node into two nodes. The mediam key will be moved up to the child's parent.
     *
     * @param parent             The parent node whose child is being split.
     * @param childPointerIndex  The index of the child node to split in the parent's children array.
     * @param child              The child node to split.
     */
    private void splitChild(BTreeNode parent, int childPointerIndex, BTreeNode child) throws IOException {
        BTreeNode newChild = new BTreeNode(degree);
        newChild.isLeaf = child.isLeaf;
        newChild.numKeys = degree - 1;

        TreeObject medianKey = child.keys[degree];

        // copy keys in the second half of the child node to the new child
        for (int j = 0; j < degree - 1; j++) {
            if (child.keys[degree + j] != medianKey) { // do not insert median key, as it will be moved to the parent node
                newChild.keys[j] = child.keys[degree + j];
            }
            child.keys[degree + j] = null; // clear the object in the older child
        }

        // update number of keys in the older child
        child.numKeys -= newChild.numKeys;

        // copy child pointers in the second half of the child node to the new child, if not a leaf
        if (!child.isLeaf) {
            for (int j = 0; j < degree; j++) {
                newChild.children[j] = child.children[degree + j];
                child.children[degree + j] = -1; // clear the child pointer in the child node
            }
        }

        // Move child pointers in parent to make space for the pointer to the new child
        // The new child will be placed after the older child (childPointerIndex + 1)
        for (int j = parent.numKeys + 1; j > childPointerIndex; j--) {
            if (j > childPointerIndex + 1) {
                parent.children[j] = parent.children[j - 1];
            } else {
                parent.children[j] = newChild.getLocation();
            }
        }
        
        // Move keys in the parent node to make space for the median key from the child node
        for (int j = parent.numKeys; j > childPointerIndex; j--) {
            parent.keys[j] = parent.keys[j - 1];
        }
        // Insert the median key from the child node into the correct position in the parent node
        parent.keys[childPointerIndex] = medianKey;
        parent.numKeys++;

        diskWrite(child);
        diskWrite(newChild);
        diskWrite(parent);
    }

    @Override
    public void dumpToFile(PrintWriter out) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'dumpToFile'");
    }

    @Override
    public TreeObject search(long key) throws IOException {
        return recursiveSearch(root, key);
    }

    /**
     * Helper function for search.
     * @param node The current node to be searched
     * @param key  The key value to search for
     * @return     The TreeObject with the matching key, null if key is not found
     * @throws IOException
     */
    private TreeObject recursiveSearch(BTreeNode node, long key) throws IOException {
        int i = 0;
        while (i <= node.numKeys && key < node.keys[i].getKey()) {
            i++;
        }
        if (i <= node.numKeys && key == node.keys[i].getKey()) {
            return node.keys[i];
        }
        if (node.isLeaf) {
            return null; // not found
        } else {
            BTreeNode nextNode = diskRead(node.getLocation() + node.getNodeSize());
            return recursiveSearch(nextNode, key);
        }
    }

    public long[] getSortedKeyArray() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSortedKeyArray'");
    }

    // size of file, address of root node, degree, height, numNodes
    public int getMetaDataSize() {
        return Long.BYTES * 2 + Integer.BYTES * 3;
    }

    /**
     * write meta data to top of the file
     * DO NOT change the write order
     * @throws IOException
     */
    public void writeMetaData() throws IOException {
        disk.position(0); // set to start of file
        ByteBuffer buffer = ByteBuffer.allocate(getMetaDataSize());

        // initialize the buffer with the data
        buffer.putLong(size);
        buffer.putLong(root.getLocation());
        buffer.putInt(degree);
        buffer.putInt(height);
        buffer.putInt(nodeCount);

        // write the data to the disk
        buffer.flip();
        disk.write(buffer);
    }
}
