import java.io.*;
import java.util.*;

/**
 * This class performs all kind operations on a table like create, insert, update, delete, truncate, drop.
 */
public class Table {

    /**
     * The method is used to create a new table.
     *
     * @param query the method takes the query as the parameter.
     * @return the method returns true if the table was created else it returns false.
     * @throws IOException
     */
    public boolean createTable(String query) throws IOException {
        if (query.endsWith(")")) {
            String temp = query.substring(0, query.length() - 1);
            String[] q = temp.split(" ");
            if (q[3].equals("(")) {
                FileWriter writeTable = new FileWriter("database.database", true);
                writeTable.append("\n" + "###" + q[2] + "$$$");
                writeTable.close();
                File obj = new File(q[2] + ".txt");
                obj.createNewFile();
                String args = temp.split("[(]")[1];
                args = args.trim();
                String[] attr = args.split(",");
                List cols = new ArrayList<>();
                List datatype = new ArrayList<>();

                for (int i = 0; i < attr.length; i++) {
                    cols.add(attr[i].split(" ")[0]);
                    datatype.add(attr[i].split(" ")[1]);
                }

                FileWriter w1 = new FileWriter(q[2] + ".txt", true);
                for (int i = 0; i < cols.size(); i++) {
                    if (i == 0) {
                        w1.append(cols.get(i) + "%%%");
                    } else if (i == cols.size() - 1) {
                        w1.append(cols.get(i) + "");
                    } else {
                        w1.append(cols.get(i) + "<<<");
                    }
                }
                w1.append("\n");
                w1.close();

                FileWriter w2 = new FileWriter(q[2] + ".txt", true);
                for (int i = 0; i < datatype.size(); i++) {
                    if (i == 0) {
                        w2.append(datatype.get(i) + "%%%");
                    } else if (i == datatype.size() - 1) {
                        w2.append(datatype.get(i) + "");
                    } else {
                        w2.append(datatype.get(i) + "<<<");
                    }
                }
                w2.append("\n");
                for (int i = 0; i < datatype.size(); i++) {
                    if (i == 0) {
                        w2.append("nn" + "%%%");
                    } else if (i == datatype.size() - 1) {
                        w2.append("n" + "");
                    } else {
                        w2.append("n" + "<<<");
                    }
                }
                w2.close();
                return true;
            }
            return false;

        } else {
            System.out.println("Invalid create table query.");
            return false;
        }

    }

    /**
     * The method is used to drop a table.
     *
     * @param tablename the method takes tablename as the parameter.
     * @return the method returns true if the table was dropped else it returns false.
     */
    public boolean dropTable(String tablename) {

        try {
            File obj = new File("database.database");
            Scanner sc = new Scanner(obj);
            String p = "";
            boolean flag = false;
            List temp = new ArrayList();
            while (sc.hasNextLine()) {
                p = sc.nextLine();
                if (!p.contains("###" + tablename + "$$$")) {
                    temp.add(p);
                } else {
                    flag = true;
                }
            }
            FileWriter write = new FileWriter("database.database");
            for (int i = 0; i < temp.size(); i++) {
                write.write(temp.get(i).toString());
                write.write("\n");
            }
            write.close();
            if (flag) {
                File table = new File(tablename + ".txt");
                table.delete();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }


    /**
     * The method is used to truncate a table.
     *
     * @param tablename the method takes the tablename as a parameter.
     * @return returns boolean value true if table is truncated else it returns false.
     */
    public boolean truncate(String tablename) {
        try {
            File obj = new File("database.database");
            Scanner sc = new Scanner(obj);
            String p = "";
            String r = "";
            List temp = new ArrayList();
            Boolean fileFound = false;
            while (sc.hasNextLine()) {
                p = sc.nextLine();
                if (p.contains("###" + tablename + "$$$")) {
                    fileFound = true;
                }
            }
            if (fileFound) {
                File obj1 = new File(tablename + ".txt");
                Scanner read = new Scanner(obj1);
                while (read.hasNextLine()) {
                    if (temp.size() < 3) {
                        r = read.nextLine();
                        temp.add(r);
                    } else {
                        break;
                    }
                }
            } else {
                return false;
            }
            FileWriter write = new FileWriter(tablename + ".txt");
            for (int i = 0; i < temp.size(); i++) {
                write.write(temp.get(i).toString());
                write.write("\n");
            }
            write.close();
            return true;
        } catch (Exception e) {
            System.out.println("Enter a valid truncate query.");
            return false;
        }
    }

    /**
     * This method is used to insert a record into a table.
     *
     * @param tempquery the method takes the query as parameter.
     * @return method returns true if the values were inserted in the table else it returns false.
     */
    public boolean insertIntoTable(String tempquery) {
        try {
            FileWriter write = new FileWriter(tempquery.split(" ")[2] + ".txt", true);
            String q = tempquery.split("[(]")[1];
            q = q.trim();
            String[] values = q.split(",");
            write.append("\n");
            for (int i = 0; i < values.length; i++) {
                if (i == 0) {
                    write.append(values[i] + "%%%");
                } else if (i == values.length - 1) {
                    write.append(values[i] + "");
                } else {
                    write.append(values[i] + "<<<");
                }
            }
            write.close();
            System.out.println("Values Inserted.");
            return true;
        } catch (Exception e) {
            System.out.println("Cannot insert into table.");
            return false;
        }

    }

    /**
     * The method is used to update a row in the table.
     *
     * @param tempquery the method takes the query as a parameter.
     * @return the method returns true if the table was updated else it returns false.
     */
    public boolean updateTable(String tempquery) {
        try {
            File folder = new File("./");
            File[] listOfFiles = folder.listFiles();
            boolean f = false;
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile() && listOfFiles[i].getName().contains(tempquery.split(" ")[1] + ".txt")) {
                    f = true;
                }
            }
            if (f) {
                String attr = tempquery.split("set")[1].split("where")[0];
                String[] cond = attr.split("=|>=|<=|>|<|!=");
                String setcol = cond[0];
                setcol = setcol.trim();
                String setvalue = cond[1];
                setvalue = setvalue.replace("\'", "").trim();
                File table = new File(tempquery.split(" ")[1] + ".txt");
                Scanner s = new Scanner(table);
                String c = "";
                String where = tempquery.split("where")[1];
                String[] wh1 = where.split("=|>=|<=|>|<|!=");
                String checkcol = wh1[0];
                checkcol = checkcol.trim();
                String checkval = wh1[1];
                String op1 = where.replace(wh1[0], "").replace(wh1[1], "");

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

                File obj1 = new File(tempquery.split(" ")[1] + ".txt");
                Scanner sc1 = new Scanner(obj1);

                String p = "";
                String row = "";
                String result = "";
                int count = 0;
                while (sc1.hasNextLine()) {
                    if (count == 3)
                        break;
                    result = result.concat(sc1.nextLine() + "\n");
                    count++;
                }
                File obj = new File(tempquery.split(" ")[1] + ".txt");
                Scanner sc = new Scanner(obj);
                int setindex = 0;
                int windex = 0;
                p = sc.nextLine();
                List temp = new ArrayList();
                if (p.contains(checkcol)) {

                    row = sc.nextLine();
                    if (!row.contains(checkval)) {
                        temp.add(row);
                    } else {
                        if (wh1 != null) {
                            wh1[1] = wh1[1].replace("'", "");
                            int counter1 = 0;
                            for (int i = 0; i < columns.size(); i++) {
                                if (wh1[0].equals(columns.get(i))) {
                                    counter1 = i;
                                    break;
                                }
                            }
                            for (int i = 0; i < columns.size(); i++) {
                                if (setcol.equalsIgnoreCase(columns.get(i))) {
                                    setindex = i;
                                }
                                if (checkcol.equalsIgnoreCase(columns.get(i))) {
                                    windex = i;
                                }
                            }

                            row = sc.nextLine();
                            while (sc.hasNextLine()) {
                                row = sc.nextLine();
                                String id = row.split("%%%")[0];
                                String[] data = row.split("%%%")[1].split("<<<");
                                String[] fulldata = new String[data.length + 1];
                                fulldata[0] = id;
                                for (int i = 1; i <= data.length; i++) {
                                    fulldata[i] = data[i - 1];
                                }
                                if (fulldata[windex].equalsIgnoreCase(checkval)) {
                                    fulldata[setindex] = setvalue;
                                }


                                for (int i = 0; i < fulldata.length; i++) {
                                    if (i == 0) {
                                        result = result.concat(fulldata[i] + "%%%");
                                    } else if (i == fulldata.length - 1) {
                                        result = result.concat(fulldata[i] + "\n");
                                    } else {
                                        result = result.concat(fulldata[i] + "<<<");
                                    }
                                }
                                temp.add(result);
                                continue;

                            }


                        } else {
                            return true;
                        }
                    }
                    FileWriter writer = new FileWriter(tempquery.split(" ")[1] + ".txt", false);
                    writer.write((String) temp.get(temp.size() - 1));
                    writer.close();

                    for (int i = 0; i < temp.size(); i++) {
                        writer.append(temp.get(i).toString());
                        writer.append("\n");
                    }
                    writer.write((String) temp.get(temp.size() - 1));
                    writer.close();

                    return true;

                } else {
                    return true;
                }


            } else {
                return true;
            }
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * This method deletes a row from a table.
     *
     * @param tablename the method takes tablename as one of its parameters.
     * @param where     the method takes the where clause of the query as another one of its parameters.
     */
    public void delete(String tablename, String where) {
        try {
            File folder = new File("./");
            File[] listOfFiles = folder.listFiles();
            boolean f = false;
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile() && listOfFiles[i].getName().contains(tablename + ".txt")) {
                    f = true;
                }
            }
            if (f) {
                String[] wh = where.split("=|>=|<=|>|<|!=");
                String col = wh[0];
                String val = wh[1];
                if (val.contains("'")) {
                    val = val.replace("'", "").trim();
                }
                String op = where.replace(col, "").replace(val, "");
                List temp = new ArrayList<>();
                File obj = new File(tablename + ".txt");
                Scanner sc = new Scanner(obj);
                String p = "";
                while (sc.hasNextLine()) {
                    p = sc.nextLine();
                    if (!p.contains(val)) {
                        temp.add(p);

                    } else {
                        continue;
                    }
                }

                FileWriter fw = new FileWriter(tablename + ".txt", false);
                for (int i = 0; i < temp.size(); i++) {
                    fw.write((String) temp.get(i) + "\n");
                }
                fw.close();
                System.out.println("Row deleted.");
                return;
            } else {
                System.out.println("Table does not exist. Enter a valid table name.");
                return;
            }

        } catch (Exception e) {
            System.out.println("Enter valid condition.");
            e.printStackTrace();
            return;
        }

    }
}
