import javax.sound.midi.Soundbank;
import java.io.*;
import java.sql.SQLOutput;
import java.util.*;

/**
 * Main Class
 */
public class Main {
    /**
     * main method
     */
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        Query q = new Query();
        int i = 0;
        while (i < 3) {
            System.out.println("\n1. Login. \n2. Create New User. \n3. Exit.");
            i = sc.nextInt();
            UserManagement user = new UserManagement();
            if (i == 2) {
                user.register();
            } else if (i == 1) {
                User u = user.login();
                while (q.insertQuery(u)) {
                    q.insertQuery(u);
                }

            }
        }

    }
}