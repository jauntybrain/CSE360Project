/**
 * 
 */
package cse360Project.test;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import cse360Project.models.ArticleGroupUser;

/*******
 * <p>
 * ArticleGroupUserJUnitTesting JUnit Testing Class
 * </p>
 * 
 * <p>
 * Description: A class to test the actual ArticleGroupUser class.
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
public class ArticleGroupUserJUnitTesting {

	private static ArticleGroupUser articleGroupUser;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		articleGroupUser = new ArticleGroupUser(1, "user1", true);
	}

	@Test
	public void testGetGroupId() {
		assertEquals("ArticleGroupUser get group id", articleGroupUser.getGroupId(), 1);
	}
	
	@Test
	public void testGetUserId() {
		assertEquals("ArticleGroupUser get user id", articleGroupUser.getUserId(), "user1");
	}

	@Test
	public void testIsAdmin() {
		assertEquals("ArticleGroupUser is admin", articleGroupUser.isAdmin(), true);
	}
	
}
