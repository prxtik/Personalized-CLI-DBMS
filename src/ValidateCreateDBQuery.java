import java.io.File;
import java.io.FileWriter;
import java.util.Map;

public class ValidateCreateDBQuery {

    /**
     * This method performs the pre checks of the query
     * 
     * @param queryMap This contains all the parts of the query separated by spaces
     *                 in a map
     * @param query    This contains the entire query as input by the end user in
     *                 lowercase
     */
    public static void preCheckCreateDBQuery(Map<Integer, String> queryMap, String query) {
        if (queryMap.get(0).equals("create")
                && queryMap.get(1).equals("database")
                && (!queryMap.containsKey(2)
                        || !queryMap.get(2).matches("^[a-zA-Z]*$"))) {
            System.out.println("Malformed query: please enter a valid database name");
            DetermineQueryType.getQuery();
        } else {
            createDatabase(queryMap.get(2).trim(), query);
        }
    }

    /**
     * This method creates the database if not already present
     * 
     * @param dbName This contains the name of the database
     * @param query  This contains the entire query as input by the end user in
     *               lowercase
     */
    public static void createDatabase(String dbName, String query) {
        try {
            if (ReadDataFromFile.readDatabaseEnvFile().equals("")) {
                DetermineQueryType.logCorrectQuery(query, false);
                File fileObj2 = new File("./system.env");
                FileWriter fileWriter = new FileWriter("system.env",
                        true);
                fileWriter.write(dbName);
                fileWriter.close();

                System.out.println(dbName + " database is created successfully!");
                UserManagement.exitSession();
            } else {
                System.out.println("One Database already exists, cannot create a new one.");
                DetermineQueryType.getQuery();
            }
        } catch (Exception e) {
            System.out.println("Error occurred while executing the query!");
            DetermineQueryType.getQuery();
        }
    }
}