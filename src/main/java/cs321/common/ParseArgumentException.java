package cs321.common;

/**
 * Custom exception class for handling errors related to argument parsing.
 * This exception is thrown to indicate issues that arise when processing and validating
 * input arguments, such as those passed to command-line applications.
 */
public class ParseArgumentException extends Exception
{
    /**
     * Constructs a new ParseArgumentException with the specified detail message.
     * The detail message is saved for later retrieval by the {@link Throwable#getMessage()} method.
     *
     * @param message the detail message, which provides further information about the parsing error.
     */
    public ParseArgumentException(String message)
    {
        super(message);
    }
}
