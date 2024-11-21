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

	/**
     * Instance used for testing.
     */
	private static ArticleGroup articleGroup;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		articleGroup = new ArticleGroup(1, "ag1", true, true);
	}

	/**
     * Testing articlegroup id equality.
     */
	@Test
	public void testGetId() {
		assertEquals("ArticleGroup id", articleGroup.getId(), 1);
	}
	
	/**
     * Testing article group id equality.
     */
	@Test
	public void testGetName() {
		assertEquals("ArticleGroup name", articleGroup.getName(), "ag1");
	}
	
	/**
     * Testing article group is admin.
     */
	@Test
	public void testIsAdmin() {
		assertEquals("ArticleGroup is admin", articleGroup.isAdmin(), true);
	}
	
	/**
     * Testing article group is protected.
     */
	@Test
	public void testIsProtected() {
		assertEquals("ArticleGroup is protected", articleGroup.isProtected(), true);
	}
	
	/**
     * Testing article set id.
     */
	@Test
	public void testSetId() {
		articleGroup.setId(2);
		assertEquals("ArticleGroup change id", articleGroup.getId(), 2);
	}
	
	/**
     * Testing article set name.
     */
	@Test
	public void testSetName() {
		articleGroup.setName("ag2");
		assertEquals("ArticleGroup change name", articleGroup.getName(), "ag2");
	}
	
	/**
     * Testing article set protected.
     */
	@Test
	public void testSetIsProtected() {
		articleGroup.setProtected(false);
		assertEquals("ArticleGroup change is protected", articleGroup.isProtected(), false);
	}
}
