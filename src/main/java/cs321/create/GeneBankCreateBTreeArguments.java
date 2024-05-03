package cs321.create;
import java.io.File;

/**
 * The GeneBankCreateBTreeArguments class parsed arguements for
 * creating a BTree and outputs any errors that occur with invalid
 * arguments
 * 
 * @author Damon Wargo, Caleb Tolman
 */
public class GeneBankCreateBTreeArguments {

    private int useCache; //determines whether cache is used
    private int degree; //degree of BTree
    private String gbkFileName; //GBK filepath
    private int subsequenceLength; //length of subsequence
    private int cacheSize; //size of cache if cache used
    private int debugLevel; //determines dumpfile creation
    private boolean validArgs; //tracks valid args


    /**
     * Parses args from command line and 
     * assigns values according to each arg
     * 
     * @param args command line args
     */
    public GeneBankCreateBTreeArguments(String[] args) {

        if (args.length < 4 || args.length > 6) {
            printUsage();
            validArgs = false;
            return;
        }

        //set intitial values
        useCache = -1;
        degree = -1;
        gbkFileName = "";
        subsequenceLength = -1;
        cacheSize = -1;
        debugLevel = 0;
        validArgs = true;

        for(String arg : args) {

            //see if start of arg matches any expected entry and assigns if true
            try {
                if(arg.length() >= 8 && arg.substring(0, 8).equals("--cache=")) {
                    useCache = Integer.parseInt(arg.substring(8));
                } else if (arg.length() >= 9 && arg.substring(0, 9).equals("--degree=")) {
                    degree = Integer.parseInt(arg.substring(9));
                } else if (arg.length() >= 10 && arg.substring(0, 10).equals("--gbkfile=")) {
                    gbkFileName = arg.substring(10);
                } else if (arg.length() >= 9 && arg.substring(0,9).equals("--length=")) {
                    subsequenceLength = Integer.parseInt(arg.substring(9));
                } else if (arg.length() >= 8 && arg.substring(0, 8).equals("--debug=")) {
                    debugLevel = Integer.parseInt(arg.substring(8));
                } else if (arg.length() >= 12 && arg.substring(0, 12).equals("--cachesize=")) {
                    cacheSize = Integer.parseInt(arg.substring(12));
                } else {
                    //no valid matching arg found
                    System.out.println("Error: invalid arg " + arg);
                    validArgs = false;
                    printUsage();
                    return;
                }
            } catch (Exception e) {
                System.out.println(e.toString());
                e.printStackTrace();
                validArgs = false;
                printUsage();
                return;
            }
        }

        /*
         * Once args have been processed succesfully, 
         * confirm that each arg is valid value
         */
        if (useCache != 0 && useCache != 1) {
            validArgs = false;
            System.out.println("Error: invalid arg --cache=" + useCache);
            printUsage();
            return;
        }

        if (degree < 0) {
            validArgs = false;
            System.out.println("Error: invalid arg --degree=" + degree);
            printUsage();
            return;
        }

        try {
            File f = new File(gbkFileName);
            if (!f.exists()) {
                validArgs = false;
                System.out.println("Error: invalid arg --gbkfile=" + gbkFileName);
                printUsage();
                return;
            }
        } catch (Exception e) {
            validArgs = false;
            System.out.println(e.toString());
            printUsage();
            return;
        }

        if(subsequenceLength < 1 || subsequenceLength > 31) {
            validArgs = false;
            System.out.println("Error: invalid arg --length=" + subsequenceLength);
            printUsage();
            return;
        }

        if(useCache == 1 && (cacheSize < 100 || cacheSize > 10000)) {
            validArgs = false;
            System.out.println("Error: invalid arg --cachesize=" + cacheSize);
            printUsage();
        }

        if(debugLevel != 0 && debugLevel != 1) {
            validArgs = false;
            System.out.println("Error: invalid arg --debug=" + debugLevel);
        }

    }

    /**
     * Prints usage to console
     */
    public void printUsage() {
        System.out.println("Usage: java -jar build/libs/GeneBankCreateBTree.jar --cache=<0|1>  --degree=<btree-degree> --gbkfile=<gbk-file> --length=<sequence-length> [--cachesize=<n>] [--debug=0|1]");
    }

    /**
     * checks whether args were valid
     * 
     * @return true if valid args
     */
    public boolean validate() {
        return validArgs;
    }

    /**
     * @return filepath for gbkfile
     */
	public String getGbkFileName() {
		return gbkFileName;
	}

    /**
     * @return specified degree
     */
	public int getDegree() {
		return degree;
	}

    /**
     * @return subsequence length
     */
	public int getSubsequenceLength() {
		return subsequenceLength;
	}

    /**
     * returns boolean based on option
     * selected in command line
     * 
     * @return true if cache used
     */
	public boolean getUseCache() {
		if (useCache == 0) {
            return false;
        } else {
            return true;
        }
	}

    /**
     * @return size of cache
     */
	public int getCacheSize() {
		return cacheSize;
	}

    /**
     * @return debug level
     */
	public int getDebugLevel() {
		return debugLevel;
	}
}
