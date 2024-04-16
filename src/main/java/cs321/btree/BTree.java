package cs321.btree;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class BTree implements BTreeInterface
{
    static private final int OPTIMAL_DEGREE = 51; // calculated optimal degree t TODO - recalculate after a design is agreed upon

    private long size; // BTree size in Bytes
    private int height;
    private int degree;
    private int nodeCount;
    private BTreeNode root;
    private FileChannel disk;

    public BTree(String filePath) {
        this(filePath, OPTIMAL_DEGREE);
    }

    public BTree(String filePath, int degree) {
        this.size = 0;
        this.height = 0;
        this.degree = degree;
        this.nodeCount = 1;

        File file = new File(filePath);

        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            if (file.exists()) {
                disk = raf.getChannel(); // initialize channel with the unique FileChannel associated with the RAF
                
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
                root = BTreeNode.fromByteBuffer(rootBuffer);
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
    
            // Read keys
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
  
  	public void diskWrite(BTreeNode node, FileChannel fileChannel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(getNodeDiskSize(node));

        // Start by writing the node metadata
        buffer.putInt(node.numKeys);
        buffer.put((byte) (node.isLeaf ? 1 : 0));

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

        buffer.flip();  // prepare buffer for writing
        fileChannel.position(node.getLocation());  // Set position in file (if needed)
        fileChannel.write(buffer);

        // Append a newline character after writing the node data. This will be used as the delimiter
        ByteBuffer newlineBuffer = ByteBuffer.wrap("\n".getBytes());
        fileChannel.write(newlineBuffer);

        fileChannel.force(true);  // ensure changes are written to disk
	}

	private int getNodeDiskSize(BTreeNode node) {
	    int size = Integer.BYTES; // for 'n'
	    size += 1; // for isLeaf
	    size += Long.BYTES * node.objects.length; // for keys
	    if (!node.isLeaf) {
	        size += Long.BYTES * node.children.length; // for children addresses
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
