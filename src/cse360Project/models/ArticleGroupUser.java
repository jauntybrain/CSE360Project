package cse360Project.models;

import java.io.Serializable;

public class ArticleGroupUser implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int groupId;
    private String userId;
    private boolean isAdmin;

    public ArticleGroupUser(int groupId, String userId, boolean isAdmin) {
        this.groupId = groupId;
        this.userId = userId;
        this.isAdmin = isAdmin;
    }

    // Getters
    public int getGroupId() {
        return groupId;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}