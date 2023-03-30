import com.sun.source.tree.BreakTree;

import java.io.*;
import java.util.*;

/**
 * This class performs all operations on the user inputted query.
 */
public class Query {
    //    String q = "";
    String q = new String();

    /**
     * The method allows each user to write sql queries in the console and validates the query.
     *
     * @param user The method takes each user as its parameter and then allows the user to input the query.
     * @return true if the user inputs a query else if user types exit it returns false.
     */
    public boolean insertQuery(User user) {

        System.out.println("Enter a query: ");
        Scanner s = new Scanner(System.in);
        q = s.nextLine();
        if (q.equals("logout")) {
            Logger log = new Logger();
            log.logActivity(user.getUsername(), "Logout", null);
            return false;
        }
        validateQuery(user);
        return true;
    }

    /**
     * The method is used to validate each query inputted by the user.
     *
     * @param user The method takes each user as its parameter and then validates the inputted query.
     */
    public void validateQuery(User user) {
        q = q.toLowerCase().trim();
        if (q.endsWith(";")) {


            if (q.startsWith("select")) {
                String tempquery = q.substring(0, q.length() - 1);
                String[] query = tempquery.split(" ");
                try {
                    if (query[0].equals("select")) {
                        if (query[1].equals("*") && query[2].equals("from") && query[3] != null) {
                            if (query.length > 4) {
                                if (query[4].equals("where")) {
                                    if (tempquery.split("where").length == 2) {
                                        String temp = tempquery.split("where")[1];
                                        String[] w = temp.trim().split(" ");

                                        if (w[0].split("=|>=|<=|>|<|!=").length != 2) {
                                            System.out.println("Enter the correct where clause");
                                            return;
                                        } else if (w.length > 2) {
                                            if (w[1].equals("and") || w[1].equals("or")) {

                                                if (w[0].split("=|>=|<=|>|<|!=").length != 2) {
                                                    System.out.println("Enter the correct where clause");
                                                    return;
                                                }

                                                if (w.length == 3) {
                                                    if (w[2].split("=|>=|<=|>|<|!=").length != 2) {
                                                        System.out.println("Enter the correct where clause");
                                                        return;
                                                    } else {
                                                        System.out.println("Display all columns from " + query[3] + " table where " + w[0] + " " + w[1] + " " + w[2]);
                                                        if (getTableData(q, query[3], w[0], w[2])) {
                                                            Logger log = new Logger();
                                                            log.logActivity(user.getUsername(), "Inserted a query", q);
                                                            return;
                                                        }
                                                    }
                                                } else {
                                                    System.out.println("Enter the correct where clause");
                                                    return;
                                                }
                                            }

                                        } else {
                                            if (w.length == 1) {
                                                if (w[0].split("=|>=|<=|>|<|!=").length != 2) {
                                                    System.out.println("Enter the correct where clause");
                                                    return;
                                                } else {

                                                    if (getTableData(q, query[3], w[0], null)) {
                                                        Logger log = new Logger();
                                                        log.logActivity(user.getUsername(), "Inserted a query", q);
                                                        return;
                                                    }

                                                }

                                            } else {
                                                System.out.println("Enter the correct where clause");
                                                return;
                                            }
                                        }
                                    } else {
                                        System.out.println("Enter the correct where clause");
                                        return;
                                    }
                                }
                            } else {

                                if (getTableData(q, query[3], null, null)) {
                                    Logger log = new Logger();
                                    log.logActivity(user.getUsername(), "Inserted a query", q);
                                    return;
                                }

                            }
                        } else if ((query[1].contains(",") || !query[1].contains(",")) && !query[1].endsWith(",") && query[2].equals("from") && query[3] != null) {
                            String[] columns = query[1].split(",");
                            if (query.length > 4) {
                                if (query[4].equals("where")) {
                                    if (tempquery.split("where").length == 2) {
                                        String temp = tempquery.split("where")[1];
                                        String[] w = temp.trim().split(" ");

                                        if (w[0].split("=|>=|<=|>|<|!=").length != 2) {
                                            System.out.println("Enter the correct where clause");
                                            return;
                                        } else if (w.length > 2) {
                                            if (w[1].equals("and") || w[1].equals("or")) {

                                                if (w[0].split("=|>=|<=|>|<|!=").length != 2) {
                                                    System.out.println("Enter the correct where clause");
                                                    return;
                                                }

                                                if (w.length == 3) {
                                                    if (w[2].split("=|>=|<=|>|<|!=").length != 2) {
                                                        System.out.println("Enter the correct where clause");
                                                        return;
                                                    } else {
                                                        if (getTableData(q, query[3], w[0], w[2])) {
                                                            Logger log = new Logger();
                                                            log.logActivity(user.getUsername(), "Inserted a query", q);
                                                            return;
                                                        }
                                                    }
                                                } else {
                                                    System.out.println("Enter the correct where clause");
                                                    return;
                                                }
                                            }

                                        } else {
                                            if (w.length == 1) {
                                                if (w[0].split("=|>=|<=|>|<|!=").length != 2) {
                                                    System.out.println("Enter the correct where clause");
                                                    return;
                                                } else {
                                                    if (getTableData(q, query[3], w[0], null)) {
                                                        Logger log = new Logger();
                                                        log.logActivity(user.getUsername(), "Inserted a query", q);
                                                        return;
                                                    }
                                                }

                                            } else {
                                                System.out.println("Enter the correct where clause");
                                                return;
                                            }
                                        }
                                    } else {
                                        System.out.println("Enter the correct where clause");
                                        return;
                                    }
                                }
                            } else {
                                if (getTableData(q, query[3], null, null)) {
                                    Logger log = new Logger();
                                    log.logActivity(user.getUsername(), "Inserted a query", q);
                                    return;
                                }

                            }
                        } else {
                            System.out.println("Enter a valid select query");
                            return;
                        }
                    }

                } catch (Exception e) {
                    System.out.println("Enter a valid select query.");
                    return;
                }
            } else if (q.startsWith("create")) {
                try {
                    String tempquery = q.substring(0, q.length() - 1);
                    if (create(tempquery)) {
                        Logger log = new Logger();
                        log.logActivity(user.getUsername(), "Inserted a query", q);
                        return;
                    }
                } catch (Exception e) {
                    System.out.println("Enter a valid create query.");
                    return;
                }
            } else if (q.startsWith("insert")) {
                try {
                    String tempquery = q.substring(0, q.length() - 1);
                    if (insert(tempquery)) {
                        Logger log = new Logger();
                        log.logActivity(user.getUsername(), "Inserted a query", q);
                        return;
                    } else {
                        System.out.println("Enter a valid insert query.");
                        return;
                    }
                } catch (Exception e) {
                    System.out.println("Enter a valid insert query.");
                    return;
                }
            } else if (q.startsWith("update")) {
                try {
                    String tempquery = q.substring(0, q.length() - 1);
                    if (tempquery.split(" ")[1] != null && tempquery.split(" ")[2].equalsIgnoreCase("set")) {
                        Table t = new Table();
                        if (t.updateTable(tempquery)) {
                            System.out.println(tempquery.split(" ")[1] + " table updated.");
                            Logger log = new Logger();
                            log.logActivity(user.getUsername(), "Inserted a query", q);
                            return;
                        } else {
                            System.out.println("Enter a valid table name.");
                            return;
                        }
                    } else {
                        System.out.println("Enter a valid update query.");
                        return;
                    }

                } catch (Exception e) {
                    System.out.println("Enter a valid update query.");
                    return;
                }
            } else if (q.startsWith("delete")) {
                try {
                    String tempquery = q.substring(0, q.length() - 1);
                    String[] s = tempquery.split(" ");
                    if (s[1].equalsIgnoreCase("from") && s[3].equalsIgnoreCase("where") && s[2] != null) {
                        s[4] = s[4].trim();
                        Table t = new Table();
                        t.delete(s[2], s[4]);
                        Logger log = new Logger();
                        log.logActivity(user.getUsername(), "Inserted a query", q);
                        return;
                    }

                } catch (Exception e) {
                    System.out.println("Enter a valid delete query.");
                    return;
                }
            } else if (q.startsWith("drop")) {
                try {
                    String tempquery = q.substring(0, q.length() - 1);
                    String[] temp = tempquery.split(" ");
                    if (temp[1].equalsIgnoreCase("table")) {
                        Table tb = new Table();
                        if (tb.dropTable(temp[2])) {
                            System.out.println(temp[2] + " table dropped.");
                            Logger log = new Logger();
                            log.logActivity(user.getUsername(), "Inserted a query", q);
                            return;
                        } else {
                            System.out.println("Table doesn't exist.");
                            return;
                        }
                    } else if (tempquery.split(" ")[1].equalsIgnoreCase("database")) {
                        Db db = new Db();
                        if (db.dropDb(tempquery)) {
                            System.out.println("Database dropped.");
                            Logger log = new Logger();
                            log.logActivity(user.getUsername(), "Inserted a query", q);
                            return;
                        } else {
                            System.out.println("Enter a valid database name.");
                            return;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Enter valid drop query.");
                    return;

                }
            } else if (q.startsWith("truncate")) {
                try {
                    String tempquery = q.substring(0, q.length() - 1);
                    String[] temp = tempquery.split(" ");
                    if (temp[1].equalsIgnoreCase("table")) {
                        Table tb = new Table();
                        if (tb.truncate(temp[2])) {
                            System.out.println(temp[2] + " table truncated.");
                            Logger log = new Logger();
                            log.logActivity(user.getUsername(), "Inserted a query", q);
                            return;
                        } else {
                            System.out.println("Table doesn't exist.");
                            return;
                        }

                    } else {
                        System.out.println("Enter a valid truncate query.");
                        return;
                    }
                } catch (Exception e) {
                    System.out.println("Enter a valid truncate query.");
                    return;
                }
            } else {
                System.out.println("Enter a valid query.");
                return;
            }

        } else {
            System.out.println("Malformed query. Enter a valid query.");
            return;
        }
    }

    /**
     * The method is used to extract the data from the given table.
     *
     * @param query     the method takes the query as one of its parameter.
     * @param tableName the method takes tablename as another one of its parameters.
     * @param w1        The method takes first where condition as another one of its parameters.
     * @param w2        The method takes second where condition as another one of its parameters.
     * @return the method returns false if the table given in the query does not exist.
     * @throws Exception
     */
    private boolean getTableData(String query, String tableName, String w1, String w2) throws Exception {
        try {
            File obj = new File("database.database");
            Scanner sc = new Scanner(obj);
            String p = "";
            while (sc.hasNextLine()) {
                p = sc.nextLine();
                if (p.contains("###" + tableName + "$$$")) {
                    System.out.println(tableName + " table exists.");
                    File table = new File(tableName + ".txt");
                    Scanner s = new Scanner(table);
                    String c = "";

                    //getting column names in ArrayList columns
                    String primary_key_name = "";

                    ArrayList<String> columns = new ArrayList<>();
                    if (s.hasNextLine()) {
                        c = s.nextLine();
                        primary_key_name = c.split("%%%")[0];
                        columns.add(primary_key_name);
                        String[] cols = c.split("%%%")[1].split("<<<");
                        for (int i = 0; i < cols.length; i++) {

                            columns.add(cols[i]);

                        }
                    }
                    String[] wh1 = new String[100];
                    String[] wh2 = new String[100];
                    String op1, op2;
                    int counter1 = 0;
                    int counter2 = 0;


                    System.out.println("Columns: " + columns);

                    //getting columns datatype
                    ArrayList<String> colDatatype = new ArrayList<>();
                    c = s.nextLine();
                    colDatatype.add(c.split("%%%")[0]);
                    String[] datatype = c.split("%%%")[1].split("<<<");
                    for (int i = 0; i < datatype.length; i++) {

                        colDatatype.add(datatype[i]);

                    }
                    System.out.println("Datatype: " + colDatatype);

                    //get the null constraints
                    ArrayList<String> nullConstraints = new ArrayList<>();
                    c = s.nextLine();
                    nullConstraints.add(c.split("%%%")[0]);
                    String[] nnc = c.split("%%%")[1].split("<<<");
                    for (int i = 0; i < nnc.length; i++) {

                        nullConstraints.add(nnc[i]);

                    }
                    System.out.println("Null constraints for attributes: " + nullConstraints);

                    //getting data
                    if (s.hasNextLine()) {

                        Map<String, Map<String, String>> map = new LinkedHashMap<String, Map<String, String>>();
                        for (int i = 0; s.hasNextLine(); i++) {
                            c = s.nextLine();
                            String primary_key_value = c.split("%%%")[0];
                            String remainingData = c.split("%%%")[1];
                            String[] r = remainingData.split("<<<");
                            String[] remaining = new String[r.length + 1];
                            for (int e = 0; e < remaining.length; e++) {
                                if (e == 0) {
                                    remaining[e] = primary_key_value;
                                } else {
                                    remaining[e] = r[e - 1];
                                }
                            }

                            Map<String, String> temp = new LinkedHashMap<String, String>();

                            for (int j = 0; j < remaining.length; j++) {
                                if (columns.get(j).equals(primary_key_name)) {
                                    temp.put(columns.get(j), primary_key_value);
                                } else {
                                    temp.put(columns.get(j), remaining[j]);
                                }

                            }
                            map.put(primary_key_value, temp);
                        }
                        if (w1 == null && w2 == null) {
                            System.out.println("Query result: ");
                            query = query.substring(0, query.length() - 1);
                            String[] q = query.split(" ");
                            if (q[1].equals("*")) {
                                displayResult(map, columns);
                            } else if (q[1].contains(",")) {
                                String[] dispCols = q[1].split(",");
                                columns.clear();
                                columns.addAll(List.of(dispCols));
                                displayResult(map, columns);

                            } else {
                                columns.clear();
                                columns.add(q[1]);
                                displayResult(map, columns);
                            }
                        }

                        //checking where conditions


                        if (w1 != null || w2 != null) {
                            Map<String, Map<String, String>> finalValues1 = new LinkedHashMap<String, Map<String, String>>();
                            Map<String, Map<String, String>> finalValues2 = new LinkedHashMap<String, Map<String, String>>();
                            query = query.substring(0, query.length() - 1);
                            String[] q = query.split(" ");
                            Map<String, Map<String, String>> result = new LinkedHashMap<String, Map<String, String>>();
                            if (w1 != null) {
                                wh1 = w1.split("=|>=|<=|>|<|!=");
                                op1 = w1.replace(wh1[0], "").replace(wh1[1], "");
                                wh1[1] = wh1[1].replace("'", "");
                                counter1 = 0;
                                for (int i = 0; i < columns.size(); i++) {
                                    if (wh1[0].equals(columns.get(i))) {
                                        counter1 = i;
                                        break;
                                    }
                                }
//                            System.out.println(counter1); counter is used to store the column position
                                for (Map.Entry<String, Map<String, String>> i : map.entrySet()) {
                                    Map<String, String> innerTemp = i.getValue();
                                    for (int j = 0; j < columns.size(); j++) {
                                        for (Map.Entry<String, String> inTemp : innerTemp.entrySet()) {
                                            if (op1.equals("=")) {
                                                if (inTemp.getKey().equals(wh1[0]) && inTemp.getValue().equals(wh1[1])) {
                                                    finalValues1.put(i.getKey(), i.getValue());
                                                    result.putAll(finalValues1);
                                                }
                                            } else if (op1.equals(">")) {
                                                if (inTemp.getKey().equals(wh1[0]) && (Integer.parseInt(inTemp.getValue()) > Integer.parseInt(wh1[1]))) {
                                                    finalValues1.put(i.getKey(), i.getValue());
                                                    result.putAll(finalValues1);
                                                }
                                            } else if (op1.equals("<")) {
                                                if (inTemp.getKey().equals(wh1[0]) && (Integer.parseInt(inTemp.getValue()) < Integer.parseInt(wh1[1]))) {
                                                    finalValues1.put(i.getKey(), i.getValue());
                                                    result.putAll(finalValues1);
                                                }
                                            } else if (op1.equals(">=")) {
                                                if (inTemp.getKey().equals(wh1[0]) && (Integer.parseInt(inTemp.getValue()) >= Integer.parseInt(wh1[1]))) {
                                                    finalValues1.put(i.getKey(), i.getValue());
                                                    result.putAll(finalValues1);
                                                }
                                            } else if (op1.equals("<=")) {
                                                if (inTemp.getKey().equals(wh1[0]) && (Integer.parseInt(inTemp.getValue()) <= Integer.parseInt(wh1[1]))) {
                                                    finalValues1.put(i.getKey(), i.getValue());
                                                    result.putAll(finalValues1);
                                                }
                                            } else if (op1.equals("!=")) {
                                                if (inTemp.getKey().equals(wh1[0]) && !inTemp.getValue().equals(wh1[1])) {
                                                    finalValues1.put(i.getKey(), i.getValue());
                                                    result.putAll(finalValues1);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (w2 != null) {
                                wh2 = w2.split("=|>=|<=|>|<|!=");
                                op2 = w2.replace(wh2[0], "").replace(wh2[1], "");
                                wh2[1] = wh2[1].replace("'", "");
                                counter2 = 0;
                                for (int i = 0; i < columns.size(); i++) {
                                    if (wh2[0].equals(columns.get(i))) {
                                        counter2 = i;
                                        break;
                                    }
                                }
//                            System.out.println(counter2); counter is used to store the column position
                                for (Map.Entry<String, Map<String, String>> i : map.entrySet()) {
                                    Map<String, String> innerTemp = i.getValue();
                                    for (int j = 0; j < columns.size(); j++) {
                                        for (Map.Entry<String, String> inTemp : innerTemp.entrySet()) {
                                            if (op2.equals("=")) {
                                                if (inTemp.getKey().equals(wh2[0]) && inTemp.getValue().equals(wh2[1])) {
                                                    finalValues2.put(i.getKey(), i.getValue());
                                                }
                                            } else if (op2.equals("<")) {
                                                if (inTemp.getKey().equals(wh2[0]) && (Integer.parseInt(inTemp.getValue()) < Integer.parseInt(wh2[1]))) {
                                                    finalValues1.put(i.getKey(), i.getValue());
                                                    result.putAll(finalValues1);
                                                }
                                            } else if (op2.equals(">")) {
                                                if (inTemp.getKey().equals(wh2[0]) && (Integer.parseInt(inTemp.getValue()) > Integer.parseInt(wh2[1]))) {
                                                    finalValues1.put(i.getKey(), i.getValue());
                                                    result.putAll(finalValues1);
                                                }
                                            } else if (op2.equals("<=")) {
                                                if (inTemp.getKey().equals(wh2[0]) && (Integer.parseInt(inTemp.getValue()) <= Integer.parseInt(wh2[1]))) {
                                                    finalValues1.put(i.getKey(), i.getValue());
                                                    result.putAll(finalValues1);
                                                }
                                            } else if (op2.equals(">=")) {
                                                if (inTemp.getKey().equals(wh2[0]) && (Integer.parseInt(inTemp.getValue()) >= Integer.parseInt(wh2[1]))) {
                                                    finalValues1.put(i.getKey(), i.getValue());
                                                    result.putAll(finalValues1);
                                                }
                                            } else if (op2.equals("!=")) {
                                                if (inTemp.getKey().equals(wh2[0]) && !inTemp.getValue().equals(wh2[1])) {
                                                    finalValues1.put(i.getKey(), i.getValue());
                                                    result.putAll(finalValues1);
                                                }
                                            }

                                        }
                                    }
                                }


                                if (q[6].contains("or")) {
                                    finalValues1.putAll(finalValues2);
                                    result.putAll(finalValues1);
                                } else if (q[6].contains("and")) {
                                    result.clear();
                                    for (String i : finalValues1.keySet()) {
                                        if (finalValues2.containsKey(i)) {
                                            result.put(i, finalValues1.get(i));
                                        }
                                    }

                                }
                            }
                            System.out.println("Query result: ");
                            if (q[1].equals("*")) {
                                displayResult(result, columns);
                            } else if (q[1].contains(",")) {
                                String[] dispCols = q[1].split(",");
                                columns.clear();
                                columns.addAll(List.of(dispCols));
                                displayResult(result, columns);

                            } else {
                                columns.clear();
                                columns.add(q[1]);
                                displayResult(result, columns);
                            }


                        }

                    } else {
                        System.out.println("No data in the table " + tableName);
                    }

                    return true;
                }
            }
            return false;


        } catch (Exception e) {
            System.out.println("No data in the table " + tableName);
        }
        return false;
    }

    /**
     * The method is used to display the result of the user inputted select query.
     *
     * @param result  the method takes the result map as of its parameters.
     * @param columns the method takes list of columns as another one of its parameters.
     */
    private void displayResult(Map<String, Map<String, String>> result, ArrayList<String> columns) {
        for (int i = 0; i < columns.size(); i++) {
            System.out.print(columns.get(i) + "\t\t|\t");
        }
        System.out.println();
        System.out.println("-------------------------------------------------------");
        for (Map.Entry<String, Map<String, String>> m : result.entrySet()) {
            for (Map.Entry<String, String> mp : m.getValue().entrySet()) {
                for (int i = 0; i < columns.size(); i++) {
                    if (mp.getKey().equals(columns.get(i))) {
                        System.out.print(mp.getValue() + "\t\t|\t");
                    }
                }
            }
            System.out.println("");
        }
        return;
    }

    /**
     * The method is used to insert a row into a table.
     *
     * @param tempquery the method takes the query as its parameter.
     * @return the method returns true if a row was inserted in the table else it returns false.
     */
    private boolean insert(String tempquery) {
        try {
            String[] q = tempquery.split(" ");
            if (q[1].equalsIgnoreCase("into") && q[2] != null) {
                File folder = new File("./");
                File[] listOfFiles = folder.listFiles();
                boolean f = false;
                for (int i = 0; i < listOfFiles.length; i++) {
                    if (listOfFiles[i].isFile() && listOfFiles[i].getName().contains(q[2] + ".txt")) {
                        f = true;
                    }
                }
                if (f) {
                    if (q[3].equalsIgnoreCase("values")) {
                        tempquery = tempquery.substring(0, tempquery.length() - 1);
                        Table t = new Table();
                        if (t.insertIntoTable(tempquery)) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                } else {
                    System.out.println(q[2] + " table doesn't exist.");
                }

            } else {
                return false;
            }

        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * The method is used to create a database or a table.
     *
     * @param tempquery the method takes the query as the parameter.
     * @return the method returns true if the database or the table was created.
     */
    private boolean create(String tempquery) {
        String[] query = tempquery.split(" ");
        try {
            if (query[1].equalsIgnoreCase("database")) {
                if (query[2] != null) {
                    File folder = new File("./");
                    File[] listOfFiles = folder.listFiles();
                    for (int i = 0; i < listOfFiles.length; i++) {
                        if (listOfFiles[i].isFile() && listOfFiles[i].getName().contains(query[2] + ".database")) {
                            System.out.println("Database already exists.");
                            return false;
                        }
                    }
                    Db db = new Db();
                    if (db.createDb(query[2])) {
                        System.out.println("Database created");
                        return true;
                    } else {
                        System.out.println("Enter a valid database name.");
                        return false;
                    }

                }

            } else if (query[1].equals("table")) {
                if (query[2] != null) {
                    File folder = new File("./");
                    File[] listOfFiles = folder.listFiles();
                    for (int i = 0; i < listOfFiles.length; i++) {
                        if (listOfFiles[i].isFile() && listOfFiles[i].getName().contains(query[2] + ".txt")) {
                            System.out.println(query[2] + " table already exists.");
                            return false;
                        }
                    }
                    Table t = new Table();
                    if (t.createTable(tempquery)) {
                        System.out.println(query[2] + " table created.");
                        return true;
                    } else {
                        System.out.println("Enter a valid tablename.");
                        return false;
                    }
                }

            }
        } catch (Exception e) {
            System.out.println("Enter a valid create query.");
            return false;
        }
        return false;
    }
}
