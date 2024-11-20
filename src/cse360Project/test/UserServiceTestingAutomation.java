package cse360Project.test;

import cse360Project.models.Role;
import cse360Project.models.User;
import cse360Project.services.UserService;

/*******
 * <p> UserService Testing Automation Class </p>
 * 
 * <p> Description: A class to test the actual UserService class. </p>
 * 
 * <p> Copyright: CSE 360 Team Th02 © 2024 </p>
 * 
 * @author CSE 360 Team Th02
 * 
 * @version 1.00 2024-10-09 Phase one
 * 
 */

public class UserServiceTestingAutomation {

    /** 
     * The total number of passed tests 
     */
	static int numPassed = 0;
	
    /** 
     * The total number of failed tests 
     */
    static int numFailed = 0;
    
    /**********
     * This is the main method of the testing automation class that runs all of the test cases
     * and displays why each individual test failed and shows the total amounts of passed and failed
     * tests
     * 
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        /**
        * Formatting.
        */
        System.out.println("____________________________________________________________________________");
        System.out.println("\nUser Service Class Automated Testing");

        /**
        * Data that will be used for testing.
        */
        String testUsername = "username";
        String testPassword = "Pass1wor!d";
        UserService testInstance = UserService.getInstance();

        /**
        * Testing that initially there is 0 users.
        */
        assertEqual("Empty Users Table", testInstance.getAllUsers().size(), 0);

        /**
        * Testing user registration.
        */
        User testUser = testInstance.register(testUsername, testPassword);
        assertEqual("User Amount", testInstance.getAllUsers().size(), 1);

        /**
        * Testing first user roles.
        */
        assertEqual("First User Roles", testUser.getRoles().get(0), Role.ADMIN);

        /**
        * Testing setting current user.
        */
        testInstance.setCurrentUser(testUser);
        assertEqual("Current User", testInstance.getCurrentUser().getUuid(), testUser.getUuid());

        /**
        * Testing the username validator.
        */
        assertTrue("Username Validation 1", testInstance.isValidUsername("Bestgamer123"));
        assertTrue("Username Validation 2", testInstance.isValidUsername("123123123123"));
        assertFalse("Username Validation 3", testInstance.isValidUsername("User?"));
        assertFalse("Username Validation 4", testInstance.isValidUsername("6".repeat(37)));
        assertFalse("Username Validation 5", testInstance.isValidUsername(""));

        /**
        * Testing login.
        */
        assertNull("Non Existent User", testInstance.login(testUsername, ""));
        assertNotNull("Existing User", testInstance.login(testUsername, testPassword));

        /**
        * Testing the password validator.
        */
        assertTrue("Password Validation 1", testInstance.isValidPassword("123ASD!asd"));
        assertFalse("Password Validation 2", testInstance.isValidPassword("123qwe!asd"));
        assertFalse("Password Validation 3", testInstance.isValidPassword("123ASDddasd"));
        assertFalse("Password Validation 4", testInstance.isValidPassword("QWerty!iuo"));
        assertFalse("Password Validation 5", testInstance.isValidPassword("asd"));
        assertFalse("Password Validation 6", testInstance.isValidPassword("123ASD!asd".repeat(9)));

        /**
        * Testing the email validator.
        */
        assertTrue("Email Validation 1", testInstance.isValidEmail("student@asu.edu"));
        assertFalse("Email Validation 2", testInstance.isValidEmail("student@"));
        assertFalse("Email Validation 3", testInstance.isValidEmail("student.asu"));
        assertFalse("Email Validation 4", testInstance.isValidEmail("student@asuedu"));
        assertFalse("Email Validation 5", testInstance.isValidEmail("student@asu..edu"));

        /**
        * Testing the name validator.
        */
        assertTrue("Name Validation 1", testInstance.isValidName("Real Name"));
        assertFalse("Name Validation 2", testInstance.isValidName("Notrealname123"));
        assertFalse("Name Validation 3", testInstance.isValidName("Name".repeat(20)));

        /**
        * Testing the name validator.
        */
        String oneTimePassword = testInstance.setOneTimePassword(testUsername);
        assertTrue("One Time Password", testInstance.verifyOneTimePassword(testUsername, oneTimePassword));

        
        /**
        * Testing current invitaion code.
        */
        String testValidationCode = "code";
        testInstance.setCurrentValidationCode(testValidationCode);
        assertEqual("Validation Code", testInstance.getCurrentInvitationCode(), testValidationCode);

        /**
        * Testing deleting a user.
        */
        final boolean deleteUserResult = testInstance.deleteUser(testUsername);
        assertTrue("Delete User", deleteUserResult);
        assertEqual("Delete User", testInstance.getAllUsers().size(), 0);

        /**
        * Clean up.
        */
        final boolean cleanDBResult = testInstance.cleanDB();
        assertTrue("Clean DB", cleanDBResult);

        /**
        * Formatting.
        */
        System.out.println("\nNumber of tests passed: " + numPassed);
        System.out.println("Number of tests failed: " + numFailed);
        System.out.println();
        System.out.println("____________________________________________________________________________");
    }
    
    /**
     * Asserts that the test result equals to the expected value.
     * 
     * @param testName The test name to display.
     * @param testCase The value to be tested.
     * @param expectedCase The expected value.
     */
    private static <T> void assertEqual(String testName, T testCase, T expectedCase) {
        if (testCase.equals(expectedCase)) {
            numPassed++;
            System.out.println("\nTest " + testName + " - passed");
        } else {
            numFailed++;
            System.out.println("\nTest " + testName + " - failed");
            System.out.println("Test value " + testCase + " || Expected value " + expectedCase);
        }
    }
    
    /**
     * Asserts that the test result value is true.
     * 
     * @param testName The test name to display.
     * @param testCase The value to be tested.
     */
    private static void assertTrue(String testName, boolean testCase) {
        if (testCase) {
            numPassed++;
            System.out.println("\nTest " + testName + " - passed");
        } else {
            numFailed++;
            System.out.println("\nTest " + testName + " - failed");
            System.out.println("Test value " + testCase + " || Expected value true");
        }
    }

    /**
     * Asserts that the test result value is false.
     * 
     * @param testName The test name to display.
     * @param testCase The value to be tested.
     */
    private static void assertFalse(String testName, boolean testCase) {
        if (testCase == false) {
            numPassed++;
            System.out.println("\nTest " + testName + " - passed");
        } else {
            numFailed++;
            System.out.println("\nTest " + testName + " - failed");
            System.out.println("Test value " + testCase + " || Expected value false");
        }
    }

    /**
     * Asserts that the test result value is null.
     * 
     * @param testName The test name to display.
     * @param testCase The value to be tested.
     */
    private static <T> void assertNull(String testName, T testCase) {
        if (testCase == null) {
            numPassed++;
            System.out.println("\nTest " + testName + " - passed");
        } else {
            numFailed++;
            System.out.println("\nTest " + testName + " - failed");
            System.out.println("Test value " + testCase + " || Expected value null");
        }
    }

    /**
     * Asserts that the test result value is not null.
     * 
     * @param testName The test name to display.
     * @param testCase The value to be tested.
     */
    private static <T> void assertNotNull(String testName, T testCase) {
        if (testCase != null) {
            numPassed++;
            System.out.println("\nTest " + testName + " - passed");
        } else {
            numFailed++;
            System.out.println("\nTest " + testName + " - failed");
            System.out.println("Test value " + testCase + " || Expected value not null");
        }
    }
}