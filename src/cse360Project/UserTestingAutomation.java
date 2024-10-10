package cse360Project;

import java.util.ArrayList;
import java.util.List;

/*******
 * <p> User Testing Automation Class </p>
 * 
 * <p> Description: A class to test the actual User class. </p>
 * 
 * <p> Copyright: CSE 360 Team Th02 © 2024 </p>
 * 
 * @author CSE 360 Team Th02
 * 
 * @version 1.00 2024-10-09 Phase one
 * 
 */

public class UserTestingAutomation {

    /** The total number of passed tests */
	static int numPassed = 0;
	
	/** The total number of passed tests */
    static int numFailed = 0;
    
    /**********
	 * This is the main method of the testing automation class that runs all of the test cases
	 * and displays why each individual test failed and shows the total amounts of passed and failed
	 * tests
	 */
    public static void main(String[] args) {
        // Formatting
        System.out.println("____________________________________________________________________________");
        System.out.println("\nUser Class Automated Testing");

        String testUsername = "some username";
        byte[] testPassword = "super secret password".getBytes();
        String testFirstName = "firstname";
        String testLastName = "lastname";
        String testMiddleName = "middlename";
        String testEmail = "email@example.com";

        User testUser = new User(testUsername, testPassword);

        assertEqual("Username Equality", testUser.getUsername(), testUsername);
        
        assertEqual("Password Equality", testUser.getPassword(), testPassword);

        List<Topic> testTopics = new ArrayList<>();
        testTopics.add(Topic.INTERMEDIATE);
        assertEqual("Topics Equality", testUser.getTopics(), testTopics);

        assertFalse("isFullyRegistered1", testUser.isFullyRegistered());

        testUser.setFirstName(testFirstName);

        assertEqual("First name Equality", testUser.getFirstName(),testFirstName);

        testUser.setLastName(testLastName);

        assertEqual("Last name Equality", testUser.getLastName(), testLastName);

        testUser.setEmail(testEmail);

        assertEqual("Email Equality", testUser.getEmail(), testEmail);

        testUser.setMiddleName(testMiddleName);

        assertEqual("Middle Name Equality", testUser.getMiddleName(), testMiddleName);

        assertTrue("isFullyRegistered2", testUser.isFullyRegistered());

        // Formatting
        System.out.println("\nNumber of tests passed: " + numPassed);
        System.out.println("Number of tests failed: " + numFailed);
        System.out.println();
        System.out.println("____________________________________________________________________________");
    }
    
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