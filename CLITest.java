import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.*;
import java.io.IOException;
import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
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
    public void testDynamicMkdir()
    {
        System.out.print("Enter directory names for mkdir test (space-separated): ");
        String userDirs = scanner.nextLine();
        String[] directories = userDirs.split("\\s+");

        cli.mkdir(directories);

        for (String dir : directories)
        {
            assertTrue(new File(testDirectory, dir).exists());
            deleteFileOrDirectory(new File(testDirectory, dir).getPath());
        }
    }

    @Test
    public void testDynamicRmdir()
    {
        System.out.print("Enter a directory name for rmdir test: ");
        String dirName = scanner.nextLine();
        File directory = new File(testDirectory, dirName);
        directory.mkdir();

        cli.rmdir(dirName);

        assertFalse(directory.exists());
    }


    @Test
    public void testDynamicTouch()
    {
        System.out.print("Enter a filename for touch test: ");
        String fileName = scanner.nextLine();

        cli.touch(fileName);

        assertTrue(new File(testDirectory, fileName).exists());
        deleteFileOrDirectory(new File(testDirectory, fileName).getPath());
    }

    @Test
    public void testDynamicMv() {
        // الحالة الأولى: إعادة تسمية ملف
        System.out.print("Enter a filename for mv test (create then rename): ");
        String fileName = scanner.nextLine();
        cli.touch(fileName); // إنشاء الملف للاختبار

        System.out.print("Enter the new filename (for rename): ");
        String newFileName = scanner.nextLine();

        cli.mv(fileName, newFileName); // إعادة التسمية

        // التحقق من أن إعادة التسمية قد تمت بنجاح
        assertTrue(new File(testDirectory, newFileName).exists());
        assertFalse(new File(testDirectory, fileName).exists());

        // تنظيف الملف بعد إعادة التسمية
        deleteFileOrDirectory(new File(testDirectory, newFileName).getPath());

        // الحالة الثانية: نقل الملف إلى مجلد آخر
        System.out.print("Enter a directory name for mv test (destination directory): ");
        String dirName = scanner.nextLine();
        File destinationDir = new File(testDirectory, dirName);
        destinationDir.mkdir(); // إنشاء المجلد الوجهة للاختبار

        cli.touch(fileName); // إعادة إنشاء الملف للاختبار

        cli.mv(fileName, dirName); // نقل الملف إلى المجلد الوجهة

        // التحقق من أن الملف تم نقله إلى داخل المجلد الوجهة
        assertTrue(new File(destinationDir, fileName).exists());
        assertFalse(new File(testDirectory, fileName).exists());

        // تنظيف الملفات والمجلدات بعد الاختبار
        deleteFileOrDirectory(new File(destinationDir, fileName).getPath());
        deleteFileOrDirectory(destinationDir.getPath());
    }


}
