package cse360Project.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cse360Project.models.Role;
import cse360Project.models.Topic;
import cse360Project.models.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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
    private InvitationCodeService invitationCodeService;
    private DatabaseService databaseService;

    private Map<String, User> users = new HashMap<>(); // uuid -> user
    private User currentUser;
    private String currentInvitationCode;

    /**
     * Private constructor to initialize the service and load users from local file.
     */
    private UserService() {
        try {
            invitationCodeService = InvitationCodeService.getInstance();
            databaseService = DatabaseService.getInstance();
            currentUser = null;
            currentInvitationCode = null;
            loadUsersFromDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
     * Inserts a user into the database.
     * 
     * @param user The user to insert.
     * @throws SQLException if an error occurs while inserting the user.
     */
    private void insertUser(User user) throws SQLException {
        // Save user details
        String userSql = """
                    INSERT INTO users (uuid, username, password, email, first_name, middle_name,
                    last_name, preferred_name, has_one_time_password, one_time_password_expires)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        databaseService.executeUpdate(userSql, user.getUuid(), user.getUsername(), user.getPassword(),
                user.getEmail(),
                user.getFirstName(), user.getMiddleName(), user.getLastName(), user.getPreferredName(),
                user.getHasOneTimePassword(), user.getOneTimePasswordExpires());

        insertRoles(user);
        insertTopics(user);
    }

    /**
     * Inserts the roles for a user into the database.
     * 
     * @param user The user to insert the roles for.
     * @throws SQLException if an error occurs while inserting the roles.
     */
    private void insertRoles(User user) throws SQLException {
        for (Role role : user.getRoles()) {
            insertUserRole(user, role);
        }
    }

    /**
     * Inserts the topics for a user into the database.
     * 
     * @param user The user to insert the topics for.
     * @throws SQLException if an error occurs while inserting the topics.
     */
    private void insertTopics(User user) throws SQLException {
        for (Topic topic : user.getTopics()) {
            insertUserTopic(user, topic);
        }
    }

    /**
     * Inserts a role for a user into the database.
     * 
     * @param user The user to insert the role for.
     * @param role The role to insert.
     * @throws SQLException if an error occurs while inserting the role.
     */
    private void insertUserRole(User user, Role role) throws SQLException {
        String rolesSql = """
                    INSERT INTO user_roles (user_id, role)
                    VALUES (?, ?)
                """;
        databaseService.executeUpdate(rolesSql, user.getUuid(), role.name());
    }

    /**
     * Inserts a topic for a user into the database.
     * 
     * @param user  The user to insert the topic for.
     * @param topic The topic to insert.
     * @throws SQLException if an error occurs while inserting the topic.
     */
    private void insertUserTopic(User user, Topic topic) throws SQLException {
        String topicsSql = """
                    INSERT INTO user_topics (user_id, topic)
                    VALUES (?, ?)
                """;
        databaseService.executeUpdate(topicsSql, user.getUuid(), topic.name());
    }

    /**
     * Updates a user in the database.
     * 
     * @param user The user to update.
     * @throws SQLException if an error occurs while updating the user.
     */
    private void updateUserInDB(User user) throws SQLException {
        String userSql = """
                    UPDATE users
                    SET email = ?,
                    password = ?,
                    first_name = ?,
                    middle_name = ?,
                    last_name = ?,
                    preferred_name = ?,
                    has_one_time_password = ?,
                    one_time_password_expires = ?
                    WHERE uuid = ?
                """;

        databaseService.executeUpdate(userSql, user.getEmail(), user.getPassword(), user.getFirstName(),
                user.getMiddleName(), user.getLastName(), user.getPreferredName(), user.getHasOneTimePassword(),
                user.getOneTimePasswordExpires(), user.getUuid());

        // delete roles and topics, re-insert one by one
        deleteUserRoles(user);
        deleteUserTopics(user);
        insertRoles(user);
        insertTopics(user);
    }

    /**
     * Deletes the roles for a user from the database.
     * 
     * @param user The user to delete the roles for.
     * @throws SQLException if an error occurs while deleting the roles.
     */
    private void deleteUserRoles(User user) throws SQLException {
        String rolesSql = "DELETE FROM user_roles WHERE user_id = ?";
        databaseService.executeUpdate(rolesSql, user.getUuid());
    }

    /**
     * Deletes the topics for a user from the database.
     * 
     * @param user The user to delete the topics for.
     * @throws SQLException if an error occurs while deleting the topics.
     */
    private void deleteUserTopics(User user) throws SQLException {
        String topicsSql = "DELETE FROM user_topics WHERE user_id = ?";
        databaseService.executeUpdate(topicsSql, user.getUuid());
    }

    /**
     * Loads users from the database.
     * 
     * @throws SQLException if an error occurs while loading the users.
     */
    private void loadUsersFromDB() throws SQLException {
        users.clear();

        String sql = "SELECT * FROM users";
        ResultSet rs = databaseService.executeQuery(sql);

        while (rs.next()) {
            String uuid = rs.getString("uuid");
            String username = rs.getString("username");
            byte[] password = rs.getBytes("password");
            User user = new User(uuid, username, password);

            user.setEmail(rs.getString("email"));
            user.setFirstName(rs.getString("first_name"));
            user.setMiddleName(rs.getString("middle_name"));
            user.setLastName(rs.getString("last_name"));
            user.setPreferredName(rs.getString("preferred_name"));
            user.setHasOneTimePassword(rs.getBoolean("has_one_time_password"));

            Timestamp expires = rs.getTimestamp("one_time_password_expires");
            if (expires != null) {
                user.setOneTimePasswordExpires(expires.toLocalDateTime());
            }

            // Load roles
            loadUserRoles(user);
            // Load topics
            loadUserTopics(user);

            users.put(uuid, user);
        }
    }

    /**
     * Loads the roles for a user from the database.
     * 
     * @param user The user to load the roles for.
     * @throws SQLException if an error occurs while loading the roles.
     */
    private void loadUserRoles(User user) throws SQLException {
        String sql = "SELECT role FROM user_roles WHERE user_id = ?";
        ResultSet rs = databaseService.executeQuery(sql, user.getUuid());
        while (rs.next()) {
            user.getRoles().add(Role.valueOf(rs.getString("role")));
        }
    }

    /**
     * Loads the topics for a user from the database.
     * 
     * @param user The user to load the topics for.
     * @throws SQLException if an error occurs while loading the topics.
     */
    private void loadUserTopics(User user) throws SQLException {
        String sql = "SELECT topic FROM user_topics WHERE user_id = ?";
        ResultSet rs = databaseService.executeQuery(sql, user.getUuid());
        while (rs.next()) {
            user.getTopics().add(Topic.valueOf(rs.getString("topic")));
        }
    }

    /**
     * Deletes a user from the database.
     * 
     * @param uuid The id of the user to delete.
     * @throws SQLException if an error occurs while deleting the user.
     */
    private void deleteUserFromDB(String uuid) throws SQLException {
        String sql = "DELETE FROM users WHERE uuid = ?";
        databaseService.executeUpdate(sql, uuid);
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
        if (checkUsernameTaken(username)) {
            throw new IllegalArgumentException("Username is already taken.");
        }
        try {
            byte[] hashedPassword = hashPassword(password);
            User registeredUser = new User(null, username, hashedPassword);
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
            if (users.containsKey(registeredUser.getUuid())) {
                throw new IllegalArgumentException("Username is already taken.");
            }
            users.put(registeredUser.getUuid(), registeredUser);
            setCurrentUser(registeredUser);
            insertUser(registeredUser);
            return registeredUser;
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error hashing password: " + e.getMessage());
            return null;
        } catch (SQLException e) {
            System.out.println("Error registering user: " + e.getMessage());
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
            User user = getUserByUsername(username);
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
     * Checks if a username is taken.
     * 
     * @param username The username to check.
     * @return true if the username is taken, false otherwise.
     */
    private boolean checkUsernameTaken(String username) {
        return users.values().stream()
                .anyMatch(user -> user.getUsername().equals(username));
    }

    /**
     * Returns a user by their username.
     * 
     * @param username The username of the user to return.
     * @return The user with the given username, or null if no such user exists.
     */
    private User getUserByUsername(String username) {
        return users.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
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
    public boolean updateUser(User user) {
        users.put(user.getUuid(), user);
        try {
            updateUserInDB(user);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Deletes a user from the user service and saves it to the local file.
     * 
     * @param uuid The id of the user to delete.
     */
    public boolean deleteUser(String uuid) {
        if (users.containsKey(uuid)) {
            users.remove(uuid);
            if (currentUser != null && currentUser.getUuid().equals(uuid)) {
                currentUser = null;
            }

            try {
                deleteUserFromDB(uuid);
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Adds a role to a user.
     * 
     * @param uuid The id of the user.
     * @param role The role to add.
     */
    public boolean addRole(String uuid, Role role) {
        User user = users.get(uuid);
        if (user != null) {
            user.getRoles().add(role);
            try {
                updateUserInDB(user);
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Removes a role from a user.
     * 
     * @param uuid The id of the user.
     * @param role The role to remove.
     */
    public boolean removeRole(String uuid, Role role) {
        User user = users.get(uuid);
        if (user != null) {
            if (role == Role.ADMIN && user.getRoles().contains(Role.ADMIN)) {
                long adminCount = users.values().stream()
                        .filter(u -> u.getRoles().contains(Role.ADMIN))
                        .count();
                if (adminCount <= 1) {
                    System.out.println("Cannot remove the admin role from the last admin user.");
                    return false;
                }
            }
            user.getRoles().remove(role);
            try {
                updateUserInDB(user);
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Updates a user's password.
     * 
     * @param uuid        The id of the user.
     * @param newPassword The new password of the user.
     */
    public void updateUserPassword(String uuid, String newPassword) {
        User user = users.get(uuid);
        if (user != null) {
            try {
                byte[] newPasswordHash = hashPassword(newPassword);
                user.setPassword(newPasswordHash);
                user.setHasOneTimePassword(false);
                user.setOneTimePasswordExpires(null);
                updateUser(user);
                // saveUsersToFile();
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
     * @param uuid The id of the user.
     * @return The one-time password.
     */
    public String setOneTimePassword(String uuid) {
        User user = users.get(uuid);
        if (user != null) {
            String oneTimePassword = generateOneTimePassword();
            LocalDateTime expiration = LocalDateTime.now().plusMinutes(10);
            try {
                user.setPassword(hashPassword(oneTimePassword));
            } catch (NoSuchAlgorithmException e) {
                System.out.println("Error hashing password: " + e.getMessage());
                return null;
            }
            user.setHasOneTimePassword(true);
            user.setOneTimePasswordExpires(expiration);
            updateUser(user);
            return oneTimePassword;
        } else {
            return null;
        }
    }

    /**
     * Verifies that one-time password is valid for a user.
     * 
     * @param uuid            The id of the user.
     * @param oneTimePassword The one-time password to check.
     * @return true if the one-time password is valid, false otherwise.
     */
    public boolean verifyOneTimePassword(String uuid, String oneTimePassword) {
        User user = users.get(uuid);
        System.out.println("User found? " + (user != null));
        System.out.println("User has one-time password? " + user.getHasOneTimePassword());
        if (user != null && user.getHasOneTimePassword()) {
            System.out.println("One-time password expires: " + user.getOneTimePasswordExpires());
            System.out.println("Current time: " + LocalDateTime.now());
            System.out.println("One-time password is valid: "
                    + LocalDateTime.now().isBefore(user.getOneTimePasswordExpires()));
            try {
                System.out.println("Comparing: " + hashPassword(oneTimePassword).toString() + " and "
                        + user.getPassword());
            } catch (NoSuchAlgorithmException e) {
                System.out.println("Error hashing password: " + e.getMessage());
                return false;
            }
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
     * @param uuid The id of the user.
     */
    public void deleteOneTimePassword(String uuid) {
        User user = users.get(uuid);
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
     * Cleans the database of all users.
     * 
     * @return true if the database was cleaned successfully, false otherwise.
     */
    public boolean cleanDB() {
        users.clear();
        try {
            databaseService.executeUpdate("DELETE FROM users");
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
