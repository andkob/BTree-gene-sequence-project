package cs321.search;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GeneBankSearchDatabaseTest {

    @Test
    public void testGeneBankSearchDatabaseArgs() {

        String args[] = {"--database=test.db", "--queryfile=query1"};
        GeneBankSearchDatabase search = new GeneBankSearchDatabase(args);

        assertEquals("jdbc:sqlite:test.db", search.getURL());
        assertEquals("results/query-results/query1", search.getQuery());

    }

    @Test
    public void testGeneBankSearchDumpFileExists() {
        String args[] = {"--database=test.db", "--queryfile=query5"};
        GeneBankSearchDatabase search = new GeneBankSearchDatabase(args);

        File file = null;

        try {
            file = new File(search.getQuery() + ".dump");
        } catch (Exception e) {} 

        assertNotNull(file);

        file.delete();
    }

    @Test
    public void testGeneBankSearchResultSetExists() {
        String args[] = {"--database=test.db", "--queryfile=query5"};
        GeneBankSearchDatabase search = new GeneBankSearchDatabase(args);

        Connection connection = null;

        try {

            connection = DriverManager.getConnection(search.getURL());
            Statement statement = connection.createStatement();

            ResultSet rs = statement.executeQuery("SELECT * from dna");
            assertEquals(rs.next(), true);

            connection.close();

        } catch (SQLException e) {
            fail();
        }

    }
}
