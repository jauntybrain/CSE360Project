package cse360Project.services;

import java.sql.*;

/*******
 * <p>
 * DatabaseService Class
 * </p>
 * 
 * <p>
 * Description: Manages database connections and provides generic query
 * functions.
 * </p>
 * 
 * <p>
 * Copyright: CSE 360 Team Th02 © 2024
 * </p>
 * 
 * @version 1.00 2024-10-09 Phase one
 */
public class DatabaseService {
    private static DatabaseService instance;

    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:~/cse360ProjectDatabase";
    private static final String USER = "sa";
    private static final String PASS = "";

    private DatabaseService() {
        // Initialize the database driver
        try {
            Class.forName(JDBC_DRIVER);
            createTables();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Database driver not found", e);
        } catch(SQLException e) {
        	throw new RuntimeException("Could not create tables", e);
        }
    }

    public static DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }

    private void createTables() throws SQLException {
        createArticlesTable();
        createUsersTable();
        createInvitationCodesTable();
    }

    private void createArticlesTable() throws SQLException {
        final Connection connection = getConnection();
        final Statement statement = connection.createStatement();

        // Create articles table
        String articleTable = """
                CREATE TABLE IF NOT EXISTS articles (
                    uuid VARCHAR(36) UNIQUE NOT NULL PRIMARY KEY,
                    title TEXT,
                    authors TEXT,
                    abstract TEXT,
                    keywords TEXT,
                    body TEXT,
                    references TEXT,
                    level TEXT,
                    iv TEXT
                )
                """;

        // Create article_groups table
        String articleGroupsTable = """
                CREATE TABLE IF NOT EXISTS article_groups (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    article_uuid VARCHAR(36) NOT NULL,
                    group_name VARCHAR(255) NOT NULL,
                    FOREIGN KEY (article_uuid) REFERENCES articles(uuid)
                    ON DELETE CASCADE
                )
                """;

        try {
            statement.execute(articleTable);
            statement.execute(articleGroupsTable);
        } finally {
            statement.close();
            connection.close();
        }
    }

    private void createUsersTable() throws SQLException {
        final Connection connection = getConnection();
        final Statement statement = connection.createStatement();

        // Create users table
        String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    uuid VARCHAR(36) UNIQUE NOT NULL PRIMARY KEY,
                    username VARCHAR(36) NOT NULL,
                    password BINARY(32) NOT NULL,
                    email VARCHAR(255),
                    first_name VARCHAR(60),
                    middle_name VARCHAR(60),
                    last_name VARCHAR(60),
                    preferred_name VARCHAR(60),
                    has_one_time_password BOOLEAN,
                    one_time_password_expires TIMESTAMP
                )
                """;

        // Create roles table
        String createRolesTable = """
                CREATE TABLE IF NOT EXISTS user_roles (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id VARCHAR(36) NOT NULL,
                    role VARCHAR(20) NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(uuid)
                    ON DELETE CASCADE
                )
                """;

        // Create topics table
        String createTopicsTable = """
                CREATE TABLE IF NOT EXISTS user_topics (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id VARCHAR(36) NOT NULL,
                    topic VARCHAR(20) NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(uuid)
                    ON DELETE CASCADE
                )
                """;

        try {
            statement.execute(createUsersTable);
            statement.execute(createRolesTable);
            statement.execute(createTopicsTable);
        } finally {
            statement.close();
            connection.close();
        }
    }

    private void createInvitationCodesTable() throws SQLException {
        final Connection connection = getConnection();
        final Statement statement = connection.createStatement();

        String createInvitationCodesTable = """
                CREATE TABLE IF NOT EXISTS invitation_codes (
                    uuid VARCHAR(36) UNIQUE NOT NULL PRIMARY KEY,
                    code VARCHAR(36) NOT NULL,
                    used BOOLEAN NOT NULL
                )
                """;

        String createInvitationCodeRolesTable = """
                CREATE TABLE IF NOT EXISTS invitation_code_roles (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    invitation_code_id VARCHAR(36) NOT NULL,
                    role VARCHAR(20) NOT NULL,
                    FOREIGN KEY (invitation_code_id) REFERENCES invitation_codes(uuid)
                    ON DELETE CASCADE
                )
                """;

        try {
            statement.execute(createInvitationCodesTable);
            statement.execute(createInvitationCodeRolesTable);
        } finally {
            statement.close();
            connection.close();
        }
    }

    /**
     * Gets a database connection.
     * 
     * @return A Connection object
     * @throws SQLException if a database access error occurs
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    /**
     * Executes an update SQL statement.
     * 
     * @param sql    The SQL query to execute
     * @param params The parameters for the prepared statement
     * @return The number of rows affected
     * @throws SQLException if a database access error occurs
     */
    public int executeUpdate(String sql, Object... params) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        setParameters(stmt, params);
        return stmt.executeUpdate();
    }

    /**
     * Executes a query SQL statement.
     * 
     * @param sql    The SQL query to execute
     * @param params The parameters for the prepared statement
     * @return A ResultSet object
     * @throws SQLException if a database access error occurs
     */
    public ResultSet executeQuery(String sql, Object... params) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        setParameters(stmt, params);
        return stmt.executeQuery();
    }

    /**
     * Sets parameters for a prepared statement.
     * 
     * @param stmt   The prepared statement
     * @param params The parameters to set
     * @throws SQLException if a database access error occurs
     */
    private void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            if (params[i] == null) {
                stmt.setNull(i + 1, Types.NULL);
            } else if (params[i] instanceof String) {
                stmt.setString(i + 1, (String) params[i]);
            } else if (params[i] instanceof Integer) {
                stmt.setInt(i + 1, (Integer) params[i]);
            } else if (params[i] instanceof Long) {
                stmt.setLong(i + 1, (Long) params[i]);
            } else if (params[i] instanceof Double) {
                stmt.setDouble(i + 1, (Double) params[i]);
            } else if (params[i] instanceof Boolean) {
                stmt.setBoolean(i + 1, (Boolean) params[i]);
            } else if (params[i] instanceof Date) {
                stmt.setDate(i + 1, (Date) params[i]);
            } else if (params[i] instanceof Timestamp) {
                stmt.setTimestamp(i + 1, (Timestamp) params[i]);
            } else if (params[i] instanceof byte[]) {
                stmt.setBytes(i + 1, (byte[]) params[i]);
            } else {
                throw new SQLException("Unsupported parameter type: " + params[i].getClass());
            }
        }
    }
}