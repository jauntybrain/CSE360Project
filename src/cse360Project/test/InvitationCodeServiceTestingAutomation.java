package cse360Project.test;

import java.util.ArrayList;
import java.util.List;

import cse360Project.models.Role;
import cse360Project.services.InvitationCodeService;

/*******
 * <p> InvitationCodeService Testing Automation Class </p>
 * 
 * <p> Description: A class to test the actual InvitationCodeService class. </p>
 * 
 * <p> Copyright: CSE 360 Team Th02 © 2024 </p>
 * 
 * @author CSE 360 Team Th02
 * 
 * @version 1.00 2024-10-09 Phase one
 * 
 */

public class InvitationCodeServiceTestingAutomation {

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
        System.out.println("\nInvitationCodeService Class Automated Testing");

        /**
        * Data that will be used for testing.
        */
        String testCode = "secretcode";
        List<Role> testRoles = new ArrayList<>();
        testRoles.add(Role.STUDENT);

        /**
        * The InvitationCodeService instance that is going to be tested.
        */
        InvitationCodeService testInstance = InvitationCodeService.getInstance();
        
        /**
        * Testing roles equality.
        */
        boolean result = testInstance.addInvitationCode(testCode, testRoles);
        assertTrue("Code successfully created", result);
        assertEqual("Roles equality", testInstance.getRolesForInvitationCode(testCode), testRoles);

        /**
        * Testing if one time code was used.
        */
        String oneTimeCode = testInstance.generateOneTimeCode(testRoles);
        assertTrue("One Time Code Not Used", testInstance.validateInvitationCode(oneTimeCode));
        
        /**
        * Testing redeeming one time code.
        */
        assertEqual("Redeem One Time Code", testInstance.redeemInvitationCode(oneTimeCode), testRoles);

        /**
        * Testing if one time code was used.
        */
        assertFalse("One Time Code Used", testInstance.validateInvitationCode(oneTimeCode));

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
}