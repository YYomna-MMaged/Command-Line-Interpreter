# Command Line Interpreter (CLI) Project

## Overview
The Command Line Interpreter (CLI) is a Java-based tool designed to mimic shell functionality. It allows users to interact with the file system, manage files, and process text via commands.

---

## Features
### Directory and File Commands
- **`pwd`**: Prints the current working directory.
- **`cd <dir>`**: Changes the working directory to `<dir>`.
- **`ls`**: Lists non-hidden files in alphabetical order.
- **`ls-a`**: Lists all files (including hidden).
- **`ls-r`**: Lists non-hidden files in reverse order.
- **`mkdir <dir> <dir1>`**: Creates one or more directories named `<dir>`, `<dir1>`.
- **`rmdir <dir> <dir1>`**: Removes one or more empty directory.
- **`touch <file>`**: Creates an empty file.
- **`rm <file>`**: Deletes a file.
- **`mv <src> <dst>`**: Moves or renames a file.

### Text Processing Commands
- **`cat <file>`**: Displays file contents.
- **`>`**: Overwrites a file.
- **`>>`**: Appends content to a file.
- **`sort <file>`**: Sorts file contents.
- **`uniq`**: Removes duplicate lines from sorted input.

### Advanced Features
- **Piping (`|`)**: Combines commands for advanced functionality.
