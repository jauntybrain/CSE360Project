package cse360Project.test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cse360Project.models.Topic;
import cse360Project.models.User;

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

    /** 
     * The total number of passed tests 
     */
	static int numPassed = 0;
	
    /** 
     * The total number of passed tests 
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
        System.out.println("\nUser Class Automated Testing");

        /**
        * Data that will be used for testing.
        */
        String testUsername = "some username";
        byte[] testPassword = "super secret password".getBytes();
        String testFirstName = "firstname";
        String testLastName = "lastname";
        String testMiddleName = "middlename";
        String testEmail = "email@example.com";

        /**
        * Test user which will be used for test cases.
        */
        User testUser = new User(null, testUsername, testPassword);

        /**
        * Testing username equality.
        */
        assertEqual("Username equality", testUser.getUsername(), testUsername);
        
        /**
        * Testing password equality.
        */
        assertEqual("Password equality", testUser.getPassword(), testPassword);

        /**
        * Testing topics equality.
        */
        List<Topic> testTopics = new ArrayList<>();
        testTopics.add(Topic.INTERMEDIATE);
        assertEqual("Topics equality", testUser.getTopics(), testTopics);

        /**
        * Testing if user is fully registered.
        */
        assertFalse("Is User Fully Registered False", testUser.isFullyRegistered());

        /**
        * Testing first name equality.
        */
        testUser.setFirstName(testFirstName);
        assertEqual("First name equality", testUser.getFirstName(),testFirstName);

        /**
        * Testing last name equality.
        */
        testUser.setLastName(testLastName);
        assertEqual("Last name equality", testUser.getLastName(), testLastName);

        /**
        * Testing email equality.
        */
        testUser.setEmail(testEmail);
        assertEqual("Email equality", testUser.getEmail(), testEmail);

        /**
        * Testing middle name equality.
        */
        testUser.setMiddleName(testMiddleName);
        assertEqual("Middle Name equality", testUser.getMiddleName(), testMiddleName);

        /**
        * Testing if user is fully registered.
        */
        assertTrue("Is User Fully Registered True", testUser.isFullyRegistered());

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
}