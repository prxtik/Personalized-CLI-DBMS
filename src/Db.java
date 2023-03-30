import java.io.*;
import java.util.*;

/**
 * This class performs database operarions like create and drop database.
 */
public class Db {
    /**
     * The method is used to create a database.
     *
     * @param dbname the method takes database name as the input parameter.
     * @return the method returns true if the database was created else it returns false.
     */
    public boolean createDb(String dbname) {
        try {
            File obj = new File(dbname + ".database");
            obj.createNewFile();
            return true;
        } catch (Exception exceptn) {
            System.out.println("Database not created.");
            exceptn.printStackTrace();
        }
        return false;
    }

    /**
     * The method is used to drop a database.
     *
     * @param query the method takes the query as the input parameter.
     * @return the method returns true if the database was dropped.
     */
    public boolean dropDb(String query) {
        try {
            String[] q = query.split(" ");
            if (q[1].equalsIgnoreCase("database")) {
                if (q[2] != null) {
                    File folder = new File("./");
                    File[] listOfFiles = folder.listFiles();
                    for (int i = 0; i < listOfFiles.length; i++) {
                        if (listOfFiles[i].isFile() && listOfFiles[i].getName().contains(q[2] + ".database")) {
                            listOfFiles[i].delete();
                            return true;
                        }
                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Enter a valid database name.");
            return false;
        }
        return false;
    }
}
