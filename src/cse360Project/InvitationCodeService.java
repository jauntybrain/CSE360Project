package cse360Project;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InvitationCodeService {
	public static InvitationCodeService instance;
	
    private static final String INVITATION_CODES_FILE = "invitationCodes.txt";
    private Map<String, InvitationCode> invitationCodes = new HashMap<>();

    public InvitationCodeService() {
        loadInvitationCodesFromFile();
    }
    
    public static InvitationCodeService getInstance() {
        if (instance == null) {
            instance = new InvitationCodeService();
        }
        return instance;
    }

    private void saveInvitationCodesToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(INVITATION_CODES_FILE))) {
            oos.writeObject(invitationCodes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    public void addInvitationCode(String code, List<Role> roles) {
        invitationCodes.put(code, new InvitationCode(code, roles));
        saveInvitationCodesToFile();
    }

    public String generateOneTimeCode(List<Role> roles) {
        String code = UUID.randomUUID().toString();
        addInvitationCode(code, roles);
        return code;
    }

    public boolean validateInvitationCode(String code) {
        InvitationCode invitationCode = invitationCodes.get(code);
        return invitationCode != null && !invitationCode.isUsed();
    }

    public List<Role> getRolesForInvitationCode(String code) {
        InvitationCode invitationCode = invitationCodes.get(code);
        if (invitationCode != null && !invitationCode.isUsed()) {
            return invitationCode.getRoles();
        }
        return null;
    }

    public List<Role> redeemInvitationCode(String code) {
        InvitationCode invitationCode = invitationCodes.get(code);
        if (invitationCode != null && !invitationCode.isUsed()) {
            invitationCode.setUsed(true);
            saveInvitationCodesToFile();
            return invitationCode.getRoles();
        }
        return null;
    }
}