package cs321.common;

/**
 * Utility class providing static methods to assist in parsing and validating command-line arguments.
 * This class includes methods to verify that an integer falls within a specified range and to convert
 * a string representation of an integer into its int type.
 */
public class ParseArgumentUtils
{
    /**
     * Verifies if lowRangeInclusive <= argument <= highRangeInclusive
     */
    public static void verifyRanges(int argument, int lowRangeInclusive, int highRangeInclusive) throws ParseArgumentException
    {

    }

    /**
     * Converts a string representation of an integer into its int type.
     * If the string cannot be converted to an integer, a {@link ParseArgumentException} is thrown.
     *
     * @param argument the string representation of the integer to convert.
     * @return the integer value of the argument.
     * @throws ParseArgumentException if the string does not contain a parsable integer.
     */
    public static int convertStringToInt(String argument) throws ParseArgumentException
    {
        return 0;
    }
}
