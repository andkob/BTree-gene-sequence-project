package cs321.search;

import java.io.File;

public class GeneBankSearchBTreeArguments {
    private int useCache;
    private int degree;
    private String btreeFilename;
    private int seqLength;
    private String queryFilename;
    private int cacheSize;
    private int debugLevel;
    private boolean validArgs;

    public GeneBankSearchBTreeArguments(String[] args) {
        if (args.length < 5 || args.length > 7) {
            printUsage();
            validArgs = false;
            return;
        }

        // initialize fields
        useCache = -1;
        degree = -1;
        btreeFilename = null;
        seqLength = -1;
        queryFilename = null;
        cacheSize = -1;
        debugLevel = 0; // default zero if not provided by user
        validArgs = true;

        for (String arg : args) {
            if(arg.length() >= 8 && arg.substring(0, 8).equals("--cache=")) {
                useCache = Integer.parseInt(arg.substring(8));
            } else if (arg.length() >= 9 && arg.substring(0, 9).equals("--degree=")) {
                degree = Integer.parseInt(arg.substring(9));
            } else if (arg.length() >= 12 && arg.substring(0, 12).equals("--btreefile=")) {
                btreeFilename = arg.substring(12);
            } else if (arg.length() >= 9 && arg.substring(0, 9).equals("--length=")) {
                seqLength = Integer.parseInt(arg.substring(9));
            } else if (arg.length() >= 12 && arg.substring(0, 12).equals("--queryfile=")) {
                queryFilename = arg.substring(12);
            } else if (arg.length() >= 12 && arg.substring(0, 12).equals("--cachesize=")) {
                cacheSize = Integer.parseInt(arg.substring(12));
            } else if (arg.length() >= 8 && arg.substring(0, 8).equals("--debug=")) {
                debugLevel = Integer.parseInt(arg.substring(8));
            } else {
                System.out.println("Error: invalid arg " + arg);
                validArgs = false;
                printUsage();
                return;
            }
        }

        // make sure cache is a valid argument
        if (useCache != 0 && useCache != 1) {
            validArgs = false;
            System.out.println("Error: invalid arg --cache=" + useCache);
            printUsage();
            return;
        }

        // make sure degree is valid
        if (degree < 0) {
            validArgs = false;
            System.out.println("Error: invalid arg --degree=" + degree);
            printUsage();
            return;
        }

        // make sure the btree file is valid
        try {
            File f = new File(btreeFilename);
            if (!f.exists()) {
                validArgs = false;
                System.out.println("Error: invalid arg --btreefile=" + btreeFilename);
                printUsage();
                return;
            }
        } catch (Exception e) {
            validArgs = false;
            System.out.println(e.toString());
            printUsage();
            return;
        }

        // make sure sequence length is valid
        if(seqLength < 1 || seqLength > 31) {
            validArgs = false;
            System.out.println("Error: invalid arg --length=" + seqLength);
            printUsage();
            return;
        }

        // make sure the query file is valid
        try {
            File f = new File(queryFilename);
            if (!f.exists()) {
                validArgs = false;
                System.out.println("Error: invalid arg --btreefile=" + queryFilename);
                printUsage();
                return;
            }
        } catch (Exception e) {
            validArgs = false;
            System.out.println(e.toString());
            printUsage();
            return;
        }

        // make sure cache size is valid
        if(useCache == 1 && (cacheSize < 100 || cacheSize > 10000)) {
            validArgs = false;
            System.out.println("Error: invalid arg --cachesize=" + cacheSize);
            printUsage();
        }

        // make sure debug level is valid
        if(debugLevel != 0 && debugLevel != 1) {
            validArgs = false;
            System.out.println("Error: invalid arg --debug=" + debugLevel);
        }
    }

    public boolean validate() {
        return validArgs;
    }

    private void printUsage() {
        System.out.println("java -jar build/libs/GeneBankSearchBTree.jar --cache=<0/1> "
        + "--degree=<btree-degree> "
        + "--btreefile=<b-tree-file> --length=<sequence-length> --queryfile=<query-file> "
        + "[--cachesize=<n>] [--debug=0|1]"
        );
    }

    public int getUseCache() {
        return useCache;
    }

    public int getDegree() {
        return degree;
    }

    public String getBtreeFilename() {
        return btreeFilename;
    }

    public int getSeqLength() {
        return seqLength;
    }

    public String getQueryFilename() {
        return queryFilename;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public int getDebugLevel() {
        return debugLevel;
    }
}
