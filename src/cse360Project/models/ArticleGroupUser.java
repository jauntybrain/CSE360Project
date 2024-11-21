package cse360Project.models;

import java.io.Serializable;

/*******
 * <p>
 * ArticleGroupUser.
 * </p>
 * 
 * <p>
 * Description: Represents the article group user data structure. Used as a DTO in
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
public class ArticleGroupUser implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int groupId;
    private String userId;
    private boolean isAdmin;

    /**
     * Creates a new ArticleGroupUser.
     * 
     * @param groupId the ID of the group.
     * @param userId  the ID of the user.
     * @param isAdmin whether the user has admin privileges.
     */
    public ArticleGroupUser(int groupId, String userId, boolean isAdmin) {
        this.groupId = groupId;
        this.userId = userId;
        this.isAdmin = isAdmin;
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
     * Gets the ID of the user.
     * 
     * @return the ID of the user.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Gets whether the user has admin privileges.
     * 
     * @return whether the user has admin privileges.
     */
    public boolean isAdmin() {
        return isAdmin;
    }
}