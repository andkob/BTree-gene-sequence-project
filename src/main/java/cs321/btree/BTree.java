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
	
    static private final int DEGREE = 51; // Constant for this project

    private long size; // BTree size in Bytes
    private int height;
    private int degree;
    private int nodeCount;
    private BTreeNode root;
    private FileChannel channel;

    /**
     * Constructs a BTree using the default degree and initializes it from a file
     * if it exists, or creates a new one if it does not.
     *
     * @param filePath the path to the file that stores the B-Tree on disk
     */
    public BTree(String filePath) {
        this(filePath, DEGREE);
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
        RandomAccessFile raf = null;

        try {
            if (file.exists()) {
                raf = new RandomAccessFile(filePath, "r");
                channel = raf.getChannel();
                // Read metadata from the file
                ByteBuffer metadataBuffer = ByteBuffer.allocate(getMetaDataSize());
                channel.read(metadataBuffer);
                metadataBuffer.flip();

                // Parse metadata | DO NOT change the read order
                size = metadataBuffer.getLong();
                long rootPointer = metadataBuffer.getLong();
                degree = metadataBuffer.getInt();
                height = metadataBuffer.getInt();
                nodeCount = metadataBuffer.getInt();

                // Read root node from disk based on the rootPointer
                ByteBuffer rootBuffer = ByteBuffer.allocate(BTreeNode.NODE_SIZE);
                channel.read(rootBuffer, rootPointer);
                rootBuffer.flip();

                // Construct the root node from the buffer
                root = BTreeNode.fromByteBuffer(rootBuffer); // 
            } else {
                file.createNewFile();
                raf = new RandomAccessFile(file, "rw");
                channel = raf.getChannel();
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
     * Converts the ByteBuffer to a byte array for further processing.
     *
     * @param nodePointer the pointer to the node's position in the file
     * @return byte array containing the node's data
     */
    public byte[] diskRead(long nodePointer) {
        try {
            // Calculate the position of the node in the file based on the nodePointer
            long position = getMetaDataSize() + (nodePointer * BTreeNode.NODE_SIZE);
    
            // Read the node from the file
            ByteBuffer nodeBuffer = ByteBuffer.allocate(BTreeNode.NODE_SIZE);
            channel.read(nodeBuffer, position);
            nodeBuffer.flip();
    
            // Convert the ByteBuffer to a byte array
            byte[] nodeData = new byte[BTreeNode.NODE_SIZE];
            nodeBuffer.get(nodeData);
    
            return nodeData;
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
    public void diskWrite(BTreeNode node, FileChannel fileChannel) throws IOException {
        // Determine the size of the node to allocate a buffer of appropriate size
        ByteBuffer buffer = ByteBuffer.allocate(getNodeDiskSize(node));

        // Write the number of keys in the node
        buffer.putInt(node.n);
        // Write the leaf status as a byte (1 for true, 0 for false)
        buffer.put((byte) (node.isLeaf ? 1 : 0));

        // Write all keys in this node
        for (int i = 0; i < node.n; i++) {
            buffer.putLong(node.keys[i]);
        }

        // If the node is not a leaf, write addresses of its children
        if (!node.isLeaf) {
            for (int i = 0; i <= node.n; i++) {  // note that there are n+1 children
                buffer.putLong(node.children[i]);
            }
        }

        // Prepare the buffer to be written by setting the position back to the start
        buffer.flip();
        // Set the position in the file to the node's disk address
        fileChannel.position(node.diskAddress);
        // Write the buffer to the file at the current position
        fileChannel.write(buffer);
        // Optionally force writing to disk for data integrity
        fileChannel.force(true);
    }

    /**
     * Calculates the required disk size for a BTreeNode based on its properties.
     *
     * @param node the BTreeNode for which to calculate the disk size
     * @return the size in bytes required to store the node on disk
     */
    private int getNodeDiskSize(BTreeNode node) {
        int size = Integer.BYTES; // Space for 'n', the number of keys
        size += 1; // Space for the leaf indicator (1 byte)
        size += Long.BYTES * node.keys.length; // Space for keys

        // If not a leaf, also allocate space for addresses of children
        if (!node.isLeaf) {
            size += Long.BYTES * node.children.length;
        }
        return size;
    }	

	@Override
	public long getSize() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getSize'");
	}

	@Override
	public int getDegree() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getDegree'");
	}

	@Override
	public int getNumberOfNodes() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getNumberOfNodes'");
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getHeight'");
	}

	@Override
	public void delete(long key) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'delete'");
	}

	@Override
	public void insert(TreeObject obj) throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'insert'");
	}

	@Override
	public void dumpToFile(PrintWriter out) throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'dumpToFile'");
	}

	@Override
	public TreeObject search(long key) throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'search'");
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
			channel.position(0); // set to start of file
			ByteBuffer buffer = ByteBuffer.allocate(getMetaDataSize());

			buffer.putLong(size);
			buffer.putLong(root.getPointer());
			buffer.putInt(degree);
			buffer.putInt(height);
			buffer.putInt(nodeCount);

			channel.write(buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
