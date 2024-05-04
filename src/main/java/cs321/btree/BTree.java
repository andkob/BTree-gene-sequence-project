package cs321.btree;

import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.LinkedList;

import cs321.create.SequenceUtils;

/**
 * Represents a B-Tree structure that provides efficient data insertion, deletion,
 * and lookup. This implementation specifically supports disk-based operations
 * to accommodate large data sets that do not fit into main memory.
 * 
 * @author Andrew Kobus
 * @author Caleb Tolman
 */
public class BTree implements BTreeInterface {

    static private final int OPTIMAL_DEGREE = 101; // calculated optimal degree t

    private long size; // BTree size in Bytes
    private int height; // Height of the tree
    private int degree; // Degree of the tree
    private int nodeCount; // Total number of BTreeNodes
    private BTreeNode root; // Reference to the root node in the file
    private FileChannel disk; // Named this 'disk' for simplicity. All I/O operations should be done through this file channel.
    private int numKeys; // Total number of keys in the BTree
    private int seqLength;
//--------------------------------------------------------
    private boolean usingCache = false;
    private Cache<BTreeNode> cache;
//--------------------------------------------------------
    /**
     * Constructs a BTree using the default degree and initializes it from a file
     * if it exists, or creates a new one if it does not.
     *
     * @param filePath the path to the file that stores the B-Tree on disk
     */
    public BTree(String filePath) {
        this(OPTIMAL_DEGREE, filePath, 0, false, 0);
    }
    
    /**
     * Constructs a BTree from a specified file path and degree.
     * If the file exists, it reads the BTree metadata and root from the file.
     * Otherwise, it initializes a new empty BTree with a specified degree.
     *
     * @param degree   the minimum degree of the BTree
     * @param filePath the path of the file to store the BTree
     */
    public BTree(int degree, String filePath) {
        this(degree, filePath, 0, false, 0);
    }
    
    /**
     * Constructs a B-Tree from a specified file path and degree.
     * If the file exists, it reads the B-Tree metadata and the root node from the file.
     * Otherwise, it initializes a new empty B-Tree with a specified degree.
     * This B-Tree has an option to use cache for faster I/O operations.
     * 
     * @param degree    The degree of the B-tree.
     * @param filePath  The path to the file containing the B-tree data.
     * @param seqLength The sequence length for encoding.
     * @param useCache  Specifies whether to use caching.
     * @param cacheSize The size of the cache, if caching is enabled.
     */
    @SuppressWarnings("resource") // raf must stay open
    public BTree(int degree, String filePath, int seqLength, boolean useCache, int cacheSize) {
        // initialize fields
        this.size = 0;
        this.height = 0;
        this.degree = degree;
        this.nodeCount = 1;
        this.seqLength = seqLength;
//--------------------------------------------------------------------
        if(useCache) {
			cache = new Cache<BTreeNode>(cacheSize);
			this.usingCache = true;
		}
//--------------------------------------------------------------------
        File file = new File(filePath);
        try {
            if (file.exists()) { // Read in data from the file
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                disk = raf.getChannel(); // initialize file channel with the unique FileChannel associated with the RAF

                // Read metadata from the file
                ByteBuffer metadataBuffer = ByteBuffer.allocate(getMetaDataSize());
                disk.position(0); // set the file channel to the beginning of the file
                disk.read(metadataBuffer);

                // Parse metadata | DO NOT change the read order
                metadataBuffer.flip();
                size = metadataBuffer.getLong();
                long rootPointer = metadataBuffer.getLong();
                degree = metadataBuffer.getInt();
                height = metadataBuffer.getInt();
                nodeCount = metadataBuffer.getInt();

                // Read root node from disk based on the rootPointer
                root = diskRead(rootPointer);
            } else { // Initialize a new file to store all the data for this B-Tree
                file.createNewFile();
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                disk = raf.getChannel(); // initialize file channel with the unique FileChannel associated with the RAF
                root = new BTreeNode(degree); // initialize the root node as a new node
                root.setLocation(getMetaDataSize()); // set the root location after the BTree metadata
                size = getMetaDataSize() + root.getNodeSize(); // Initial BTree size (237 Bytes)
                writeMetaData(); // write meta data to file
                diskWrite(root); // write the root node to the disk
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the data of a BTreeNode from disk at a specified position.
     *
     * @param nodePointer The pointer to the node's position in the file
     * @return BTreeNode The node at the specified pointer
     * @throws IOException if there is an error during reading
     */
    public BTreeNode diskRead(long nodePointer) throws IOException {
//---------------------------------------------------------------------------------------
        // check if it is in the cache first
		if(usingCache) {
            BTreeNode existingNode = cache.getObject(nodePointer);

            // add the node to the cache if the cache does not contain it
            // or move it up to the top of the cache
            if (existingNode != null) {
                cache.addObject(nodePointer, existingNode);
                return existingNode;
            }
        }
//---------------------------------------------------------------------------------------

        // Create a new BTreeNode object to hold the read data
        BTreeNode node = new BTreeNode(degree);
        int nodeSize = node.getNodeSize(); // Determine the size of the node for the byte buffer

        // Read the node from the file
        ByteBuffer nodeBuffer = ByteBuffer.allocate(nodeSize);
        disk.position(nodePointer); // set position starting at the node's location
        disk.read(nodeBuffer);
        nodeBuffer.flip(); // reset to start at the nodePointer

        // Read the node's metadata from the buffer
        node.numKeys = nodeBuffer.getInt(); // Read the number of keys 'n'
        node.isLeaf = nodeBuffer.get() == 1; // Read the isLeaf flag
        node.setParent(nodeBuffer.getLong()); // Read the parent pointer
        node.setLocation(nodeBuffer.getLong()); // Read the location

        // Read Objects
        for (int i = 0; i < node.numKeys; i++) {
            long key = nodeBuffer.getLong(); // Read the key
            int frequency = nodeBuffer.getInt(); // read the key's frequency
            node.keys[i] = new TreeObject(key, frequency); // Create a TreeObject with the data and store it
        }

        // Read child pointers if the node is not a leaf
        if (!node.isLeaf) {
            for (int i = 0; i <= node.numKeys; i++) {
                node.children[i] = nodeBuffer.getLong(); // Read the child pointer
            }
        }

//-----------------------------------------------------------------------------
        // Insert into cache if cache is being utilized
		if (usingCache) {
			// Inserts new, updated node into cache
			cache.addObject(nodePointer, node);
		}
//-----------------------------------------------------------------------------

        return node;
    }

    /**
     * Writes a BTreeNode's data to disk at the node's specified disk address.
     *
     * @param node The BTreeNode to write to disk
     * @throws IOException if there is an error during writing
     */
    public void diskWrite(BTreeNode node) throws IOException {
        // Create a byte buffer with capacity: nodeSize
        ByteBuffer buffer = ByteBuffer.allocate(node.getNodeSize());

        // write node metadata to the buffer
        buffer.putInt(node.numKeys); // write amount of keys
        buffer.put((byte) (node.isLeaf ? 1 : 0)); // Write the leaf status as a byte (1 for true, 0 for false)
        buffer.putLong(node.getParentPointer()); // write the parent pointer
        buffer.putLong(node.getLocation()); // write the byte offset

        // Write the data for each key (TreeObject)
        for (int i = 0; i < node.numKeys; i++) {
            if (node.keys[i] != null) {
                buffer.putLong(node.keys[i].getKey()); // write the DNA sequence value
                buffer.putInt(node.keys[i].getCount()); // write the frequency of the key
            }
        }

        // If not a leaf, write children addresses
        if (!node.isLeaf) {
            for (int i = 0; i <= node.numKeys; i++) { // note that there are n+1 children
                buffer.putLong(node.children[i]);
            }
        }

        // Write the buffer to the file at the current position
        buffer.flip();
        disk.position(node.getLocation());
        disk.write(buffer);
//-----------------------------------------------------------------------------
        // Insert into cache if cache is being utilized
		if (usingCache) {
			// Inserts new, updated node into cache
			cache.addObject(node.getLocation(), node);
		}
//------------------------------------------------------------------------------
    }

    @Override
    public long getSize() {
        return numKeys;
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

    /**
     * Returns a sorted array of all the keys in this B-Tree
     * @return Array of each key in this B-Tree, sorted in non-decreasing order
     */
    public long[] getSortedKeyArray() throws IOException {
        long[] sortedKeys = null;
        sortedKeys = new long[(int) numKeys]; // initialize array with size: total keys in the B-Tree
        int index = 0;
        // traverse and collect keys
        traverseAndCollectKeys(root, sortedKeys, index);
        Arrays.sort(sortedKeys); // ensure keys are sorted
        return sortedKeys;
    }

    /**
     * Traverse the BTree and collect keys into the sortedKeys array.
     * 
     * @param node       The current node being visited
     * @param sortedKeys The array to store sorted keys
     * @param index      The current index in the sortedKeys array
     */
    private int traverseAndCollectKeys(BTreeNode node, long[] sortedKeys, int index) throws IOException {
        if (node != null) {
            for (int i = 0; i < node.numKeys; i++) {
                sortedKeys[index++] = node.keys[i].getKey();
            }
            if (!node.isLeaf) {
                for (int i = 0; i <= node.numKeys; i++) {
                    index = traverseAndCollectKeys(diskRead(node.children[i]), sortedKeys, index);
                }
                return index;
            }
        }
        return index;
    }

    @Override
    public void delete(long key) {
        // Javadoc says "not implemented" sooo \O/
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void insert(TreeObject obj) throws IOException {
        if (root.numKeys >= (2 * degree - 1)) {  // check if the root node is full
            // Initialize a new node to act as the new root
            BTreeNode newNode = new BTreeNode(degree);
            newNode.children[0] = root.getLocation(); // set first child to the old root
            newNode.setLocation(getMetaDataSize() + newNode.getNodeSize() * nodeCount);
            nodeCount++;
            height++;
            // Split the full root and continue the insert operation
            splitChild(newNode, 0, root);
            root = newNode; // update root reference
            insertNonFull(newNode, obj);
        } else { // not full
            insertNonFull(root, obj);
        }
    }

    /**
     * Insert a given sequence in the B-Tree. If the sequence already exists in the B-Tree,
     * the frequency count is incremented. Otherwise a new node is inserted following the
     * B-Tree insertion algorithm.
     * 
     * @param targetNode
     * @param key
     * @throws IOException
     */
    private void insertNonFull(BTreeNode targetNode, TreeObject key) throws IOException {
        int i = targetNode.numKeys - 1; // Initialize an insertion index
        if (targetNode.isLeaf) {

            // check for duplicates
            for (int j = 0; j < targetNode.numKeys; j++) {
                if (targetNode.keys[j].compareTo(key) == 0) {
                    targetNode.keys[j].incrementFrequency();
                    diskWrite(targetNode);
                    return; // exit insert
                }
            }

            // Shift keys to make room for the new key after the insertion index
            while (i >= 0 && targetNode.keys[i].compareTo(key) > 0) {
                targetNode.keys[i + 1] = targetNode.keys[i];
                i--; // decrement insertion index until the correct one is found
            }

            // Insert the key into the node if there are no duplicates, otherwise increment the key's frequency
            if (i > -1) { // if i = -1, the node is empty
                targetNode.keys[i + 1] = key;
                targetNode.numKeys++;
                numKeys++;
            } else {
                targetNode.keys[0] = key;
                targetNode.numKeys++;
                numKeys++;
            }
        } else { // Handle non-leaf nodes
            
            // check for duplicates in the targetNode
            for (int j = 0; j < targetNode.numKeys; j++) {
                if (targetNode.keys[j].compareTo(key) == 0) {
                    targetNode.keys[j].incrementFrequency();
                    diskWrite(targetNode);
                    return; // exit insert
                }
            }

            // Shift keys to make room for the new key after the insertion index
            while (i >= 0 && targetNode.keys[i].compareTo(key) > 0) {
                i--;
            }

            i++; // Increment 'i' by 1 to move to the next child pointer
            BTreeNode targetChild = diskRead(targetNode.children[i]); // get the next child
            // Split the node if it's full
            if (targetChild.numKeys == 2 * degree - 1) {

                // Edge Case Check: Before splitting, make sure the child does not contain the duplicate
                for (int j = 0; j < targetChild.numKeys; j++) {
                    if (targetChild.keys[j].compareTo(key) == 0) {
                        targetChild.keys[j].incrementFrequency();
                        diskWrite(targetChild);
                        return; // exit insert
                    }
                }
                
                splitChild(targetNode, i, targetChild);

                // Find if the key goes into the child at i or i + 1
                if (i < targetNode.numKeys && targetNode.keys[i].compareTo(key) < 0) {
                    i++;
                    targetChild = diskRead(targetNode.children[i]);
                }
            }
            insertNonFull(targetChild, key);
        }
        diskWrite(targetNode);
    }

    /**
     * Splits a child of a B-tree node into two nodes. The mediam key will be moved
     * up to the child's parent.
     *
     * @param parent            The parent node whose child is being split.
     * @param childPointerIndex The index of the child node to split in the parent's children array.
     * @param child             The child node to split.
     */
    private void splitChild(BTreeNode parent, int childPointerIndex, BTreeNode child) throws IOException {
        parent.isLeaf = false; // parent will gain at least one child, so ensure it is set to a leaf
        
        // Initialize the new child node
        BTreeNode newChild = new BTreeNode(degree);
        newChild.isLeaf = child.isLeaf;
        newChild.numKeys = degree - 1;
        newChild.setLocation(getMetaDataSize() + newChild.getNodeSize() * nodeCount); // set location to the next open spot in the file
        nodeCount++;

        // find the median key in the older child node
        TreeObject medianKey = child.keys[degree - 1];
        child.keys[degree - 1] = null; // clear the median key

        // copy keys in the second half of the child node to the new child
        for (int j = 0; j < degree - 1; j++) {
            newChild.keys[j] = child.keys[degree + j];
            child.keys[degree + j] = null; // clear the object in the older child
        }

        // copy child pointers in the second half of the child node to the new child, if not a leaf
        if (!child.isLeaf) {
            for (int j = 0; j < degree; j++) {
                newChild.children[j] = child.children[degree + j];
                child.children[degree + j] = -1; // clear the child pointer in the older child
            }
        }
        child.numKeys = degree - 1; // update child's numKeys

        // Move child pointers in parent to make space for the pointer to the new child
        // The new child will be placed after the older child (childPointerIndex + 1)
        for (int j = parent.numKeys; j >= childPointerIndex + 1; j--) {
            parent.children[j + 1] = parent.children[j];
        }
        parent.children[childPointerIndex + 1] = newChild.getLocation();

        // Move keys in the parent node to make space for the median key from the child node
        for (int j = parent.numKeys - 1; j >= childPointerIndex; j--) {
            parent.keys[j + 1] = parent.keys[j];
        }

        // Insert the median key from the child node into the correct position in the parent node
        parent.keys[childPointerIndex] = medianKey;
        parent.numKeys++;

        // Ensure both child nodes are pointing to their parent
        child.setParent(parent.getLocation());
        newChild.setParent(parent.getLocation());

        // Write all nodes to the disk
        diskWrite(child);
        diskWrite(newChild);
        diskWrite(parent);
    }

    @Override
    public void dumpToFile(PrintWriter out) throws IOException {
        // Check if the tree is empty
        if (root == null) {
            out.println("The B-tree is empty.");
            return;
        }
        // Start the recursive in-order traversal from the root
        dumpNode(root, out);
    }
    
    /**
     * A helper method to perform an in-order traversal of the B-tree, starting from
     * a given node, and writing each key to a PrintWriter.
     *
     * @param node The node to start the traversal from.
     * @param out  The PrintWriter to write the keys to.
     * @throws IOException If there is an error reading from the disk.
     */
    private void dumpNode(BTreeNode node, PrintWriter out) throws IOException {
        if (node == null) {
            return; // Base case: reached a leaf's child.
        }
    
        // If the node is not a leaf, recurse on the left child of the first key.
        if (!node.isLeaf) {
            for (int i = 0; i < node.numKeys; i++) {
                // Recursively visit the left child
                if (node.children[i] != -1) {
                    BTreeNode child = diskRead(node.children[i]);
                    dumpNode(child, out);
                }
                // Visit the current key
                if (node.keys[i] != null) {
                    // decode sequence and count the occurences of DNA bases
                    String sequence = SequenceUtils.longToDnaString(node.keys[i].getKey(), seqLength);
                    out.println(sequence + " " + node.keys[i].getCount());
                }
            }
            // Recursively visit the right child of the last key
            // if (i == node.numKeys - 1 && node.children[i + 1] != -1) {
                BTreeNode child = diskRead(node.children[node.numKeys]);
                dumpNode(child, out);
            // }
        } else {
            // Leaf node, simply print all keys
            for (int i = 0; i < node.numKeys; i++) {
                long encodedSequence = node.keys[i].getKey();
                if (encodedSequence != -1) {
                    // decode sequence and count the occurences of DNA bases
                    String sequence = SequenceUtils.longToDnaString(encodedSequence, seqLength);
                    out.println(sequence + " " + node.keys[i].getCount());
                }
            }
        }
    }
    

    @Override
    public TreeObject search(long key) throws IOException {
        return recursiveSearch(root, key);
    }

    /**
     * Helper function for search.
     * 
     * @param node The current node to be searched
     * @param key  The key value to search for
     * @return The TreeObject with the matching key, null if key is not found
     * @throws IOException
     */
    private TreeObject recursiveSearch(BTreeNode node, long key) throws IOException {
        if (node == null || node.numKeys == 0) {
            return null;  // Return null if the node is null or empty
        }

        int i = 0;
        while (i < node.numKeys && key > node.keys[i].getKey()) {
            i++;
        }
        if (i < node.numKeys && key == node.keys[i].getKey()) {
            return node.keys[i];
        }
        if (node.isLeaf) {
            return null; // not found
        } else {
            BTreeNode nextNode = diskRead(node.children[i]);
            TreeObject foundKey = recursiveSearch(nextNode, key);
            if (foundKey == null) { // not found so go to the other child
                foundKey = recursiveSearch(diskRead(node.children[i - 1]), key);
            }
            return foundKey;
        }
    }

    /**
     * Calculates and returns the total size in bytes of the B-Tree metadata
     * 
     * @return The size of the file, address of root node, degree, height, and
     *         numNodes variables
     */
    public int getMetaDataSize() {
        return Long.BYTES * 2 + Integer.BYTES * 3;
    }

    /**
     * write meta data to top of the file
     * DO NOT change the write order
     * 
     * @throws IOException
     */
    public void writeMetaData() throws IOException {
        disk.position(0); // set to start of file
        ByteBuffer buffer = ByteBuffer.allocate(getMetaDataSize());
        buffer.position(0);

        // initialize the buffer with the data
        buffer.putLong(size); // write BTreeSize in bytes
        buffer.putLong(root.getLocation()); // write the location of the root
        buffer.putInt(degree);
        buffer.putInt(height);
        buffer.putInt(nodeCount);

        // write the data to the disk
        buffer.flip();
        disk.write(buffer);
    }

    /**
     * Closes the file channel associated with this B-Tree.
     * This should be called whenever you are done with this B-Tree
     * to prevent resource leaks and ensure proper file deletion.
     */
    public void close() {
        try {
            disk.close();
        } catch (IOException e) {
            System.out.println("Error when trying to close file channel");
            e.printStackTrace();
        }
    }
}
