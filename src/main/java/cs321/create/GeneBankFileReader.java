package cs321.create;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * GeneBankFileDeader parses GBK files
 * and outputs DNA sequences of a specified length
 * 
 * @author Andrew Kobus
 */
public class GeneBankFileReader implements GeneBankFileReaderInterface {

    private BufferedReader reader;
    private int seqLength;
    private boolean originFound = false;
    private boolean eof = false;

    public GeneBankFileReader(File file, int seqLength) throws IOException {
        this.seqLength = seqLength;
        reader = new BufferedReader(new FileReader(file));
    }

    @Override
    public long getNextSequence() throws IOException {
        StringBuilder sequenceBuilder = new StringBuilder();
        int charsAdded = 0;
        char[] buffer = new char[seqLength];
        
        while (!eof) {
            int numCharsRead = reader.read(buffer);
            if (numCharsRead == -1) {
                eof = true;
                break;
            }
            
            for (int i = 0; i < numCharsRead; i++) {
                char currentChar = buffer[i];
                if (!originFound) {
                    checkForOrigin(currentChar);
                } else {
                    if (currentChar == 'N' || currentChar == 'n') {
                        sequenceBuilder.setLength(0);
                        charsAdded = 0;
                    } else if (Character.isLetter(currentChar)) {
                        sequenceBuilder.append(currentChar);
                        charsAdded++;
                        if (charsAdded == seqLength) {
                            return SequenceUtils.dnaStringToLong(sequenceBuilder.toString());
                        }
                    }
                }
            }
        }
        
        return -1L;
    }

    private void checkForOrigin(char currentChar) {
        if (currentChar == 'O') {
            originFound = true;
        }
    }
} 