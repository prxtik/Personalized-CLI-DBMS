import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DisplayOutput {

    /**
     * This method displays the output of the select query in a tabular format
     * 
     * @param tableData This is the final data which needs to be shown to the user
     *                  in a table format
     */
    public static void displayTable(Map<String, Map<String, String>> tableData) {
        Set<String> columns = new LinkedHashSet<String>();
        String format = new String();
        String underline = new String();
        List<Integer> highestCharCount = new ArrayList<Integer>();

        for (Map.Entry<String, Map<String, String>> pair : tableData.entrySet()) {
            Map<String, String> innerPair = pair.getValue();
            columns.addAll(new ArrayList<String>(innerPair.keySet()));
        }

        List<String> colList = new ArrayList<String>(columns);

        for (int i = 0; i < colList.size(); i++) {
            Integer tempHighLength = 0;
            for (Map.Entry<String, Map<String, String>> pair : tableData.entrySet()) {
                String data = pair.getValue().get(colList.get(i));
                if (tempHighLength == 0 || (data != null && data != "" && tempHighLength < data.length())
                        || tempHighLength < colList.get(i).length()) {
                    if (data.length() > colList.get(i).length()) {
                        tempHighLength = data.length();
                    } else {
                        tempHighLength = colList.get(i).length();

                    }
                }
            }
            highestCharCount.add(tempHighLength);
        }

        for (int i = 0; i < colList.size(); i++) {
            format = format.concat("| " + colList.get(i));
            int diffCount = Math.abs(highestCharCount.get(i) - colList.get(i).length());
            for (int j = 0; j < diffCount; j++) {
                format = format.concat(" ");
            }
            format = format.concat("        ");
        }

        for (int i = 0; i <= format.length(); i++) {
            underline = underline.concat("-");
        }
        System.out.printf(underline + "\n");
        System.out.printf(format + "|\n");
        System.out.printf(underline + "\n");

        for (Map.Entry<String, Map<String, String>> pair : tableData.entrySet()) {
            String eachRowValues = new String();
            for (int i = 0; i < colList.size(); i++) {
                String data = pair.getValue().get(colList.get(i));
                if (data == "" || data == null) {
                    data = "null";
                }
                eachRowValues = eachRowValues.concat("| " + data);
                int diffCount = Math.abs(highestCharCount.get(i) - data.length());
                for (int j = 0; j < diffCount; j++) {
                    eachRowValues = eachRowValues.concat(" ");
                }
                eachRowValues = eachRowValues.concat("        ");
            }
            System.out.printf(eachRowValues + "|\n");
        }
        System.out.printf(underline + "\n");
    }
}
