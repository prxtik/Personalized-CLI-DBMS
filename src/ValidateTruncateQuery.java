import java.io.FileWriter;
import java.util.Map;

public class ValidateTruncateQuery {

    /**
     * This method performs the pre checks of the query
     * 
     * @param queryMap This contains all the parts of the query separated
     *                 by spaces in a map
     * @param query    This contains the entire query as input by the end
     *                 user in lowercase
     */
    public static void preCheckTruncateQuery(Map<Integer, String> queryMap, String query) {
        if (queryMap.get(0).equals("truncate")
                && queryMap.get(1).equals("table")
                && (!queryMap.containsKey(2)
                        || !queryMap.get(2).matches("^[a-zA-Z]*$"))) {
            System.out.println("Malformed query: please enter a valid table name");
            DetermineQueryType.getQuery();
        } else {
            truncateTable(queryMap.get(2).trim(), query);
        }
    }

    /**
     * This method truncates the table data by removing all the data from the table
     * file
     * 
     * @param tableName This contains the name of the table
     * @param query     This contains the entire query as input by the end
     *                  user in lowercase
     */
    public static void truncateTable(String tableName, String query) {
        try {
            if (!ReadDataFromFile.readAllTablesPresent().isEmpty()
                    && ReadDataFromFile.readAllTablesPresent().contains(tableName)) {
                DetermineQueryType.logCorrectQuery(query, false);
                FileWriter myWriter = new FileWriter(tableName + ".table.txt");
                myWriter.close();
                System.out.println("Truncated table successfully!");
                UserManagement.exitSession();

            } else {
                System.out.println("Table does not exist.");
                DetermineQueryType.getQuery();
            }
        } catch (Exception e) {
            System.out.println("Error occurred while executing the query!");
            DetermineQueryType.getQuery();
        }
    }
}
