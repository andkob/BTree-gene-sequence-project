package cs321.btree;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class BTree implements BTreeInterface {
	
	public void diskWrite(BTreeNode node, FileChannel fileChannel) throws IOException {
	    ByteBuffer buffer = ByteBuffer.allocate(getNodeDiskSize(node));

	    // Start by writing the node metadata
	    buffer.putInt(node.n);
	    buffer.put((byte) (node.isLeaf ? 1 : 0));

	    // Write keys
	    for (int i = 0; i < node.n; i++) {
	        buffer.putLong(node.keys[i]);
	    }

	    // If not a leaf, write children addresses
	    if (!node.isLeaf) {
	        for (int i = 0; i <= node.n; i++) {  // note that there are n+1 children
	            buffer.putLong(node.children[i]);
	        }
	    }

	    buffer.flip();  // prepare buffer for writing
	    fileChannel.position(node.diskAddress);  // Set position in file (if needed)
	    fileChannel.write(buffer);
	    fileChannel.force(true);  // ensure changes are written to disk
	}

	private int getNodeDiskSize(BTreeNode node) {
	    int size = Integer.BYTES; // for 'n'
	    size += 1; // for isLeaf
	    size += Long.BYTES * node.keys.length; // for keys
	    if (!node.isLeaf) {
	        size += Long.BYTES * node.children.length; // for children addresses
	    }
	    return size;
	}

	
	@Override
	public long getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDegree() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumberOfNodes() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void delete(long key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insert(TreeObject obj) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dumpToFile(PrintWriter out) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TreeObject search(long key) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
