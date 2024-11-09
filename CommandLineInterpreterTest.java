package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;

class CommandLineInterpreterTest {
    private CommandLineInterpreter interpreter;
    private Path tempFile;

    @BeforeEach
    void setUp() throws IOException {
        interpreter = new CommandLineInterpreter();
        tempFile = Files.createTempFile("testfile", ".txt");
    }

    @Test
    void testHelp() {
        // Ensure that calling help() doesn't cause an error.
        assertDoesNotThrow(CommandLineInterpreter::help);
    }

    @Test
    void testReadFile() throws IOException {
        String content = "apple banana\nbanana apple";
        Files.writeString(tempFile, content);

        List<String> expected = Arrays.asList("apple", "banana", "banana", "apple");
        List<String> result = CommandLineInterpreter.readFile(tempFile.toString());

        assertEquals(expected, result);
    }

    @Test
    void testSortFile() throws IOException {
        String content = "orange\napple\nbanana";
        Files.writeString(tempFile, content);

        List<String> sortedResult = interpreter.sortFile(tempFile.toString());

        List<String> expected = Arrays.asList("apple", "banana", "orange");
        assertEquals(expected, sortedResult);
    }

    @Test
    void testSortList() throws IOException {
        List<String> input = Arrays.asList( "orange", "apple", "banana");
        List<String> expected = Arrays.asList("apple", "banana", "orange");

        List<String> result = interpreter.sortList(input);

        assertEquals(expected, result);
    }

    @Test
    void testUniq() {
        List<String> input = Arrays.asList("apple", "apple", "banana", "banana", "apple");
        List<String> expected = Arrays.asList("apple", "banana", "apple");

        List<String> result = interpreter.uniq(input);

        assertEquals(expected, result);
    }

    @Test
    void testPipeSortAndUniq() throws IOException {
        String content = "banana\napple\napple\norange\nbanana";
        Files.writeString(tempFile, content);

        interpreter.execute("sort " + tempFile.toString() + " | uniq");

        List<String> expected = Arrays.asList("apple", "banana", "orange");

        List<String> result = interpreter.uniq(interpreter.sortFile(tempFile.toString()));

        assertEquals(expected, result);
    }

    @Test
    void testRunpipe_with_inputSortAndUniq() throws IOException {
        List<String> input = Arrays.asList("orange", "banana", "apple", "apple", "banana");

        List<String> sortedList = interpreter.sortList(input);

        List<String> uniqResult = interpreter.uniq(sortedList);

        List<String> expected = Arrays.asList("apple", "banana", "orange");

        assertEquals(expected, uniqResult);
    }

    @Test
    void testCatCommand() throws IOException {
        String content = "hello world\nthis is a test file";
        Files.writeString(tempFile, content);

        List<String> expected = Arrays.asList("hello world", "this is a test file");
        List<String> result = CommandLineInterpreter.cat(tempFile.toString());

        assertEquals(expected, result);
    }

    @Test
    void testPipeCatSortAndUniq() throws IOException {
        String content = "orange\napple\nbanana\napple\norange";
        Files.writeString(tempFile, content);

        interpreter.execute("cat " + tempFile.toString() + " | sort | uniq");

        List<String> expected = Arrays.asList("apple", "banana", "orange");

        List<String> result = interpreter.uniq(interpreter.sortFile(tempFile.toString()));

        assertEquals(expected, result);
    }


//    @Test
//    void testExit() {
//        // Since System.exit() is used in the exit method, we can only assert it doesn't throw an exception
//        assertDoesNotThrow(CommandLineInterpreter::exit);
//    }
}
