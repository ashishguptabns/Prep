
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class FindProblem {

    public static void main(String[] a) {
        try {
            String content = Files.readString(Path.of("dsa-problems.json"));
            int start = content.indexOf('[');
            int end = content.lastIndexOf(']');
            if (start == -1 || end == -1) {
                throw new IOException("Problems array not found");
            }
            String arrStr = content.substring(start + 1, end);
            String[] problems = arrStr.split("\",\"");
            if (problems.length > 0) {
                problems[0] = problems[0].replaceFirst("^\\\"", "");
                problems[problems.length - 1] = problems[problems.length - 1].replaceFirst("\\\"$", "");
            }
            int idx = new Random().nextInt(problems.length);
            System.out.println(problems[idx]);
        } catch (Exception e) {
            System.out.println("Error reading or parsing file: " + e.getMessage());
        }
    }
}
