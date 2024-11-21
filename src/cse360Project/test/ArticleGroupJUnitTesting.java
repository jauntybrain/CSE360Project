/**
 * 
 */
package cse360Project.test;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import cse360Project.models.ArticleGroup;

/*******
 * <p>
 * ArticleGroupJUnitTesting JUnit Testing Class
 * </p>
 * 
 * <p>
 * Description: A class to test the actual ArticleGroup class.
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
public class ArticleGroupJUnitTesting {

	private static ArticleGroup articleGroup;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		articleGroup = new ArticleGroup(1, "ag1", true, true);
	}

	@Test
	public void testGetId() {
		assertEquals("ArticleGroup id", articleGroup.getId(), 1);
	}
	
	@Test
	public void testGetName() {
		assertEquals("ArticleGroup name", articleGroup.getName(), "ag1");
	}
	
	@Test
	public void testIsAdmin() {
		assertEquals("ArticleGroup is admin", articleGroup.isAdmin(), true);
	}
	
	@Test
	public void testIsProtected() {
		assertEquals("ArticleGroup is protected", articleGroup.isProtected(), true);
	}
	
	@Test
	public void testSetId() {
		articleGroup.setId(2);
		assertEquals("ArticleGroup change id", articleGroup.getId(), 2);
	}
	
	@Test
	public void testSetName() {
		articleGroup.setName("ag2");
		assertEquals("ArticleGroup change name", articleGroup.getName(), "ag2");
	}
	
	@Test
	public void testSetIsProtected() {
		articleGroup.setProtected(false);
		assertEquals("ArticleGroup change is protected", articleGroup.isProtected(), false);
	}
}
