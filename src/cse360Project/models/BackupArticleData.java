package cse360Project.models;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/*******
 * <p>
 * BackupArticleData.
 * </p>
 * 
 * <p>
 * Description: Represents the article backup data structure. Used as a DTO in
 * backups.
 * </p>
 * 
 * <p>
 * Copyright: CSE 360 Team Th02 © 2024
 * </p>
 * 
 * @version 1.00 2024-11-21 Phase three
 * 
 */
public class BackupArticleData implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Map<String, String>> encryptedArticles;
    private List<ArticleGroup> groups;
    private List<ArticleGroupUser> groupUsers;
    private List<ArticleGroupArticle> groupArticles;

    /**
     * Creates a new BackupArticleData.
     * 
     * @param encryptedArticles the encrypted articles.
     * @param groups            the groups.
     * @param groupUsers        the group users.
     * @param groupArticles     the group articles.
     */
    public BackupArticleData(List<Map<String, String>> encryptedArticles,
            List<ArticleGroup> groups,
            List<ArticleGroupUser> groupUsers,
            List<ArticleGroupArticle> groupArticles) {
        this.encryptedArticles = encryptedArticles;
        this.groups = groups;
        this.groupUsers = groupUsers;
        this.groupArticles = groupArticles;
    }

    /**
     * Gets the encrypted articles.
     * 
     * @return the encrypted articles.
     */
    public List<Map<String, String>> getEncryptedArticles() {
        return encryptedArticles;
    }

    /**
     * Gets the groups.
     * 
     * @return the groups.
     */
    public List<ArticleGroup> getGroups() {
        return groups;
    }

    /**
     * Gets the group users.
     * 
     * @return the group users.
     */
    public List<ArticleGroupUser> getGroupUsers() {
        return groupUsers;
    }

    /**
     * Gets the group articles.
     * 
     * @return the group articles.
     */
    public List<ArticleGroupArticle> getGroupArticles() {
        return groupArticles;
    }
}