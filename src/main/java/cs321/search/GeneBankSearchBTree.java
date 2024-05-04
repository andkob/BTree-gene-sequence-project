package cs321.search;

import cs321.btree.BTree;
import cs321.btree.TreeObject;
import cs321.common.ParseArgumentException;
import cs321.common.ParseArgumentUtils;
import cs321.create.SequenceUtils;

import java.io.File;
import java.util.Scanner;

/**
 * This class is designed to search a BTree for specific DNA sequences provided in a query file.
 * It utilizes command-line arguments to configure the search process, including the specification of the BTree file,
 * whether to use a cache, and the cache size. The results of the search are printed to the standard output, indicating
 * whether each query sequence was found in the BTree and its frequency.
 *
 * @author Team 06
 * 
 */
public class GeneBankSearchBTree
{

    /**
     * Main method to run GeneBankSearchBTree. It initializes the BTree with the specified configuration and performs searches
     * based on sequences listed in the query file.
     *
     * @param args the command-line arguments, specifying the BTree file, query file, whether to use cache, and the cache size.
     * @throws Exception if there is an error during the BTree operation or file handling.
     */
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

