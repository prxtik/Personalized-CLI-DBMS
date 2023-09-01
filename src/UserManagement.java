import java.io.File;
import java.io.FileWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class UserManagement {
    public static Boolean isNewFile = false;
    public static Boolean isUserLoggedIn = false;
    public static Map<String, String> logMap = new LinkedHashMap<String, String>();
    public static Map<String, Map<String, String>> hm = new LinkedHashMap<String, Map<String, String>>();
    public static UserLog logObj = new UserLog();

    /*
     * This method gets the user input from a list of options
     */
    public static void getUserInput() {
        try {
            File fileObj = new File("users.env");
            if (fileObj.createNewFile()) {
                isNewFile = true;
            }
            System.out.println("Choose one option from the following:");
            System.out.println("1. Register");
            if (fileObj.length() != 0) {
                System.out.println("2. Login");
            }
            Scanner scanOption = new Scanner(System.in);
            String option = scanOption.nextLine();

            readData();

            if (option.equals("1")) {
                registerUser();
            } else if (option.equals("2")) {
                loginUser();
            } else {
                System.out.println("Please enter valid input");
                getUserInput();
            }
        } catch (Exception e) {
            System.out.println("Something went wrong!");
            getUserInput();
        }

    }

    /*
     * This method will read the registered user data from the file
     */
    public static void readData() {
        try {
            File myObj = new File("./users.env");
            Scanner myReader = new Scanner(myObj);

            while (myReader.hasNextLine()) {

                Map<String, String> tempMap = new LinkedHashMap<String, String>();
                String innerLine = EncryptionDecryptionAES.encryptDecrypt(myReader.nextLine(), "decrypt");
                String[] keyValueOuter = innerLine.split("--");
                for (int i = 0; i < keyValueOuter.length; i++) {
                    String[] keyValueInner = keyValueOuter[i].split("=");
                    tempMap.put(keyValueInner[0], keyValueInner[1]);
                }
                hm.put(tempMap.get("userName"), tempMap);
            }
        } catch (Exception e) {
            System.out.println("Something went wrong!");
            getUserInput();
        }
    }

    /*
     * This method will get the username, password and answer to the
     * security question and will verify it against the stored data and if matched,
     * will give access to the user to the present database
     */
    public static void loginUser() {
        try {
            if (hm == null || hm.isEmpty()) {
                readData();
            }
            System.out.println("------LOGIN SCREEN------");
            System.out.println("Enter username:");
            Scanner scanUsrnm = new Scanner(System.in);
            String usrnm = scanUsrnm.nextLine();

            Map<String, String> userDetails = new LinkedHashMap<String, String>();
            userDetails = hm.get(usrnm);
            if (userDetails == null || userDetails.isEmpty()) {
                System.out.println("User does not exist, please enter correct username");
                loginUser();
            } else {
                System.out.println("Enter password:");
                Scanner scanPswrd = new Scanner(System.in);
                String pswrd = md5Encrypt(scanPswrd.nextLine());

                System.out.println(userDetails.get("seqQuestion"));
                Scanner scanAns = new Scanner(System.in);
                String answer = scanAns.nextLine();

                if (pswrd.equals(userDetails.get("password")) && answer.equals(userDetails.get("seqAnswer"))) {
                    System.out.println("Login successful!");
                    isUserLoggedIn = true;
                    logMap.put("operation", "login");
                    logMap.put("userName", usrnm);
                    logMap.put("loginTimeStamp", getCurrentDateTime());
                    logUserActivity(logMap, false);
                    System.out.println("------INPUT QUERY SCREEN------");
                    DetermineQueryType determineQuery = new DetermineQueryType();
                    determineQuery.getQuery();
                } else {
                    System.out.println("Sorry, login unsuccessful. Please try again");
                    loginUser();
                }
            }
        } catch (Exception e) {
            System.out.println("Something went wrong!");
            getUserInput();
        }

    }

    /*
     * This method will register the user for the first time and store
     * the user information in the database
     */
    public static void registerUser() {
        try {

            System.out.println("------REGISTER SCREEN------");

            System.out.println("Please enter username:");
            Scanner scanUserName = new Scanner(System.in);
            String userName = scanUserName.nextLine();

            Map<String, String> existingUserDetails = new LinkedHashMap<String, String>();
            existingUserDetails = hm.get(userName);

            if (existingUserDetails == null || existingUserDetails.isEmpty()) {
                System.out.println("Please enter password:");
                Scanner scanPassword = new Scanner(System.in);
                String password = scanPassword.nextLine();

                System.out.println("Please enter your security question:");
                Scanner scanSeqQuestion = new Scanner(System.in);
                String seqQuestion = scanSeqQuestion.nextLine();

                System.out.println("Please enter your security answer:");
                Scanner scanSeqAnswer = new Scanner(System.in);
                String seqAnswer = scanSeqAnswer.nextLine();

                String hashedPassword = md5Encrypt(password);
                if (hashedPassword != null && hashedPassword != "") {
                    FileWriter fileWriter = new FileWriter("users.env", true);

                    String userData = "userName=" + userName + "--password=" + hashedPassword + "--seqQuestion="
                            + seqQuestion + "--seqAnswer=" + seqAnswer;
                    fileWriter.write(EncryptionDecryptionAES.encryptDecrypt(userData, "encrypt"));
                    fileWriter.write("\n");
                    fileWriter.close();

                    System.out.println("You are now added as a registered User and can access the database!");
                    loginUser();
                }

            } else {
                System.out.println("This username is already taken, please choose another one");
                registerUser();
            }
        } catch (Exception e) {
            System.out.println("Something went wrong!");
            getUserInput();
        }

    }

    /**
     * This method logs the user activity
     * 
     * @param logMap          This is a map of type of detail and actual details to
     *                        be stored in the user logs
     * @param isLastOperation This denotes if this is the last operation of the
     *                        user, for example: logout
     */
    public static void logUserActivity(Map<String, String> logMap, Boolean isLastOperation) {
        try {
            logObj.writeLogs(logMap, false);
        } catch (Exception e) {
            System.out.println("Something went wrong!");
            getUserInput();
        }
    }

    /*
     * This methods provides the option to enter another query or exit the session
     */
    public static void exitSession() {
        try {
            System.out.println("Choose one option from the following:");
            System.out.println("1. Enter another query");
            System.out.println("2. Exit");
            Scanner scanExit = new Scanner(System.in);
            String exitOption = scanExit.nextLine();
            if (exitOption.equals("2") && isUserLoggedIn) {
                logMap.clear();
                logMap.put("operation", "logout");
                logMap.put("logoutTimeStamp", getCurrentDateTime());
                logObj.writeLogs(logMap, true);
                logMap.clear();
                getUserInput();
            } else if (exitOption.equals("1")) {
                DetermineQueryType determineQuery = new DetermineQueryType();
                determineQuery.getQuery();
            } else {
                System.out.println("Please enter valid input");
                exitSession();
            }
        } catch (Exception e) {
            System.out.println("Something went wrong!");
            getUserInput();
        }
    }

    /**
     * This method gets the current date and time
     * 
     * @return String This returns teh current date and time in a string format
     */
    public static String getCurrentDateTime() {
        String formattedDateTime = java.time.LocalDateTime.now().toString();
        return formattedDateTime;
    }

    /**
     * This method encrypts the password for security
     * 
     * @param password This is the password taken from the user as an input
     * @return String This returns the hashed/encrypted password to be stored in
     *         database or matched against user input
     */
    public static String md5Encrypt(String password) {
        String hashedPassword = new String();
        try {
            /*
             * Code adapted from the example in the article by InfoSe Scout
             * Available: https://infosecscout.com/decrypt-md5-in-java/
             */

            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(password.getBytes(), 0, password.length());
            hashedPassword = new BigInteger(1, m.digest()).toString(16);

        } catch (Exception e) {
            System.out.println("Something went wrong!");
            getUserInput();
        }
        return hashedPassword;
    }
}