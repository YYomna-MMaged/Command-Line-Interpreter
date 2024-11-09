package org.example;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.nio.file.Files;

public class CommandLineInterpreter {
    File currentDirectory;

    public CommandLineInterpreter() {
        currentDirectory = new File(System.getProperty("user.home"));
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

    public static void help(){
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
        System.out.println("    > <file>      | - Redirect output of the <file> (overwrite).");
        System.out.println("    >> <file>     | - Append output to <file>.");
        System.out.println("    |             | - Pipe output of one file to another");
        System.out.println("    exit          | - Exit the CLI.");
        System.out.println("    help          | - Display this help message.");
    }

    public static void exit(){
        System.out.println("Exiting the CLT, see you later :)");
        System.exit(0);
    }

    public static List<String> cat(String filename) throws IOException {
        return Files.readAllLines(Paths.get(filename));
    }


    public static void main(String[] args) throws IOException {
        CommandLineInterpreter ci = new CommandLineInterpreter();

        ci.execute("sort C:\\Users\\file3.txt | uniq");
        ci.execute("sort C:\\Users\\file3.txt");
        ci.execute("uniq C:\\Users\\file3.txt | sort");
//
        ci.execute(" sort  C:\\Users\\file3.txt    |   uniq ");
//        ci.execute("help");
        ci.execute("cat C:\\Users\\file3.txt | sort | uniq");
        ci.execute("cat C:\\Users\\file3.txt");
        ci.execute("exit");


    }
}