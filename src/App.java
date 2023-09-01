public class App {

    /**
     * This method is the starting point for the code
     * 
     * @param args Default params for the main method to execute the code
     */
    public static void main(String[] args) {
        try {
            UserManagement.getUserInput();
        } catch (Exception e) {
            System.out.println("Error executing the code.");
        }
    }
}
