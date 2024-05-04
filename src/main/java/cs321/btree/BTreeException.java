package cs321.btree;

/**
 * Custom exception class for handling specific errors that arise within the BTree operations.
 * This class extends the standard Java {@link Exception} class and is used to indicate
 * exceptions specific to the behavior and operations of a BTree.
 */
public class BTreeException extends Exception {
    /**
     * Constructs a new BTreeException with the specified detail message.
     * The detail message is saved for later retrieval by the {@link Throwable#getMessage()} method.
     *
     * @param message the detail message. The detail message is saved for later retrieval
     *                by the {@link Throwable#getMessage()} method.
     */
    public BTreeException(String message) {
        super(message);
    }
}
