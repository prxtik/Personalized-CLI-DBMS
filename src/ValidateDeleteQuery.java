import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ValidateDeleteQuery {
    public static Boolean isMalformedQuery = false;
    public static String logicalOperator = new String();
    public static String tableName = new String();
    public static String primaryKey = new String();
    public static Map<Integer, String> columns = new LinkedHashMap<Integer, String>();
    public static Map<Integer, List<String>> conditions = new LinkedHashMap<Integer, List<String>>();
    public static Map<String, Map<String, String>> filteredTableData = new LinkedHashMap<String, Map<String, String>>();
    public static Map<String, List<String>> tableMetaData = new LinkedHashMap<String, List<String>>();
    public static Map<String, Map<String, String>> tableData = new LinkedHashMap<String, Map<String, String>>();

    /**
     * This method performs the pre checks of the query
     * 
     * @param queryMap This contains all the parts of the query separated by spaces
     *                 in a map
     * @param query    This contains the entire query as input by the end user in
     *                 lowercase
     */
    public static void preCheckDeleteQuery(Map<Integer, String> queryMap, String query) {
        if (!queryMap.get(0).equals("delete") && !queryMap.get(1).equals("from")) {
            isMalformedQuery = true;
            resetGlobalVariables();
            System.out.println("Malformed query");
            DetermineQueryType.getQuery();
        } else if (queryMap.get(0).equals("delete") && queryMap.containsKey(1) && queryMap.get(1).equals("from")) {
            decomposeDeleteQuery(queryMap, query);
        } else {
            isMalformedQuery = true;
            resetGlobalVariables();
            System.out.println("Malformed query");
            DetermineQueryType.getQuery();
        }
    }

    /**
     * This method decomposes the delete query, validates the input query and
     * performs the delete operation on the data read from the file
     * 
     * @param queryMap This contains all the parts of the query separated by spaces
     *                 in a map
     * @param query    This contains the entire query as input by the end user in
     *                 lowercase
     */
    public static void decomposeDeleteQuery(Map<Integer, String> queryMap, String query) {
        Map<String, Map<String, List<String>>> entireTableMetaData = new LinkedHashMap<String, Map<String, List<String>>>();

        try {
            for (Map.Entry<Integer, String> pair : queryMap.entrySet()) {
                if (pair.getValue().equals("from")) {
                    if (!queryMap.get(pair.getKey() - 1).equals("delete") || queryMap.get(pair.getKey() + 1) == null) {
                        System.out.println("Malformed query");
                        isMalformedQuery = true;
                        resetGlobalVariables();
                        DetermineQueryType.getQuery();
                        break;
                    } else if (queryMap.get(pair.getKey() + 1) != null) {
                        tableName = queryMap.get(pair.getKey() + 1);
                    }
                }
                if (tableName != "" && pair.getValue().equals(tableName)
                        && !queryMap.containsKey(pair.getKey() + 1)) {
                    // delete all rows
                    if (!ReadDataFromFile.readAllTablesPresent().isEmpty()
                            && ReadDataFromFile.readAllTablesPresent().contains(tableName)) {
                        DetermineQueryType.logCorrectQuery(query, false);
                        FileWriter myWriter = new FileWriter(tableName + ".table.txt");
                        myWriter.close();
                    }
                    break;
                }
                if (pair.getValue().equals("where") && !queryMap.containsKey(pair.getKey() + 1)) {
                    System.out.println("Malformed query");
                    isMalformedQuery = true;
                    resetGlobalVariables();
                    DetermineQueryType.getQuery();
                    break;
                }
            }

            if (!isMalformedQuery) {
                if (!ReadDataFromFile.readAllTablesPresent().isEmpty()
                        && ReadDataFromFile.readAllTablesPresent().contains(tableName)) {
                    ReadDataFromFile readObj = new ReadDataFromFile();
                    entireTableMetaData = readObj.readTableMdt(tableName);
                    for (Map.Entry<String, Map<String, List<String>>> tableMdt : entireTableMetaData.entrySet()) {
                        tableMetaData = tableMdt.getValue();
                        primaryKey = tableMdt.getKey();
                    }
                    ReadDataFromFile readDataObj = new ReadDataFromFile();
                    tableData = readDataObj.readData(tableName, primaryKey);

                    if (query.contains("where")) {
                        ValidateUpdateQuery updateObj = new ValidateUpdateQuery();
                        if (updateObj.validateWhereCondition(query.split("where")[1].trim(), tableMetaData)) {
                            columns.put(0, "*");
                            conditions = updateObj.getConditions(query.split("where")[1].trim(), tableMetaData);
                            getLogicalOperator(query.split("where")[1].trim());
                            fetchData(query);
                        } else {
                            resetGlobalVariables();
                            System.out.println("Malformed query");
                            DetermineQueryType.getQuery();
                        }
                    }
                } else {
                    resetGlobalVariables();
                    System.out.println("Malformed query: Table does not exist");
                    DetermineQueryType.getQuery();
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while executing the query!");
            resetGlobalVariables();
            DetermineQueryType.getQuery();
        }
    }

    /**
     * This method get the logical operator from the where clause of the query
     * 
     * @param whereCondition This contains a where clause along with conditions
     */
    public static void getLogicalOperator(String whereCondition) {
        if (whereCondition.contains("and")) {
            if (whereCondition.split("\\s[a][n][d]\\s").length == 2) {
                logicalOperator = "and";
            }
        }
        if (whereCondition.contains("or")) {
            if (whereCondition.split("\\s[o][r]\\s").length == 2) {
                logicalOperator = "or";
            }
        }
    }

    /**
     * This method fetches the data from the table mentioned and performs the delete
     * operation
     * 
     * @param query This contains the entire query as input by the end user in
     *              lowercase
     */
    public static void fetchData(String query) {
        FetchRecords fetchObj = new FetchRecords();
        filteredTableData = fetchObj.getRows(tableName, columns, conditions,
                logicalOperator);

        tableData.entrySet().removeAll(filteredTableData.entrySet());
        try {

            DetermineQueryType.logCorrectQuery(query, false);
            WriteDataToFile writeObj = new WriteDataToFile();
            writeObj.writeEntireDataToFile(tableData, tableName);
            System.out.println("Deleted row(s) successfully!");
            resetGlobalVariables();
            UserManagement.exitSession();

        } catch (Exception e) {
            System.out.println("Error occurred while executing the query!");
            DetermineQueryType.getQuery();
        }
    }

    /*
     * This method resets the global variables
     */
    public static void resetGlobalVariables() {
        isMalformedQuery = false;
        logicalOperator = new String();
        tableName = new String();
        primaryKey = new String();
        columns.clear();
        conditions.clear();
        filteredTableData.clear();
        tableMetaData.clear();
        tableData.clear();
    }
}
