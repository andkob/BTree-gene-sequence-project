package cs321.search;

import cs321.btree.BTree;
import cs321.btree.TreeObject;
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
        int debugLevel = arguments.getDebugLevel();

        BTree btree = new BTree(degree, btreefilename, seqLength, useCache, cacheSize);

        // Search BTree for all sequences in the query file
        File queryfile = new File(arguments.getQueryFilename());
        Scanner queryScanner = new Scanner(queryfile);
        
        long totalSearchTimeStart = System.currentTimeMillis();
        int totalMatches = 0;
        // loop through all sequences in the query file
        while (queryScanner.hasNextLine()) {

            String sequence = queryScanner.nextLine();
            long sequenceAsLong = SequenceUtils.dnaStringToLong(sequence); // convert sequence to long

            // search BTree for the sequence and its complement
            long startTime = System.nanoTime();
            TreeObject foundObject = btree.search(sequenceAsLong);
            long endTime = System.nanoTime();
            long elapsedSearchTime = endTime - startTime;

            // if a match is found, print the key and its frequency
            System.out.print("For query '" + sequence + "', ");
            if (foundObject != null) {
                String foundSequence = SequenceUtils.longToDnaString(foundObject.getKey(), seqLength);
                System.out.println("found: " + foundSequence + " " + foundObject.getCount());
                totalMatches++;
                if (debugLevel == 1) {
                    System.out.println("\tsearch time: " + elapsedSearchTime + "ns");
                }
            } else {
                System.out.println("no key found");
            }
        }
        long totalSearchTimeEnd = System.currentTimeMillis();
        System.out.println("Total matches: " + totalMatches);
        System.out.println("\n Total time to search: " + (totalSearchTimeEnd - totalSearchTimeStart) + "ms");

        queryScanner.close();
    }
}

