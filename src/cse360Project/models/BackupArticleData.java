package cse360Project.models;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class BackupArticleData implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Map<String, String>> encryptedArticles;
    private List<ArticleGroup> groups;
    private List<ArticleGroupUser> groupUsers;
    private List<ArticleGroupArticle> groupArticles;

    public BackupArticleData(List<Map<String, String>> encryptedArticles,
            List<ArticleGroup> groups,
            List<ArticleGroupUser> groupUsers,
            List<ArticleGroupArticle> groupArticles) {
        this.encryptedArticles = encryptedArticles;
        this.groups = groups;
        this.groupUsers = groupUsers;
        this.groupArticles = groupArticles;
    }

    // Getters
    public List<Map<String, String>> getEncryptedArticles() {
        return encryptedArticles;
    }

    public List<ArticleGroup> getGroups() {
        return groups;
    }

    public List<ArticleGroupUser> getGroupUsers() {
        return groupUsers;
    }

    public List<ArticleGroupArticle> getGroupArticles() {
        return groupArticles;
    }
}