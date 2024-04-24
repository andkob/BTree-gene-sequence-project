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


public class GeneBankSearchDatabase
{

    public static void main(String[] args) throws Exception {

        GeneBankSearchDatabaseArguments searchArgs = new GeneBankSearchDatabaseArguments(args);
        if (searchArgs.validate() == false) {
            return;
        }

        //temporary code to create database to search
        //TODO remove/edit when CreateBTree implemented

        String url = "jdbc:sqlite:" + searchArgs.getDatabasePath();
        String query = "results/query-results/" + searchArgs.getQueryFile();
        Connection connection = null;

        try {

            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();

            //will need to move

            statement.executeUpdate("drop table if exists dna");
            statement.executeUpdate("create table dna (sequence string, frequency integer)");

            File f = new File("results/dumpfiles/test0.gbk.dump.5");
            Scanner s = new Scanner(f);
            String sequence;
            int frequency;
            while(s.hasNext()) {
                sequence = s.next();
                frequency = Integer.parseInt(s.next());
                statement.executeUpdate("insert into dna values('" + sequence + "', " + frequency + ")");
            }

            s.close();

            //end delete

            //TODO update for specified query
            f = new File(query);
            s = new Scanner(f);

            try {
                File file = new File(query + ".dump");
                file.createNewFile();
                PrintWriter out = new PrintWriter(query + ".dump");

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

}
