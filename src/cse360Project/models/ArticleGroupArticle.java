package cse360Project.models;

import java.io.Serializable;

/*******
 * <p>
 * ArticleGroupArticle.
 * </p>
 * 
 * <p>
 * Description: Represents the article group article data structure. Used as a
 * DTO in backups.
 * </p>
 * 
 * <p>
 * Copyright: CSE 360 Team Th02 © 2024
 * </p>
 * 
 * @version 1.00 2024-11-21 Phase three
 * 
 */
public class ArticleGroupArticle implements Serializable {
    private static final long serialVersionUID = 1L;

    private int groupId;
    private String articleId;

    /**
     * Creates a new ArticleGroupArticle.
     * 
     * @param groupId   the ID of the group.
     * @param articleId the ID of the article.
     */
    public ArticleGroupArticle(int groupId, String articleId) {
        this.groupId = groupId;
        this.articleId = articleId;
    }

    /**
     * Gets the ID of the group.
     * 
     * @return the ID of the group.
     */
    public int getGroupId() {
        return groupId;
    }

    /**
     * Gets the ID of the article.
     * 
     * @return the ID of the article.
     */
    public String getArticleId() {
        return articleId;
    }
}