package cse360Project.models;

import java.io.Serializable;

public class ArticleGroupArticle implements Serializable {
    private static final long serialVersionUID = 1L;

    private int groupId;
    private String articleId;

    public ArticleGroupArticle(int groupId, String articleId) {
        this.groupId = groupId;
        this.articleId = articleId;
    }

    // Getters
    public int getGroupId() {
        return groupId;
    }

    public String getArticleId() {
        return articleId;
    }
}