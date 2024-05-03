package cs321.search;

import cs321.btree.BTree;
import cs321.common.ParseArgumentException;
import cs321.common.ParseArgumentUtils;
import cs321.create.SequenceUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.io.PrintWriter;


public class GeneBankSearchDatabase {

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

    public String getURL() {
        return url;
    }

    public String getQuery() {
        return query;
    }


}
