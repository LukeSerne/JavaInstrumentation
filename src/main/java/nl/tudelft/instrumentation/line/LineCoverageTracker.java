package nl.tudelft.instrumentation.line;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class collects coverage data (gathered by {@code CoverageVisitor}) and stores them
 * in a {@code JsonObject}
 */
public class LineCoverageTracker {
    /** JsonObject to store filenames and line numbers to true (executed) or false (not executed). */
    private static JsonObject coveredLines = new JsonObject();

    /**
     * Write the content of {@code coveredLines} into a Json file
     */
    private static void writeCoverageToFile() {
        String coverage = generateCoverage();
        String outputFile = System.getProperty("coverage.report.path", "line-coverage.json");
        FileWriter fWriter = null;
        try {
            fWriter = new FileWriter(outputFile);
            fWriter.write(coverage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                fWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String generateCoverage() {
        return coveredLines.toString();
    }

    /**
     * This method register a new entry for the Json file ({@code JsonObject})
     * @param filename name of the Java file under analysis
     * @param line line of teh file to register for coverage
     */
    public static void registerLine(String filename, int line) {
        if (!coveredLines.has(filename)) {
            coveredLines.add(filename, new JsonObject());
        }
        JsonObject file = (JsonObject) coveredLines.get(filename);
        file.add(String.valueOf(line), new JsonPrimitive("false"));
    }

    /**
     * This method updates the coverage information for the instrumented file. Calls to this method
     * are automatically added into the instrumented Java file by the class {@code CoverageVisitor}
     * @param filename name of the Java file for which we want to register coverage
     * @param line covered line
     */
    public static void updateCoverage(String filename, int line) {
        if (!coveredLines.has(filename)) {
            coveredLines.add(filename, new JsonObject());
        }
        JsonObject file = (JsonObject) coveredLines.get(filename);
        file.add(String.valueOf(line), new JsonPrimitive("true"));
    }

    /**
     * This method is always executed when the execution ends to write the coverage
     * results into a json file
     */
    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override public void run() {
                writeCoverageToFile();
            }
        });
    }
}
