package cs321.create;

import cs321.common.ParseArgumentException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * This class contains unit tests for the GeneBankCreateBTreeArguments class.
 * It tests the parsing functionality to ensure that the command-line arguments
 * are interpreted correctly and the appropriate values are set in the
 * GeneBankCreateBTreeArguments instance.
 *
 * @author Team 06
 * 
 */
public class GeneBankCreateBTreeTest {

    /**
     * Tests the parsing of command-line arguments for creating a B-Tree.
     * This test case provides a set of typical command-line arguments and checks
     * if the GeneBankCreateBTreeArguments object correctly parses and sets its internal state.
     * The test verifies each parameter, including cache usage, B-tree degree, gene bank file path,
     * subsequence length, debug level, and the default cache size when it's not specified.
     * 
     * @throws ParseArgumentException if there is a problem parsing the arguments.
     */
    @Test
    public void ParseArgumentsTest() throws ParseArgumentException {
        String args[] = {"--cache=0", "--degree=15", "--gbkfile=data/files_gbk/test0.gbk", "--length=10", "--debug=1"};
        GeneBankCreateBTreeArguments test = new GeneBankCreateBTreeArguments(args);

        assertEquals(false,test.getUseCache());
        assertEquals(15, test.getDegree());
        assertEquals("data/files_gbk/test0.gbk", test.getGbkFileName());
        assertEquals(10, test.getSubsequenceLength());
        assertEquals(1, test.getDebugLevel());
        assertEquals(-1, test.getCacheSize());
    }

}
