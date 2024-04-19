package cs321.btree;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Represents a B-Tree structure that provides efficient data insertion, deletion,
 * and lookup. This implementation specifically supports disk-based operations
 * to accommodate large data sets that do not fit into main memory.
 */
public class BTree implements BTreeInterface {
	
    static private final int OPTIMAL_DEGREE = 85; // calculated optimal degree t

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
    public BTree(String filePath) {
        this(filePath, OPTIMAL_DEGREE);
    }

    /**
     * Constructs a BTree from a specified file path and degree.
     * If the file exists, it reads the BTree metadata and root from the file.
     * Otherwise, it initializes a new empty BTree with a specified degree.
     *
     * @param filePath the path of the file to store the BTree
     * @param degree   the minimum degree of the BTree
     */
    public BTree(String filePath, int degree) {
        this.size = 0;
        this.height = 0;
        this.degree = degree;
        this.nodeCount = 1;

        File file = new File(filePath);

        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            if (file.exists()) {
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
            }
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  
    /**
     * Reads the data of a BTreeNode from disk at a specified position.
     *
     * @param nodePointer the pointer to the node's position in the file
     * @return BTreeNode the node at the specified pointer
     */
    public BTreeNode diskRead(long nodePointer) {
        try {
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
            node.setLocation(nodeBuffer.getInt()); // Read the location
    
            // Read Objects
            for (int i = 0; i < node.numKeys; i++) {
                long key = nodeBuffer.getLong(); // Read the key
                node.objects[i] = new TreeObject(key); // Create a TreeObject and store the key
            }
    
            // Read child pointers if the node is not a leaf
            if (!node.isLeaf) {
                for (int i = 0; i <= node.numKeys; i++) {
                    node.children[i] = nodeBuffer.getLong(); // Read the child pointer
                }
            }
    
            return node;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
  
    /**
     * Writes a BTreeNode's data to disk at the node's specified disk address.
     * Ensures that the node data is written as a single atomic operation.
     *
     * @param node        the BTreeNode to write to disk
     * @param fileChannel the FileChannel to use for writing
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
            buffer.putLong(node.objects[i].getKey());
        }

        // If not a leaf, write children addresses
        if (!node.isLeaf) {;
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
        if (root.numKeys == (2 * degree - 1)) {
            BTreeNode newNode = new BTreeNode(degree);
            newNode.numKeys = 0;
            newNode.isLeaf = false;
            newNode.children[0] = root.getLocation(); // set first child to the old root

            
            splitChild(newNode, 0, root);
            root = newNode; // update root
        } else {
            insertNonFull(obj);
        }
    }

    private void insertNonFull(TreeObject obj) throws IOException {

    }

    private void splitChild(BTreeNode node, int i, BTreeNode nodesChild) {
        BTreeNode z = new BTreeNode(degree); // this is the new splitted node
        z.isLeaf = nodesChild.isLeaf; // set as leaf if nodes child is a leaf
        z.numKeys = degree - 1;
        
        // copy keys
        for (int j = 0; j < degree - 1; j++) {
            z.objects[j] = nodesChild.objects[degree-1 + j];
        }

        // copy child pointers if not a leaf
        if (!nodesChild.isLeaf) {
            for (int j = 0; j < degree; j++) {
                z.children[j] = nodesChild.children[degree + j];
            }
        }
    }

    @Override
    public void dumpToFile(PrintWriter out) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'dumpToFile'");
    }

    @Override
    public TreeObject search(long key) throws IOException {
        return helperSearch(root, key);
    }

    private TreeObject helperSearch(BTreeNode node, long key) {
        int i = 0;
        while (i <= node.numKeys && key < node.objects[i].getKey()) {
            i++;
        }
        if (i <= node.numKeys && key == node.objects[i].getKey()) {
            return node.objects[i];
        }
        if (node.isLeaf) {
            return null; // not found
        } else {
            BTreeNode nextNode = diskRead(node.getLocation() + node.getNodeSize());
            return helperSearch(nextNode, key); // check this return statement
        }
    }

    // size of file, address of root node, degree, height, numNodes
    public int getMetaDataSize() {
        return Long.BYTES * 2 + Integer.BYTES * 3;
    }

  /**
   * write meta data to top of the file
   * DO NOT change the write order
   */
  public void writeMetaData() {
      try {
          disk.position(0); // set to start of file
          ByteBuffer buffer = ByteBuffer.allocate(getMetaDataSize());

          buffer.putLong(size);
          buffer.putLong(root.getLocation());
          buffer.putInt(degree);
          buffer.putInt(height);
          buffer.putInt(nodeCount);

          disk.write(buffer);
      } catch (IOException e) {
          e.printStackTrace();
      }
   }
}
