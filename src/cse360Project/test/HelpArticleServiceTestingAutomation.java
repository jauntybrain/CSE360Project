package cse360Project.test;

import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import java.util.UUID;

import cse360Project.models.HelpArticle;
import cse360Project.models.Role;
import cse360Project.models.Topic;
import cse360Project.models.User;
import cse360Project.services.HelpArticleService;
import cse360Project.services.UserService;

/*******
 * <p>
 * HelpArticleService Testing Automation Class
 * </p>
 * 
 * <p>
 * Description: A class to test the actual HelpArticleService class.
 * </p>
 * 
 * <p>
 * Copyright: CSE 360 Team Th02 © 2024
 * </p>
 * 
 * @author CSE 360 Team Th02
 * 
 * @version 1.00 2024-10-09 Phase one
 * 
 */

public class HelpArticleServiceTestingAutomation {

	/**
	 * The total number of passed tests
	 */
	static int numPassed = 0;

	/**
	 * The total number of failed tests
	 */
	static int numFailed = 0;

	/**********
	 * This is the main method of the testing automation class that runs all of the
	 * test cases and displays why each individual test failed and shows the total
	 * amounts of passed and failed tests
	 * 
	 * @param args The command line arguments.
	 */
	public static void main(String[] args) {
		/**
		 * Formatting.
		 */
		System.out.println("____________________________________________________________________________");
		System.out.println("\nHelp Article Service Class Automated Testing");

		/**
		 * Data that will be used for testing.
		 */
		String testUuid1 = UUID.randomUUID().toString();
		String testUuid2 = UUID.randomUUID().toString();
		char[] testTitle = "testTitle".toCharArray();
		char[][] testAuthors = { "author1".toCharArray(), "author2".toCharArray(), "author3".toCharArray() };
		char[] testAbstractText = "abstract".toCharArray();
		char[][] testKeywords = { "key1".toCharArray(), "key2".toCharArray(), "key3".toCharArray() };
		char[] testBody = "body".toCharArray();
		char[][] testReferences = { "ref1".toCharArray(), "ref2".toCharArray(), "ref3".toCharArray() };
		List<Integer> testGroups = Arrays.asList(1, 2, 3);
		Topic testLevel = Topic.ADVANCED;

		/**
		 * Test help article which will be used for test cases.
		 */
		HelpArticle testHelpArticle1 = new HelpArticle(testUuid1, testTitle, testAuthors, testAbstractText,
				testKeywords, testBody, testReferences, testGroups, testLevel);
		HelpArticle testHelpArticle2 = new HelpArticle(testUuid2, testTitle, testAuthors, testAbstractText,
				testKeywords, testBody, testReferences, testGroups, testLevel);

		try {
			HelpArticleService testService = new HelpArticleService();
			final boolean cleanDBResult = testService.cleanDB();
			assertTrue("Clean DB", cleanDBResult);
			UserService testUserService = UserService.getInstance();
			testUserService.setCurrentUser(new User(null, "username", UserService.hashPassword("password")));
			testUserService.setCurrentRole(Role.STUDENT);

			/**
			 * Testing that initially there is 0 articles.
			 */
			assertEqual("Empty Articles Table", testService.getAllArticles().size(), 0);

			/**
			 * Testing that initially there is 0 groups.
			 */
			assertEqual("Empty Groups Table", testService.getAllArticles().size(), 0);

			/**
			 * Testing adding 2 articles to the db.
			 */
			testService.modifyArticle(testHelpArticle1, false);
			testService.modifyArticle(testHelpArticle2, false);
			assertEqual("Add 2 articles to the db", testService.getAllArticles().size(), 2);

			/**
			 * Testing deleting an article.
			 */
			testService.deleteArticle(testUuid2);
			assertEqual("Deleting an article", testService.getAllArticles().size(), 1);

			/**
			 * Testing editing an article.
			 */
			char[] testTitle2 = "Advanced Java Concepts".toCharArray();
			testHelpArticle1.setTitle(testTitle2);
			char[][] testAuthors2 = { "author3".toCharArray(), "author4".toCharArray(), "author5".toCharArray() };
			testHelpArticle1.setAuthors(testAuthors2);
			testService.modifyArticle(testHelpArticle1, true);
			assertEqual("Editing article title", testService.getAllArticles().get(0).getTitle(), testTitle2);
			assertEqual("Editing article authors", testService.getAllArticles().get(0).getAuthors(), testAuthors2);

			/**
			 * Testing back up and restore.
			 */
			testService.backupArticles("articlesBackup.bak", Collections.emptyList());
			testService.restoreArticles("articlesBackup.bak", false);
			assertEqual("Back up and restore", testService.getAllArticles().size(), 1);

			/**
			 * Clean up.
			 */
			final boolean cleanDBResult2 = testService.cleanDB();
			assertTrue("Clean DB", cleanDBResult2);
		} catch (Exception e) {
			System.out.println();
			System.out.println(e);
		}

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
	 * @param testName     The test name to display.
	 * @param testCase     The value to be tested.
	 * @param expectedCase The expected value.
	 */
	private static <T> void assertEqual(String testName, T testCase, T expectedCase) {
		boolean isEqual;
		if (testCase instanceof char[] && expectedCase instanceof char[]) {
			isEqual = Arrays.equals((char[]) testCase, (char[]) expectedCase);
		} else if (testCase instanceof char[][] && expectedCase instanceof char[][]) {
			isEqual = Arrays.deepEquals((char[][]) testCase, (char[][]) expectedCase);
		} else {
			isEqual = testCase.equals(expectedCase);
		}

		if (isEqual) {
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