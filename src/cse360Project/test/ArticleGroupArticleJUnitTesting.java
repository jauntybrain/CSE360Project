/**
 * 
 */
package cse360Project.test;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import cse360Project.models.ArticleGroupArticle;

/*******
 * <p>
 * ArticleGroupArticleJUnitTesting JUnit Testing Class
 * </p>
 * 
 * <p>
 * Description: A class to test the actual ArticleGroupArticle class.
 * </p>
 * 
 * <p>
 * Copyright: CSE 360 Team Th02 © 2024
 * </p>
 * 
 * @author CSE 360 Team Th02
 * 
 * @version 1.00 2024-11-20 Phase three
 * 
 */
public class ArticleGroupArticleJUnitTesting {

	/**
     * Instance used for testing.
     */
	private static ArticleGroupArticle articleGroupArticle;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		articleGroupArticle = new ArticleGroupArticle(1, "aga1");
	}

	/**
     * Testing article id equality.
     */
	@Test
	public void testGetArticleId() {
		assertEquals("ArticleGroupArticle get article id", articleGroupArticle.getArticleId(), "aga1");
	}

	/**
     * Testing group id equality.
     */
	@Test
	public void testGetGroupId() {
		assertEquals("ArticleGroupArticle get group id", articleGroupArticle.getGroupId(), 1);
	}
	
}
