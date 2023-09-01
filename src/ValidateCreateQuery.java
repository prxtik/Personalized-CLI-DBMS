import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ValidateCreateQuery {
    public static Boolean isMalformedQuery = false;
    public static String tableName = new String();
    public static Map<String, Map<String, String>> attributeData = new LinkedHashMap<String, Map<String, String>>();

    /**
     * This method performs the pre checks of the query
     * 
     * @param queryMap This contains all the parts of the query separated by spaces
     *                 in a map
     * @param query    This contains the entire query as input by the end user in
     *                 lowercase
     */
    public static void preCheckCreateQuery(Map<Integer, String> queryMap, String query) {
        if (!queryMap.get(0).equals("create")) {
            isMalformedQuery = true;
            System.out.println("Malformed query");
            resetGlobalVariables();
            DetermineQueryType.getQuery();
        } else if (queryMap.get(0).equals("create") && queryMap.get(1) != null) {
            decomposeCreateQuery(queryMap, query);
        }
    }

    /**
     * This method decomposes the create table query, validates for correct input
     * and stores the values of key elements
     * 
     * @param queryMap This contains all the parts of the query separated by spaces
     *                 in a map
     * @param query    This contains the entire query as input by the end user in
     *                 lowercase
     */
    public static void decomposeCreateQuery(Map<Integer, String> queryMap, String query) {
        Boolean isTableAlreadyExists = false;
        String tableDataConstraints = new String();
        List<String> allExistingTables = ReadDataFromFile.readAllTablesPresent();

        try {
            for (Map.Entry<Integer, String> pair : queryMap.entrySet()) {
                if (pair.getValue().equals("table")) {
                    if (!queryMap.get(pair.getKey() - 1).equals("create") || !queryMap.containsKey(pair.getKey() + 1)) {
                        System.out.println("Malformed query");
                        resetGlobalVariables();
                        DetermineQueryType.getQuery();
                        break;
                    } else if (queryMap.get(pair.getKey() + 1) != null) {
                        tableName = queryMap.get(pair.getKey() + 1);
                    }
                }
            }

            if (!allExistingTables.isEmpty() && tableName != "") {
                if (allExistingTables.contains(tableName)) {
                    System.out.println("Table already exists");
                    resetGlobalVariables();
                    isTableAlreadyExists = true;
                }
            }

            if (!isTableAlreadyExists) {
                String[] afterTableNameData = query.split(tableName);
                if (afterTableNameData.length == 2) {
                    tableDataConstraints = afterTableNameData[1].trim();
                    if (!tableDataConstraints.contains("primary key")) {
                        System.out.println("Malformed query: Please enter a primary key with constraint of NOT NULL");
                        resetGlobalVariables();
                        DetermineQueryType.getQuery();
                    } else {
                        String[] decomposeConstraints = tableDataConstraints.split(",");

                        for (int i = 0; i < decomposeConstraints.length; i++) {
                            String temp = decomposeConstraints[i].trim();
                            if (temp.startsWith("(")) {
                                temp = temp.substring(i + 1, temp.length());
                            } else if (temp.endsWith(")")) {
                                temp = temp.substring(0, temp.length() - 1);
                            }
                            String[] eachAttributeData = temp.split(" ");
                            if (eachAttributeData.length < 2) {
                                System.out.println("Malformed query");
                                resetGlobalVariables();
                                DetermineQueryType.getQuery();
                            } else {
                                Map<String, String> innerAttributeData = new LinkedHashMap<String, String>();
                                if (eachAttributeData[0].equals("primary")) {
                                    innerAttributeData
                                            .put("primaryKey",
                                                    eachAttributeData[2].trim().substring(
                                                            eachAttributeData[2].trim().indexOf("(") + 1,
                                                            eachAttributeData[2].trim().indexOf(")")));
                                } else if (eachAttributeData.length == 2) {
                                    innerAttributeData.put(eachAttributeData[1].trim(), "null");
                                } else if (eachAttributeData.length == 3) {
                                    innerAttributeData.put(eachAttributeData[1].trim(), eachAttributeData[2].trim());
                                } else if (eachAttributeData.length == 4) {
                                    innerAttributeData.put(eachAttributeData[1].trim(),
                                            eachAttributeData[2].trim() + eachAttributeData[3].trim());
                                }
                                attributeData.put(eachAttributeData[0], innerAttributeData);
                            }
                        }
                    }

                    if (!attributeData.containsKey(attributeData.get("primary").get("primaryKey"))) {
                        System.out.println(
                                "Malformed query: Key defined as Primary is not included in the list of attributes");
                        resetGlobalVariables();
                        DetermineQueryType.getQuery();
                    }

                    if (!attributeData.isEmpty()) {
                        Map<String, String> primaryKeyConstraint = attributeData
                                .get(attributeData.get("primary").get("primaryKey"));
                        for (Map.Entry<String, String> pk : primaryKeyConstraint.entrySet()) {
                            if (pk.getValue() != null && pk.getValue().equals("null")) {
                                System.out.println("Malformed query: Primary key cannot have a NULL constraint");
                                resetGlobalVariables();
                                DetermineQueryType.getQuery();
                            }
                        }
                    }
                } else {
                    System.out.println("Malformed query: Please provide the attributes and constraints");
                    resetGlobalVariables();
                    DetermineQueryType.getQuery();
                }

            } else {
                resetGlobalVariables();
                DetermineQueryType.getQuery();
            }
            if (!attributeData.isEmpty()) {
                DetermineQueryType.logCorrectQuery(query, false);
                WriteDataToFile writeObj = new WriteDataToFile();
                writeObj.writeTableMdtToFile(attributeData, tableName);
                System.out.println(tableName + " created successfully!");
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
        attributeData.clear();
    }
}
