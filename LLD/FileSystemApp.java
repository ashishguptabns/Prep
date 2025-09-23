package LLD;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

interface IFileSystem {
    public void mkdir(String path);

    public void addFile(String path, String content);

    public String readFile(String path);

    public List<String> ls(String path);

    public void move(String src, String dest);
}

class DirNode {
    Map<String, DirNode> children = new ConcurrentHashMap<>();
    final boolean isFile;
    String content;

    public DirNode(boolean isFile) {
        this.isFile = isFile;
    }
}

class FileSystem implements IFileSystem {

    private String normalisePath(String path) {
        if (path == null || "".equals(path) || path.equals("/")) {
            return "";
        }
        if (path.startsWith("/")) {
            return path.substring(1);
        }
        if (path.endsWith("/")) {
            return path.substring(0, path.length() - 1);
        }
        return path;
    }

    DirNode root = new DirNode(false);

    Map<String, Object> pathLocks = new ConcurrentHashMap<>();
    final Object rootLock = new Object();

    private Object getLockForParent(String path) {
        int index = path.lastIndexOf('/');
        String parent = index > 0 ? path.substring(0, index) : "";
        if ("".equals(parent)) {
            return rootLock;
        }
        pathLocks.putIfAbsent(parent, new Object());
        return pathLocks.get(parent);
    }

    @Override
    public void mkdir(String path) {
        path = normalisePath(path);
        synchronized (getLockForParent(path)) {
            String[] parts = path.split("/");
            DirNode curr = root;
            for (String part : parts) {
                curr = curr.children.computeIfAbsent(part, k -> new DirNode(false));
            }
        }
    }

    @Override
    public void addFile(String path, String content) {
        path = normalisePath(path);
        synchronized (getLockForParent(path)) {
            String[] parts = path.split("/");
            DirNode curr = root;
            for (int i = 0; i < parts.length - 1; i++) {
                curr = curr.children.computeIfAbsent(parts[i], k -> new DirNode(false));
            }
            String fileName = parts[parts.length - 1];
            curr.children.compute(fileName, (k, node) -> {
                if (node == null) {
                    DirNode file = new DirNode(true);
                    file.content = content;
                    return file;
                } else if (!node.isFile) {
                    throw new IllegalStateException("wrong");
                } else {
                    node.content = content;
                    return node;
                }
            });
            System.out.println("addFile - " + path);
        }
    }

    @Override
    public String readFile(String path) {
        path = normalisePath(path);
        synchronized (getLockForParent(path)) {
            String[] parts = path.split("/");
            DirNode curr = root;
            for (String part : parts) {
                if (curr == null || curr.isFile) {
                    return null;
                }
                curr = curr.children.get(part);
            }
            if (curr == null || !curr.isFile) {
                return null;
            }
            return curr.content;
        }
    }

    @Override
    public List<String> ls(String path) {
        path = normalisePath(path);
        synchronized (getLockForParent(path)) {
            if ("/".equals(path)) {
                return new ArrayList<>(root.children.keySet());
            }
            String[] parts = path.split("/");
            DirNode curr = root;
            for (String part : parts) {
                if (curr == null) {
                    return new ArrayList<>();
                }
                curr = curr.children.get(part);
            }
            if (curr == null) {
                return new ArrayList<>();
            }
            if (curr.isFile) {
                return List.of(parts[parts.length - 1]);
            }

            return new ArrayList<>(curr.children.keySet());
        }
    }

    @Override
    public void move(String src, String dest) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

public class FileSystemApp {
    public static void main(String[] a) {
        FileSystem fs = new FileSystem();
        int i = 10;
        while (i-- > 0) {
            final int count = i;
            Thread writer = new Thread(() -> {
                fs.addFile(String.format("/test/file_%d.txt", count), Thread.currentThread().getName());
            });
            Thread reader = new Thread(() -> {
                System.out.println("reader - " + fs.readFile(String.format("/test/file_%d.txt", count)));
            });
            Thread lister = new Thread(() -> {
                System.out.println("lister - " + fs.ls("/test/"));
            });
            writer.start();
            reader.start();
            lister.start();
        }
    }
}
