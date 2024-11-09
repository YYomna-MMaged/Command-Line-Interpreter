import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.*;

//import java.io.ByteArrayOutputStream;
import java.io.*;
//import java.io.PrintStream;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.io.File;
import java.util.Scanner;

//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;

import static org.junit.jupiter.api.Assertions.*;

public class CLITest {
    private CLI cli;
    private Path tempDir;
    private File testDirectory;
    private final InputStream systemIn = System.in;
    private final Scanner scanner = new Scanner(System.in);
    private CLI interpreter;
    private Path tempFile;




    @BeforeEach
    public void beforeEach() throws IOException {
        // Create a temporary directory for testing
        tempDir = Files.createTempDirectory("CLI-test");
        cli = new CLI();

        // Set the current directory to the temp directory for the CLI
        cli.currentDir = tempDir.toAbsolutePath();
    }
    @Before
    public void setUp() {
        testDirectory = new File("testDirectory");
        testDirectory.mkdir(); // إنشاء دليل للاختبار
        cli = new CLI(testDirectory);
    }


    @AfterEach
    public void afterEach() throws IOException {
        // Clean up the temporary directory after each test
        Files.walk(tempDir)
                .map(Path::toFile)
                .forEach(File::delete);
    }
    @After
    public void tearDown()
    {
        System.setIn(systemIn);
        deleteFileOrDirectory(testDirectory.getPath()); // إزالة دليل الاختبار بعد الانتهاء
    }

    private void deleteFileOrDirectory(String path)
    {
        File file = new File(path);
        if (file.exists())
        {
            file.delete();
        }
    }

    private void provideInput(String data)
    {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
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


//    @Test
//    public void testCdInVaildDirectory() {
//        // Try to change to a non-existent directory
//        cli.cd(new String[]{"noneExistent"});
//        // The current directory should remain unchanged
//        assertEquals(tempDir.toAbsolutePath().toString(), cli.currentDir.toAbsolutePath().toString());
//    }


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
        //if tempDir is /home/user/temp, then tempDir.resolve("file1.txt") will return a Path representing /home/user/temp/file1.txt.
        Files.createFile(tempDir.resolve("file1.txt"));
        Files.createFile(tempDir.resolve("file2.txt"));
        // Files.createFile(tempDir.resolve(".hiddenfile"));
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
    @Test
    void testMkdir() {
        cli.mkdir(new String[]{"newDir"});
        File createdDir = new File(testDirectory, "newDir");
        assertTrue(createdDir.exists() && createdDir.isDirectory(), "Directory should be created");
    }

    @Test
    void testRmdir() {
        File dirToDelete = new File(testDirectory, "dirToDelete");
        dirToDelete.mkdir();
        cli.rmdir("dirToDelete");
        assertFalse(dirToDelete.exists(), "Directory should be deleted");
    }
    @Test
    void testTouch() {
        cli.touch("newFile.txt");
        File createdFile = new File(testDirectory, "newFile.txt");
        assertTrue(createdFile.exists() && createdFile.isFile(), "File should be created");
    }

    @Test
    void testMv() {
        File sourceFile = new File(testDirectory, "sourceFile.txt");
        File destinationFile = new File(testDirectory, "destinationFile.txt");

        try {
            assertTrue(sourceFile.createNewFile(), "Source file should be created");
            cli.mv("sourceFile.txt", "destinationFile.txt");
            assertTrue(destinationFile.exists(), "File should be moved/renamed to destination");
            assertFalse(sourceFile.exists(), "Source file should no longer exist");
        } catch (IOException e) {
            fail("IOException occurred during testMv");
  }
    }

    @BeforeEach
    void setUpp() throws IOException {
        interpreter = new CLI();
        tempFile = Files.createTempFile("testfile", ".txt");
    }

    @Test
    void testHelp() {
        // Ensure that calling help() doesn't cause an error.
        assertDoesNotThrow(CLI::help);
    }

    @Test
    void testReadFile() throws IOException {
        String content = "apple banana\nbanana apple";
        Files.writeString(tempFile, content);

        List<String> expected = Arrays.asList("apple", "banana", "banana", "apple");
        List<String> result = CLI.readFile(tempFile.toString());

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
        List<String> result = CLI.cat(tempFile.toString());

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
    @Test
    // testing the rm method
    public void test_rm()throws Exception{
        File testFile =new File(cli.getCurrentDirectory(), "testFile.txt");
        testFile.createNewFile();

        boolean testing = cli.rm("testFile.txt");

        //Assert the file was deleted ?
        assertTrue(testing, "File sould be deleted"); //->Confirms that rm returned true, meaning it believed the file was deleted.
        assertFalse(testFile.exists(), "File shouldn't exist now");
    }
    // testing the cat function
    @Test
    public void test_cat() throws IOException{
        File testFile = new File(cli.getCurrentDirectory(), "testFile.txt");
        try(FileWriter writer = new FileWriter(testFile)){
            writer.write("For you a thousands times over!");
        }
        String content = cli.Cat("testFile.txt");
        assertEquals("For you a thousands times over!", content,"they should be the same");
    }
    //testing the override function
    @Test
    public void testWriteToFile(){
        String testingWToF = cli.writeToFile("newFile.txt","A beautiful thing is never prtfect");

        String content = cli.Cat("newFile.txt");
        assertEquals("file written successfully",testingWToF,"succeed");//->This assertion checks the return value of the writeToFile method.
        assertEquals("A beautiful thing is never prtfect", content,"they should match");
    }
    //testing appending to a file
    @Test
    public void testAppendTFile()throws IOException{
        //creating file with initial content
        File testFile = new File(cli.getCurrentDirectory(), "initial_content.txt");
        try(FileWriter writer = new FileWriter(testFile)){
            writer.write("small steps, ");
        }
        String result = cli.appendToFile("initial_content.txt","every day");
        String content = cli.Cat("initial_content.txt");
        assertEquals("content was appended successfully",result,"succeed");
        assertEquals("small steps, every day",content,"they should be equal");
    }



}
