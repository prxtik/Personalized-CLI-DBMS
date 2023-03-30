import java.io.*;
import java.sql.SQLOutput;
import java.util.*;
import java.security.*;
import java.math.*;

/**
 * This class performs various user operations like signup, login, etc.
 */
public class UserManagement extends User {
    /**
     * The method is used to log in by a user.
     *
     * @return the method returns the user object.
     * @throws Exception
     */
    public User login() throws Exception {
        File obj = new File("users.txt");
        Scanner sc = new Scanner(System.in);
        User u = new User();
        String s;
        do {
            System.out.println("Enter Username: ");
            u.setUsername(sc.nextLine());
            if (isPresent(u.getUsername())) {
                do {
                    System.out.println("Enter Password: ");
                    String passwd = sc.nextLine();
                    passwd = hashing(passwd);
                    u.setPassword(passwd);
                    s = isPresent(u.getUsername(), u.getPassword());
                    if (s == null) {
                        System.out.println("Invalid Password. Enter Valid Password.");
                    }
                } while (s == null);
                System.out.println("Security Ques: " + s.split("@@@")[1].split(":::")[0]);
                String ans;
                do {
                    System.out.println("Enter Answer: ");
                    u.setSecurityans(sc.nextLine());
                    ans = s.split(":::")[1].split("&&&")[0];
                    if (u.getSecurityans().equals(ans)) {
                        System.out.println("Welcome, " + u.getUsername() + ". You are successfully logged in.");

                        // Logging users login activity in the log file
                        Logger log = new Logger();
                        log.logActivity(u.getUsername(), "Login", "");
                        break;
                    }
                    System.out.println("Invalid Answer. Enter Correct Answer.");
                } while (!u.getSecurityans().equals(ans));
                break;
            }
            System.out.println("Invalid Username. Enter Valid Username.");
        } while (!isPresent(u.getUsername()));
        return u;
    }

    /**
     * The method is used to register a user during signup.
     *
     * @return the method returns a file that stores all the user data.
     * @throws Exception the method throws an exception if the user file creation was not successful.
     */
    public File register() throws Exception {
//      first create a new file for the first user and inputting user data
        File objc = createFile();
        objc.createNewFile();
        User u = new User();
        Scanner myReader = new Scanner(System.in);
        do {
            System.out.println("Enter username:");
            u.setUsername(myReader.nextLine());
            if (isPresent(u.getUsername())) {
                System.out.println("User already exists. Kindly use a different username.");
            }
        } while (isPresent(u.getUsername()));
        System.out.println("Enter password:");
        String passwd = myReader.nextLine();
        passwd = hashing(passwd);
        u.setPassword(passwd);
        System.out.println("Enter security question:");
        u.setSecurityque(myReader.nextLine());
        System.out.println("Enter answer:");
        u.setSecurityans(myReader.nextLine());

        FileWriter myWriter = new FileWriter("users.txt", true);
        myWriter.append("###" + u.username + "$$$" +
                u.password + "@@@" +
                u.securityque + ":::" +
                u.securityans + "&&&" +
                '\n');
        myWriter.close();

        System.out.println("User Registered");
        return objc;
    }

    /**
     * The method just creates a user file.
     *
     * @return the method returns a users file.
     */
    public File createFile() {
        Logger logFile = new Logger();
        boolean result = logFile.createFile("users");
        if (result) {
            return new File("users.txt");
        } else {
            return null;
        }
    }

    /**
     * The method is used to check if the usrename is already taken or not
     *
     * @param usrname the method takes username as its input.
     * @return the method returns true if the user is already present.
     * @throws FileNotFoundException the method throws this exception if the user file was not located.
     */
    public Boolean isPresent(String usrname) throws FileNotFoundException {
        File obj = new File("users.txt");
        Scanner sc = new Scanner(obj);
        while (sc.hasNextLine()) {
            String p = sc.nextLine();
            if (p.contains("###" + usrname + "$$$")) {
                return true;
            }
        }
        return false;
    }

    /**
     * The method is used to check whether the user is a valid user or not during login
     *
     * @param usrname the method takes username as one of its parameter.
     * @param pswd    the method takes password as another one of its parameters.
     * @return the method returns the row where the username is found.
     */
    public String isPresent(String usrname, String pswd) {
        try {
            File obj = new File("users.txt");
            Scanner sc = new Scanner(obj);
            String p = "";
            while (sc.hasNextLine()) {
                p = sc.nextLine();
                if (p.contains("###" + usrname + "$$$" + pswd + "@@@")) {
                    return p;
                }
            }
            return null;
        } catch (Exception e) {
            System.out.println("The file does not exist.");
            return null;
        }

    }

    /**
     * The method is used to hash the user's password
     *
     * @param a the method takes the user's original password as input parameter
     * @return the method returns the hashed password.
     * @throws Exception the method throws an Exception.
     */
    public String hashing(String a) throws Exception {
        //This code is adapted from Infosec Scout
        //https://infosecscout.com/decrypt-md5-in-java/
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(a.getBytes(), 0, a.length());
        return new BigInteger(1, md.digest()).toString(16);
    }


}
