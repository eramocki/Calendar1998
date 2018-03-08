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
    private String name;
    private String passwordHash;
    private String[] securityQuestions;

    private Account(String userName, String password, String name, String[] securityQuestions) {
        this.userName = userName;
        this.name = name;
        this.securityQuestions = securityQuestions;

        setPassword(password);
    }

    /**
     * Called when the System attempts to create a new account
     *
     * @param user Account username
     * @param pass Account password
     * @return Returns true if successfully created, else false
     */
    public static boolean createAccount(String user, String pass, String name, String[] securityQuestions) {
        if (accounts.containsKey(user)) {
            return false;
        }
        Account acc = new Account(user, pass, name, securityQuestions); //Todo manage email and questions
        accounts.put(user, acc);
        saveAccounts();
        return true;
    }

    /**
     * Try and change this Account's password.
     *
     * @param oldPassword the old password (not hash)
     * @param newPassword the new password (not hash)
     * @return True if the password was changed
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        return checkPassword(oldPassword) && setPassword(newPassword);
    }

    public Boolean resetPassword(String newPassword) {
        return setPassword(newPassword);
    }

    /**
     * Update this account's password hash and save to disk.
     *
     * @param password the new password
     * @return True if the password changed
     */
    private boolean setPassword(String password) {
        try {
            this.passwordHash = PasswordStorage.createHash(password);
            saveAccounts();
            return true;
        } catch (PasswordStorage.CannotPerformOperationException e) {
            logger.log(Level.SEVERE, "Can't make password hash", e);
            //todo If the hash can't be made it will probably end up being written to disk as null
        }
        return false;
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
     * Get the account object for the given username, or null if one does not exist.
     *
     * @param userName the userName of the account
     * @return the Account
     */
    public static Account getAccount(String userName) {
        if (!accountExists(userName)) return null;
        return accounts.get(userName);
    }

    /**
     * Gets the user account's real name
     *
     * @param userName the userName of the account
     * @return real name
     */
    public static String getName(String userName) {
        if (!accountExists(userName)) return null;
        Account acc = accounts.get(userName);
        return acc.name;
    }

    /**
     * Gets the user account's security question answers
     *
     * @param userName the userName of the account
     * @return answers to the security questions
     */
    public static String[] getSecurityQuestions(String userName) {
        if (!accountExists(userName)) return null;
        Account acc = accounts.get(userName);
        return acc.securityQuestions;
    }

    /**
     * Checks if a given username and password combination exists in the System
     *
     * @param user Username
     * @param pass Password
     * @return True if the account exists and the information is correct, else false
     */
    public static boolean checkCredentials(String user, String pass) {
        Account acc = accounts.get(user);
        return acc != null && acc.checkPassword(pass);
    }

    private boolean checkPassword(String password) {
        try {
            return PasswordStorage.verifyPassword(password, passwordHash);
        } catch (PasswordStorage.CannotPerformOperationException | PasswordStorage.InvalidHashException e) {
            logger.log(Level.SEVERE, "Can't check password hash", e);
        }
        return false;
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
