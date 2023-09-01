import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUpdateQuery {
    public static Boolean isMalformedQuery = false;
    public static String logicalOperator = new String();
    public static String tableName = new String();
    public static String primaryKey = new String();
    public static Map<Integer, String> columns = new LinkedHashMap<Integer, String>();
    public static Map<Integer, List<String>> conditions = new LinkedHashMap<Integer, List<String>>();
    public static Map<Integer, List<String>> valuesSet = new LinkedHashMap<Integer, List<String>>();
    public static Map<String, List<String>> tableMetaData = new LinkedHashMap<String, List<String>>();
    public static Map<String, Map<String, String>> tableData = new LinkedHashMap<String, Map<String, String>>();
    public static Map<String, Map<String, String>> filteredTableData = new LinkedHashMap<String, Map<String, String>>();

    /**
     * This method performs the pre checks of the query
     * 
     * @param queryMap           This contains all the parts of the query separated
     *                           by spaces in a map
     * @param query              This contains the entire query as input by the end
     *                           user in lowercase
     * @param caseSensitiveQuery This contains the entire query as input by the end
     *                           user maintaining the cases
     */
    public static void preCheckUpdateQuery(Map<Integer, String> queryMap, String query, String caseSensitiveQuery) {
        if (!queryMap.get(0).equals("update")) {
            isMalformedQuery = true;
            System.out.println("Malformed query");
            resetGlobalVariables();
            DetermineQueryType.getQuery();
        } else if (queryMap.get(0).equals("update") && queryMap.get(1) != null) {
            decomposeUpdateQuery(queryMap, query, caseSensitiveQuery);
        }
    }

    /**
     * This method decomposes the update query, validates the query and if valid,
     * performs the update operation
     * 
     * @param queryMap           This contains all the parts of the query separated
     *                           by spaces in a map
     * @param query              This contains the entire query as input by the end
     *                           user in lowercase
     * @param caseSensitiveQuery This contains the entire query as input by the end
     *                           user maintaining the cases
     */
    public static void decomposeUpdateQuery(Map<Integer, String> queryMap, String query, String caseSensitiveQuery) {
        Map<String, Map<String, List<String>>> entireTableMetaData = new LinkedHashMap<String, Map<String, List<String>>>();

        if (query.contains("update") && query.contains("set") && query.contains("where")) {
            for (Map.Entry<Integer, String> pair : queryMap.entrySet()) {
                if (pair.getValue().equals("set")) {
                    if (queryMap.get(pair.getKey() - 1).equals("update") || queryMap.get(pair.getKey() + 1) == null) {
                        System.out.println("Malformed query");
                        isMalformedQuery = true;
                        resetGlobalVariables();
                        DetermineQueryType.getQuery();
                        break;
                    } else if (queryMap.get(pair.getKey() - 1) != null) {
                        tableName = queryMap.get(pair.getKey() - 1);
                    }
                } else if (pair.getValue().equals("where")) {
                    if (queryMap.get(pair.getKey() - 1).contains(",") || queryMap.get(pair.getKey() + 1) == null) {
                        System.out.println("Malformed query");
                        isMalformedQuery = true;
                        resetGlobalVariables();
                        DetermineQueryType.getQuery();
                        break;
                    }
                }
            }

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

                if (validateSetValues(caseSensitiveQuery)
                        && validateWhereCondition(query.split("where")[1].trim(), tableMetaData)) {
                    updateData(query);
                } else {
                    DetermineQueryType.getQuery();
                }
            } else {
                System.out.println("Malformed query: Table does not exist");
                resetGlobalVariables();
                DetermineQueryType.getQuery();
            }

        } else {
            System.out.println("Malformed query");
            isMalformedQuery = true;
            resetGlobalVariables();
            DetermineQueryType.getQuery();
        }
    }

    /**
     * This method validates the where condition of the where clause of the update
     * query
     * 
     * @param tableMetaData This contains the table metadata which denotes all
     *                      columns and their constraints
     * @return Boolean This will return true is the where clause is valid
     */
    public static Boolean validateWhereCondition(String whereCondition, Map<String, List<String>> tableMetaData) {
        Boolean isWhereConditionValid = false;
        String regex = "(.*?)(>=|<=|<>|>|<|=|!=)(.*)";
        String[] splitConditions = new String[] {};

        if (whereCondition.contains("and")) {
            splitConditions = whereCondition.split("\\s[a][n][d]\\s");
            logicalOperator = "and";
        }
        if (whereCondition.contains("or")) {
            splitConditions = whereCondition.split("\\s[o][r]\\s");
            logicalOperator = "or";
        }
        if (splitConditions.length == 2) {
            for (int i = 0; i < splitConditions.length; i++) {
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(splitConditions[i]);
                List<String> values = new ArrayList<String>();

                if (matcher.find()) {
                    values.add(matcher.group(2).trim());
                    String leftOperator = matcher.group(1).trim();
                    String rightOperator = matcher.group(3).trim();

                    if (!leftOperator.equals("") && !rightOperator.equals("")) {
                        String leftTemp = new String();
                        String rightTemp = new String();

                        if (leftOperator.contains("\"") || leftOperator.contains("\'")) {
                            leftTemp = leftOperator.substring(1, leftOperator.length() - 1);
                        } else {
                            leftTemp = leftOperator.trim();
                        }
                        if (checkIfColumnsExist(leftTemp, tableMetaData)) {
                            isWhereConditionValid = true;
                            values.add(leftTemp);
                        } else {
                            isWhereConditionValid = false;
                            values.clear();
                        }

                        if (rightOperator.contains("\"") || rightOperator.contains("\'")) {
                            rightTemp = rightOperator.substring(1, rightOperator.length() - 1);
                        } else {
                            rightTemp = rightOperator.trim();
                        }
                        if (rightTemp.equals("")) {
                            isWhereConditionValid = false;
                            System.out.println("Please enter a valid WHERE condition");
                            resetGlobalVariables();
                            values.clear();
                        } else {
                            values.add(rightTemp);
                        }

                        if (!values.isEmpty()) {
                            conditions.put(i, values);
                        }
                    } else {
                        isWhereConditionValid = false;
                        System.out.println("Please enter a valid WHERE condition");
                        resetGlobalVariables();
                        break;
                    }
                } else {
                    break;
                }
            }
        } else if (whereCondition.split(">=|>|<=|<|=|!=").length == 2) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(whereCondition);
            List<String> values = new ArrayList<String>();

            if (matcher.find()) {
                values.add(matcher.group(2).trim());
                String leftOperator = matcher.group(1).trim();
                String rightOperator = matcher.group(3).trim();

                if (!leftOperator.equals("") && !rightOperator.equals("")) {
                    String leftTemp = new String();
                    String rightTemp = new String();

                    if (leftOperator.contains("\"") || leftOperator.contains("\'")) {
                        leftTemp = leftOperator.substring(1, leftOperator.length() - 1);
                    } else {
                        leftTemp = leftOperator.trim();
                    }
                    if (checkIfColumnsExist(leftTemp, tableMetaData)) {
                        isWhereConditionValid = true;
                        values.add(leftTemp);
                    } else {
                        isWhereConditionValid = true;
                        values.clear();
                    }

                    if (rightOperator.contains("\"") || rightOperator.contains("\'")) {
                        rightTemp = rightOperator.substring(1, rightOperator.length() - 1);
                    } else {
                        rightTemp = rightOperator.trim();
                    }
                    if (rightTemp.equals("")) {
                        isWhereConditionValid = false;
                        System.out.println("Please enter a valid WHERE condition");
                        resetGlobalVariables();
                        values.clear();
                    } else {
                        values.add(rightTemp);
                    }

                    if (!values.isEmpty()) {
                        conditions.put(0, values);
                    }
                } else {
                    isWhereConditionValid = false;
                    System.out.println("Please enter a valid WHERE condition");
                    resetGlobalVariables();
                }
            }
        }
        return isWhereConditionValid;
    }

    /**
     * This method validates the set clause of the update query
     * 
     * @param query This contains the entire query as input by the end
     *              user in lowercase
     * @return Boolean This will return true is the values to be updated are valid
     *         based on their data type and other constraints
     */
    public static Boolean validateSetValues(String query) {
        Boolean isSetValuesValid = false;

        String regex = "set(.*?)where";

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(query);

        if (matcher.find()) {
            String valuesToSet = matcher.group(1).trim();
            if (valuesToSet.contains(",")) {
                String[] multipleColumnsToUpdate = valuesToSet.split(",");
                for (int i = 0; i < multipleColumnsToUpdate.length; i++) {
                    if (checkEachSetValue(multipleColumnsToUpdate[i].trim(), i)) {
                        isSetValuesValid = true;
                    } else {
                        isSetValuesValid = false;
                        break;
                    }
                }
            } else {
                if (!checkEachSetValue(valuesToSet.trim(), 0)) {
                    System.out.println("Malformed query");
                    resetGlobalVariables();
                    isSetValuesValid = false;
                } else {
                    isSetValuesValid = true;
                }
            }
        }
        return isSetValuesValid;
    }

    /**
     * This method validates each value in the set condition of the update query
     * 
     * @param valueSet This contains each set of column name and new value to be
     *                 updated
     * @param index    This contains the index of the value-set
     * @return Boolean This will return true if the values to be updated are valid
     *         based on data type and if the columns are present in the database
     */
    public static Boolean checkEachSetValue(String valueSet, Integer index) {
        Boolean isValueSetValid = false;
        String regex = "(.*?)(>=|<=|<>|>|<|=|!=)(.*)";

        if (valueSet.split(">=|>|<=|<|=|!=").length == 2) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(valueSet);
            List<String> values = new ArrayList<String>();

            if (matcher.find()) {
                values.add(matcher.group(2).trim());
                String leftOperator = matcher.group(1).trim();
                String rightOperator = matcher.group(3).trim();

                String leftTemp = new String();
                String rightTemp = new String();

                if (leftOperator.contains("\"") || leftOperator.contains("\'")) {
                    leftTemp = leftOperator.substring(1, leftOperator.length() - 1);
                } else {
                    leftTemp = leftOperator.trim();
                }
                if (checkIfColumnsExist(leftTemp, tableMetaData)) {
                    isValueSetValid = true;
                    values.add(leftTemp);
                } else {
                    isValueSetValid = false;
                    values.clear();
                }

                if (rightOperator.contains("\"") || rightOperator.contains("\'")) {
                    rightTemp = rightOperator.substring(1, rightOperator.length() - 1);
                } else {
                    rightTemp = rightOperator.trim();
                }
                if (checkDataTypeConstraints(leftTemp, rightTemp)) {
                    isValueSetValid = true;
                    values.add(rightTemp);
                } else {
                    isValueSetValid = false;
                    values.clear();
                }

                if (!values.isEmpty() && checkPrimaryKeyNullConstraints(leftTemp, rightTemp)) {
                    valuesSet.put(index, values);
                    isValueSetValid = true;
                } else {
                    isValueSetValid = false;
                }
            }
        }

        return isValueSetValid;
    }

    /**
     * This methods check the data type and character length constraints of the
     * attributes mentioned in the table metadata and also check that primary key
     * should be unique and if valid then adds the values to be inserted
     * 
     * @param column This contains the name of the column to identify their stored
     *               constraints
     * @param value  This contains the value against which the constraints need to
     *               be validated
     * @return Boolean This will return true if the constraints are validated
     *         successfully
     */
    public static Boolean checkDataTypeConstraints(String column, String value) {
        Boolean isCorrectDataType = false;

        Pattern patternNumber = Pattern.compile("-?\\d+(\\.\\d+)?");
        Pattern patternDate = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

        for (Map.Entry<String, List<String>> pair : tableMetaData.entrySet()) {
            if (column.trim().equalsIgnoreCase(pair.getValue().get(0))) {
                String constr = pair.getValue().get(1);
                if ((constr.contains("int") && value != null
                        && patternNumber.matcher(value).matches())
                        || (constr.contains("date")
                                && patternDate.matcher(value).matches())
                        || (constr.contains("varchar"))) {
                    if (constr.contains("(")) {
                        Integer constraint = Integer
                                .parseInt(constr.substring(constr.indexOf("(") + 1, constr.indexOf(")")));
                        if (value.length() <= constraint) {
                            isCorrectDataType = true;
                        } else {
                            System.out.println("Data exceeding constraint for column " + column);
                            resetGlobalVariables();
                            isCorrectDataType = false;
                            break;
                        }
                    } else {
                        isCorrectDataType = true;
                    }
                } else {
                    System.out.println("Datatype mismatch for column " + column);
                    resetGlobalVariables();
                    isCorrectDataType = false;
                    break;
                }
            }
        }

        return isCorrectDataType;
    }

    /**
     * This method checks the null constraints and if the primary key is unique
     * 
     * @param column This contains the name of the column to identify their stored
     *               constraints
     * @param value  This contains the value against which the constraints need to
     *               be validated
     * @return Boolean This will return true if the null constraints are validated
     *         successfully
     */
    public static Boolean checkPrimaryKeyNullConstraints(String column, String value) {
        Boolean isNullCheckValid = true;

        for (Map.Entry<String, List<String>> pair : tableMetaData.entrySet()) {
            if (!tableData.isEmpty() && pair.getKey().equals(primaryKey)
                    && tableData.containsKey(value)) {
                System.out.println("Primary Key should be unique, the entered value already exists");
                resetGlobalVariables();
                isNullCheckValid = false;
                break;
            }
            if (pair.getValue().get(2).equals("notnull")
                    && pair.getValue().get(0).equals(column)
                    && (value.equals("")
                            || value.equals("''")
                            || value.equals("' '"))) {
                System.out.println("Value for " + pair.getValue().get(0) + " cannot be null");
                resetGlobalVariables();
                isNullCheckValid = false;
                break;
            }
        }

        return isNullCheckValid;
    }

    /**
     * This method checks if the columns exist in the table metadata
     * 
     * @param tableMetaData This contains the metadata os the table along with the
     *                      constraints
     * @return Boolean This will return true if the columns exist in the table
     */
    public static Boolean checkIfColumnsExist(String columnName, Map<String, List<String>> tableMetaData) {
        Boolean isColumnExist = true;
        if (!tableMetaData.keySet().contains(columnName.trim())) {
            System.out.println(columnName + " column does not exist for given table.");
            resetGlobalVariables();
            isColumnExist = false;
        }
        return isColumnExist;
    }

    /**
     * This method performs the update operation
     * 
     * @param query This contains the entire query as input by the end
     *              user in lowercase
     */
    public static void updateData(String query) {
        try {
            if (!tableData.isEmpty()) {
                columns.put(0, "*");
                FetchRecords fetchObj = new FetchRecords();
                filteredTableData = fetchObj.getRows(tableName, columns, conditions,
                        logicalOperator);
                if (!filteredTableData.isEmpty()) {
                    for (Map.Entry<String, Map<String, String>> pair : filteredTableData.entrySet()) {
                        for (Map.Entry<Integer, List<String>> col : valuesSet.entrySet()) {
                            for (Map.Entry<String, String> innerpair : pair.getValue().entrySet()) {
                                if (innerpair.getKey().equals(col.getValue().get(1))) {
                                    Map<String, String> temp = tableData.get(pair.getKey());
                                    temp.replace(innerpair.getKey(), col.getValue().get(2));
                                    tableData.replace(pair.getKey(), pair.getValue(), temp);
                                }
                            }
                        }
                    }
                }
            }

            if (!tableData.isEmpty()) {
                DetermineQueryType.logCorrectQuery(query, false);
                WriteDataToFile writeObj = new WriteDataToFile();
                writeObj.writeEntireDataToFile(tableData, tableName);
                System.out.println("Updated row(s) successfully!");
                resetGlobalVariables();
                UserManagement.exitSession();
            }
        } catch (Exception e) {
            System.out.println("Error occurred while executing the query!");
            DetermineQueryType.getQuery();
        }
    }

    /**
     * This method validates the where clause of the query
     * 
     * @param whereCondition This contains the where clause of the query
     * @param tableMetaData  This contains the metadata of the table
     * @return Map<Integer, List<String>> This will return the map of the conditions
     *         present in the query
     */
    public static Map<Integer, List<String>> getConditions(String whereCondition,
            Map<String, List<String>> tableMetaData) {
        validateWhereCondition(whereCondition, tableMetaData);
        return conditions;
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
        valuesSet.clear();
        tableMetaData.clear();
        tableData.clear();
        filteredTableData.clear();
    }
}
