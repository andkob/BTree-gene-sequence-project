package cs321.btree;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class BTree implements BTreeInterface
{

    public BTree() {
        // TODO constructor
    }

    public static void main(String[] args) {
        BTree temp = new BTree();
        String filePath = "C:\\Users\\Kobus\\VSCodeWorkspace\\cs321\\CS321_BTree_Project\\data\\files_gbk\\test0.gbk";
        long position = 4205;

        // Read data from file
        byte[] dataRead = temp.diskRead(filePath, position, 64);
        if (dataRead != null) {
            // Process the read data
            System.out.println("Data read successfully: " + new String(dataRead));
        } else {
            System.out.println("Failed to read data from file.");
        }
    }

    public byte[] diskRead(String filePath, long position, int bytesToRead) {
        try (RandomAccessFile file = new RandomAccessFile(filePath, "r");
            FileChannel channel = file.getChannel()) {

                channel.position(position); // set the file cursor to the specified position
                ByteBuffer buffer = ByteBuffer.allocate(bytesToRead); // create a byte buffer to read data
                channel.read(buffer); // read data from the file into the byte buffer

                // Get the bytes from the byte buffer
                byte[] data = new byte[bytesToRead];
                buffer.flip();
                buffer.get(data);

                return data;
            } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
}
