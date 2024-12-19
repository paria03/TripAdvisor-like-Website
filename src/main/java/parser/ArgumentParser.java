package parser;

import java.util.HashMap;
import java.util.Map;

/**
 * This class parses command-line arguments and stores them in a map.
 */
public class ArgumentParser {
    private final Map<String, String> argMap = new HashMap<>();

    /**
     * Parses commandline arguments
     * The arguments should be given in pairs, where the first element in each pair
     * is a key starting with a hyphen (like: "-key"), and the second element is the
     * corresponding value (the path).
     *
     * @param args an array of strings representing command-line arguments; it must
     *             contain an even number of elements, with at least one pair. Each
     *             pair must consist of a non-null key starting with a hyphen and a
     *             non-null value.
     * @throws IllegalArgumentException if the number of arguments is less than 4 or
     *                                  if any key does not start with a hyphen, or if
     *                                  a value is null.
     */
    public void parseArgs(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("No argument is provided");
        }
        for (int i = 0; i < args.length; i += 2) {
            if (args[i].startsWith("-")) {
                if (i + 1 < args.length && args[i + 1] != null) {
                    argMap.put(args[i], args[i + 1]);
                }
            }
        }
    }

    //Getter
    public String getArgValue(String argName) {
        return argMap.get(argName);
    }

}
