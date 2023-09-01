import java.util.Map;

public class ValidateDropQuery {

    /**
     * This method performs the pre checks of the query
     * 
     * @param queryMap This contains all the parts of the query separated by spaces
     *                 in a map
     * @param query    This contains the entire query as input by the end user in
     *                 lowercase
     */
    public static void preCheckDropQuery(Map<Integer, String> queryMap, String query) {
        if (queryMap.get(0).equals("drop")
                && queryMap.get(1).equals("table")
                && (!queryMap.containsKey(2)
                        || !queryMap.get(2).matches("^[a-zA-Z]*$"))) {
            System.out.println("Malformed query: please enter a valid table name");
            DetermineQueryType.getQuery();
        } else {
            dropTable(queryMap.get(2).trim(), query);
        }
    }

    /**
     * This method drops the table from the environment
     * 
     * @param tableName This contains the name of the table
     * @param query     This contains the entire query as input by the end user in
     *                  lowercase
     */
    public static void dropTable(String tableName, String query) {
        try {
            if (!ReadDataFromFile.readAllTablesPresent().isEmpty()
                    && ReadDataFromFile.readAllTablesPresent().contains(tableName)) {
                DetermineQueryType.logCorrectQuery(query, false);
                DeleteFiles deleteObj = new DeleteFiles();
                deleteObj.deleteSingleTableFile(tableName);
                System.out.println(tableName + " dropped successfully!");
                UserManagement.exitSession();
            } else {
                System.out.println(tableName + " does not exist.");
                DetermineQueryType.getQuery();
            }
        } catch (Exception e) {
            System.out.println("Error occurred while executing the query!");
            DetermineQueryType.getQuery();
        }
    }
}
