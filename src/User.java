import java.io.*;
import java.util.*;

/**
 * This class represents the user entity with various attributes associated with it.
 */
public class User {
    /**
     * The method is used to get username.
     *
     * @return this method returns username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * This method is to set the username.
     *
     * @param username this method takes the username as input parameter.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * The method is used to get the user password.
     *
     * @return the method returns password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * This method is to set the user password.
     *
     * @param password the method takes the user's password as input parameter.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * The method is used to get the user's security question.
     *
     * @return the method returns user's security question.
     */
    public String getSecurityque() {
        return securityque;
    }

    /**
     * This method is to set the user's security question.
     *
     * @param securityque the method takes the user's security question as input parameter.
     */
    public void setSecurityque(String securityque) {
        this.securityque = securityque;
    }

    /**
     * The method is used to get the user's security question's answer.
     *
     * @return the method returns user's security question's answer.
     */
    public String getSecurityans() {
        return securityans;
    }

    /**
     * This method is to set the user's security question's answer.
     *
     * @param securityans the method takes the user's security question's answer as input parameter.
     */
    public void setSecurityans(String securityans) {
        this.securityans = securityans;
    }

    String username, password, securityque, securityans;
}
