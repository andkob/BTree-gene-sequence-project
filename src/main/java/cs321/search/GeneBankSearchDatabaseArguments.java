package cs321.search;

/**
 * Processes args for the GeneBankSearchDatabase class
 * 
 * @author Damon Wargo
 */
public class GeneBankSearchDatabaseArguments {

    private String queryFile; //path to query
    private String databasePath; //path to database
    private Boolean validArgs = true; //tracks if args are valid

    /**
     * Constructor that process args and
     * assigns values according to each arg
     * 
     * @param args command line args
     */
    public GeneBankSearchDatabaseArguments(String[] args) {

        if(args.length != 2) {
            printUsage();
            validArgs = false;
            return;
        }

        for(String arg : args) {

            //see if start of arg matches any expected entry and assigns if true
            try {
                if(arg.length() >= 11 && arg.substring(0, 11).equals("--database=")) {
                    databasePath = arg.substring(11);
                } else if (arg.length() >- 12 && arg.substring(0, 12).equals("--queryfile=")) {
                    queryFile = arg.substring(12);
                } else {
                    //no matching args found
                    printUsage();
                    validArgs = false;
                    return;
                }
            } catch (Exception e) {
                validArgs = false;
                printUsage();
                return;
            }
        }
    }

    /**
     * checks whether args are valid
     * 
     * @return true if valid args
     */
    public Boolean validate(){
        return validArgs;
    }

    /**
     * @return path to query
     */
    public String getQueryFile(){
        return queryFile;
    }

    /**
     * @return path to database
     */
    public String getDatabasePath(){
        return databasePath;
    }

    /**
     * Prints usage to console
     */
    public void printUsage(){
        System.out.println("Usage: java -jar build/libs/GeneBankSearchDatabase.jar --database=<SQLite-database-path> --queryfile=<query-file>");
    }

}

