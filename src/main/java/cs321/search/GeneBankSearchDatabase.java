package cs321.search;

import cs321.create.SequenceUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.io.PrintWriter;

/**
 * This class is designed to search for DNA sequences in a database populated from a BTree dump file.
 * It uses a SQLite database to perform the search and outputs the results to a file. The class reads sequences
 * from a specified query file, searches for these sequences and their complements in the database,
 * and writes the accumulated frequencies to an output file.
 *
 * @author Team 06
 * 
 */
public class GeneBankSearchDatabase {

    /**
     * Constructs a GeneBankSearchDatabase instance which sets up a connection to a SQLite database,
     * reads query sequences from a file, and searches for these sequences and their complements in the database.
     * Results are written to an output file.
     *
     * @param args Command-line arguments that specify the database path and query file.
     * @throws Exception If there is a problem opening the database connection, reading from the query file,
     *                   executing SQL queries, or writing to the output file.
     */
    public static void main(String[] args) throws Exception {
        new GeneBankSearchDatabase(args);
    }

    private String url;
    private String query;

    public GeneBankSearchDatabase(String args[]) {

        GeneBankSearchDatabaseArguments searchArgs = new GeneBankSearchDatabaseArguments(args);
        if (searchArgs.validate() == false) {
            return;
        }

        url = "jdbc:sqlite:" + searchArgs.getDatabasePath();
        query = searchArgs.getQueryFile();
        Connection connection = null;

        try {

            System.out.println("Attempting search...");

            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();

            File f = new File(query);
            Scanner s = new Scanner(f);
            int frequency;
            String sequence;
            String queryOutName = searchArgs.getDatabasePath().substring(0, searchArgs.getDatabasePath().indexOf("gbk") + 3);

            try {
                File file = new File(query + "-" + queryOutName + ".out");
                file.createNewFile();
                PrintWriter out = new PrintWriter(query + "-" + queryOutName + ".out");

                while(s.hasNext()) {
                    sequence = s.next().toLowerCase();
                    ResultSet rs = statement.executeQuery("SELECT * from dna where sequence = '" + sequence + "'");
                    frequency = 0;
                    if (rs.next()) {
                        frequency = rs.getInt("frequency");
                    } 
                    String altsequence = SequenceUtils.longToDnaString(SequenceUtils.getComplement(SequenceUtils.dnaStringToLong(sequence), sequence.length()), sequence.length());
                    rs = statement.executeQuery("SELECT frequency from dna where sequence = '" + altsequence + "'");
                    if (rs.next()) {
                        frequency += rs.getInt("frequency");
                    }
                    out.println(sequence + " " + frequency);
                }

                System.out.println("Search complete!");
                System.out.println("Query result output to " + query + "-" + queryOutName + ".out" );

                out.close();

                } catch (Exception e) {
                    System.out.println (e.toString());
                }

            s.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }

    /**
     * Retrieves the database URL used to connect to the SQLite database.
     *
     * @return The URL of the database.
     */
    public String getURL() {
        return url;
    }

    /**
     * Retrieves the path to the query file containing DNA sequences to be searched in the database.
     *
     * @return The query file path.
     */
    public String getQuery() {
        return query;
    }


}
