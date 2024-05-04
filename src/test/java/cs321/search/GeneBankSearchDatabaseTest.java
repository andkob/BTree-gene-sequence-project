package cs321.search;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Unit tests for the GeneBankSearchDatabase class, which is responsible for searching
 * DNA sequences in a database. These tests verify the correct initialization of the class,
 * the existence of dump files, and the accessibility of results from the database.
 * 
 * @author Team 06
 * 
 */
public class GeneBankSearchDatabaseTest {

    /**
     * Tests the initialization of GeneBankSearchDatabase with provided arguments.
     * Checks whether the constructed URLs and query file paths are as expected.
     */
    @Test
    public void testGeneBankSearchDatabaseArgs() {

        String args[] = {"--database=test.db", "--queryfile=data/queries/query1"};
        GeneBankSearchDatabase search = new GeneBankSearchDatabase(args);

        assertEquals("jdbc:sqlite:test.db", search.getURL());
        assertEquals("data/queries/query1", search.getQuery());

    }

    /**
     * Verifies that the expected dump file is created as a result of the search operation.
     * This test ensures that the file creation part of the search process is functioning
     * as expected.
     */
    @Test
    public void testGeneBankSearchDumpFileExists() {

        String args[] = {"--database=test.db", "--queryfile=data/queries/query5"};
        GeneBankSearchDatabase search = new GeneBankSearchDatabase(args);

        File file = null;

        try {
            file = new File("data/queries/query5-te.out");
            assertEquals(true, file.exists());
        } catch (Exception e) {} 

        assertNotNull(file);

        file.delete();
    }

    /**
     * Tests that a valid result set is obtained when querying the DNA database.
     * This test ensures that the database is properly queried and can return results,
     * indicating the functional integrity of the SQL execution within the application.
     */
    @Test
    public void testGeneBankSearchResultSetExists() {
        String args[] = {"--database=test.db", "--queryfile=data/queries/query5"};
        GeneBankSearchDatabase search = new GeneBankSearchDatabase(args);

        Connection connection = null;

        try {

            connection = DriverManager.getConnection(search.getURL());
            Statement statement = connection.createStatement();

            statement.executeUpdate("drop table if exists test");
            statement.executeUpdate("create table test (sequence string, frequency integer)");
            statement.executeUpdate("INSERT into test values('test',0)");

            ResultSet rs = statement.executeQuery("SELECT * from dna");
            assertEquals(rs.next(), true);

            connection.close();

        } catch (SQLException e) {
            fail();
        }

    }
}
