#Command Line Interpreter (CLI) Project
Overview
The Command Line Interpreter (CLI) project is a Java-based tool that replicates common shell functionality. It provides file and directory management, text processing commands, and supports command piping. The project includes a set of test cases to validate functionality using the JUnit testing framework.

Features
Directory and File Commands
pwd: Prints the current working directory.
cd <dir>: Changes the working directory to <dir>.
ls: Lists non-hidden files in the current directory in alphabetical order.
ls-a: Lists all files, including hidden files, in alphabetical order.
ls-r: Lists non-hidden files in reverse alphabetical order.
mkdir <dir>: Creates a new directory named <dir>.
rmdir <dir>: Removes an empty directory named <dir>.
touch <file>: Creates an empty file named <file>.
rm <file>: Deletes the specified file.
mv <src> <dst>: Moves or renames a file from <src> to <dst>.
Text Processing Commands
cat <file>: Displays the contents of <file>.
>: Overwrites content to a file.
>>: Appends content to a file.
sort <file>: Sorts the contents of a file.
uniq: Removes duplicate lines from sorted input.
Advanced Features
Pipes (|): Enables combining commands, such as cat <file> | sort | uniq.
