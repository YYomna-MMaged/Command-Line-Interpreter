import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CLI {

    Path currentDir;
    private final Scanner scanner = new Scanner(System.in);
    private File currentDirectory;



    public CLI()
    {
        currentDir= Paths.get("").toAbsolutePath();// Start in the current directory
        this.currentDirectory = new File(System.getProperty("user.dir"));

    }
    public CLI(File currentDirectory) {
        this.currentDirectory = currentDirectory;
    }
    public void setCurrentDirectory(File directory) {
        this.currentDirectory = directory;
    }
    public File getCurrentDirectory() {
        return currentDirectory;
    }




    //pwd - Print Working Directory
    // Displays the current working directory path.
    public void pwd()
    {
        System.out.println(currentDir.toAbsolutePath()); // retrieves the absolute path of the currentDirectory.
    }

    //cd<directory>Change Directory
    // Changes the working directory to the specified directory.
    public void cd(String[] dir)
    {
        if(dir.length==0)
        {
            System.out.println("cd:missing Directory");
            return;
        }
        // Resolve the new path relative to the current directory and normalize it (clean up any redundant parts)
        Path NewPath=currentDir.resolve(dir[0]).normalize();
        // Convert the Path object to a File object to check if it exists and is a directory
        File Directory = NewPath.toFile();

        if(Directory.exists()&&Directory.isDirectory())
        {
            currentDir=NewPath;// Update the current directory to the new path
        }
        else
        {
            System.out.println("cd:directory does not exist");
        }

    }

    //ls - List Directory Contents
    // Lists the non-hidden files in the current directory in alphabetical order.
    public void ls()
    {
        File Dir=currentDir.toFile();
        String[] files=Dir.list();

        if(files==null){
            System.out.println("ls:missing Files"+currentDir);
            return;
        }
        Arrays.sort(files);
        for (String file : files) {
            if(!file.startsWith(".")){//checks whether the file name starts with a dot (.). In Unix-based systems (Linux, macOS), files that start with a dot are considered "hidden files."
                System.out.println(file);
            }
        }

    }

    // ls-a -List All Directory Contents (including hidden files)
    // Lists all files in the current directory, including hidden files, in alphabetical order.
    public void lsA()
    {
        File Dir=currentDir.toFile();
        String[] files=Dir.list();

        if(files==null){
            System.out.println("lsA:missing Files"+currentDir);
            return;
        }
        Arrays.sort(files);
        for (String file : files) {
            System.out.println(file); // Include hidden files
        }
    }

    //ls-r - List Directory Contents in Reverse Order.]
    //Lists the non-hidden files in the current directory in reverse alphabetical order.
    public void lsR()
    {
        File Dir=currentDir.toFile();
        String[] files=Dir.list();

        if(files==null){
            System.out.println("lsR:missing Files"+currentDir);
            return;
        }

        Arrays.sort(files, Collections.reverseOrder());
        for (String file : files) {
            if(!file.startsWith(".")){ // Exclude hidden files
                System.out.println(file);
            }
        }
    }

    //Mkdir
    public void mkdir(String[] directories) {
        for (String dir : directories) {
            File directory = new File(currentDirectory, dir);
            if (!directory.exists()) {
                if (directory.mkdir()) {
                    System.out.println("Directory created: " + directory.getPath());
                } else {
                    System.out.println("Failed to create directory: " + directory.getPath());
                }
            } else {
                System.out.println("Directory already exists: " + directory.getPath());
            }
        }
    }

    //Rmdir
    public void rmdir(String dirName) {
        File directory = new File(currentDirectory, dirName);
        if (directory.exists() && directory.isDirectory()) {
            if (directory.delete()) {
                System.out.println("Directory removed: " + directory.getPath());
            } else {
                System.out.println("Failed to remove directory: " + directory.getPath());
            }
        } else {
            System.out.println("Directory not found: " + directory.getPath());
        }
    }

    //Touch
    public void touch(String fileName) {
        File file = new File(currentDirectory, fileName);
        try {
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getPath());
            } else {
                System.out.println("File already exists: " + file.getPath());
            }
        } catch (IOException e) {
            System.out.println("Error creating file: " + file.getPath());
        }
    }

    //mv
    public void mv(String source, String destination) {
        File sourceFile = new File(currentDirectory, source);
        File destFile = new File(currentDirectory, destination);

        if (!sourceFile.exists()) {
            System.out.println("Source not found: " + sourceFile.getPath());
            return;
        }

        if (destFile.exists() && destFile.isDirectory()) {
            destFile = new File(destFile, sourceFile.getName());
        }

        if (sourceFile.renameTo(destFile)) {
            System.out.println("Moved/Renamed " + sourceFile.getPath() + " to " + destFile.getPath());
        } else {
            System.out.println("Failed to move/rename " + sourceFile.getPath());
        }
    }

    public static List<String> readFile(String filePath) throws IOException {
        List<String> words = new ArrayList<>();

        // Read all lines from the file
        List<String> lines = Files.readAllLines(Paths.get(filePath));

        // Process each line
        for (String line : lines) {
            // Split each line by one or more spaces
            String[] tokens = line.split("\\s+");

            // Add each word to the words list
            for (String token : tokens) {
                words.add(token);
            }
        }
        return words;
    }

    public void execute(String command) throws IOException {
        if (command.contains("|")){
            String[] commandParts = command.split("\\|");

            pipe(commandParts);
        }
        else {
            if (command.equals("help")) {
                help();
            }
            else if (command.startsWith("sort")) {
                String[] parts = command.split(" ");

                List<String> sorted = sortFile(parts[1]);
                for (String part : sorted) {
                    System.out.println(part);
                }
            }
            else if (command.startsWith("cat")) {
                String[] parts = command.split(" ");

                List<String> lines = cat(parts[1]);
                for (String part : lines) {
                    System.out.println(part);
                }
            }
            else if (command.equals("exit")) {
                exit();
            }
            else {
                throw new IllegalArgumentException("Unrecognized command: " + command);
            }
        }

    }
    public void pipe(String[] commands) throws IOException {
        List<String> out_in = null;

        for (String command : commands) {
            command = command.trim();

            if(out_in == null){
                out_in = runpipe(command);
            }
            else {
                out_in = runpipe_with_input(command, out_in);
            }
        }

        for (String line : out_in) {
            System.out.println(line);
        }
    }

    private List<String> runpipe(String command) throws IOException {
        String[] parts = command.split(" ");
        ArrayList<String> cleanParts = new ArrayList<>();
        for (String part : parts){
            if (!Objects.equals(part, "")){
                cleanParts.add(part);
            }
        }

        String[] newparts = cleanParts.toArray(new String[cleanParts.size()]);
        switch (newparts[0]){
            case "sort":
                return sortFile(newparts[1]);
            case "uniq":
                return uniq(sortFile(newparts[1]));
            case "cat":
                return cat(newparts[1]);
            default:
                throw new IllegalArgumentException("Unrecognized command: " + command);
        }
    }

    private List<String> runpipe_with_input(String command, List<String> input){
        String[] parts = command.split(" ");

        switch (command){
            case "sort":
                return sortList(input);
            case "uniq":
                return uniq(input);
            default:
                throw new RuntimeException("Unrecognized command: " + command);
        }

    }

    List<String> sortFile(String filename) throws IOException {
        List<String> lines = readFile(filename);
        Collections.sort(lines);

        return lines;
    }

    public List<String> sortList(List<String> lines){
        Collections.sort(lines);
        return lines;
    }

    List<String> uniq(List<String> list){
        List<String> uniq = new ArrayList<>();
        String prev_line = null;

        for (String line : list){
            if (!line.equals(prev_line)){
                uniq.add(line);
                prev_line = line;
            }
        }
        return uniq;
    }
    public static List<String> cat(String filename) throws IOException {
        return Files.readAllLines(Paths.get(filename));
    }
    //To Delete a File
    public boolean rm (String file_name){
        File file = new File(currentDirectory,file_name);
        return file.exists() && file.isFile() && file.delete();
        //Deleting Files: file.delete()
        // returns true if the file
        // was successfully deleted and false if not.
    }
    //cat for reading file content
    public String Cat (String file_name){
        File file = new File(currentDirectory,file_name);
        if (file.exists() && file.isFile()){
            try{
                return Files.readString(Path.of(file.getPath()));
            } catch (IOException e) {
                return "Error reading the File: " + e.getMessage();
            }
        }else{
            return "File not found or is a directory";
        }
    }
    // '>' write to a File (overwrite)
    public String writeToFile(String file_name, String content){
        File file = new File(currentDirectory, file_name);
        try(FileWriter writer = new FileWriter(file, false)){
            //set append to false->overwrite mode
            writer.write(content);
            //i.e. ->If the file doesn’t exist,
            // FileWriter creates it.
            // If it exists, it overwrites
            // the contents.
            //try-with-resources,
            // you avoid common bugs by letting Java handle closing
            //We declare writer inside the parentheses
            // after try. Any resource declared here (like writer) will be automatically closed once the try block finishes.
            return "file written successfully";
        }catch(IOException e){
            return "error while writting to the file"+e.getMessage();
        }
    }
    //'>>' Append to a File
    public String appendToFile (String file_name, String content){
        File file = new File(currentDirectory, file_name);
        try(FileWriter writer = new FileWriter(file, true)){
            writer.write(content);
            //FileWriter creates the file if doesn't exist
            return "content was appended successfully";
        }catch (IOException e){
            return "error while appending to the file: "+ e.getMessage();
        }
    }


    public void executeCommand(String command) {
        String[] cmd = command.trim().split("\\s+");
        String mainCommand = cmd[0];
        String[] args = Arrays.copyOfRange(cmd, 1, cmd.length);

        try {
            switch (mainCommand) {
                case "pwd":
                    pwd();
                    break;
                case "cd":
                    cd(args);
                    break;
                case "ls":
                    if (args.length == 0) {
                        ls();
                    } else {
                        System.out.println("ls: invalid arguments");
                    }
                    break;
                case "ls-a":
                    if (args.length == 0) {
                        lsA();
                    } else {
                        System.out.println("ls-a: invalid arguments");
                    }
                    break;
                case "ls-r":
                    if (args.length == 0) {
                        lsR();
                    } else {
                        System.out.println("ls-r: invalid arguments");
                    }
                    break;
                case "mkdir":
                    mkdir(args);
                    break;
                case "rmdir":
                    if (args.length == 1) {
                        rmdir(args[0]);
                    } else {
                        System.out.println("Usage: rmdir <directory>");
                    }
                    break;
                case "touch":
                    if (args.length == 1) {
                        touch(args[0]);
                    } else {
                        System.out.println("Usage: touch <filename>");
                    }
                    break;
                case "mv":
                    if (args.length == 2) {
                        mv(args[0], args[1]);
                    } else {
                        System.out.println("Usage: mv <source> <destination>");
                    }
                    break;
                case "Cat":
                    if (args.length == 1) {
                        List<String> lines = cat(args[0]);
                        lines.forEach(System.out::println);
                    } else {
                        System.out.println("Usage: cat <filename>");
                    }
                    break;
                case ">":
                    if (args.length == 2) {
                        System.out.println(writeToFile(args[0], args[1]));
                    } else {
                        System.out.println("Usage: > <filename> <content>");
                    }
                    break;
                case ">>":
                    if (args.length == 2) {
                        System.out.println(appendToFile(args[0], args[1]));
                    } else {
                        System.out.println("Usage: >> <filename> <content>");
                    }
                    break;
                case "rm":
                    if (args.length == 1) {
                        boolean result = rm(args[0]);
                        System.out.println(result ? "File deleted successfully" : "File deletion failed or file not found.");
                    } else {
                        System.out.println("Usage: rm <filename>");
                    }
                    break;
                case "help":
                    help();
                    break;
                case "exit":
                    exit();
                    break;
                default:
                    execute(command);
            }
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }



    public static void help () {
        // Display list of available commands
        System.out.println("Available commands:");
        System.out.println("    pwd           | - Print the current directory.");
        System.out.println("    cd <dir>      | - Change directory to <dir>.");
        System.out.println("    mkdir <dir>   | - Create a new directory named <dir>.");
        System.out.println("    rmdir <dir>   | - Remove the directory named <dir>.");
        System.out.println("    ls -a -r      | - List files in the current directory (-a for all files and -r for reverse).");
        System.out.println("    touch <file>  | - Create a file named <file>.");
        System.out.println("    mv <src> <dst>| - Move or rename file from <src> to <dst>.");
        System.out.println("    rm <file>     | - Remove file from <file>.");
        System.out.println("    cat <file>    | - Display the contents of a <file>.");
        System.out.println("    > <file>      | - Redirect output to <file> (overwrite).");
        System.out.println("    >> <file>     | - Append output to <file>.");
        System.out.println("    |             | - Pipe output of one file to another.");
        System.out.println("    exit          | - Exit the CLI.");
        System.out.println("    help          | - Display this help message.");
    }
    public static void exit () {
        // Exit the Command Line Interpreter
        System.out.println("Exiting the CLI, see you later :)");
        System.exit(0);
    }
    // CLI run loop
    public void start() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print(currentDir + " $ ");
            String input = scanner.nextLine();
            executeCommand(input);
        }
    }

    public static void main(String[] args) {
        CLI cli = new CLI();
        cli.start();
    }
}



