package cse360Project.test;

import java.util.ArrayList;
import java.util.List;

import cse360Project.models.InvitationCode;
import cse360Project.models.Role;

/*******
 * <p> InvitationCode Testing Automation Class </p>
 * 
 * <p> Description: A class to test the actual InvitationCode class. </p>
 * 
 * <p> Copyright: CSE 360 Team Th02 © 2024 </p>
 * 
 * @author CSE 360 Team Th02
 * 
 * @version 1.00 2024-10-09 Phase one
 * 
 */

public class InvitationCodeTestingAutomation {

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
        System.out.println("\nInvitationCode Class Automated Testing");

        /**
        * Data that will be used for testing.
        */
        String code = "secretcode";
        List<Role> testRoles = new ArrayList<>();
        testRoles.add(Role.STUDENT);

        /**
        * Test code that will be used for test cases.
        */
        InvitationCode testCode = new InvitationCode(null, code, testRoles);

        /**
        * Testing code equality.
        */
        assertEqual("Code equality", testCode.getCode(), code);

        /**
        * Testing code usage equality.
        */
        testCode.setUsed(true);
        assertTrue("Used Code", testCode.isUsed());

        /**
        * Testing roles equality.
        */
        List<Role> testRoles2 = new ArrayList<>();
        testRoles2.add(Role.STUDENT);
        assertEqual("Roles equality", testCode.getRoles(), testRoles2);

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
}