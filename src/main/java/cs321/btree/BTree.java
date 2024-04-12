package cs321.btree;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;

public class BTree implements BTreeInterface
{

    static private final int DEGREE = 51;

    private long size; // BTree size in Bytes
    private int height;
    private int degree;
    private int nodeCount;
    private BTreeNode root;
    private FileChannel channel;

    public BTree(String filePath) {
        this(filePath, DEGREE);
    }

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
                this.root = new BTreeNode();
                writeMetaData(); // write meta data to file
            }
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
