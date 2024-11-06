import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class CommandLineInterpreter {

    private static File currentDirectory;

    public CommandLineInterpreter() {
        currentDirectory = new File(System.getProperty("user.home"));
    }

    public File getCurrentDirectory() {
        return currentDirectory;
    }
###########################################################################################################
  #AyaEssam
    public void mkdir(String dirName, OutputStream output) throws IOException {
        if (dirName == null || dirName.isEmpty()) {
            output.write("Usage: mkdir <directoryName>\n".getBytes());
            return;
        }
        File dir = new File(currentDirectory, dirName);
        if (dir.exists()) {
            output.write(("Directory already exists: " + dirName + "\n").getBytes());
        } else if (dir.mkdirs()) {
            output.write(("Directory created: " + dirName + "\n").getBytes());
        } else {
            output.write(("Failed to create directory: " + dirName + "\n").getBytes());
        }
    }

    public void rmdir(String dirName, OutputStream output) throws IOException {
        if (dirName == null || dirName.isEmpty()) {
            output.write("Usage: rmdir <directoryName>\n".getBytes());
            return;
        }
        File dir = new File(currentDirectory, dirName);
        if (!dir.exists()) {
            output.write(("Directory not found: " + dirName + "\n").getBytes());
        } else if (!dir.isDirectory()) {
            output.write((dirName + " is not a directory\n").getBytes());
        } else if (dir.delete()) {
            output.write(("Directory deleted: " + dirName + "\n").getBytes());
        } else {
            output.write(("Failed to delete directory: " + dirName + "\n").getBytes());
        }
    }

    public void mv(String sourceFileName, String destinationFileName, OutputStream output) throws IOException {
        assert sourceFileName != null && !sourceFileName.isEmpty() : "Source file name is null or empty";
        assert destinationFileName != null && !destinationFileName.isEmpty() : "Destination file name is null or empty";

        File sourceFile = new File(currentDirectory, sourceFileName);
        File destinationFile = new File(currentDirectory, destinationFileName);

        assert sourceFile.exists() : "Source file not found: " + sourceFileName;
        assert !destinationFile.exists() : "Destination file already exists: " + destinationFileName;

        if (sourceFile.equals(destinationFile)) {
            output.write(("Source and destination are the same, no action needed.\n").getBytes());
        } else {
            if (sourceFile.getAbsolutePath().equals(destinationFile.getAbsolutePath())) {
                assert sourceFile.renameTo(destinationFile) : "Failed to rename " + sourceFileName + " to " + destinationFileName;
                output.write(("Renamed " + sourceFileName + " to " + destinationFileName + "\n").getBytes());
            } else {
                File destinationDir = new File(destinationFile.getParent());
                assert destinationDir.exists() : "Destination directory does not exist: " + destinationFile.getParent();
                assert sourceFile.renameTo(destinationFile) : "Failed to move " + sourceFileName + " to " + destinationFileName;
                output.write(("Moved " + sourceFileName + " to " + destinationFileName + "\n").getBytes());
            }
        }
    }

    public void rm(String fileName, OutputStream output) throws IOException {
        if (fileName == null || fileName.isEmpty()) {
            output.write("Usage: rm <fileName>\n".getBytes());
            return;
        }
        File file = new File(currentDirectory, fileName);
        if (!file.exists()) {
            output.write(("File not found: " + fileName + "\n").getBytes());
        } else if (!file.isFile()) {
            output.write((fileName + " is not a file\n").getBytes());
        } else if (file.delete()) {
            output.write(("File deleted: " + fileName + "\n").getBytes());
        } else {
            output.write(("Failed to delete file: " + fileName + "\n").getBytes());
        }
    }

    public void executeCommand(String commandLine) throws IOException {
        String[] commandParts = commandLine.split("\\s+");
        String command = commandParts[0];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        switch (command) {
            case "mkdir":
                mkdir(commandParts.length > 1 ? commandParts[1] : null, outputStream);
                break;
            case "rmdir":
                rmdir(commandParts.length > 1 ? commandParts[1] : null, outputStream);
                break;
            case "mv":
                if (commandParts.length > 2) {
                    mv(commandParts[1], commandParts[2], outputStream);
                } else {
                    outputStream.write("Usage: mv <source> <destination>\n".getBytes());
                }
                break;
            case "rm":
                rm(commandParts.length > 1 ? commandParts[1] : null, outputStream);
                break;
            case "help":
                help();
                break;
            default:
                outputStream.write(("Unknown command: " + command + "\n").getBytes());
                break;
        }

        System.out.write(outputStream.toByteArray());
    }

    public static void main(String[] args) throws IOException {
        CommandLineInterpreter cli = new CommandLineInterpreter();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Welcome to Command Line Interpreter. Type 'exit' to quit.");
        String commandLine;
        while (true) {
            System.out.print(cli.currentDirectory.getAbsolutePath() + "> ");
            commandLine = reader.readLine();
            if ("exit".equalsIgnoreCase(commandLine)) {
                cli.exit();
            }
            cli.executeCommand(commandLine);
        }
    }

}
################################################################################################################3
