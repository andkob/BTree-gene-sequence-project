package cs321.create;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.lang.StringBuilder;

/**
 * GeneBankFileDeader parses GBK files
 * and outputs DNA sequences of a specified length
 * 
 * @author Damon Wargo
 */
public class GeneBankFileReader implements GeneBankFileReaderInterface  {

    int seqLength;
    int trackedIndex = 0; //index to start parsing at
    boolean originFound = false;
    FileReader f;
    StringBuilder sb;
    String fileAsString;

    /**
     * Constructor for GeneBankFileReader
     * with specified file and sequencelength
     * 
     * @param file GBK file to parse
     * @param seqLength length of DNA sequences to return
     * @throws IOException On file read error
     */
    public GeneBankFileReader(File file, int seqLength) throws IOException {

        this.seqLength = seqLength;
        //build string with characters read from file
        f = new FileReader(file);
        sb = new StringBuilder();
        int current = 0;

        while((current = f.read()) != -1) {
            sb.append((char)current);
        }
        fileAsString = sb.toString();
        trackedIndex = 0;
    }

    @Override
    public long getNextSequence() throws IOException {
        String sequence = "";
        char currentChar;
        int charsAdded = 0;

        //parse only in ORIGIN section
        if(!originFound) {
            checkForOrigin(trackedIndex);
        }

        if(originFound) {
            for(int i = trackedIndex; i < sb.length(); i++) {
                currentChar = sb.charAt(i);
                //end of ORIGIN section
                if (currentChar == '/') {
                    originFound = false;
                    trackedIndex++;
                    //recursion gauruntees sequence or EOF
                    return getNextSequence();
                } else if (currentChar == 'N' || currentChar == 'n') {
                    sequence = "";
                    charsAdded = 0;
                    trackedIndex += seqLength + 1;
                } else if (Character.isLetter(currentChar)) {
                    sequence += (char)currentChar;
                    charsAdded++;
                    if(charsAdded == seqLength) {
                        trackedIndex++;
                        System.out.println(sequence);
                        return 0L;
                    }
                } else {
                    if(charsAdded == 0) {
                        trackedIndex++;
                        break;
                    }
                }
            }
        } 
        return -1L;
    }
    
    /**
     * reads file until "ORIGIN" is found to
     * find index in file to begin sequencing
     * 
     * @return whether origin found
     * @throws IOException
     */
    private void checkForOrigin(int index) throws IOException {
        int originIndex = fileAsString.indexOf("ORIGIN", index);
            if (originIndex != -1) {
                originFound = true;
                trackedIndex = originIndex + 6; // Move past "ORIGIN"
        } else {
            originFound = false;
        }
    }
    
}