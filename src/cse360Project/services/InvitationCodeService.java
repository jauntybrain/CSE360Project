package cse360Project.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cse360Project.models.InvitationCode;
import cse360Project.models.Role;

/*******
 * <p>
 * InvitationCodeService Class
 * </p>
 * 
 * <p>
 * Description: Manages the creation, validation, and redemption of invitation
 * codes.
 * </p>
 * 
 * <p>
 * Copyright: CSE 360 Team Th02 © 2024
 * </p>
 * 
 * @version 1.00 2024-10-09 Phase one
 * 
 */
public class InvitationCodeService {
    private static InvitationCodeService instance;
    private final DatabaseService databaseService;

    /**
     * Private constructor to initialize the service and load invitation codes from
     * local file.
     */
    private InvitationCodeService() {
        databaseService = DatabaseService.getInstance();
    }

    /**
     * Returns the singleton instance of InvitationCodeService.
     * 
     * @return The singleton instance of InvitationCodeService.
     */
    public static InvitationCodeService getInstance() {
        if (instance == null) {
            instance = new InvitationCodeService();
        }
        return instance;
    }

    private InvitationCode getInvitationCode(String code) {
        try {
            String invitationSql = """
                        SELECT * FROM invitation_codes WHERE code = ?
                    """;
            final ResultSet resultSet = databaseService.executeQuery(invitationSql, code);

            if (resultSet.next()) {
                final List<Role> roles = getInvitationCodeRoles(resultSet.getString("uuid"));
                return new InvitationCode(resultSet.getString("uuid"), resultSet.getString("code"), roles);
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    private List<Role> getInvitationCodeRoles(String uuid) throws SQLException {
        String invitationCodeRolesSql = """
                    SELECT role FROM invitation_code_roles WHERE invitation_code_id = ?
                """;
        final ResultSet resultSet = databaseService.executeQuery(invitationCodeRolesSql, uuid);
        final List<Role> roles = new ArrayList<>();
        while (resultSet.next()) {
            roles.add(Role.valueOf(resultSet.getString("role")));
        }
        return roles;
    }

    /**
     * Inserts an invitation code into the database.
     * 
     * @param invitation The invitation code.
     * @throws SQLException if a database access error occurs
     */
    private void insertInvitationCode(InvitationCode invitation) throws SQLException {

        // Save invitation code details
        String invitationSql = """
                    INSERT INTO invitation_codes (uuid, code, used)
                    VALUES (?, ?, ?)
                """;

        databaseService.executeUpdate(invitationSql, invitation.getUuid(), invitation.getCode(),
                invitation.isUsed());

        insertInvitationCodeRoles(invitation);

    }

    /**
     * Inserts the roles for an invitation code into the database.
     * 
     * @param invitation The invitation code.
     * @throws SQLException if a database access error occurs
     */
    private void insertInvitationCodeRoles(InvitationCode invitation) throws SQLException {
        for (Role role : invitation.getRoles()) {
            insertInvitationCodeRole(invitation, role);
        }
    }

    /**
     * Inserts an invitation code role into the database.
     * 
     * @param invitation The invitation code.
     * @param role       The role to insert.
     * @throws SQLException if a database access error occurs
     */
    private void insertInvitationCodeRole(InvitationCode invitation, Role role) throws SQLException {
        String invitationCodeRolesSql = """
                    INSERT INTO invitation_code_roles (invitation_code_id, role)
                    VALUES (?, ?)
                """;
        databaseService.executeUpdate(invitationCodeRolesSql, invitation.getUuid(), role.name());
    }

    /**
     * Sets the used status of an invitation code in the database.
     * 
     * @param invitation The invitation code.
     * @throws SQLException if a database access error occurs
     */
    public void setInvitationCodeUsed(InvitationCode invitation) throws SQLException {
        String invitationSql = """
                    UPDATE invitation_codes SET used = ? WHERE uuid = ?
                """;
        databaseService.executeUpdate(invitationSql, invitation.isUsed(), invitation.getUuid());
    }

    /**
     * Adds a new invitation code with assigned roles.
     * 
     * @param code  The invitation code.
     * @param roles The list of roles assigned with the invitation code.
     */
    public boolean addInvitationCode(String code, List<Role> roles) {
        final InvitationCode newCode = new InvitationCode(null, code, roles);
        try {
            insertInvitationCode(newCode);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Generates a one-time invitation code with assigned roles.
     * 
     * @param roles The list of roles assigned with the invitation code.
     * @return The generated one-time invitation code.
     */
    public String generateOneTimeCode(List<Role> roles) {
        String code = UUID.randomUUID().toString();
        final boolean success = addInvitationCode(code, roles);
        return success ? code : null;
    }

    /**
     * Validates an invitation code.
     * 
     * @param code The invitation code to validate.
     * @return true if the invitation code is valid and not used, false otherwise.
     */
    public boolean validateInvitationCode(String code) {
        final InvitationCode invitationCode = getInvitationCode(code);
        return invitationCode != null && !invitationCode.isUsed();
    }

    /**
     * Retrieves the roles assigned to the invitation code.
     * 
     * @param code The invitation code.
     * @return A list of roles assigned to the invitation code, or null if the code
     *         is invalid or used.
     */
    public List<Role> getRolesForInvitationCode(String code) {
        final InvitationCode invitationCode = getInvitationCode(code);
        if (invitationCode != null && !invitationCode.isUsed()) {
            return invitationCode.getRoles();
        }
        return null;
    }

    /**
     * Redeems an invitation code, marking it as used and returning the assigned
     * roles.
     * 
     * @param code The invitation code to redeem.
     * @return A list of roles assigned to the invitation code, or null if the code
     *         is invalid or already used.
     */
    public List<Role> redeemInvitationCode(String code) {
        final InvitationCode invitationCode = getInvitationCode(code);
        if (invitationCode != null && !invitationCode.isUsed()) {
            invitationCode.setUsed(true);
            try {
                setInvitationCodeUsed(invitationCode);
            } catch (SQLException e) {
                return null;
            }
            return invitationCode.getRoles();
        }
        return null;
    }

    /**
     * Cleans the database of all invitation codes.
     * 
     * @return true if the database was cleaned successfully, false otherwise.
     */
    public boolean cleanDB() {
        try {
            databaseService.executeUpdate("DELETE FROM invitation_codes");
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
