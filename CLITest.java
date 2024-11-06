import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.*;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CLITest {
    private CLI cli;
    private Path tempDir;


    @BeforeEach
    public void beforeEach() throws IOException {
        // Create a temporary directory for testing
        tempDir = Files.createTempDirectory("CLI-test");
        cli = new CLI();

        // Set the current directory to the temp directory for the CLI
        cli.currentDir = tempDir.toAbsolutePath();
    }

    @AfterEach
    public void afterEach() throws IOException {
        // Clean up the temporary directory after each test
        Files.walk(tempDir)
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @Test
    public void testPwd() {
        // Test the pwd command (Print Working Directory)
        cli.pwd();
        assertEquals(tempDir.toAbsolutePath().toString(), cli.currentDir.toAbsolutePath().toString());
    }

    @Test
    public void testCdVaildDirectory() throws IOException {
        // Create a new directory and test if cd changes to it
        Path SubDir = Files.createDirectory(tempDir.resolve("SubDir"));//tempDir.resolve("subdir"): This resolves the path of "subdir" relative to the tempDir directory.
        cli.cd(new String[]{"SubDir"});
        assertEquals(SubDir.toAbsolutePath().toString(), cli.currentDir.toAbsolutePath().toString());
    }


    @Test
    public void testCdInVaildDirectory() {
        // Try to change to a non-existent directory
        cli.cd(new String[]{"noneExistent"});
        // The current directory should remain unchanged
        assertEquals(tempDir.toAbsolutePath().toString(), cli.currentDir.toAbsolutePath().toString());
    }


    @Test
    public void testLs() throws IOException {
        // Create files in the directory and test ls command
        Files.createFile(tempDir.resolve("file1.txt"));
        Files.createFile(tempDir.resolve("file2.txt"));

        cli.ls();
        File dir = tempDir.toFile();
        String[] files = dir.list();
        assertNotNull(files);
        assertEquals(2, files.length);
    }

    @Test
    public void testLsA() throws IOException {
        // Create files and hidden files
        Files.createFile(tempDir.resolve("file1.txt"));
        Files.createFile(tempDir.resolve("file2.txt"));
        Files.createFile(tempDir.resolve(".hiddenfile"));

        cli.lsA();
        File dir = tempDir.toFile();
        String[] files = dir.list();
        assertNotNull(files);
        assertTrue(List.of(files).contains("file1.txt"));
        assertTrue(List.of(files).contains(".hiddenfile"));
    }

    @Test
    public void testLsR() throws IOException {
        // Create files in the directory
        Files.createFile(tempDir.resolve("file1.txt"));
        Files.createFile(tempDir.resolve("file2.txt"));
        // Capture the output of the lsR() method
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // Redirect System.out (standard output) to the ByteArrayOutputStream
//        System.setOut(new PrintStream(outputStream));
        // Test reverse listing
        cli.lsR();
        // Split the captured output into lines and check the order
//        String[] lines=outputStream.toString().split("\n");
//        assertEquals("file2.txt", lines[0].trim());  // First file should be file2.txt
//        assertEquals("file1.txt", lines[1].trim());  // Second file should be file1.txt
    }

}
