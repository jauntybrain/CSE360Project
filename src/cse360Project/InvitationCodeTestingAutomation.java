package cse360Project;

import java.util.ArrayList;
import java.util.List;

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
        System.out.println("\nInvitationCode Class Automated Testing");

        String code = "secretcode";
        List<Role> testRoles = new ArrayList<>();
        testRoles.add(Role.STUDENT);

        InvitationCode testCode = new InvitationCode(code, testRoles);

        assertEqual("Code Equality", testCode.getCode(), code);

        testCode.setUsed(true);

        assertTrue("Used Code", testCode.isUsed());

        List<Role> testRoles2 = new ArrayList<>();
        testRoles2.add(Role.STUDENT);
        assertEqual("Roles Equality", testCode.getRoles(), testRoles2);

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
}