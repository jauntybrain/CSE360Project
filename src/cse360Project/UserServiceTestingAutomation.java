package cse360Project;

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

public class UserServiceTestingAutomation {

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
        System.out.println("\nUser Service Class Automated Testing");

        String testUsername = "username";
        String testPassword = "Pass1wor!d";
        UserService testInstance = UserService.getInstance();

        assertEqual("Empty Users Table", testInstance.getAllUsers().size(), 0);

        User testUser = testInstance.register(testUsername, testPassword);
        
        assertEqual("User Amount", testInstance.getAllUsers().size(), 1);

        assertEqual("First User Roles", testUser.getRoles().get(0), Role.ADMIN);

        testInstance.setCurrentUser(testUser);

        assertEqual("Current User", testInstance.getCurrentUser().getUsername(), testUser.getUsername());

        assertTrue("Username Validation 1", testInstance.isValidUsername("Bestgamer123"));
        assertTrue("Username Validation 2", testInstance.isValidUsername("123123123123"));
        assertFalse("Username Validation 3", testInstance.isValidUsername("User?"));
        assertFalse("Username Validation 4", testInstance.isValidUsername("6".repeat(37)));
        assertFalse("Username Validation 5", testInstance.isValidUsername(""));

        assertNull("Non Existent User", testInstance.login(testUsername, ""));
        assertNotNull("Existing User", testInstance.login(testUsername, testPassword));

        assertTrue("Password Validation 1", testInstance.isValidPassword("123ASD!asd"));
        assertFalse("Password Validation 2", testInstance.isValidPassword("123qwe!asd"));
        assertFalse("Password Validation 3", testInstance.isValidPassword("123ASDddasd"));
        assertFalse("Password Validation 4", testInstance.isValidPassword("QWerty!iuo"));
        assertFalse("Password Validation 5", testInstance.isValidPassword("asd"));
        assertFalse("Password Validation 6", testInstance.isValidPassword("123ASD!asd".repeat(9)));

        assertTrue("Email Validation 1", testInstance.isValidEmail("student@asu.edu"));
        assertFalse("Email Validation 2", testInstance.isValidEmail("student@"));
        assertFalse("Email Validation 3", testInstance.isValidEmail("student.asu"));
        assertFalse("Email Validation 4", testInstance.isValidEmail("student@asuedu"));
        assertFalse("Email Validation 5", testInstance.isValidEmail("student@asu..edu"));

        assertTrue("Name Validation 1", testInstance.isValidName("Real Name"));
        assertFalse("Name Validation 2", testInstance.isValidName("Notrealname123"));
        assertFalse("Name Validation 3", testInstance.isValidName("Name".repeat(20)));

        String oneTimePassword = testInstance.setOneTimePassword(testUsername);
        assertTrue("One Time Password", testInstance.verifyOneTimePassword(testUsername, oneTimePassword));

        String testValidationCode = "code";
        testInstance.setCurrentValidationCode(testValidationCode);

        assertEqual("Validation Code", testInstance.getCurrentInvitationCode(), testValidationCode);

        testInstance.deleteUser(testUsername);

        assertEqual("Delete User", testInstance.getAllUsers().size(), 0);

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