import java.io.File;
import java.io.FileWriter;
import java.util.Map;

public class UserLog {
    public static Boolean isNewFile = false;

    /**
     * This method writes the user activity to the user logs
     * 
     * @param infoMap         This contains the details of the operation and
     *                        associated timestamp
     * @param isLastOperation This denotes if the given operation is last operation
     *                        for the logged in the user for example: logout
     */
    public void writeLogs(Map<String, String> infoMap, Boolean isLastOperation) {
        try {
            File fileObj = new File("userActivityLog.env");
            if (fileObj.createNewFile()) {
                isNewFile = true;
            } else {
                isNewFile = false;
            }
            FileWriter fileWriter = new FileWriter("userActivityLog.env", true);
            String operationType = infoMap.get("operation");
            if (operationType.equals("login")) {
                if (isNewFile) {
                    fileWriter.write("##\n");
                } else {
                    fileWriter.write("\n");
                    fileWriter.write("##\n");
                }
            }
            String temp = new String();
            for (Map.Entry<String, String> pair : infoMap.entrySet()) {
                temp = temp.concat(pair.getKey() + "=" + pair.getValue() + "--");
            }
            if (isLastOperation) {
                temp = temp.substring(0, temp.length() - 2);
            }
            fileWriter.write(temp);
            fileWriter.close();
        } catch (Exception e) {
            System.out.println("Something went wrong!");
            UserManagement.getUserInput();
        }
    }
}
