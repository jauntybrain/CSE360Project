package cse360Project.services;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import cse360Project.services.encryption.*;
import cse360Project.models.ArticleGroup;
import cse360Project.models.HelpArticle;
import cse360Project.models.Role;
import cse360Project.models.Topic;
import cse360Project.models.User;
import cse360Project.screens.ArticleGroupsPage.UserListItem;

/*******
 * <p>
 * HelpArticleService class.
 * </p>
 * 
 * <p>
 * Description: Service class for managing help articles in a database with
 * encryption.
 * </p>
 * 
 * <p>
 * Copyright: CSE 360 Team Th02 © 2024
 * </p>
 * 
 * @version 1.00 2024-10-30 Phase two
 * 
 */
public class HelpArticleService {
    private EncryptionService encryptionService;
    private DatabaseService databaseService;
    private UserService userService;
    private static HelpArticleService instance;

    /**
     * Creates HelpArticleService and initializes EncryptionService.
     * 
     * @throws Exception if an error occurs during initialization.
     */
    public HelpArticleService() throws Exception {
        encryptionService = new EncryptionService();
        databaseService = DatabaseService.getInstance();
        userService = UserService.getInstance();
    }

    public static HelpArticleService getInstance() {
        if (instance == null) {
            try {
                instance = new HelpArticleService();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    /**
     * Encrypts and modifies an article in the database.
     * 
     * @param article the article to modify.
     * @param update  create or update.
     * @throws Exception if an encryption or database error occurs.
     */
    public void modifyArticle(HelpArticle article, boolean update) throws Exception {
        // Create unique IV
        byte[] iv = EncryptionUtils.getInitializationVector(UUID.randomUUID().toString().toCharArray());

        // Encrypt fields
        String encryptedTitle = encryptField(article.getTitle(), iv);
        String encryptedAuthors = encryptField(charArraysToString(article.getAuthors()), iv);
        String encryptedAbstract = encryptField(article.getAbstractText(), iv);
        String encryptedKeywords = encryptField(charArraysToString(article.getKeywords()), iv);
        String encryptedBody = encryptField(article.getBody(), iv);
        String encryptedReferences = encryptField(charArraysToString(article.getReferences()), iv);
        String encryptedLevel = encryptField(article.getLevel().toString(), iv);

        String encodedIV = Base64.getEncoder().encodeToString(iv);

        // Prepare SQL statement
        String insertArticleSql = "INSERT INTO articles (title, authors, abstract, keywords, body, references, level, iv, uuid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String updateArticleSql = "UPDATE articles SET title = ?, authors = ?, abstract = ?, keywords = ?, body = ?, references = ?, level = ?, iv = ? WHERE uuid = ?";

        databaseService.executeUpdate(update ? updateArticleSql : insertArticleSql,
                encryptedTitle, encryptedAuthors, encryptedAbstract, encryptedKeywords, encryptedBody,
                encryptedReferences, encryptedLevel, encodedIV, article.getUuid());

        // create or update groups if needed
        // delete, then insert
        databaseService.executeUpdate("DELETE FROM article_group_articles WHERE article_id = ?", article.getUuid());
        for (int group : article.getGroups()) {
            databaseService.executeUpdate("INSERT INTO article_group_articles (group_id, article_id) VALUES (?, ?)",
                    group, article.getUuid());
        }
    }

    /**
     * Deletes an article from the database.
     * 
     * @param uuid the UUID of the article to delete.
     * @throws SQLException if a database error occurs.
     */
    public void deleteArticle(String uuid) throws SQLException {
        String deleteSQL = "DELETE FROM articles WHERE uuid = ?";
        databaseService.executeUpdate(deleteSQL, uuid);
    }

    /**
     * Fetches all articles from the database and decrypts them.
     * 
     * @return list of all articles.
     * @throws Exception if decryption or database error occurs.
     */
    public List<HelpArticle> getAllArticles() throws Exception {
        String query = """
                SELECT *
                FROM articles a
                JOIN article_group_articles aga ON a.uuid = aga.article_id
                JOIN article_groups ag ON aga.group_id = ag.id
                LEFT JOIN article_group_users agu ON ag.id = agu.group_id
                WHERE (ag.is_protected = FALSE OR agu.user_id = ?)
                AND (agu.user_id IS NULL OR agu.group_id = ag.id);
                """;

        List<HelpArticle> articles = new ArrayList<>();

        ResultSet rs = databaseService.executeQuery(query, userService.getCurrentUser().getUuid());
        while (rs.next()) {
            HelpArticle article = decryptArticle(rs);
            List<Integer> groups = getArticleGroupIds(article.getUuid());
            article.setGroups(groups);
            articles.add(article);
        }
        return articles;
    }

    /**
     * /**
     * Backs up articles to a user-specified file.
     * 
     * @param filename       the name of the file.
     * @param selectedGroups the groups to backup.
     * @throws Exception if I/O or encryption error occurs.
     */
    public void backupArticles(String filename, List<Integer> selectedGroups) throws Exception {
        List<HelpArticle> articlesToBackup;

        // Get articles to backup
        if (selectedGroups.isEmpty()) {
            articlesToBackup = getAllArticles();
        } else {
            articlesToBackup = getArticlesByGroups(new ArrayList<>(selectedGroups));
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            for (HelpArticle article : articlesToBackup) {
                // Create a unique IV
                byte[] iv = EncryptionUtils.getInitializationVector(UUID.randomUUID().toString().toCharArray());

                // Encrypt each field of the article
                String encryptedTitle = encryptField(article.getTitle(), iv);
                String encryptedAuthors = encryptField(charArraysToString(article.getAuthors()), iv);
                String encryptedAbstractText = encryptField(article.getAbstractText(), iv);
                String encryptedKeywords = encryptField(charArraysToString(article.getKeywords()), iv);
                String encryptedBody = encryptField(article.getBody(), iv);
                String encryptedReferences = encryptField(charArraysToString(article.getReferences()), iv);
                String encryptedGroups = encryptField(article.getGroups().stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(",")), iv);
                String encryptedLevel = encryptField(article.getLevel().toString(), iv);

                // Store encrypted data
                oos.writeObject(encryptedTitle);
                oos.writeObject(encryptedAuthors);
                oos.writeObject(encryptedAbstractText);
                oos.writeObject(encryptedKeywords);
                oos.writeObject(encryptedBody);
                oos.writeObject(encryptedReferences);
                oos.writeObject(encryptedGroups);
                oos.writeObject(encryptedLevel);

                oos.writeObject(Base64.getEncoder().encodeToString(iv));
                oos.writeObject(article.getUuid());
            }
        }
    }

    /**
     * Loads articles from a user-specified file.
     * 
     * @param filename the name of the file.
     * @param merge    whether to merge with existing articles.
     * @throws Exception if I/O or decryption error occurs.
     */
    public void restoreArticles(String filename, boolean merge) throws Exception {
        // Clear all articles if not merging
        if (!merge) {
            cleanDB();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            while (true) {
                try {
                    // Get encrypted fields
                    String encryptedTitle = (String) ois.readObject();
                    String encryptedAuthors = (String) ois.readObject();
                    String encryptedAbstractText = (String) ois.readObject();
                    String encryptedKeywords = (String) ois.readObject();
                    String encryptedBody = (String) ois.readObject();
                    String encryptedReferences = (String) ois.readObject();
                    String encryptedGroups = (String) ois.readObject();
                    String encryptedLevel = (String) ois.readObject();

                    String encodedIV = (String) ois.readObject();
                    String articleUuid = (String) ois.readObject();

                    // Handle merging logic
                    if (merge) {
                        String checkSQL = "SELECT uuid FROM articles WHERE uuid = ?";
                        ResultSet rs = databaseService.executeQuery(checkSQL, articleUuid);
                        if (rs.next()) {
                            // Article already exists, skipping it
                            continue;
                        }
                    }

                    // Load the IV
                    byte[] iv = Base64.getDecoder().decode(encodedIV);

                    // Decrypt fields
                    char[] title = decryptFieldToString(encryptedTitle, iv).toCharArray();
                    char[][] authors = stringToCharArrays(decryptFieldToString(encryptedAuthors, iv));
                    char[] abstractText = decryptFieldToString(encryptedAbstractText, iv).toCharArray();
                    char[][] keywords = stringToCharArrays(decryptFieldToString(encryptedKeywords, iv));
                    char[] body = decryptFieldToString(encryptedBody, iv).toCharArray();
                    char[][] references = stringToCharArrays(decryptFieldToString(encryptedReferences, iv));
                    List<Integer> groups = Arrays.asList(decryptFieldToString(encryptedGroups, iv).split(","))
                            .stream()
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());
                    Topic level = Topic.valueOf(decryptFieldToString(encryptedLevel, iv));

                    // Create and insert article
                    HelpArticle article = new HelpArticle(articleUuid, title, authors, abstractText,
                            keywords, body, references, groups, level);
                    modifyArticle(article, false);
                } catch (EOFException e) {
                    break;
                }
            }
        }
    }

    /**
     * Gets all groups from all articles.
     * 
     * @return list of all groups.
     * @throws Exception if an error occurs.
     */
    public List<ArticleGroup> getAllGroups() throws SQLException {
        List<ArticleGroup> groups = new ArrayList<>();

        String query = """
                    SELECT ag.id, ag.name, agu.is_admin, ag.is_protected
                    FROM article_groups ag
                    JOIN article_group_users agu ON ag.id = agu.group_id
                    WHERE ag.is_protected = FALSE OR agu.user_id = ?;
                """;

        ResultSet rs = databaseService.executeQuery(query, userService.getCurrentUser().getUuid());
        while (rs.next()) {
            groups.add(new ArticleGroup(rs.getInt("id"), rs.getString("name"), rs.getBoolean("is_protected"),
                    rs.getBoolean("is_admin")));
        }

        return groups;
    }

    public List<Integer> getArticleGroupIds(String uuid) throws SQLException {
        String query = """
                SELECT ag.id
                FROM article_groups ag
                JOIN article_group_articles aga ON ag.id = aga.group_id
                WHERE aga.article_id = ?;
                """;
        ResultSet rs = databaseService.executeQuery(query, uuid);
        List<Integer> groups = new ArrayList<>();
        while (rs.next()) {
            groups.add(rs.getInt("id"));
        }
        return groups;
    }

    public List<HelpArticle> getGroupArticles(int groupId) throws SQLException {
        String query = "SELECT * FROM articles a JOIN article_group_articles aga ON a.uuid = aga.article_id WHERE aga.group_id = ?";
        ResultSet rs = databaseService.executeQuery(query, groupId);
        List<HelpArticle> articles = new ArrayList<>();
        while (rs.next()) {
            try {
                HelpArticle article = decryptArticle(rs);
                articles.add(article);
            } catch (Exception e) {
                System.err.println("Error decrypting article: " + e.getMessage());
            }
        }
        return articles;
    }

    public HashMap<User, Boolean> getGroupUsers(int groupId) {
        try {
            String query = "SELECT u.uuid, u.username, agu.is_admin FROM users u JOIN article_group_users agu ON u.uuid = agu.user_id WHERE agu.group_id = ?";
            ResultSet rs = databaseService.executeQuery(query, groupId);
            HashMap<User, Boolean> users = new HashMap<>();
            while (rs.next()) {
                final User user = new User(rs.getString("uuid"), rs.getString("username"), null);
                final List<Role> roles = userService.loadUserRoles(user);
                user.setRoles(roles);
                users.put(user, rs.getBoolean("is_admin"));
            }
            return users;
        } catch (SQLException e) {
            System.err.println("Error getting group users: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets articles by groups.
     * 
     * @param groups list of groups.
     * @return list of articles.
     * @throws Exception if an error occurs.
     */
    public List<HelpArticle> getArticlesByGroups(List<Integer> groups) throws Exception {
        List<HelpArticle> allArticles = getAllArticles();
        return allArticles.stream()
                .filter(article -> {
                    for (Integer articleGroup : article.getGroups()) {
                        if (groups.contains(articleGroup)) {
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    /**
     * Modifies a group.
     * 
     * @param update   whether to update or create.
     * @param group    the group to modify.
     * @param articles list of articles.
     * @param users    list of users.
     * @throws SQLException if an error occurs.
     */
    public void modifyGroup(ArticleGroup group, List<String> articles, List<UserListItem> users) throws SQLException {
        final boolean update = group.getId() != -1;
        final User currentUser = userService.getCurrentUser();

        // If updating, first delete existing relationships
        if (update) {
            databaseService.executeUpdate("DELETE FROM article_group_articles WHERE group_id = ?", group.getId());
            databaseService.executeUpdate("DELETE FROM article_group_users WHERE group_id = ?", group.getId());

            // Update group details
            databaseService.executeUpdate(
                    "UPDATE article_groups SET name = ?, is_protected = ? WHERE id = ?",
                    group.getName(), group.isProtected(), group.getId());
        } else {
            // Insert new group
            String insertSQL = "INSERT INTO article_groups (name, is_protected) VALUES (?, ?)";
            try (PreparedStatement stmt = databaseService.getConnection().prepareStatement(insertSQL,
                    Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, group.getName());
                stmt.setBoolean(2, group.isProtected());
                stmt.executeUpdate();

                // Get the generated ID
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        group.setId(rs.getInt(1));
                    }
                }
            }
        }

        // Insert article relationships
        if (articles != null && !articles.isEmpty()) {
            for (String articleId : articles) {
                databaseService.executeUpdate(
                        "INSERT INTO article_group_articles (group_id, article_id) VALUES (?, ?)",
                        group.getId(), articleId);
            }
        }

        // Insert user relationships
        if (users != null && !users.isEmpty()) {
            // Add all users (including current user if present in the list)
            for (UserListItem user : users) {
                databaseService.executeUpdate(
                        "INSERT INTO article_group_users (group_id, user_id, is_admin) VALUES (?, ?, ?)",
                        group.getId(), userService.getUserByUsername(user.getUsername()).getUuid(),
                        user.isAdmin());
            }

            // Add current user as admin if not in the list
            boolean currentUserInList = users.stream()
                    .anyMatch(u -> u.getUsername().equals(currentUser.getUsername()));
            if (!currentUserInList) {
                databaseService.executeUpdate(
                        "INSERT INTO article_group_users (group_id, user_id, is_admin) VALUES (?, ?, true)",
                        group.getId(), currentUser.getUuid());
            }
        } else {
            // If no users specified, just add current user as admin
            databaseService.executeUpdate(
                    "INSERT INTO article_group_users (group_id, user_id, is_admin) VALUES (?, ?, true)",
                    group.getId(), currentUser.getUuid());
        }
    }

    /**
     * Deletes a group from the database.
     * 
     * @param groupId the ID of the group to delete.
     * @throws SQLException if an error occurs.
     */
    public void deleteGroup(int groupId) throws SQLException {
        databaseService.executeUpdate("DELETE FROM article_groups WHERE id = ?", groupId);
        databaseService.executeUpdate("DELETE FROM article_group_articles WHERE group_id = ?", groupId);
        databaseService.executeUpdate("DELETE FROM article_group_users WHERE group_id = ?", groupId);
    }

    /**
     * Encrypts an array of characters.
     * 
     * @param data character array to encrypt.
     * @param iv   initialization vector.
     * @return encrypted data as base64 string.
     * @throws Exception if encryption error occurs.
     */
    private String encryptField(char[] data, byte[] iv) throws Exception {
        return Base64.getEncoder().encodeToString(
                encryptionService.encrypt(EncryptionUtils.toByteArray(data), iv));
    }

    /**
     * Encrypts a string.
     * 
     * @param data string to encrypt.
     * @param iv   initialization vector.
     * @return encrypted data as base64 string.
     * @throws Exception if encryption error occurs.
     */
    private String encryptField(String data, byte[] iv) throws Exception {
        return Base64.getEncoder().encodeToString(
                encryptionService.encrypt(data.getBytes(), iv));
    }

    /**
     * Decrypts an article.
     * 
     * @param rs article data.
     * @return decrypted Article.
     * @throws Exception if decryption error occurs.
     */
    private HelpArticle decryptArticle(ResultSet rs) throws Exception {
        // Get the IV
        String encodedIV = rs.getString("iv");
        byte[] iv = Base64.getDecoder().decode(encodedIV);

        // Decrypt article fields
        String uuid = rs.getString("uuid");
        char[] title = decryptField(rs.getString("title"), iv);
        char[][] authors = stringToCharArrays(decryptFieldToString(rs.getString("authors"), iv));
        char[] abstractText = decryptField(rs.getString("abstract"), iv);
        char[][] keywords = stringToCharArrays(decryptFieldToString(rs.getString("keywords"), iv));
        char[] body = decryptField(rs.getString("body"), iv);
        char[][] references = stringToCharArrays(decryptFieldToString(rs.getString("references"), iv));
        Topic level = Topic.valueOf(decryptFieldToString(rs.getString("level"), iv));

        // Return decrypted article
        return new HelpArticle(uuid, title, authors, abstractText, keywords, body, references, new ArrayList<>(),
                level);
    }

    /**
     * Decrypts a base64 string into an array of characters.
     * 
     * @param encrypted encrypted string.
     * @param iv        initialization vector.
     * @return decrypted array of characters.
     * @throws Exception if decryption error occurs.
     */
    private char[] decryptField(String encrypted, byte[] iv) throws Exception {
        return EncryptionUtils.toCharArray(
                encryptionService.decrypt(Base64.getDecoder().decode(encrypted), iv));
    }

    /**
     * Decrypts a base64 string into a string.
     * 
     * @param encrypted encrypted string.
     * @param iv        initialization vector.
     * @return decrypted data as a string.
     * @throws Exception if decryption error occurs.
     */
    private String decryptFieldToString(String encrypted, byte[] iv) throws Exception {
        return new String(
                encryptionService.decrypt(Base64.getDecoder().decode(encrypted), iv));
    }

    /**
     * Converts a 2D char array to a string.
     * 
     * @param charArrays the 2D character array.
     * @return the string.
     */
    private String charArraysToString(char[][] charArrays) {
        StringBuilder sb = new StringBuilder();
        for (char[] array : charArrays) {
            sb.append(array).append("\n");
        }
        return sb.toString();
    }

    /**
     * Converts a string to a 2D character array.
     * 
     * @param str the string.
     * @return the 2D character array.
     */
    private char[][] stringToCharArrays(String str) {
        String[] lines = str.split("\n");
        char[][] charArrays = new char[lines.length][];
        for (int i = 0; i < lines.length; i++) {
            charArrays[i] = lines[i].toCharArray();
        }
        return charArrays;
    }

    /**
     * Cleans the database of all articles.
     * 
     * @return true if the database was cleaned successfully, false otherwise.
     */
    public boolean cleanDB() {
        try {
            databaseService.executeUpdate("DELETE FROM articles");
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    // Add this method to send a help request
    public void sendHelpRequest(String userId, String message, String searchHistory) throws SQLException {
        System.out.println("INFO: Sending help request with message: " + message + " and search history: " + searchHistory);
        String sql = """
            INSERT INTO help_requests (user_id, message, search_history)
            VALUES (?, ?, ?)
        """;
        databaseService.executeUpdate(sql, userId, message, searchHistory);
    }
}
