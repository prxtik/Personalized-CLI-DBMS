import java.io.File;
import java.util.Arrays;

public class DeleteFiles {
    /*
     * This method deletes all the table files from the directory having extension
     * .txt
     */
    public static void deleteTablesFiles() {
        File folder = new File("./");
        Arrays.stream(folder.listFiles())
                .filter(f -> f.getName().endsWith(".txt"))
                .forEach(File::delete);
    }

    /**
     * This method deleted a single table file and its metadata from the root
     * directory
     * 
     * @param tableName This contains the name of the table as present in the query
     *                  entered by the user
     */
    public static void deleteSingleTableFile(String tableName) {
        File folder = new File("./");
        Arrays.stream(folder.listFiles())
                .filter(f -> f.getName().endsWith(tableName + ".table.txt"))
                .forEach(File::delete);
        Arrays.stream(folder.listFiles())
                .filter(f -> f.getName().endsWith(tableName + ".table.metadata.txt"))
                .forEach(File::delete);
    }
}
