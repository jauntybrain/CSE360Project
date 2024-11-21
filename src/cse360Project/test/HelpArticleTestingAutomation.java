package cse360Project.test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import cse360Project.models.HelpArticle;
import cse360Project.models.Topic;

/*******
 * <p>
 * User Testing Automation Class
 * </p>
 * 
 * <p>
 * Description: A class to test the actual User class.
 * </p>
 * 
 * <p>
 * Copyright: CSE 360 Team Th02 © 2024
 * </p>
 * 
 * @author CSE 360 Team Th02
 * 
 * @version 1.00 2024-10-30 Phase two
 * 
 */

public class HelpArticleTestingAutomation {

    /**
     * The total number of passed tests
     */
    static int numPassed = 0;

    /**
     * The total number of passed tests
     */
    static int numFailed = 0;

    /**********
     * This is the main method of the testing automation class that runs all of the
     * test cases
     * and displays why each individual test failed and shows the total amounts of
     * passed and failed
     * tests
     * 
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        /**
         * Formatting.
         */
        System.out.println("____________________________________________________________________________");
        System.out.println("\nHelpArticle Class Automated Testing");

        /**
         * Data that will be used for testing.
         */
        String testUuid = UUID.randomUUID().toString();
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
        HelpArticle testHelpArticle = new HelpArticle(testUuid, testTitle, testAuthors, testAbstractText, testKeywords,
                testBody, testReferences, testGroups, testLevel);

        /**
         * Testing uuid equality.
         */
        assertEqual("Uuid equality", testHelpArticle.getUuid(), testUuid);

        /**
         * Testing title equality.
         */
        assertEqual("Title equality", testHelpArticle.getTitle(), new String(testTitle));

        /**
         * Testing authors equality.
         */
        assertEqual("Authors equality", testHelpArticle.getAuthors(), testAuthors);

        /**
         * Testing abstract text equality.
         */
        assertEqual("Abstract text equality", testHelpArticle.getAbstractText(), testAbstractText);

        /**
         * Testing keywords equality.
         */
        assertEqual("Keywords equality", testHelpArticle.getKeywords(), testKeywords);

        /**
         * Testing body equality.
         */
        assertEqual("Body equality", testHelpArticle.getBody(), testBody);

        /**
         * Testing references equality.
         */
        assertEqual("References equality", testHelpArticle.getReferences(), testReferences);

        /**
         * Testing groups equality.
         */
        assertEqual("Groups equality", testHelpArticle.getGroups(), testGroups);

        /**
         * Testing level equality.
         */
        assertEqual("Level equality", testHelpArticle.getLevel(), testLevel);

        /**
         * Testing title equality after setter.
         */
        char[] testTitle2 = "Advanced Java Concepts".toCharArray();
        testHelpArticle.setTitle(testTitle2);
        assertEqual("Title equality after setter", testHelpArticle.getTitle(), new String(testTitle2));

        /**
         * Testing authors equality after setter.
         */
        char[][] testAuthors2 = { "author3".toCharArray(), "author4".toCharArray(), "author5".toCharArray() };
        testHelpArticle.setAuthors(testAuthors2);
        assertEqual("Authors equality after setter", testHelpArticle.getAuthors(), testAuthors2);

        /**
         * Testing abstract text equality after setter.
         */
        char[] testAbstractText2 = "abs".toCharArray();
        testHelpArticle.setAbstractText(testAbstractText2);
        assertEqual("Abstract text equality after setter", testHelpArticle.getAbstractText(), testAbstractText2);

        /**
         * Testing keywords equality after setter.
         */
        char[][] testKeywords2 = { "key2".toCharArray(), "key3".toCharArray(), "key4".toCharArray() };
        testHelpArticle.setKeywords(testKeywords2);
        assertEqual("Keywords equality after setter", testHelpArticle.getKeywords(), testKeywords2);

        /**
         * Testing body equality after setter.
         */
        char[] testBody2 = "something else".toCharArray();
        testHelpArticle.setBody(testBody2);
        assertEqual("Body equality after setter", testHelpArticle.getBody(), testBody2);

        /**
         * Testing references equality after setter.
         */
        char[][] testReferences2 = { "ref4".toCharArray(), "ref55".toCharArray(), "ref6".toCharArray() };
        testHelpArticle.setReferences(testReferences2);
        assertEqual("References equality after setter", testHelpArticle.getReferences(), testReferences2);

        /**
         * Testing groups equality after setter.
         */
        List<Integer> testGroups2 = Arrays.asList(2, 3, 4);
        testHelpArticle.setGroups(testGroups2);
        assertEqual("Groups equality after setter", testHelpArticle.getGroups(), testGroups2);

        /**
         * Testing level equality after setter.
         */
        Topic testLevel2 = Topic.INTERMEDIATE;
        testHelpArticle.setLevel(testLevel2);
        assertEqual("Level equality after setter", testHelpArticle.getLevel(), testLevel2);

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