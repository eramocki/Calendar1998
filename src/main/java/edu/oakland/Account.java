package edu.oakland;


import java.util.HashMap;

public class Account {
    private static HashMap<String, Account> accounts = new HashMap<>();
    private String userName;
    private String email;
    private int passwordHash;
    private String[] securityQuestions;

    private Account(String userName, String password, String email, String[] questions) {
        this.userName = userName;
        passwordHash = generatePasswordHash(password);
        this.email = email;
        securityQuestions = questions;
    }

    /**
     * Called when the System attempts to create a new account
     * @param user Account username
     * @param pass Account password
     * @return Returns true if successfully created, else false
     */
    public static boolean createAccount(String user, String pass) {
        if (accounts.containsKey(user)) {
            return false;
        }
        Account acc = new Account(user, pass, "", new String[] {}); //Todo manage email and questions
        accounts.put(user, acc);
        return true;
    }

    /**
     * Checks if an account with the given username exists
     * @param user Username
     * @return True if an account with this username exists, else false
     */
    public static boolean accountExists(String user) {
        return accounts.containsKey(user);
    }

    /**
     * Checks if a given username and password combination exists in the System
     * @param user Username
     * @param pass Password
     * @return True if the account exists and the information is correct, else false
     */
    public static boolean login(String user, String pass) {
        Account acc = accounts.get(user);
        return acc != null && acc.checkPassword(pass);
    }

    private boolean checkPassword(String password) {
        return password.hashCode() == passwordHash; //Todo Security
    }

    private int generatePasswordHash(String password) {
        return password.hashCode(); //Todo Security
    }
}
