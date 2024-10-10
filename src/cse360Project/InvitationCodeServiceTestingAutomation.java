package cse360Project;

import java.util.ArrayList;
import java.util.List;

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
        System.out.println("\nInvitationCodeService Class Automated Testing");

        String testCode = "secretcode";
        List<Role> testRoles = new ArrayList<>();
        testRoles.add(Role.STUDENT);

        InvitationCodeService testInstance = InvitationCodeService.getInstance();

        testInstance.addInvitationCode(testCode, testRoles);

        assertEqual("Roles Equality", testInstance.getRolesForInvitationCode(testCode), testRoles);

        String oneTimeCode = testInstance.generateOneTimeCode(testRoles);
        assertTrue("One Time Code Not Used", testInstance.validateInvitationCode(oneTimeCode));
        
        assertEqual("Redeem One Time Code", testInstance.redeemInvitationCode(oneTimeCode), testRoles);

        assertFalse("One Time Code Used", testInstance.validateInvitationCode(oneTimeCode));

        testInstance.cleanDB();
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
}