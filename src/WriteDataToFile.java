import java.io.File;
import java.io.FileWriter;
import java.util.Map;

public class WriteDataToFile {
    public static Boolean isNewFile;

    /**
     * This method writes the metadata of the table to the file
     * 
     * @param data      This contains map of data to be written in the file
     * @param tableName This contains the name of the table
     */
    public static void writeTableMdtToFile(Map<String, Map<String, String>> data, String tableName) {
        try {
            File fileObj = new File(tableName + ".table.metadata.txt");

            FileWriter fileWriter = new FileWriter(tableName + ".table.metadata.txt",
                    true);
            String primaryKeyData = "primaryKey=" +
                    data.get("primary").get("primaryKey") + "--##--";

            String attributeData = new String();
            for (Map.Entry<String, Map<String, String>> pair : data.entrySet()) {
                Map<String, String> innerPairs = pair.getValue();
                for (Map.Entry<String, String> innerPair : innerPairs.entrySet()) {
                    if (!innerPair.getKey().equals("primaryKey")) {
                        attributeData = attributeData.concat(pair.getKey() + "=" + innerPair.getKey() + "="
                                + innerPair.getValue() + "--");
                    }
                }
            }
            attributeData = attributeData.substring(0, attributeData.length() - 2);
            String finalData = primaryKeyData.concat(attributeData);
            fileWriter.write(EncryptionDecryptionAES.encryptDecrypt(finalData, "encrypt"));
            fileWriter.close();
        } catch (Exception e) {
            System.out.println("Error writing data to the file!");
        }
    }

    /**
     * This method writes the table data to the file
     * 
     * @param valuesToSetInColumns This contains the values to be written in the
     *                             file mapped against the column names
     * @param tableName            This contains the name of the table
     * @param append               This denotes whether the file needs to be
     *                             overwritten or appended
     */
    public static void writeTableDataToFile(Map<String, String> valuesToSetInColumns, String tableName,
            Boolean append) {
        try {
            File fileObj = new File(tableName + ".table.txt");
            if (fileObj.createNewFile()) {
                isNewFile = true;
                System.out.println("File created: " + fileObj.getName());
            } else {
                isNewFile = false;
            }

            FileWriter fileWriter = new FileWriter(tableName + ".table.txt",
                    append);

            String rowData = new String();
            for (Map.Entry<String, String> pair : valuesToSetInColumns.entrySet()) {
                rowData = rowData.concat(pair.getKey() + "=" + pair.getValue() + "--");
            }
            rowData = rowData.substring(0, rowData.length() - 2);
            fileWriter.write(EncryptionDecryptionAES.encryptDecrypt(rowData, "encrypt"));
            fileWriter.write("\n");
            fileWriter.close();

        } catch (Exception e) {
            System.out.println("Error writing data to the file!");
        }
    }

    /**
     * This method writes the entire data to the file
     * 
     * @param tableData This contains the map of entire table data to be written in
     *                  the file
     * @param tableName This contains the name of the table
     */
    public static void writeEntireDataToFile(Map<String, Map<String, String>> tableData, String tableName) {
        try {
            File fileObj = new File(tableName + ".table.txt");
            if (fileObj.createNewFile()) {
                isNewFile = true;
                System.out.println("File created: " + fileObj.getName());
            } else {
                isNewFile = false;
            }

            FileWriter fileWriter = new FileWriter(tableName + ".table.txt",
                    false);

            if (!tableData.isEmpty()) {
                for (Map.Entry<String, Map<String, String>> pair : tableData.entrySet()) {
                    Map<String, String> tempPair = pair.getValue();
                    String rowData = new String();
                    for (Map.Entry<String, String> innerpair : tempPair.entrySet()) {
                        rowData = rowData.concat(innerpair.getKey() + "=" + innerpair.getValue() + "--");
                    }
                    rowData = rowData.substring(0, rowData.length() - 2);
                    fileWriter.write(EncryptionDecryptionAES.encryptDecrypt(rowData, "encrypt"));
                    fileWriter.write("\n");
                }
            } else {
                fileWriter.write("");
            }
            fileWriter.close();

        } catch (Exception e) {
            System.out.println("Error writing data to the file!");
        }
    }
}
