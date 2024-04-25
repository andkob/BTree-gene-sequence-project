package cs321.search;

/**
 * Parses args for the GeneBankSearchDatabase class
 * 
 * @author Damon Wargo
 */
public class GeneBankSearchDatabaseArguments {

    private String queryFile;
    private String databasePath;
    private Boolean validArgs = true;

    //TODO add comments

    public GeneBankSearchDatabaseArguments(String[] args) {

        if(args.length != 2) {
            printUsage();
            validArgs = false;
            return;
        }

        for(String arg : args) {

            try {
                if(arg.substring(0, 11).equals("--database=")) {
                    databasePath = arg.substring(11);
                } else if (arg.substring(0, 12).equals("--queryfile=")) {
                    queryFile = arg.substring(12);
                } else {
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

        System.out.println(databasePath);
        System.out.println(queryFile);
    }

    public Boolean validate(){
        return validArgs;
    }

    public String getQueryFile(){
        return queryFile;
    }

    public String getDatabasePath(){
        return databasePath;
    }

    public void printUsage(){
        System.out.println("Usage: java -jar build/libs/GeneBankSearchDatabase.jar --database=<SQLite-database-path> --queryfile=<query-file>");
    }

}

