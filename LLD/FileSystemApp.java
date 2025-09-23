package LLD;

/*
Design a simplified in-memory file system using Java. The system should support the following operations:

📁 Supported Operations:
mkdir(path: String)
Creates a directory at the given path. Intermediate directories must be created if they don’t exist.

addFile(path: String, content: String)
Creates a file at the given path with the given content. Intermediate directories must be created if needed.

readFile(path: String): String
Returns the content of the file at the given path.

ls(path: String): List<String>
Returns a list of all files and directories in the given path in lexicographical order.

If the path is a file, return the file name.

move(srcPath: String, destPath: String)
Moves a file or directory from srcPath to destPath.

If the destination already exists, it should overwrite a file but merge directories.

Handle cyclic moves (e.g., moving a directory into one of its own subdirectories).
*/

public class FileSystemApp {
    
}
