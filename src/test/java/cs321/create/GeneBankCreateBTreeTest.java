package cs321.create;

import cs321.common.ParseArgumentException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GeneBankCreateBTreeTest {

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
