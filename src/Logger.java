import java.io.*;
import java.sql.Time;
import java.util.*;

/**
 * This class logs the entire user activity.
 */
public class Logger {
    /**
     * This method logs everything a user does between creating/login til exit in a .txt file using limiters
     *
     * @param username the method takes username as one of input parameters.
     * @param activity the method takes the activity that the user does as another one of its input parameter.
     * @param query    The method takes the valid query that the user inputted as another one of its parameter.
     */

    public void logActivity(String username, String activity, String query) {
        boolean result = createFile("userLog");
        try {
            FileWriter writeLog = new FileWriter("userLog.txt", true);

            if (activity.equals("Login")) {
                Date date = new Date();
                writeLog.append('\n');
                writeLog.append("///" + username + " logged in at [ " + date + " ]");
                writeLog.close();
                return;
            } else if (activity.equals("Inserted a query")) {
                Date date = new Date();
                writeLog.append(" --- " + " inserted query " + "{ " + query + " }" + " at [ " + date + " ]");
                writeLog.close();
                return;

            } else if (activity.equals("Logout")) {
                Date date = new Date();
                writeLog.append(" --- " + username + " logged out at [ " + date + " ]" + " ///");
                System.out.println("Thanks, " + username + ". You are successfully logged out.");
                writeLog.close();
                return;

            }
        } catch (IOException e) {
            throw new RuntimeException(e);

        }

    }

    /**
     * The method is used to create a log file
     *
     * @param filename the method takes log file name as the input parameter
     * @return the method returns true if the log file was created.
     */
    public boolean createFile(String filename) {
        try {
            File obj = new File(filename + ".txt");
            obj.createNewFile();
            return true;
        } catch (Exception exceptn) {
            System.out.println("File not created.");
            exceptn.printStackTrace();
        }
        return false;
    }

}
