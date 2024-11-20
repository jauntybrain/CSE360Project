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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import cse360Project.services.encryption.*;
import cse360Project.models.HelpArticle;
import cse360Project.models.Topic;

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

    /**
     * Creates HelpArticleService and initializes EncryptionService.
     * 
     * @throws Exception if an error occurs during initialization.
     */
    public HelpArticleService() throws Exception {
        encryptionService = new EncryptionService();
        databaseService = DatabaseService.getInstance();
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

        // delete and reinsert groups
        databaseService.executeUpdate("DELETE FROM article_groups WHERE article_uuid = ?", article.getUuid());
        for (String group : article.getGroups()) {
            databaseService.executeUpdate("INSERT INTO article_groups (article_uuid, group_name) VALUES (?, ?)",
                    article.getUuid(), group);
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
        String query = "SELECT * FROM articles";
        List<HelpArticle> articles = new ArrayList<>();

        ResultSet rs = databaseService.executeQuery(query);
        while (rs.next()) {
            HelpArticle article = decryptArticle(rs);
            List<String> groups = getArticleGroups(article.getUuid());
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
    public void backupArticles(String filename, Set<String> selectedGroups) throws Exception {
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
                String encryptedGroups = encryptField(String.join(",", article.getGroups()), iv);
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
                    List<String> groups = Arrays.asList(decryptFieldToString(encryptedGroups, iv).split(","));
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
    public List<String> getAllGroups() throws Exception {
        Set<String> groups = new HashSet<>();
        List<HelpArticle> articles = getAllArticles();

        for (HelpArticle article : articles) {
            for (String group : article.getGroups()) {
                groups.add(group);
            }
        }

        return new ArrayList<>(groups);
    }

    private List<String> getArticleGroups(String uuid) throws SQLException {
        System.out.println("Getting article groups for UUID: " + uuid);
        String query = "SELECT group_name FROM article_groups WHERE article_uuid = ?";
        ResultSet rs = databaseService.executeQuery(query, uuid);
        List<String> groups = new ArrayList<>();
        while (rs.next()) {
            System.out.println("Group: " + rs.getString("group_name"));
            groups.add(rs.getString("group_name"));
        }
        return groups;
    }

    /**
     * Gets articles by groups.
     * 
     * @param groups list of groups.
     * @return list of articles.
     * @throws Exception if an error occurs.
     */
    public List<HelpArticle> getArticlesByGroups(List<String> groups) throws Exception {
        List<HelpArticle> allArticles = getAllArticles();
        return allArticles.stream()
                .filter(article -> {
                    for (String articleGroup : article.getGroups()) {
                        if (groups.contains(articleGroup)) {
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
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
}
