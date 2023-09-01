import java.io.File;
import java.util.Map;

public class ValidateDropDBQuery {

    /**
     * This method performs the pre checks of the query
     * 
     * @param queryMap This contains all the parts of the query separated by spaces
     *                 in a map
     * @param query    This contains the entire query as input by the end user in
     *                 lowercase
     */
    public static void preCheckDropDBQuery(Map<Integer, String> queryMap, String query) {
        if (queryMap.get(0).equals("drop")
                && queryMap.get(1).equals("database")
                && (!queryMap.containsKey(2)
                        || !queryMap.get(2).matches("^[a-zA-Z]*$"))) {
            System.out.println("Malformed query: please enter a valid database name");
            DetermineQueryType.getQuery();
        } else {
            dropDatabase(queryMap.get(2).trim(), query);
        }
    }

    /**
     * This method drops the database from the directory
     * 
     * @param dbName This contains the name of the database
     * @param query  This contains the entire query as input by the end user in
     *               lowercase
     */
    public static void dropDatabase(String dbName, String query) {
        try {
            if (ReadDataFromFile.readDatabaseEnvFile().equals(dbName)) {
                File myObj = new File("system.env");
                if (myObj.delete()) {
                    DetermineQueryType.logCorrectQuery(query, false);
                    DeleteFiles deleteFileObj = new DeleteFiles();
                    deleteFileObj.deleteTablesFiles();
                    System.out.println("Deleted the file: " + myObj.getName());
                    UserManagement.exitSession();
                } else {
                    System.out.println("Failed to delete the file.");
                    DetermineQueryType.getQuery();
                }
            } else {
                System.out.println("Database does not exist.");
                DetermineQueryType.getQuery();
            }
        } catch (Exception e) {
            System.out.println("Error occurred while executing the query!");
            DetermineQueryType.getQuery();
        }
    }
}
