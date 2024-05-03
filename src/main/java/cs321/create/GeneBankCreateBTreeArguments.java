package cs321.create;

import java.io.File;

public class GeneBankCreateBTreeArguments {
    private int useCache;
    private int degree;
    private String gbkFileName;
    private int subsequenceLength;
    private int cacheSize;
    private int debugLevel;
    private boolean validArgs;

    //TODO add comments

    public GeneBankCreateBTreeArguments(String[] args) {

        if (args.length < 4 || args.length > 6) {
            printUsage();
            validArgs = false;
            return;
        }

        useCache = -1;
        degree = -1;
        gbkFileName = "";
        subsequenceLength = -1;
        cacheSize = -1;
        debugLevel = 0;
        validArgs = true;

        for(String arg : args) {

            try {
                if(arg.length() >= 8 && arg.substring(0, 8).equals("--cache=")) {
                    useCache = Integer.parseInt(arg.substring(8));
                    System.out.println("useCache: " + useCache);
                } else if (arg.length() >= 9 && arg.substring(0, 9).equals("--degree=")) {
                    degree = Integer.parseInt(arg.substring(9));
                    System.out.println("degree: " + degree);
                } else if (arg.length() >= 10 && arg.substring(0, 10).equals("--gbkfile=")) {
                    gbkFileName = arg.substring(10);
                    System.out.println("gbkFileName: " + gbkFileName);
                } else if (arg.length() >= 9 && arg.substring(0,9).equals("--length=")) {
                    subsequenceLength = Integer.parseInt(arg.substring(9));
                    System.out.println("length: " + subsequenceLength);
                } else if (arg.length() >= 8 && arg.substring(0, 8).equals("--debug=")) {
                    debugLevel = Integer.parseInt(arg.substring(8));
                    System.out.println("debugLevel: " + debugLevel);
                } else if (arg.length() >= 12 && arg.substring(0, 12).equals("--cachesize=")) {
                    cacheSize = Integer.parseInt(arg.substring(12));
                    System.out.println("cacheSize: " + cacheSize);
                } else {
                    System.out.println("Error: invalid arg " + arg);
                    validArgs = false;
                    printUsage();
                    return;
                }
            } catch (Exception e) {
                System.out.println(e.toString());
                validArgs = false;
                printUsage();
                return;
            }
        }

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

    public void printUsage() {
        System.out.println("Usage: java -jar build/libs/GeneBankCreateBTree.jar --cache=<0|1>  --degree=<btree-degree> --gbkfile=<gbk-file> --length=<sequence-length> [--cachesize=<n>] [--debug=0|1]");
    }

    public boolean validate() {
        return validArgs;
    }

	public String getGbkFileName() {
		return gbkFileName;
	}

	public int getDegree() {
		return degree;
	}

	public int getSubsequenceLength() {
		return subsequenceLength;
	}

	public boolean getUseCache() {
		if (useCache == 0) {
            return false;
        } else {
            return true;
        }
	}

	public int getCacheSize() {
		return cacheSize;
	}

	public int getDebugLevel() {
		return debugLevel;
	}
}
