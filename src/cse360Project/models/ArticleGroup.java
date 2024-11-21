package cse360Project.models;

import java.io.Serializable;

/*******
 * <p>
 * ArticleGroup.
 * </p>
 * 
 * <p>
 * Description: Represents the article group data structure.
 * </p>
 * 
 * <p>
 * Copyright: CSE 360 Team Th02 © 2024
 * </p>
 * 
 * @version 1.00 2024-11-20 Phase three
 * 
 */
public class ArticleGroup implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private boolean isProtected;
    private boolean isAdmin;

    /**
     * Creates a new ArticleGroup.
     * 
     * @param id          unique ID for the group.
     * @param name        name of the group.
     * @param isProtected whether the group is protected.
     * @param isAdmin     whether the user is an admin.
     */
    public ArticleGroup(int id, String name, boolean isProtected, boolean isAdmin) {
        this.id = id;
        this.name = name;
        this.isProtected = isProtected;
        this.isAdmin = isAdmin;
    }

    /**
     * Gets the ID of the group.
     * 
     * @return the ID of the group.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the group.
     * 
     * @param id the ID of the group.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the name of the group.
     * 
     * @return the name of the group.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the group.
     * 
     * @param name the name of the group.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets whether the group is protected.
     * 
     * @return whether the group is protected.
     */
    public boolean isProtected() {
        return isProtected;
    }

    /**
     * Sets whether the group is protected.
     * 
     * @param isProtected whether the group is protected.
     */
    public void setProtected(boolean isProtected) {
        this.isProtected = isProtected;
    }

    /**
     * Gets whether the group can be edited.
     * 
     * @return whether the group can be edited.
     */
    public boolean isAdmin() {
        return isAdmin;
    }

    /**
     * Sets whether the group can be edited.
     * 
     * @param isAdmin whether the group can be edited.
     */
    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}
