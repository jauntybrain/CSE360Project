package cse360Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.io.*;

/*******
 * <p>
 * UserService Class
 * </p>
 * 
 * <p>
 * Description: Manages user authentication and role management.
 * </p>
 * 
 * <p>
 * Copyright: CSE 360 Team Th02 © 2024
 * </p>
 * 
 * @version 1.00 2024-10-09 Phase one
 * 
 */
public class UserService {
    private static UserService instance;

    private static final String USERS_FILE = "users.txt";
    private Map<String, User> users = new HashMap<>();
    private InvitationCodeService invitationCodeService;
    private User currentUser;
    private String currentInvitationCode;

    /**
     * Private constructor to initialize the service and load users from local file.
     */
    private UserService() {
        loadUsersFromFile();
        invitationCodeService = InvitationCodeService.getInstance();
        currentUser = null;
        currentInvitationCode = null;
    }

    /**
     * Returns the singleton instance of UserService.
     * 
     * @return The singleton instance of UserService.
     */
    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    /**
     * Saves the current users to a local file.
     */
    private void saveUsersToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads users from a local file.
     */
    @SuppressWarnings("unchecked")
    private void loadUsersFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_FILE))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                users = (Map<String, User>) obj;
            } else {
                users = new HashMap<>();
            }
        } catch (IOException | ClassNotFoundException e) {
            users = new HashMap<>();
        }
    }

    /**
     * Returns a list of all users.
     * 
     * @return A list of all users.
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    /**
     * Returns the current user.
     * 
     * @return The current user.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Sets the current user.
     * 
     * @param user The user to set as the current user.
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Validates username.
     * Only Latin letters and numbers allowed, max length 36
     * 
     * @param username The username to validate.
     * @return true if the username is valid, false otherwise.
     */
    public boolean isValidUsername(String username) {
        return username.matches("^[A-Za-z0-9]{1,36}$");
    }

    /**
     * Validates password.
     * Must be 8-36 characters long, include an uppercase letter, a number, and a
     * special character.
     * 
     * @param password The password to validate.
     * @return true if the password is valid, false otherwise.
     */
    public boolean isValidPassword(String password) {
        return password.length() >= 8 && password.length() <= 36 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[!@#$%^&*()].*") &&
                password.matches(".*\\d.*");
    }

    /**
     * Validates email.
     * Basic email format validation
     * 
     * @param email The email to validate.
     * @return true if the email is valid, false otherwise.
     */
    public boolean isValidEmail(String email) {
        return email.matches("^[\\w-\\.]+@[\\w-]+\\.[a-z]{2,4}$");
    }

    /**
     * Validates name.
     * Only Latin letters and spaces allowed, max length 60
     * 
     * @param name The name to validate.
     * @return true if the name is valid, false otherwise.
     */
    public boolean isValidName(String name) {
        return name.matches("^[A-Za-z\\s]{1,60}$");
    }

    /**
     * Registers a new user.
     * 
     * @param username The username of the new user.
     * @param password The password of the new user.
     * @return The registered user.
     * @throws IllegalArgumentException if the username already exists.
     */
    public User register(String username, String password) throws IllegalArgumentException {
        if (!isValidUsername(username)) {
            throw new IllegalArgumentException(
                    "Invalid username. Only Latin letters and numbers allowed, max length 36.");
        }
        if (!isValidPassword(password)) {
            throw new IllegalArgumentException(
                    "Invalid password. Must be 8-36 characters long, include an uppercase letter, a number, and a special character.");
        }
        if (users.containsKey(username)) {
            throw new IllegalArgumentException("Username is already taken.");
        }
        try {
            byte[] hashedPassword = hashPassword(password);
            User registeredUser = new User(username, hashedPassword);
            if (users.isEmpty()) {
                registeredUser.getRoles().add(Role.ADMIN);
            } else if (currentInvitationCode != null) {
                List<Role> roles = invitationCodeService.redeemInvitationCode(currentInvitationCode);
                if (roles != null) {
                    registeredUser.getRoles().addAll(roles);
                } else {
                    System.out.println("No roles found for this invitation code.");
                }
            }
            if (users.containsKey(username)) {
                throw new IllegalArgumentException("Username is already taken.");
            }
            users.put(username, registeredUser);
            setCurrentUser(registeredUser);
            saveUsersToFile();
            return registeredUser;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * Logs in a user with a username and password.
     * 
     * @param username The username of the user.
     * @param password The password of the user.
     * @return The logged-in user, or null if login fails.
     */
    public User login(String username, String password) {
        try {
            User user = users.get(username);
            if (user != null && validatePassword(password, user.getPassword())) {
                setCurrentUser(user);
                return user;
            }
            return null;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * Checks if the current user is the first user.
     * 
     * @return true if the current user is the first user, false otherwise.
     */
    public boolean isFirstUser() {
        return users.isEmpty();
    }

    /**
     * Updates a user in the user service and saves it to the local file.
     * 
     * @param user The user to update.
     */
    public void updateUser(User user) {
        users.put(user.getUsername(), user);
        saveUsersToFile();
    }

    /**
     * Deletes a user from the user service and saves it to the local file.
     * 
     * @param username The username of the user to delete.
     */
    public void deleteUser(String username) {
        if (users.containsKey(username)) {
            users.remove(username);
            if (currentUser != null && currentUser.getUsername().equals(username)) {
                currentUser = null;
            }
            saveUsersToFile();
        }
    }

    /**
     * Adds a role to a user.
     * 
     * @param username The username of the user.
     * @param role     The role to add.
     */
    public void addRole(String username, Role role) {
        User user = users.get(username);
        if (user != null) {
            user.getRoles().add(role);
            saveUsersToFile();
        }
    }

    /**
     * Removes a role from a user.
     * 
     * @param username The username of the user.
     * @param role     The role to remove.
     */
    public void removeRole(String username, Role role) {
        User user = users.get(username);
        if (user != null) {
            if (role == Role.ADMIN && user.getRoles().contains(Role.ADMIN)) {
                long adminCount = users.values().stream()
                        .filter(u -> u.getRoles().contains(Role.ADMIN))
                        .count();
                if (adminCount <= 1) {
                    System.out.println("Cannot remove the admin role from the last admin user.");
                    return;
                }
            }
            user.getRoles().remove(role);
            saveUsersToFile();
        }
    }

    /**
     * Updates a user's password.
     * 
     * @param username    The username of the user.
     * @param newPassword The new password of the user.
     */
    public void updateUserPassword(String username, String newPassword) {
        User user = users.get(username);
        if (user != null) {
            try {
                byte[] newPasswordHash = hashPassword(newPassword);
                user.setPassword(newPasswordHash);
                user.setHasOneTimePassword(false);
                user.setOneTimePasswordExpires(null);
                updateUser(user);
                saveUsersToFile();
            } catch (NoSuchAlgorithmException e) {
                System.err.println("Error hashing password: " + e.getMessage());
            }
        }
    }

    /**
     * Hashes a password using SHA-256.
     * 
     * @param password The password to hash.
     * @return The hashed password.
     * @throws NoSuchAlgorithmException if the algorithm is not found.
     */
    public static byte[] hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(password.getBytes());
    }

    /**
     * Validates a password.
     * 
     * @param inputPassword      The password to validate.
     * @param storedPasswordHash The hashed password to compare.
     * @return true if the password is valid, false otherwise.
     */
    public static boolean validatePassword(String inputPassword, byte[] storedPasswordHash)
            throws NoSuchAlgorithmException {
        byte[] hashedInput = hashPassword(inputPassword);
        return MessageDigest.isEqual(hashedInput, storedPasswordHash);
    }

    /**
     * Generates a one-time UUID v4 password.
     * 
     * @return A one-time password.
     */
    public String generateOneTimePassword() {
        return UUID.randomUUID().toString();
    }

    /**
     * Sets a one-time password for a user.
     * 
     * @param username The username of the user.
     * @return The one-time password.
     */
    public String setOneTimePassword(String username) {
        User user = users.get(username);
        if (user != null) {
            String oneTimePassword = generateOneTimePassword();
            LocalDateTime expiration = LocalDateTime.now().plusMinutes(10);
            try {

                user.setPassword(hashPassword(oneTimePassword));
            } catch (NoSuchAlgorithmException e) {
                return null;
            }
            user.setHasOneTimePassword(true);
            user.setOneTimePasswordExpires(expiration);
            return oneTimePassword;
        } else {
            return null;
        }
    }

    /**
     * Verifies that one-time password is valid for a user.
     * 
     * @param username        The username of the user.
     * @param oneTimePassword The one-time password to check.
     * @return true if the one-time password is valid, false otherwise.
     */
    public boolean verifyOneTimePassword(String username, String oneTimePassword) {
        User user = users.get(username);
        if (user != null && user.getHasOneTimePassword()) {
            if (LocalDateTime.now().isBefore(user.getOneTimePasswordExpires())) {
                try {
                    return validatePassword(oneTimePassword, user.getPassword());
                } catch (NoSuchAlgorithmException e) {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Deletes the one-time password for a user.
     * 
     * @param username The username of the user.
     */
    public void deleteOneTimePassword(String username) {
        User user = users.get(username);
        if (user != null) {
            user.setHasOneTimePassword(false);
            user.setOneTimePasswordExpires(null);
        }
    }

    /**
     * Sets the current invitation code.
     * 
     * @param validationCode The invitation code to set.
     */
    public void setCurrentValidationCode(String validationCode) {
        this.currentInvitationCode = validationCode;
    }

    /**
     * Returns the current invitation code.
     * 
     * @return The current invitation code.
     */
    public String getCurrentInvitationCode() {
        return currentInvitationCode;
    }

    /**
     * Cleans the existing database.
     * 
     */
    public void cleanDB() {
        File usersFile = new File(USERS_FILE);
        usersFile.delete();
        invitationCodeService.cleanDB();
    }
}
