import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ValidateInsertQuery {
    public static Boolean isMalformedQuery = false;
    public static String tableName = new String();
    public static String primaryKey = new String();
    public static String[] columnNames = new String[] {};
    public static List<String> formattedColumnValues = new LinkedList<String>();
    public static Map<String, List<String>> tableMetaData = new LinkedHashMap<String, List<String>>();
    public static Map<String, Map<String, String>> tableData = new LinkedHashMap<String, Map<String, String>>();

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
    public static void preCheckInsertQuery(Map<Integer, String> queryMap, String query, String caseSensitiveQuery) {
        if (!queryMap.get(0).equals("insert")) {
            isMalformedQuery = true;
            System.out.println("Malformed query");
            resetGlobalVariables();
            DetermineQueryType.getQuery();
        } else if (queryMap.get(0).equals("insert") && queryMap.get(1) != null) {
            decomposeInsertQuery(queryMap, query, caseSensitiveQuery);
        }
    }

    /**
     * This method decomposes the insert query, validates it and gets the key
     * elements
     * 
     * @param queryMap           This contains all the parts of the query separated
     *                           by spaces in a map
     * @param query              This contains the entire query as input by the end
     *                           user in lowercase
     * @param caseSensitiveQuery This contains the entire query as input by the end
     *                           user maintaining the cases
     */
    public static void decomposeInsertQuery(Map<Integer, String> queryMap, String query, String caseSensitiveQuery) {
        Map<String, Map<String, List<String>>> entireTableMetaData = new LinkedHashMap<String, Map<String, List<String>>>();

        for (Map.Entry<Integer, String> pair : queryMap.entrySet()) {
            if (pair.getValue().equals("into")) {
                if (!queryMap.get(pair.getKey() - 1).equals("insert") || queryMap.get(pair.getKey() + 1) == null) {
                    System.out.println("Malformed query");
                    resetGlobalVariables();
                    DetermineQueryType.getQuery();
                    break;
                } else if (queryMap.get(pair.getKey() + 1) != null) {
                    tableName = queryMap.get(pair.getKey() + 1);
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

            String[] columnNamesOrValues = Pattern.compile(tableName, Pattern.CASE_INSENSITIVE)
                    .split(caseSensitiveQuery);
            String[] columnValues = new String[] {};

            if (columnNamesOrValues[1].toLowerCase().trim().startsWith("values")) {
                String[] temp = Pattern.compile("values", Pattern.CASE_INSENSITIVE)
                        .split(columnNamesOrValues[1].trim());
                columnValues = temp[1].substring(temp[1].indexOf("(") + 1, temp[1].indexOf(")")).trim().split(",");
                setColumnsFromMdt();
                extractColumnValues(columnValues);
                checkIfColumnHasValue(caseSensitiveQuery);
            } else {
                String[] temp = Pattern.compile("values", Pattern.CASE_INSENSITIVE).split(columnNamesOrValues[1]);
                columnNames = temp[0].substring(temp[0].indexOf("(") + 1, temp[0].indexOf(")")).trim().toLowerCase()
                        .split(",");
                columnValues = temp[1].substring(temp[1].indexOf("(") + 1, temp[1].indexOf(")")).trim().split(",");
                extractColumnValues(columnValues);
                checkIfColumnHasValue(caseSensitiveQuery);
            }
        } else {
            System.out.println("The " + tableName + " does not exist!");
            resetGlobalVariables();
            DetermineQueryType.getQuery();
        }

    }

    /**
     * This method extracts the column values from the set condition of the insert
     * query
     * 
     * @param columnValues This contains the array of column names
     */
    public static void extractColumnValues(String[] columnValues) {
        Pattern patternForQuote = Pattern.compile("[\"'](.+)[\"']");

        for (int i = 0; i < columnValues.length; i++) {
            java.util.regex.Matcher matcher1 = patternForQuote.matcher(columnValues[i].trim());
            String tempFormatting = new String();
            if (matcher1.find()) {
                tempFormatting = matcher1.group(1).trim();
            } else {
                tempFormatting = columnValues[i].trim();
            }
            formattedColumnValues
                    .add(tempFormatting);
        }

    }

    /*
     * This method gets the name of the columns from the table metadata
     */
    public static void setColumnsFromMdt() {
        columnNames = tableMetaData.keySet().toArray(new String[tableMetaData.size()]);
    }

    /**
     * @param query This contains the entire query as input by the end
     *              user in lowercase
     */
    public static void checkIfColumnHasValue(String query) {
        if (columnNames.length != formattedColumnValues.size()) {
            System.out.println("Please enter all the values for all the columns mentioned.");
            resetGlobalVariables();
            DetermineQueryType.getQuery();
        } else {
            checkIfColumnsExist(query);
        }
    }

    /**
     * This method check if the columns mentioned in the set and where clause of the
     * insert query actually exist in the table metadata
     * 
     * @param query This contains the entire query as input by the end
     *              user in lowercase
     */
    public static void checkIfColumnsExist(String query) {
        for (int i = 0; i < columnNames.length; i++) {
            if (!tableMetaData.keySet().contains(columnNames[i].trim())) {
                System.out.println(columnNames[i] + " does not exist for given table.");
                resetGlobalVariables();
                DetermineQueryType.getQuery();
                break;
            }
        }
        checkAndAddData(query);
    }

    /**
     * This methods check the data type and character length constraints of the
     * attributes mentioned in the table metadata and also check that primary key
     * should be unique and if valid then adds the values to be inserted
     * 
     * @param query This contains the entire query as input by the end
     *              user in lowercase
     */
    public static void checkAndAddData(String query) {
        Map<String, String> valuesToSet = new LinkedHashMap<String, String>();
        Pattern patternNumber = Pattern.compile("-?\\d+(\\.\\d+)?");
        Pattern patternDate = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
        Boolean flagToBreak = false;
        try {
            for (int i = 0; i < columnNames.length; i++) {
                if (!flagToBreak) {
                    for (Map.Entry<String, List<String>> pair : tableMetaData.entrySet()) {
                        if (!flagToBreak) {
                            if (columnNames[i].trim().equalsIgnoreCase(pair.getValue().get(0))) {
                                String constr = pair.getValue().get(1);
                                if ((constr.contains("int") && formattedColumnValues.get(i) != null
                                        && patternNumber.matcher(formattedColumnValues.get(i)).matches())
                                        || (constr.contains("date")
                                                && patternDate.matcher(formattedColumnValues.get(i)).matches())
                                        || (constr.contains("varchar"))) {
                                    if (constr.contains("(")) {
                                        Integer constraint = Integer
                                                .parseInt(
                                                        constr.substring(constr.indexOf("(") + 1, constr.indexOf(")")));
                                        if (formattedColumnValues.get(i).length() <= constraint) {
                                            valuesToSet.put(pair.getValue().get(0), formattedColumnValues.get(i));
                                        } else {
                                            System.out
                                                    .println("Data exceeding constraint for column " + columnNames[i]);
                                            valuesToSet.clear();
                                            flagToBreak = true;
                                            resetGlobalVariables();
                                            DetermineQueryType.getQuery();
                                            break;
                                        }
                                    } else {
                                        valuesToSet.put(pair.getValue().get(0), formattedColumnValues.get(i));
                                    }
                                } else {
                                    System.out.println("Datatype mismatch for column " + columnNames[i]);
                                    valuesToSet.clear();
                                    flagToBreak = true;
                                    resetGlobalVariables();
                                    DetermineQueryType.getQuery();
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            if (!valuesToSet.isEmpty()) {
                for (Map.Entry<String, List<String>> pair : tableMetaData.entrySet()) {
                    if (!tableData.isEmpty() && pair.getKey().equals(primaryKey)
                            && tableData.containsKey(valuesToSet.get(primaryKey))) {
                        System.out.println("Primary Key should be unique, the entered value already exists");
                        valuesToSet.clear();
                        resetGlobalVariables();
                        DetermineQueryType.getQuery();
                        break;
                    }
                    if (pair.getValue().get(2).equals("notnull")
                            && valuesToSet.containsKey(pair.getValue().get(0))
                            && (valuesToSet.get(pair.getValue().get(0)).equals("")
                                    || valuesToSet.get(pair.getValue().get(0)).equals("''")
                                    || valuesToSet.get(pair.getValue().get(0)).equals("' '"))) {
                        System.out.println("Value for " + pair.getValue().get(0) + " cannot be null");
                        valuesToSet.clear();
                        resetGlobalVariables();
                        DetermineQueryType.getQuery();
                        break;
                    } else if (pair.getValue().get(2).equals("null")
                            && valuesToSet.containsKey(pair.getValue().get(0))
                            && (valuesToSet.get(pair.getValue().get(0)).equals("")
                                    || valuesToSet.get(pair.getValue().get(0)).equals("''")
                                    || valuesToSet.get(pair.getValue().get(0)).equals("' '"))) {
                        valuesToSet.put(pair.getValue().get(0), "null");

                    } else if (pair.getValue().get(2).equals("null")
                            && !valuesToSet.containsKey(pair.getValue().get(0))) {
                        valuesToSet.put(pair.getValue().get(0), "null");
                    }
                }
            }

            if (!valuesToSet.isEmpty()) {
                DetermineQueryType.logCorrectQuery(query, false);
                WriteDataToFile writeObj = new WriteDataToFile();
                writeObj.writeTableDataToFile(valuesToSet, tableName, true);
                System.out.println("Inserted row(s) successfully!");
                formattedColumnValues.clear();
                resetGlobalVariables();
                UserManagement.exitSession();
            }
        } catch (Exception e) {
            System.out.println("Error occurred while executing the query!");
            resetGlobalVariables();
            DetermineQueryType.getQuery();
        }

    }

    /*
     * This method resets the global variables
     */
    public static void resetGlobalVariables() {
        isMalformedQuery = false;
        tableName = new String();
        primaryKey = new String();
        columnNames = new String[] {};
        formattedColumnValues.clear();
        tableMetaData.clear();
        tableData.clear();
    }
}
