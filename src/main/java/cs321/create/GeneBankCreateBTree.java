package cs321.create;

import cs321.btree.BTree;
import cs321.btree.TreeObject;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Scanner;

public class GeneBankCreateBTree
{

    public static void main(String[] args) throws Exception {
        new GeneBankCreateBTree(args);
    }

    public GeneBankCreateBTree(String args[]) {
        try 
        {
            long startTime = 0;
            long endTime = 0;
            startTime = System.currentTimeMillis();

            // TODO - delete this later 
            File btreeFile = new File("btree.bt");
            if (btreeFile.exists()) {
                btreeFile.delete();
            }
            //

            
            GeneBankCreateBTreeArguments arguments = new GeneBankCreateBTreeArguments(args);
            if (!arguments.validate()) {
                return;
            }
            
            long sequence;
            int seqLength = arguments.getSubsequenceLength();
            
            File gbkFile = new File(arguments.getGbkFileName());
            
            GeneBankFileReader reader = new GeneBankFileReader(gbkFile, seqLength);
            
            BTree tree = new BTree(arguments.getDegree(), "btree.bt", seqLength, arguments.getUseCache(), arguments.getCacheSize());
            while ((sequence = reader.getNextSequence()) != -1) {
                tree.insert(new TreeObject(sequence));
            }
            
//------------------------------------------------------------------------------------------------------
            endTime = System.currentTimeMillis();            
            long elapsedTime = endTime - startTime;
            System.out.println("Elapsed Time: " + elapsedTime / 1000 + " seconds");
//------------------------------------------------------------------------------------------------------        
            
            if (arguments.getDebugLevel() == 1) {
            	PrintWriter printWriter = new PrintWriter(new FileWriter(arguments.getGbkFileName() + ".dump." + seqLength)); // name of dump files
                tree.dumpToFile(printWriter);
                printWriter.close();

                //Create sql
                Connection connection = null;
                String databaseName = arguments.getGbkFileName();
                databaseName = databaseName.substring(databaseName.indexOf("test"));
                connection = DriverManager.getConnection("jdbc:sqlite:" + databaseName + "." + seqLength + ".db");

                Statement statement = connection.createStatement();
                statement.executeUpdate("drop table if exists dna");
                statement.executeUpdate("create table dna (sequence string, frequency integer)");

                //insert data from dumpfile into database
                System.out.println(arguments.getGbkFileName() + ".dump." + seqLength);
                File f = new File(arguments.getGbkFileName() + ".dump." + seqLength);
                Scanner s = new Scanner(f);
                String dbSequence;
                int frequency;
                while(s.hasNext()) {
                    dbSequence = s.next();
                    frequency = Integer.parseInt(s.next());
                    statement.executeUpdate("insert into dna values('" + dbSequence + "', " + frequency + ")");
                }

                s.close();
                tree.close();
            }

            
        } 
        catch (Exception e) 
        {
        	System.err.println("An error has occurred while parsing the arguments.");
        	e.printStackTrace();
        	System.err.println();
        	printUsageAndExit(e.getMessage());
        }
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
