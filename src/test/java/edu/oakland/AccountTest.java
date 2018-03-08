package edu.oakland;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static edu.oakland.Main.DATA_DIR;
import static org.junit.Assert.*;

public class AccountTest {

    private String[] testQuestions = {"Q1", "Q2", "Q3"};
    private Account testAccount;
    private String testUserName = "Test1";
    private String testPassword = "password123";
    private String testRealName = "Real Name1";

    private String corruptedString = "H͕̻̥̤̪̦̻͗ę̮̼͕͈̠͚̗̗̃͑͆́̎͜͢l̨̤͙̤̮̺̓l̢̮͔͊ͣ̐̐͊͘o̸̻̜͂ͫ̓̑ͣ̐̉́ͅ ̸̗͈̥̥̝̗͙͕͐̋̇̓̂W̧̠̤̤͕̼͋̇̀̅̕o̻͉̣̟ͪ̋̑ͦͥr̷̤͎̝͙̣̥ͪͬ̽ͫ́͑̿lͬ́ͪ҉̗d͚͎͕͓͒̇́̕͟";

    @Before
    public void setUp() throws Exception {
        cleanDirectory(DATA_DIR.toPath());
        DATA_DIR = new java.io.File(System.getProperty("user.home") + "/Calendar1998/test/");
        if (!DATA_DIR.exists()) {
            assertTrue(DATA_DIR.mkdirs());
        }

        Account.createAccount(testUserName, testPassword, testRealName, testQuestions);
        testAccount = Account.getAccount("Test1");
    }

    @After
    public void tearDown() throws Exception {
        Account.removeAccount(testUserName);
        Account.removeAccount(corruptedString);
        Account.removeAccount("User1");
        Account.removeAccount("User2");
        Account.removeAccount("User3");
        Account.removeAccount("Test1");
        cleanDirectory(DATA_DIR.toPath());
    }

    public void cleanDirectory(Path directory) throws IOException {
        if(!Files.exists(directory)) return;
        //https://stackoverflow.com/questions/779519/delete-directories-recursively-in-java/27917071#27917071
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Test
    public void createAccount() {
        assertFalse("Should not allow creating account with null userName", Account.createAccount(null, "password123", "Real Name", testQuestions));

        assertTrue(Account.createAccount("User1", "password123", "Real Name1", testQuestions));
        assertFalse(Account.createAccount("User1", "password123", "Real Name2", testQuestions)); //Duplicate userName

        assertTrue(Account.createAccount("User2", "password123", "Real Name", testQuestions));
        assertTrue(Account.createAccount(corruptedString, "password123", "Real Name", testQuestions));
        assertTrue(Account.createAccount("User3", corruptedString, "Real Name", testQuestions));
    }

    @Test
    public void removeAccount() {
        assertFalse(Account.removeAccount("Some username"));
        assertFalse(Account.removeAccount(corruptedString));
        assertFalse(Account.removeAccount(null));

        assertTrue(Account.createAccount("UserToRemove", "password123", "Real Name", testQuestions));
        assertTrue(Account.removeAccount("UserToRemove"));
        assertNull(Account.getAccount("UserToRemove"));
    }

    @Test
    public void changePassword() {
        assertFalse(testAccount.changePassword("not the old password", "changedPassword"));
        assertTrue("Password should not have changed", testAccount.checkPassword(testPassword));

        assertFalse(testAccount.changePassword(corruptedString, "changedPassword"));
        assertTrue("Password should not have changed", testAccount.checkPassword(testPassword));

        assertTrue("Password should have changed", testAccount.changePassword("password123", "newPassword"));
    }

    @Test
    public void resetPassword() {
        assertFalse(testAccount.resetPassword("resetPassword0", new String[]{}));
        assertTrue("Password should not have changed", testAccount.checkPassword(testPassword));

        assertFalse(testAccount.resetPassword("resetPassword1", new String[]{"one"}));
        assertTrue("Password should not have changed", testAccount.checkPassword(testPassword));

        assertFalse(testAccount.resetPassword("resetPassword2", new String[]{"one", "two"}));
        assertTrue("Password should not have changed", testAccount.checkPassword(testPassword));

        assertFalse(testAccount.resetPassword("resetPassword3", new String[]{"one", "two", "three"}));
        assertTrue("Password should not have changed", testAccount.checkPassword(testPassword));

        assertFalse(testAccount.resetPassword("resetPassword4", new String[]{"one", "two", "three", "four"}));
        assertTrue("Password should not have changed", testAccount.checkPassword(testPassword));

        assertTrue(testAccount.resetPassword("resetPassword", testQuestions));
        assertTrue("Password should have changed", testAccount.checkPassword("resetPassword"));
    }

    @Test
    public void accountExists() {
        assertFalse(Account.accountExists("blah"));
        assertFalse(Account.accountExists(testRealName));
        assertFalse(Account.accountExists(corruptedString));

        assertTrue(Account.accountExists(testUserName));
    }

    @Test
    public void getAccount() {
        assertNull(Account.getAccount(corruptedString));

        Account test1 = Account.getAccount(testUserName);
        assertArrayEquals(testAccount.getSecurityQuestions(), test1.getSecurityQuestions());
        assertEquals(testAccount.getName(), test1.getName());
        assertEquals(testAccount.getUserName(), test1.getUserName());
    }

    @Test
    public void getName() {
        assertNull(Account.getName(corruptedString));

        assertEquals(testRealName, Account.getName(testUserName));
    }

    @Test
    public void getSecurityQuestions() {
        assertArrayEquals("Security questions did not match", testQuestions, testAccount.getSecurityQuestions());
    }

    @Test
    public void checkCredentials() {
        assertFalse(Account.checkCredentials("someUser", "somePassword"));
        assertFalse(Account.checkCredentials(null, "somePassword"));
        assertFalse(Account.checkCredentials("someUser", null));
        assertFalse(Account.checkCredentials(null, null));
        assertFalse(Account.checkCredentials(testUserName, "somePassword"));
        assertFalse(Account.checkCredentials("someUser", testPassword));
        assertFalse(Account.checkCredentials("someUser", corruptedString));
        assertFalse(Account.checkCredentials(corruptedString, "somePassword"));

        assertTrue(Account.checkCredentials(testUserName, testPassword));
    }

    @Test
    public void checkPassword() {
        assertFalse(testAccount.checkPassword("SomePassword"));
        assertFalse(testAccount.checkPassword(""));
        assertFalse(testAccount.checkPassword(null));
        assertFalse(testAccount.checkPassword(corruptedString));

        assertTrue(testAccount.checkPassword(testPassword));
    }
}