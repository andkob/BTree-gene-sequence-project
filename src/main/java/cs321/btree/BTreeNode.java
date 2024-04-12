package cs321.btree;

public class BTreeNode {
    int n;
    long[] keys;
    long[] children;
    boolean isLeaf;
    long diskAddress;

    public BTreeNode(int t) {
        keys = new long[2 * t - 1];  // max keys
        children = new long[2 * t];  // max children
        isLeaf = true;
        n = 0;
        diskAddress = -1;  // will be set when writing to disk
    }
}
