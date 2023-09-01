import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReadDataFromFile {

    /**
     * This method reads all the data from the table file
     * 
     * @param fileName          This is the name of the table which will be the name
     *                          of the file
     * @param primaryIdentifier This is the primary key attribute name
     * @return Map<String, Map<String, String>> This returns a map of attribute name
     *         and attribute value mapped against the value of the primary key
     */
    public static Map<String, Map<String, String>> readData(String fileName, String primaryIdentifier) {
        Map<String, Map<String, String>> tableData = new LinkedHashMap<String, Map<String, String>>();
        Set<String> keySet = readTableMdt(fileName).keySet();
        String primaryKey = keySet.iterator().next();
        try {
            File myObj = new File("./" + fileName + ".table.txt");
            if (myObj.exists()) {
                Scanner myReader = new Scanner(myObj);

                while (myReader.hasNextLine()) {
                    String strCurrentLine = myReader.nextLine();
                    if (!strCurrentLine.equals("")) {
                        Map<String, String> tempMap = new LinkedHashMap<String, String>();
                        String innerLine = EncryptionDecryptionAES.encryptDecrypt(strCurrentLine, "decrypt");
                        String[] keyValueOuter = innerLine.split("--");
                        for (int i = 0; i < keyValueOuter.length; i++) {
                            String[] keyValueInner = keyValueOuter[i].split("=");
                            tempMap.put(keyValueInner[0], keyValueInner[1]);
                        }
                        tableData.put(tempMap.get(primaryKey), tempMap);
                    }
                }
            } else {
                tableData.clear();
            }
        } catch (Exception e) {
            tableData.clear();
            System.out.println("Error occurred while reading from file!");
        }
        return tableData;

    }

    /**
     * This method reads the metadata of the table from the file
     * 
     * @param fileName This is the name of the table which will be also the name of
     *                 the metadata file which will store the attribute names and
     *                 related constraints
     * @return Map<String, Map<String, List<String>>> This will return a map of
     *         attribute name and constraints mapped to the primary key attribute
     */
    public static Map<String, Map<String, List<String>>> readTableMdt(String fileName) {
        Map<String, Map<String, List<String>>> tableMetaData = new LinkedHashMap<String, Map<String, List<String>>>();
        Map<String, List<String>> internalMdt = new LinkedHashMap<String, List<String>>();
        String primaryIdentifier = new String();

        try {
            File myObj = new File("./" + fileName + ".table.metadata.txt");
            if (myObj.exists()) {
                Scanner myReader = new Scanner(myObj);

                while (myReader.hasNextLine()) {
                    String innerLine = EncryptionDecryptionAES.encryptDecrypt(myReader.nextLine(), "decrypt");
                    String[] onlyColumnData = innerLine.split("--##--");
                    primaryIdentifier = onlyColumnData[0].split("=")[1];

                    String[] keyValueOuter = onlyColumnData[1].split("--");
                    for (int i = 0; i < keyValueOuter.length; i++) {
                        List<String> tempList = new LinkedList<String>();
                        String[] keyValueInner = keyValueOuter[i].split("=");
                        tempList.add(keyValueInner[0]);
                        tempList.add(keyValueInner[1]);
                        tempList.add(keyValueInner[2]);
                        internalMdt.put(keyValueInner[0], tempList);
                    }
                }
                tableMetaData.put(primaryIdentifier, internalMdt);
            }

        } catch (Exception e) {
            System.out.println("Error occurred while reading from file!");
        }
        return tableMetaData;

    }

    /**
     * This method reads the database environment file to get the database name
     * 
     * @return String This will return the name of the database stored in the system
     */
    public static String readDatabaseEnvFile() {
        String dbName = new String();

        try {
            File myObj = new File("./system.env");
            if (myObj.exists()) {
                Scanner myReader = new Scanner(myObj);

                while (myReader.hasNextLine()) {
                    dbName = myReader.nextLine();
                    break;
                }
            } else {
                dbName = "";
            }
        } catch (Exception e) {
            System.out.println("Error occurred while reading from file!");
        }

        return dbName;
    }

    /**
     * This method reads the names of all the tables present in the database
     * 
     * @return List<String> This will return a list of all the tables for the given
     *         database in the system
     */
    public static List<String> readAllTablesPresent() {
        Set<String> files = new HashSet<String>();
        List<String> tableNames = new ArrayList<String>();
        Pattern pattern = Pattern.compile("./(.*?)[.]");

        try {
            files.addAll(findFiles(Paths.get("./"), ".table.txt"));
            files.addAll(findFiles(Paths.get("./"), ".table.metadata.txt"));

            for (int i = 0; i < files.size(); i++) {
                Matcher matcher = pattern.matcher(files.toArray()[i].toString());
                if (matcher.find()) {
                    tableNames.add(matcher.group(1));
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while reading from file!");
        }
        return tableNames;
    }

    /**
     * This method finds all the files in the directory based on the path and
     * extension mentioned
     * 
     * @param path          This is the path to the directory in which the files are
     *                      stored
     * @param fileExtension This is the extension of the file which we need to
     *                      search for
     * @return Set<String> This will return a unique list of all the files found in
     *         the directory
     */
    public static Set<String> findFiles(Path path, String fileExtension) {
        Set<String> result = new HashSet<String>();

        if (Files.isDirectory(path)) {
            try (Stream<Path> walk = Files.walk(path)) {
                result = walk
                        .filter(p -> !Files.isDirectory(p))
                        .map(p -> p.toString().toLowerCase())
                        .filter(f -> f.endsWith(fileExtension))
                        .collect(Collectors.toSet());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        return result;
    }
}