import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FetchRecords {

    /**
     * This method gets all the rows required by the select query
     * 
     * @param table           This is the name of the table
     * @param columns         This is the map of the columns which needs to be
     *                        filtered against
     * @param conditions      This is the map of list of conditions for which data
     *                        needs to be filtered
     * @param logicalOperator This is the logical operator such as AND, OR present
     *                        in the WHERE clause of the query
     * @return Map<String, Map<String, String>> This will return filtered data based
     *         on conditions and columns
     */
    public Map<String, Map<String, String>> getRows(String table, Map<Integer, String> columns,
            Map<Integer, List<String>> conditions,
            String logicalOperator) {
        Map<String, Map<String, String>> tableData = new LinkedHashMap<String, Map<String, String>>();
        Map<String, Map<String, String>> filteredByConditions = new LinkedHashMap<String, Map<String, String>>();
        Map<String, Map<String, String>> filteredByColumns = new LinkedHashMap<String, Map<String, String>>();

        ReadDataFromFile readObj = new ReadDataFromFile();
        tableData = readObj.readData(table, "primaryKey");
        if (!conditions.isEmpty()) {
            filteredByConditions = filterByConditions(tableData, conditions, logicalOperator);
            filteredByColumns = filterByColumns(filteredByConditions, columns);
        } else {
            filteredByColumns = filterByColumns(tableData, columns);
        }

        return filteredByColumns;
    }

    /**
     * This method filters the data from the file by the conditions mentioned in the
     * where clause of the select query
     * 
     * @param tableData       This is the data read from the file for a given table
     * @param conditions      This is the map of list of conditions for which data
     *                        needs to be filtered
     * @param logicalOperator This is the logical operator such as AND, OR present
     *                        in the WHERE clause of the query
     * @return Map<String, Map<String, String>> This will return filtered data based
     *         on conditions
     */
    public static Map<String, Map<String, String>> filterByConditions(Map<String, Map<String, String>> tableData,
            Map<Integer, List<String>> conditions, String logicalOperator) {
        Map<String, Map<String, String>> filteredData = new LinkedHashMap<String, Map<String, String>>();

        Map<String, Map<String, String>> filteredDataCondition1 = new LinkedHashMap<String, Map<String, String>>();
        Map<String, Map<String, String>> filteredDataCondition2 = new LinkedHashMap<String, Map<String, String>>();

        for (Map.Entry<String, Map<String, String>> pair : tableData.entrySet()) {
            if (conditions.get(0) != null) {
                String dataFromTable1 = pair.getValue().get(conditions.get(0).get(1));
                String dataFromCondition1 = conditions.get(0).get(2);
                String dataFromOperator1 = conditions.get(0).get(0);

                if (conditions.get(1) != null) {
                    String dataFromTable2 = pair.getValue().get(conditions.get(1).get(1));
                    String dataFromCondition2 = conditions.get(1).get(2);
                    String dataFromOperator2 = conditions.get(1).get(0);

                    filteredDataCondition1.putAll(filterSingleCondition(dataFromTable1, dataFromCondition1,
                            dataFromOperator1, pair));
                    filteredDataCondition2.putAll(filterSingleCondition(dataFromTable2, dataFromCondition2,
                            dataFromOperator2, pair));

                    if (logicalOperator.equals("and")) {
                        filteredData.putAll(intersectionResult(filteredDataCondition1, filteredDataCondition2));

                    } else if (logicalOperator.equals("or")) {
                        filteredData.putAll(unionResult(filteredDataCondition1, filteredDataCondition2));
                    }
                } else {
                    filteredData
                            .putAll(filterSingleCondition(dataFromTable1, dataFromCondition1, dataFromOperator1, pair));
                }
            }
        }
        return filteredData;
    }

    /**
     * This method filters the data from the file by a single condition mentioned in
     * the where clause of the select query
     * 
     * @param dataFromTable     This contains the entire data from the file for the
     *                          given table
     * @param dataFromCondition This condition a single condition from the WHERE
     *                          clause
     * @param dataFromOperator  This contains the operator for which it needs to be
     *                          checked against
     * @param pair              A single entrySet from the iterator
     * @return Map<String, Map<String, String>> This will return filtered data based
     *         on single condition
     */
    public static Map<String, Map<String, String>> filterSingleCondition(String dataFromTable, String dataFromCondition,
            String dataFromOperator, Map.Entry<String, Map<String, String>> pair) {
        Map<String, Map<String, String>> filteredData = new LinkedHashMap<String, Map<String, String>>();

        if (dataFromOperator.equals("=")) {
            if (dataFromTable.equalsIgnoreCase(dataFromCondition)) {
                filteredData.put(pair.getKey(), pair.getValue());
            }
        } else if (dataFromOperator.equals(">")) {
            if (Integer.parseInt(dataFromTable) > Integer.parseInt((dataFromCondition))) {
                filteredData.put(pair.getKey(), pair.getValue());
            }
        } else if (dataFromOperator.equals("<")) {
            if (Integer.parseInt(dataFromTable) < Integer.parseInt((dataFromCondition))) {
                filteredData.put(pair.getKey(), pair.getValue());
            }
        } else if (dataFromOperator.equals(">=")) {
            if (Integer.parseInt(dataFromTable) >= Integer.parseInt((dataFromCondition))) {
                filteredData.put(pair.getKey(), pair.getValue());
            }
        } else if (dataFromOperator.equals("<=")) {
            if (Integer.parseInt(dataFromTable) <= Integer.parseInt((dataFromCondition))) {
                filteredData.put(pair.getKey(), pair.getValue());
            }
        } else if (dataFromOperator.equalsIgnoreCase("!=")) {
            if (!dataFromTable.equals(dataFromCondition)) {
                filteredData.put(pair.getKey(), pair.getValue());
            }
        }

        return filteredData;
    }

    /**
     * This method performs the intersection of the results of the two conditions of
     * the where clause of the select query
     * 
     * @param data1 This contains the filtered data by first condition of the WHERE
     *              clause
     * @param data2 This contains the filtered data by second condition of the WHERE
     *              clause
     * @return Map<String, Map<String, String>> This will return the resultant data
     *         where only matching records in both the datasets are found simulating
     *         AND logical operator
     */
    public static Map<String, Map<String, String>> intersectionResult(Map<String, Map<String, String>> data1,
            Map<String, Map<String, String>> data2) {
        Map<String, Map<String, String>> intersectedData = new LinkedHashMap<String, Map<String, String>>();

        for (String key : data1.keySet()) {
            if (data2.containsKey(key)) {
                intersectedData.put(key, data1.get(key));
            }
        }

        return intersectedData;
    }

    /**
     * This method performs the union of the results of the two conditions of
     * the where clause of the select query
     * 
     * @param data1 This contains the filtered data by first condition of the WHERE
     *              clause
     * @param data2 This contains the filtered data by second condition of the WHERE
     *              clause
     * @return Map<String, Map<String, String>> This will return the resultant data
     *         where unique records in both the datasets are merged simulating
     *         OR logical operator
     */
    public static Map<String, Map<String, String>> unionResult(Map<String, Map<String, String>> data1,
            Map<String, Map<String, String>> data2) {
        data1.putAll(data2);

        return data1;
    }

    /**
     * This method filters the data from the file based on the columns specified in
     * the select query
     * 
     * @param filteredByConditions This is a map of the data filtered by conditions
     *                             present in the WHERE clause of the query
     * @param columns              This it the map of the columns entered by user in
     *                             the query
     * @return Map<String, Map<String, String>> This will return filtered data based
     *         on columns
     */
    public static Map<String, Map<String, String>> filterByColumns(
            Map<String, Map<String, String>> filteredByConditions, Map<Integer, String> columns) {
        Map<String, Map<String, String>> filteredData = new LinkedHashMap<String, Map<String, String>>();

        for (Map.Entry<String, Map<String, String>> pair : filteredByConditions.entrySet()) {
            Map<String, String> subFilteredData = new LinkedHashMap<String, String>();
            for (Map.Entry<Integer, String> column : columns.entrySet()) {
                if (pair.getValue().containsKey(column.getValue()) && !column.getValue().equals("*")) {
                    subFilteredData.put(column.getValue(), pair.getValue().get(column.getValue()));
                    filteredData.put(pair.getKey(), subFilteredData);
                } else {
                    filteredData = filteredByConditions;
                    break;
                }
            }
        }

        return filteredData;
    }

    /**
     * This method call the display method to display the output in a tabular format
     * 
     * @param filteredByColumns this is a map of the data in key and value pair
     *                          mapped to the primary key value
     */
    public static void displayOutput(
            Map<String, Map<String, String>> filteredByColumns) {
        if (!filteredByColumns.isEmpty()) {
            DisplayOutput displayObj = new DisplayOutput();
            displayObj.displayTable(filteredByColumns);
        }
    }
}
