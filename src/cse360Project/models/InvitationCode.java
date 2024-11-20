package cse360Project.models;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/*******
 * <p>
 * InvitationCode Class
 * </p>
 * 
 * <p>
 * Description: Invitation code with roles and usage status.
 * </p>
 * 
 * <p>
 * Copyright: CSE 360 Team Th02 © 2024
 * </p>
 * 
 * @version 1.00 2024-10-09 Phase one
 * 
 */
public class InvitationCode implements Serializable {
    private static final long serialVersionUID = 1L;

    private String uuid;
    private String code;
    private List<Role> roles;
    private boolean used;

    /**
     * Constructor to initialize an InvitationCode with a code and roles.
     * 
     * @param uuid	The unique identifier of the invitation code.
     * @param code  The invitation code.
     * @param roles The list of roles defined for the code.
     */
    public InvitationCode(String uuid, String code, List<Role> roles) {
        this.uuid = uuid != null ? uuid : UUID.randomUUID().toString();
        this.code = code;
        this.roles = roles;
        this.used = false;
    }

    /**
     * Gets the UUID of the invitation code.
     * 
     * @return The UUID of the invitation code.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Gets the invitation code.
     * 
     * @return Invitation code string.
     */
    public String getCode() {
        return code;
    }

    /**
     * Gets the list of roles defined for the code.
     * 
     * @return List of roles.
     */
    public List<Role> getRoles() {
        return roles;
    }

    /**
     * Checks if the invitation code has already been used.
     * 
     * @return TRUE if the code has been used, FALSE otherwise.
     */
    public boolean isUsed() {
        return used;
    }

    /**
     * Sets the usage status of the invitation code.
     * 
     * @param used The usage status to set.
     */
    public void setUsed(boolean used) {
        this.used = used;
    }
}