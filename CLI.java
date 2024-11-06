import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Scanner;

public class CLI {

    Path currentDir;

    public CLI()
    {
        currentDir= Paths.get("").toAbsolutePath();// Start in the current directory
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


    public void executeCommand(String command) {
        //command.trim() removes any leading or trailing whitespace from the command string.
        //split("\\s+") splits the command string into an array of substrings using one or more spaces as the delimiter.
        String[] cmd = command.trim().split("\\s+");
        String mainCommand=cmd[0];
        String[] args = Arrays.copyOfRange(cmd, 1, cmd.length);

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
            case "help":
                help(); // Display help message
                break;
            case "exit":
                exit(); // Exit the CLI
                break;
            default:
                System.out.println("Unknown command: " + mainCommand);
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



