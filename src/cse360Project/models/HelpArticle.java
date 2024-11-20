package cse360Project.models;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/*******
 * <p>
 * Article.
 * </p>
 * 
 * <p>
 * Description: Represents the article data structure.
 * </p>
 * 
 * <p>
 * Copyright: CSE 360 Team Th02 © 2024
 * </p>
 * 
 * @version 1.00 2024-10-30 Phase two
 * 
 */
public class HelpArticle implements Serializable {
    private static final long serialVersionUID = 1L;

    private String uuid;
    private char[] title;
    private char[][] authors;
    private char[] abstractText;
    private char[][] keywords;
    private char[] body;
    private char[][] references;
    private List<String> groups;
    private Topic level;

    /**
     * Creates a new Article.
     * 
     * @param uuid         unique ID for the article.
     * @param title        article title.
     * @param authors      list of authors.
     * @param abstractText abstract of the article.
     * @param keywords     list of keywords.
     * @param body         body of the article.
     * @param references   list of references.
     * @param groups       list of groups.
     * @param level        level of the article.
     */
    public HelpArticle(String uuid, char[] title, char[][] authors, char[] abstractText,
            char[][] keywords, char[] body, char[][] references, List<String> groups, Topic level) {
        this.uuid = uuid != null ? uuid : UUID.randomUUID().toString();
        this.title = title;
        this.authors = authors;
        this.abstractText = abstractText;
        this.keywords = keywords;
        this.body = body;
        this.references = references;
        this.groups = groups;
        this.level = level;
    }

    /**
     * Gets the UUID of the article.
     * 
     * @return the UUID.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Gets the title of the article.
     * 
     * @return the title.
     */
    public char[] getTitle() {
        return title;
    }

    /**
     * Sets the title of the article.
     * 
     * @param title the title to set.
     */
    public void setTitle(char[] title) {
        this.title = title;
    }

    /**
     * Gets the authors of the article.
     * 
     * @return the list of authors.
     */
    public char[][] getAuthors() {
        return authors;
    }

    /**
     * Sets the authors of the article.
     * 
     * @param authors the list of authors to set.
     */
    public void setAuthors(char[][] authors) {
        this.authors = authors;
    }

    /**
     * Gets the abstract text of the article.
     * 
     * @return the abstract text.
     */
    public char[] getAbstractText() {
        return abstractText;
    }

    /**
     * Sets the abstract text of the article.
     * 
     * @param abstractText the abstract text to set.
     */
    public void setAbstractText(char[] abstractText) {
        this.abstractText = abstractText;
    }

    /**
     * Gets the keywords of the article.
     * 
     * @return the list of keywords.
     */
    public char[][] getKeywords() {
        return keywords;
    }

    /**
     * Sets the keywords of the article.
     * 
     * @param keywords the list of keywords to set.
     */
    public void setKeywords(char[][] keywords) {
        this.keywords = keywords;
    }

    /**
     * Gets the body of the article.
     * 
     * @return the body.
     */
    public char[] getBody() {
        return body;
    }

    /**
     * Sets the body of the article.
     * 
     * @param body the body to set.
     */
    public void setBody(char[] body) {
        this.body = body;
    }

    /**
     * Gets the references of the article.
     * 
     * @return the list of references.
     */
    public char[][] getReferences() {
        return references;
    }

    /**
     * Sets the references of the article.
     * 
     * @param references the list of references to set.
     */
    public void setReferences(char[][] references) {
        this.references = references;
    }

    /**
     * Gets the groups of the article.
     * 
     * @return the list of groups.
     */
    public List<String> getGroups() {
        return groups;
    }

    /**
     * Sets the groups of the article.
     * 
     * @param groups the list of groups to set.
     */
    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    /**
     * Gets the level of the article.
     * 
     * @return the level.
     */
    public Topic getLevel() {
        return level;
    }

    /**
     * Sets the level of the article.
     * 
     * @param level the level to set.
     */
    public void setLevel(Topic level) {
        this.level = level;
    }

    /**
     * Converts authors list into a comma-separated string.
     * 
     * @return authors as a string.
     */
    public String getAuthorsString() {
        return getConcatenatedString(authors);
    }

    /**
     * Converts groups list into a comma-separated string.
     * 
     * @return groups as a string.
     */
    public String getGroupsString() {
        return String.join(", ", getGroups());
    }

    /**
     * Converts keywords list into a comma-separated string.
     * 
     * @return keywords as a string.
     */
    public String getKeywordsString() {
        return getConcatenatedString(keywords);
    }

    /**
     * Converts references list into a comma-separated string.
     * 
     * @return references as a string.
     */
    public String getReferencesString() {
        return getConcatenatedString(references);
    }

    /**
     * Converts a 2D character array into a comma-separated string.
     * 
     * @param array the 2D character array to convert.
     * @return the concatenated string.
     */
    private String getConcatenatedString(char[][] array) {
        StringBuilder sb = new StringBuilder();
        for (char[] element : array) {
            sb.append(new String(element).trim()).append(", ");
        }
        return sb.toString().replaceAll(", $", "");
    }
}
