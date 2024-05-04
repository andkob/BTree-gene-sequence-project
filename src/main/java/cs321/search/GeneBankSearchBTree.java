package cs321.search;

import cs321.btree.BTree;
import cs321.btree.TreeObject;
import cs321.common.ParseArgumentException;
import cs321.common.ParseArgumentUtils;
import cs321.create.SequenceUtils;

import java.io.File;
import java.util.Scanner;

public class GeneBankSearchBTree
{

    public static void main(String[] args) throws Exception
    {
        GeneBankSearchBTreeArguments arguments = new GeneBankSearchBTreeArguments(args);
        if (!arguments.validate()) {
            return;
        }
        
        // Create a B-Tree with the arguments
        int degree = arguments.getDegree();
        String btreefilename = arguments.getBtreeFilename();
        int seqLength = arguments.getSeqLength();
        boolean useCache = arguments.getUseCache() == 1;
        int cacheSize = arguments.getCacheSize();

        BTree btree = new BTree(degree, btreefilename, seqLength, useCache, cacheSize);

        // Search BTree for all sequences in the query file
        File queryfile = new File(arguments.getQueryFilename());
        Scanner queryScanner = new Scanner(queryfile);
        
        while (queryScanner.hasNextLine()) {
            String sequence = queryScanner.nextLine();
            long sequenceAsLong = SequenceUtils.dnaStringToLong(sequence);

            // search BTree for the sequence and its complement
            TreeObject foundObject = btree.search(sequenceAsLong);
            System.out.print("For query '" + sequence + "', ");
            if (foundObject != null) {
                String foundSequence = SequenceUtils.longToDnaString(foundObject.getKey(), seqLength);
                System.out.println("found: " + foundSequence + " " + foundObject.getCount());
            } else {
                System.out.println("no key found");
            }
        }

        queryScanner.close();
    }
}

