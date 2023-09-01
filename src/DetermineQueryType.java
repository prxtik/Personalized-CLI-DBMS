import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class DetermineQueryType {
    public static Boolean isMalformedQuery = false;

    /*
     * This method is used to get the user input query and determine the type of the
     * query and route to the correct class and the subsequent methods
     */
    public static void getQuery() {
        System.out.println("Enter your query:");
        Scanner scanQuery = new Scanner(System.in);
        String query = scanQuery.nextLine();
        String caseSensitiveQuery = query;
        query = query.toLowerCase().trim();

        Map<Integer, String> queryMap = new LinkedHashMap<Integer, String>();
        if (!query.endsWith(";")) {
            System.out.println("Malformed query");
            getQuery();
        } else {
            query = query.substring(0, query.length() - 1);
            String[] queryArr = query.split(" ");

            for (Integer i = 0; i < queryArr.length; i++) {
                queryMap.put(i, queryArr[i]);
            }

            String queryType = checkQueryType(queryMap, query);
            if (queryType.equals("create database")) {
                ValidateCreateDBQuery createDBObj = new ValidateCreateDBQuery();
                createDBObj.preCheckCreateDBQuery(queryMap, query);
            } else if (queryType.equals("select")) {
                ValidateSelectQuery selectObj = new ValidateSelectQuery();
                selectObj.preCheckSelectQuery(queryMap, query);
            } else if (queryType.equals("create")) {
                ValidateCreateQuery createObj = new ValidateCreateQuery();
                createObj.preCheckCreateQuery(queryMap, query);
            } else if (queryType.equals("insert")) {
                ValidateInsertQuery insertObj = new ValidateInsertQuery();
                insertObj.preCheckInsertQuery(queryMap, query, caseSensitiveQuery);
            } else if (queryType.equals("drop database")) {
                ValidateDropDBQuery dropDBObj = new ValidateDropDBQuery();
                dropDBObj.preCheckDropDBQuery(queryMap, query);
            } else if (queryType.equals("drop")) {
                ValidateDropQuery dropObj = new ValidateDropQuery();
                dropObj.preCheckDropQuery(queryMap, query);
            } else if (queryType.equals("truncate")) {
                ValidateTruncateQuery truncObj = new ValidateTruncateQuery();
                truncObj.preCheckTruncateQuery(queryMap, query);
            } else if (queryType.equals("update")) {
                ValidateUpdateQuery updateObj = new ValidateUpdateQuery();
                updateObj.preCheckUpdateQuery(queryMap, query, caseSensitiveQuery);
            } else if (queryType.equals("delete")) {
                ValidateDeleteQuery deleteObj = new ValidateDeleteQuery();
                deleteObj.preCheckDeleteQuery(queryMap, query);
            } else {
                System.out.println("Query not recognized.");
            }
        }

    }

    /**
     * This method checks the entry point of the query and identifies the type of
     * the query and return that type
     * 
     * @param queryMap This contains the query which is splitted by space and mapped
     *                 serially
     * @param query    This is the original query converted to lowercase as input
     *                 from the user
     * @return String This will return the identified type of the query to perform
     *         further actions
     */
    public static String checkQueryType(Map<Integer, String> queryMap, String query) {
        String queryType = new String();

        if ((queryMap.get(0).equals("create")) && (queryMap.get(1).equals("database"))) {
            queryType = "create database";
        } else if (queryMap.get(0).equals("create")) {
            queryType = "create";
        } else if ((queryMap.get(0).equals("insert"))) {
            queryType = "insert";
        } else if ((queryMap.get(0).equals("select"))) {
            queryType = "select";
        } else if ((queryMap.get(0).equals("update"))) {
            queryType = "update";
        } else if ((queryMap.get(0).equals("delete"))) {
            queryType = "delete";
        } else if ((queryMap.get(0).equals("drop") && (queryMap.get(1).equals("database")))) {
            queryType = "drop database";
        } else if ((queryMap.get(0).equals("drop"))) {
            queryType = "drop";
        } else if ((queryMap.get(0).equals("truncate"))) {
            queryType = "truncate";
        }

        return queryType;
    }

    /**
     * This method logs the correct input query to the user activity log
     * 
     * @param query           This contains the query from the user to be logged in
     *                        the user activity log
     * @param isLastOperation This denotes whether this is the last operation for
     *                        the logged in user
     */
    public static void logCorrectQuery(String query, Boolean isLastOperation) {
        Map<String, String> logMap = new LinkedHashMap<String, String>();
        logMap.put("operation", "queryExecution");
        logMap.put("query", query);
        logMap.put("queryTimeStamp", UserManagement.getCurrentDateTime());
        UserManagement.logUserActivity(logMap, false);
    }
}
