package edu.oakland;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Account implements Serializable {

    private transient static final Logger logger = Logger.getLogger(Account.class.getName());

    private transient static HashMap<String, Account> accounts = new HashMap<>();
    private transient static final File ACCOUNT_FILE = new File(Main.DATA_DIR, "accounts.dat");

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
     *
     * @param user Account username
     * @param pass Account password
     * @return Returns true if successfully created, else false
     */
    public static boolean createAccount(String user, String pass) {
        if (accounts.containsKey(user)) {
            return false;
        }
        Account acc = new Account(user, pass, "", new String[]{}); //Todo manage email and questions
        accounts.put(user, acc);
        saveAccounts();
        return true;
    }

    /**
     * Checks if an account with the given username exists
     *
     * @param user Username
     * @return True if an account with this username exists, else false
     */
    public static boolean accountExists(String user) {
        return accounts.containsKey(user);
    }

    /**
     * Checks if a given username and password combination exists in the System
     *
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

    /**
     * Load accounts from disk to the current HashMap of accounts, overwriting it
     */
    public static void loadAccounts() {
        if (!ACCOUNT_FILE.exists()) {
            logger.warning("Wanted to load accounts but the file didn't exist");
            return;
        }

        try {
            FileInputStream fis = new FileInputStream(ACCOUNT_FILE);
            ObjectInputStream ois = new ObjectInputStream(fis);

            accounts = (HashMap<String, Account>) ois.readObject();
            logger.finest("Loaded accounts");

            fis.close();
            ois.close();

        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            logger.log(Level.SEVERE, "Could not load accounts", e);
        }
    }

    /**
     * Write the current HashMap of accounts to disk, overwriting the file
     */
    public static void saveAccounts() {
        if (!ACCOUNT_FILE.exists()) {
            logger.fine("Creating new account file");
        }

        try {
            FileOutputStream fos = new FileOutputStream(ACCOUNT_FILE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(accounts);
            logger.finer("Saved accounts");

            fos.close();
            oos.close();

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not save accounts", e);
        }
    }
}
