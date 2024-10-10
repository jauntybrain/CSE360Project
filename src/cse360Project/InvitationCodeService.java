package cse360Project;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    private static final String INVITATION_CODES_FILE = "invitationCodes.txt";
    private Map<String, InvitationCode> invitationCodes = new HashMap<>();

    /**
     * Private constructor to initialize the service and load invitation codes from
     * local file.
     */
    private InvitationCodeService() {
        loadInvitationCodesFromFile();
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

    /**
     * Saves the current invitation codes to a local file.
     */
    private void saveInvitationCodesToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(INVITATION_CODES_FILE))) {
            oos.writeObject(invitationCodes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads invitation codes from a local file.
     */
    @SuppressWarnings("unchecked")
    private void loadInvitationCodesFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(INVITATION_CODES_FILE))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                invitationCodes = (Map<String, InvitationCode>) obj;
            } else {
                invitationCodes = new HashMap<>();
            }
        } catch (IOException | ClassNotFoundException e) {
            invitationCodes = new HashMap<>();
        }
    }

    /**
     * Adds a new invitation code with assigned roles.
     * 
     * @param code  The invitation code.
     * @param roles The list of roles assigned with the invitation code.
     */
    public void addInvitationCode(String code, List<Role> roles) {
        invitationCodes.put(code, new InvitationCode(code, roles));
        saveInvitationCodesToFile();
    }

    /**
     * Generates a one-time invitation code with assigned roles.
     * 
     * @param roles The list of roles assigned with the invitation code.
     * @return The generated one-time invitation code.
     */
    public String generateOneTimeCode(List<Role> roles) {
        String code = UUID.randomUUID().toString();
        addInvitationCode(code, roles);
        return code;
    }

    /**
     * Validates an invitation code.
     * 
     * @param code The invitation code to validate.
     * @return true if the invitation code is valid and not used, false otherwise.
     */
    public boolean validateInvitationCode(String code) {
        InvitationCode invitationCode = invitationCodes.get(code);
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
        InvitationCode invitationCode = invitationCodes.get(code);
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
        InvitationCode invitationCode = invitationCodes.get(code);
        if (invitationCode != null && !invitationCode.isUsed()) {
            invitationCode.setUsed(true);
            saveInvitationCodesToFile();
            return invitationCode.getRoles();
        }
        return null;
    }

    /**
     * Cleans the existing database.
     * 
     */
    public void cleanDB() {
        File invitationCodesFile = new File(INVITATION_CODES_FILE);
        invitationCodesFile.delete();
    }
}