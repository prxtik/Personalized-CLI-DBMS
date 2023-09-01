import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateSelectQuery {
    public static String tableName = new String();
    public static String logicalOperator = new String();
    public static Boolean isMalformedQuery = false;
    public static Map<Integer, List<String>> conditions = new LinkedHashMap<Integer, List<String>>();
    public static Map<Integer, String> columns = new LinkedHashMap<Integer, String>();

    /**
     * This method performs the pre checks of the query
     * 
     * @param queryMap This contains all the parts of the query separated
     *                 by spaces in a map
     * @param query    This contains the entire query as input by the end
     *                 user in lowercase
     */
    public static void preCheckSelectQuery(Map<Integer, String> queryMap, String query) {
        try {
            if (!queryMap.get(0).equals("select")) {
                isMalformedQuery = true;
                System.out.println("Malformed query");
                resetGlobalVariables();
                DetermineQueryType.getQuery();
            } else if (queryMap.get(0).equals("select") && queryMap.get(1) != null
                    && !decomposeSelectQuery(queryMap)) {
                isMalformedQuery = true;
                System.out.println("Malformed query");
                resetGlobalVariables();
                DetermineQueryType.getQuery();
            } else {
                Map<String, Map<String, List<String>>> entireTableMetaData = new LinkedHashMap<String, Map<String, List<String>>>();
                Map<String, List<String>> tableMetaData = new LinkedHashMap<String, List<String>>();

                getTableName(query);

                if (!ReadDataFromFile.readAllTablesPresent().isEmpty()
                        && ReadDataFromFile.readAllTablesPresent().contains(tableName)) {
                    ReadDataFromFile readObj = new ReadDataFromFile();
                    entireTableMetaData = readObj.readTableMdt(tableName);
                    for (Map.Entry<String, Map<String, List<String>>> tableMdt : entireTableMetaData.entrySet()) {
                        tableMetaData = tableMdt.getValue();
                    }
                    getColumns(query);
                    for (int i = 0; i < columns.size(); i++) {
                        if (!columns.get(i).equals("*") && !tableMetaData.keySet().contains(columns.get(i).trim())) {
                            System.out.println(columns.get(i) + " does not exist for given table.");
                            isMalformedQuery = true;
                            resetGlobalVariables();
                            DetermineQueryType.getQuery();
                            break;
                        }
                    }
                    if (query.contains("where") && !validateWhereCondition(query.split("where")[1].trim())) {
                        isMalformedQuery = true;
                        System.out.println("Malformed query");
                        resetGlobalVariables();
                        DetermineQueryType.getQuery();
                    }

                    if (!columns.isEmpty() && tableName != "" && !isMalformedQuery) {
                        FetchRecords fetchObj = new FetchRecords();
                        Map<String, Map<String, String>> filteredData = fetchObj.getRows(tableName, columns, conditions,
                                logicalOperator);
                        DetermineQueryType.logCorrectQuery(query, false);
                        if (!filteredData.isEmpty()) {
                            fetchObj.displayOutput(filteredData);
                            resetGlobalVariables();
                        } else {
                            System.out.println("No records found");
                        }

                        UserManagement.exitSession();
                    } else {
                        System.out.println("Malformed query");
                        resetGlobalVariables();
                        DetermineQueryType.getQuery();
                    }
                } else {
                    System.out.println("The " + tableName + " does not exist!");
                    resetGlobalVariables();
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
     * This method decomposes the select query
     * 
     * @param queryMap This contains all the parts of the query separated
     *                 by spaces in a map
     * @return Boolean This will return true if the query is valid
     */
    public static Boolean decomposeSelectQuery(Map<Integer, String> queryMap) {
        Pattern regexBeforeFrom = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^()%!-]");
        Pattern regexBeforeWhere = Pattern.compile("[$&+,:;\\\\?@#|/'<>.^*()%!-]");
        Boolean isQueryValid = true;

        for (Map.Entry<Integer, String> pair : queryMap.entrySet()) {
            if (pair.getKey() == 0 && !pair.getValue().equals("select")) {
                isQueryValid = false;
                break;
            } else if (pair.getValue().equals("from")
                    && (regexBeforeFrom.matcher(queryMap.get(pair.getKey() - 1)).find()
                            || queryMap.get(pair.getKey() - 1).contains("select")
                            || queryMap.get(pair.getKey() + 1) == null)) {
                isQueryValid = false;
                break;
            } else if (pair.getValue().equals("where")
                    && (regexBeforeWhere.matcher(queryMap.get(pair.getKey() - 1)).find()
                            || queryMap.get(pair.getKey() - 1).contains("select")
                            || queryMap.get(pair.getKey() - 1).contains("from")
                            || queryMap.get(pair.getKey() + 1) == null)) {
                isQueryValid = false;
                break;
            }
        }

        return isQueryValid;
    }

    /**
     * This method validates the conditions mentioned in the where clause of the
     * select query
     * 
     * @param whereCondition This contains where clause of the query along with the
     *                       conditions
     * @return Boolean This will return if the conditions present in the where
     *         clause are valid
     */
    public static Boolean validateWhereCondition(String whereCondition) {
        Boolean isWhereConditionValid = false;
        String regex = "(.*?)(>=|<=|<>|>|<|=|!=)(.*)";
        String[] splitConditions = new String[] {};

        if (whereCondition.contains("and")) {
            splitConditions = whereCondition.trim().split("\\s[a][n][d]\\s");
            logicalOperator = "and";
        }
        if (whereCondition.contains("or")) {
            splitConditions = whereCondition.trim().split("\\s[o][r]\\s");
            logicalOperator = "or";
        }
        if (splitConditions != null && splitConditions.length == 2) {
            for (int i = 0; i < splitConditions.length; i++) {
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(splitConditions[i]);
                List<String> values = new ArrayList<String>();

                if (matcher.find()) {
                    values.add(matcher.group(2).trim());
                    if (matcher.group(1).isEmpty() || matcher.group(3).isEmpty()) {
                        isWhereConditionValid = false;
                        break;
                    } else {
                        String leftOperator = matcher.group(1).trim();
                        String rightOperator = matcher.group(3).trim();

                        if (leftOperator.contains("\"") || leftOperator.contains("\'")) {
                            values.add(leftOperator.substring(1, leftOperator.length() - 1));
                        } else {
                            values.add(leftOperator);
                        }
                        if (rightOperator.contains("\"") || rightOperator.contains("\'")) {
                            values.add(rightOperator.substring(1, rightOperator.length() - 1));
                        } else {
                            values.add(rightOperator.trim());
                        }
                        conditions.put(i, values);
                        if (i == splitConditions.length - 1) {
                            isWhereConditionValid = true;
                        }
                    }
                } else {
                    break;
                }
            }
        } else if (whereCondition.trim().split(">=|>|<=|<|=|!=").length == 2) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(whereCondition);
            List<String> values = new ArrayList<String>();

            if (matcher.find()) {
                values.add(matcher.group(2).trim());
                if (matcher.group(1).isEmpty() || matcher.group(3).isEmpty()) {
                    isWhereConditionValid = false;
                } else {
                    String leftOperator = matcher.group(1).trim();
                    String rightOperator = matcher.group(3).trim();

                    if (leftOperator.contains("\"") || leftOperator.contains("\'")) {
                        values.add(leftOperator.substring(1, leftOperator.length() - 1));
                    } else {
                        values.add(leftOperator);
                    }
                    if (rightOperator.contains("\"") || rightOperator.contains("\'")) {
                        values.add(rightOperator.substring(1, rightOperator.length() - 1));
                    } else {
                        values.add(rightOperator.trim());
                    }
                    conditions.put(0, values);
                    isWhereConditionValid = true;
                }
            }
        }

        return isWhereConditionValid;
    }

    /**
     * This method gets all the columns mentioned in the select query
     * 
     * @param query This contains the entire query as input by the end
     *              user in lowercase
     */
    public static void getColumns(String query) {
        String regex = "select(.*?)from";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(query);

        if (matcher.find()) {
            String columnNames = matcher.group(1);
            if (columnNames.contains(",")) {
                String[] multipleColumns = columnNames.split(",");
                for (int i = 0; i < multipleColumns.length; i++) {
                    columns.put(i, multipleColumns[i].trim());
                }
            } else {
                columns.put(0, matcher.group(1).trim());
            }
        }

    }

    /**
     * This method gets the table name from the select query
     * 
     * @param query This contains the entire query as input by the end
     *              user in lowercase
     */
    public static void getTableName(String query) {
        query = query.concat(";");
        String regex = "from(.*?)(?!.*where);";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher1 = pattern.matcher(query);

        if (matcher1.find()) {
            if (matcher1.group(1).trim().contains(" ")) {
                tableName = matcher1.group(1).trim().split(" ")[0];
            } else {
                tableName = matcher1.group(1).trim();
            }
        }
    }

    /*
     * This method resets the global variables
     */
    public static void resetGlobalVariables() {
        tableName = new String();
        logicalOperator = new String();
        isMalformedQuery = false;
        conditions.clear();
        columns.clear();
    }

}
