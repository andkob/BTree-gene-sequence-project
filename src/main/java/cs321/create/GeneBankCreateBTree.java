package cs321.create;

import cs321.btree.BTree;
import cs321.common.ParseArgumentException;

import java.io.*;
import java.util.List;

public class GeneBankCreateBTree
{

    public static void main(String[] args) throws Exception {
//        System.out.println("Hello world from cs321.create.GeneBankCreateBTree.main");
//        GeneBankCreateBTreeArguments geneBankCreateBTreeArguments = parseArgumentsAndHandleExceptions(args);
        try {
            GeneBankCreateBTreeArguments arguments = parseArgumentsAndHandleExceptions(args);
            File gbkFile = new File(arguments.getGbkFileName());
            if (!gbkFile.exists() || !gbkFile.isFile()) {
                System.err.println("Error: The specified file '" + arguments.getGbkFileName() + "' does not exist or is not a valid file.");
                System.exit(1);
            }

            BTree tree = new BTree(arguments.getDegree(), arguments.getSubsequenceLength(), arguments.getGbkFileName(), arguments.getUseCache(), arguments.getCacheSize());

            GeneBankFileReader reader = new GeneBankFileReader(gbkFile, arguments.getSubsequenceLength());
            long sequence;
            while ((sequence = reader.getNextSequence()) != -1) {
                tree.insert(sequence);
            }

            if (arguments.getDebugLevel() == 1) {
                tree.dump(); // Assuming a method in BTree that dumps all elements for debugging
            }
        } catch (Exception e) {
            System.err.println("An error has occurred while parsing your arguments.");
            e.printStackTrace();
            System.err.println();
;           printUsageAndExit(e.getMessage());
        }
    }

    private static GeneBankCreateBTreeArguments parseArgumentsAndHandleExceptions(String[] args) {
        GeneBankCreateBTreeArguments geneBankCreateBTreeArguments = null;
        try
        {
            geneBankCreateBTreeArguments = parseArguments(args);
        }
        catch (ParseArgumentException e)
        {
            printUsageAndExit(e.getMessage());
        }
        return geneBankCreateBTreeArguments;
    }

    public static GeneBankCreateBTreeArguments parseArguments(String[] args) throws ParseArgumentException {
        if (args.length < 4 || args.length > 6) {
            throw new ParseArgumentException("Incorrect number of arguments.");
        }

        boolean useCache = Integer.parseInt(args[0]) == 1;
        int degree = Integer.parseInt(args[1]);
        String gbkFileName = args[2];
        int subsequenceLength = Integer.parseInt(args[3]);

        if (subsequenceLength < 1 || subsequenceLength > 31) {
            throw new ParseArgumentException("Sequence length must be between 1 and 31.");
        }

        int cacheSize = (args.length >= 5) ? Integer.parseInt(args[4]) : 0;
        int debugLevel = (args.length == 6) ? Integer.parseInt(args[5]) : 0;

        return new GeneBankCreateBTreeArguments(useCache, degree, gbkFileName, subsequenceLength, cacheSize, debugLevel);
    }
    
    private static void printUsageAndExit(String errorMessage){
        System.err.println(errorMessage);
        System.err.println("Usage: java cs321.create.GeneBankCreateBTree <use cache> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>]");
        System.err.println("<use cache>: 0 (no cache) or 1 (use cache)");
        System.err.println("<degree>: degree of the B-tree");
        System.err.println("<gbk file>: path to the gene bank file");
        System.err.println("<sequence length>: length of DNA sequences to read (1-31)");
        System.err.println("[<cache size>]: size of cache if cache is used");
        System.err.println("[<debug level>]: 0 (no debug output) or 1 (print B-tree)");
        System.exit(1);
    }
    
}
