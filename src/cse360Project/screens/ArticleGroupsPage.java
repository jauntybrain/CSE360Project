package cse360Project.screens;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import cse360Project.models.ArticleGroup;
import cse360Project.models.HelpArticle;
import cse360Project.models.Role;
import cse360Project.models.User;
import cse360Project.screens.ArticleGroupsPage.UserListItem;
import cse360Project.services.EventService;
import cse360Project.services.HelpArticleService;
import cse360Project.services.UserService;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/*******
 * <p>
 * ArticleGroupsPage Class.
 * </p>
 * 
 * <p>
 * Description: Displays and manages article groups.
 * </p>
 * 
 * <p>
 * Copyright: CSE 360 Team Th02 © 2024
 * </p>
 * 
 * @version 1.00 2024-03-20 Phase three
 * 
 */
public class ArticleGroupsPage extends Application {
    private TableView<ArticleGroup> tableView;
    private HelpArticleService helpArticleService;
    private UserService userService = UserService.getInstance();
    private VBox rootNode;

    /**
     * Gets the root node.
     * 
     * @return the root node.
     */
    public Parent getRootNode() {
        return rootNode;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        helpArticleService = new HelpArticleService();

        tableView = new TableView<>();
        setupTableView();

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            userService.setCurrentUser(null);
            new LoginPage().start(new Stage());
            primaryStage.close();
        });

        Button createGroupButton = new Button("Create Group");
        createGroupButton.setOnAction(e -> showModifyDialog(null));
        createGroupButton.setVisible(userService.getCurrentUser().getRoles().contains(Role.INSTRUCTOR));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox buttonsRow = new HBox(10, createGroupButton, spacer, logoutButton);

        rootNode = new VBox(10, tableView, buttonsRow);
        rootNode.setPadding(new Insets(10));

        Scene scene = new Scene(rootNode, 900, 450);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Article Groups Dashboard");
        primaryStage.show();

        loadGroups();

        EventService.getInstance().addArticleGroupsPageListener(this::loadGroups);
    }

    @SuppressWarnings("unchecked")
    private void setupTableView() {
        TableColumn<ArticleGroup, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<ArticleGroup, String> protectedCol = new TableColumn<>("Protected");
        protectedCol.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().isProtected() ? "Yes" : "No"));
        protectedCol.setPrefWidth(100);

        TableColumn<ArticleGroup, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(param -> new TableCell<ArticleGroup, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttonBox = new HBox(5, editButton, deleteButton);

            {
                editButton.setOnAction(e -> {
                    ArticleGroup group = getTableView().getItems().get(getIndex());
                    showModifyDialog(group);
                });

                deleteButton.setOnAction(e -> {
                    ArticleGroup group = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(group);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    ArticleGroup group = getTableView().getItems().get(getIndex());
                    if (group.isAdmin()) {
                        setGraphic(buttonBox);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
        actionCol.setPrefWidth(150);

        tableView.getColumns().addAll(nameCol, protectedCol, actionCol);
    }

    private void loadGroups() {
        try {
            List<ArticleGroup> groups = helpArticleService.getAllGroups();
            ObservableList<ArticleGroup> data = FXCollections.observableArrayList(groups);
            tableView.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to load groups", e.getMessage());
        }
    }

    private void showModifyDialog(ArticleGroup group) {
        Dialog<ArticleGroup> dialog = new Dialog<>();
        dialog.setTitle(group == null ? "Create Group" : "Edit Group");
        dialog.setHeaderText(group == null ? "Create new group" : "Edit group: " + group.getName());

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Group name field
        TextField nameField = new TextField(group != null ? group.getName() : "");
        VBox nameBox = new VBox(5, new Label("Group Name:"), nameField);

        // Articles List
        ListView<String> articlesListView = new ListView<>();
        Button addArticleButton = new Button("Add Articles");
        Button removeArticleButton = new Button("Remove Selected");
        VBox articleButtons = new VBox(5, addArticleButton, removeArticleButton);
        HBox articlesBox = new HBox(10, new VBox(5, new Label("Articles:"), articlesListView), articleButtons);

        // Map to store article UUIDs to titles
        HashMap<String, String> articleUUIDToTitleMap = new HashMap<>();

        // Load articles if editing
        if (group != null) {
            try {
                List<HelpArticle> groupArticles = helpArticleService.getGroupArticles(group.getId());
                groupArticles.forEach(article -> {
                    articlesListView.getItems().add(article.getTitle());
                    articleUUIDToTitleMap.put(article.getUuid(), article.getTitle());
                });
            } catch (SQLException e) {
                showError("Error", "Failed to load group articles");
            }
        }

        // Protected checkbox and Users List
        CheckBox protectedCheckBox = new CheckBox("Make protected?");
        protectedCheckBox.setSelected(group != null && group.isProtected());

        ListView<UserListItem> usersListView = new ListView<>();
        Button addUserButton = new Button("Add Users");
        Button removeUserButton = new Button("Remove Selected");

        usersListView.setDisable(!protectedCheckBox.isSelected());
        addUserButton.setDisable(!protectedCheckBox.isSelected());
        removeUserButton.setDisable(!protectedCheckBox.isSelected());

        VBox userButtons = new VBox(5, addUserButton, removeUserButton);
        VBox usersBox = new VBox(5,
                protectedCheckBox,
                new Label("Users:"),
                new HBox(10, usersListView, userButtons));

        usersListView.setPrefWidth(400);

        // Load users if editing
        if (group != null) {
            HashMap<User, Boolean> groupUsers = helpArticleService.getGroupUsers(group.getId());
            if (groupUsers == null) {
                showError("Error", "Failed to load group users");
                return;
            }
            User currentUser = userService.getCurrentUser();
            usersListView.getItems().addAll(groupUsers.entrySet().stream()
                    .map(entry -> new UserListItem(
                            entry.getKey().getUsername(),
                            entry.getValue(),
                            entry.getKey().getRoles(),
                            entry.getKey().getUuid().equals(currentUser.getUuid())))
                    .collect(Collectors.toList()));
        }

        // Enable/disable users section based on protected checkbox
        protectedCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            usersListView.setDisable(!newVal);
            addUserButton.setDisable(!newVal);
            removeUserButton.setDisable(!newVal);
        });

        // Add Article button action
        addArticleButton.setOnAction(e -> showAddArticlesDialog(articlesListView, articleUUIDToTitleMap));

        // Remove Article button action
        removeArticleButton.setOnAction(e -> {
            List<String> selectedArticles = articlesListView.getSelectionModel().getSelectedItems();
            List<String> removableArticles = new ArrayList<>();

            for (String articleTitle : selectedArticles) {
                String articleUUID = articleUUIDToTitleMap.entrySet().stream()
                        .filter(entry -> entry.getValue().equals(articleTitle))
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElse(null);
                if (group != null) {
                    if (articleUUID != null) {
                        try {
                            List<Integer> associatedGroups = helpArticleService.getArticleGroupIds(articleUUID);
                            if (associatedGroups.size() > 1 || !associatedGroups.contains(group.getId())) {
                                removableArticles.add(articleTitle);
                            } else {
                                showError("Cannot Remove Article",
                                        "Article \"" + articleTitle + "\" is only associated with this group.");
                            }
                        } catch (SQLException ex) {
                            showError("Error", "Failed to check article associations");
                            ex.printStackTrace();
                        }
                    } else {
                        showError("Error", "Article \"" + articleTitle + "\" does not have a valid UUID.");
                    }
                }
            }

            articlesListView.getItems().removeAll(removableArticles);
        });

        // Add User button action
        addUserButton.setOnAction(e -> showAddUsersDialog(usersListView));

        // Remove User button action
        removeUserButton.setOnAction(e -> {
            List<UserListItem> selectedUsers = usersListView.getSelectionModel().getSelectedItems();
            List<UserListItem> removableUsers = new ArrayList<>();

            for (UserListItem user : selectedUsers) {
                if (user.isAdmin()) {
                    long adminCount = usersListView.getItems().stream()
                            .filter(UserListItem::isAdmin)
                            .count();

                    if (adminCount > 1) {
                        removableUsers.add(user);
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Admin Required");
                        alert.setHeaderText(null);
                        alert.setContentText("Cannot remove the last admin. At least one user must have admin rights.");
                        alert.showAndWait();
                    }
                } else {
                    removableUsers.add(user);
                }
            }

            usersListView.getItems().removeAll(removableUsers);
        });

        // Layout
        HBox listsContainer = new HBox(20, articlesBox, usersBox);
        VBox content = new VBox(20);
        content.getChildren().addAll(nameBox, listsContainer);
        content.setPadding(new Insets(20));
        content.setPrefWidth(900);

        dialog.getDialogPane().setContent(content);

        // Add validation before saving
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                boolean hasAdmin = usersListView.getItems().stream()
                        .anyMatch(UserListItem::isAdmin);

                if (!hasAdmin && protectedCheckBox.isSelected()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Validation Error");
                    alert.setHeaderText(null);
                    alert.setContentText("At least one user must be assigned admin rights for protected groups.");
                    alert.showAndWait();
                    return null;
                }

                // Convert ListView items to UUIDs
                List<String> articleUUIDs = articlesListView.getItems().stream()
                        .map(title -> articleUUIDToTitleMap.entrySet().stream()
                                .filter(entry -> entry.getValue().equals(title))
                                .map(Map.Entry::getKey)
                                .findFirst()
                                .orElse(null))
                        .collect(Collectors.toList());

                ArticleGroup modifiedGroup = new ArticleGroup(
                        group != null ? group.getId() : -1,
                        nameField.getText(),
                        protectedCheckBox.isSelected(),
                        group != null && group.isAdmin());

                try {
                    helpArticleService.modifyGroup(modifiedGroup, articleUUIDs, usersListView.getItems());
                } catch (SQLException e) {
                    showError("Error", "Failed to modify group");
                    e.printStackTrace();
                }
                loadGroups();
                EventService.getInstance().notifyHelpArticlesPage();
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showAddArticlesDialog(ListView<String> articlesListView,
            HashMap<String, String> articleUUIDToTitleMap) {
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("Add Articles");
        dialog.setHeaderText("Select articles to add");

        ButtonType addButtonType = new ButtonType("Add Selected", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        ListView<String> availableArticles = new ListView<>();
        availableArticles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        try {
            List<HelpArticle> allArticles = helpArticleService.getAllArticles();
            Set<String> existingTitles = new HashSet<>(articlesListView.getItems());
            availableArticles.getItems().addAll(
                    allArticles.stream()
                            .filter(article -> !existingTitles.contains(article.getTitle()))
                            .map(article -> article.getTitle() + " (" + article.getUuid() + ")")
                            .collect(Collectors.toList()));
        } catch (Exception e) {
            showError("Error", "Failed to load articles");
            e.printStackTrace();
        }

        VBox content = new VBox(10, availableArticles);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new ArrayList<>(availableArticles.getSelectionModel().getSelectedItems());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(selectedArticles -> {
            selectedArticles.forEach(entry -> {
                String[] parts = entry.split(" \\(");
                String title = parts[0];
                String uuid = parts[1].substring(0, parts[1].length() - 1); // Remove the closing parenthesis
                articlesListView.getItems().add(title);
                articleUUIDToTitleMap.put(uuid, title);
            });
        });
    }

    private void showAddUsersDialog(ListView<UserListItem> usersListView) {
        Dialog<List<UserListItem>> dialog = new Dialog<>();
        dialog.setTitle("Add Users");
        dialog.setHeaderText("Select users to add");

        ButtonType addButtonType = new ButtonType("Add Selected", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        ListView<UserListItem> availableUsers = new ListView<>();
        availableUsers.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Get the usernames of already selected users
        Set<String> existingUsernames = usersListView.getItems().stream()
                .map(UserListItem::getUsername)
                .collect(Collectors.toSet());

        // Load users from userService, excluding already selected ones
        List<User> users = userService.getAllUsers();
        availableUsers.getItems().addAll(
                users.stream()
                        .filter(user -> !existingUsernames.contains(user.getUsername()))
                        .map(user -> new UserListItem(
                                user.getUsername(),
                                false,
                                user.getRoles(),
                                false))
                        .collect(Collectors.toList()));

        VBox content = new VBox(10, availableUsers);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new ArrayList<>(availableUsers.getSelectionModel().getSelectedItems());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(selectedUsers -> usersListView.getItems().addAll(selectedUsers));
    }

    // Helper class for user list items with admin checkbox
    public static class UserListItem extends HBox {
        private CheckBox adminCheckBox;
        private final Label rolesLabel;
        private final String username;

        public UserListItem(String username, boolean isAdmin, List<Role> roles, boolean isCreator) {
            super(10);
            this.username = username;

            Label usernameLabel = new Label(username);

            // Add roles label
            String roleText = roles.stream()
                    .map(Role::name)
                    .collect(Collectors.joining(", "));
            rolesLabel = new Label("(" + roleText + ")");
            rolesLabel.setStyle("-fx-font-style: italic;");

            // Check if the user has only the Student role
            boolean isOnlyStudent = roles.size() == 1 && roles.contains(Role.STUDENT);

            if (!isOnlyStudent) {
                adminCheckBox = new CheckBox("Admin rights");
                adminCheckBox.setSelected(isAdmin);

                // If this is the creator, add a note
                if (isCreator) {
                    usernameLabel.setText(username + " (Creator)");
                }

                // Add listener to prevent unchecking if it's the last admin
                adminCheckBox.setOnAction(e -> {
                    try {
                        if (!adminCheckBox.isSelected()) {
                            ListView<UserListItem> listView = (ListView<UserListItem>) getParent().getParent();
                            long adminCount = listView.getItems().stream()
                                    .filter(item -> item != this && item.isAdmin())
                                    .count();

                            if (adminCount == 0) {
                                adminCheckBox.setSelected(true);
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Admin Required");
                                alert.setHeaderText(null);
                                alert.setContentText(
                                        "Cannot remove the last admin. At least one user must have admin rights.");
                                alert.showAndWait();
                            }
                        }
                    } catch (Exception ex) {
                    }
                });

                getChildren().addAll(usernameLabel, rolesLabel, adminCheckBox);
            } else {
                getChildren().addAll(usernameLabel, rolesLabel);
            }
        }

        public String getUsername() {
            return username;
        }

        public boolean isAdmin() {
            return adminCheckBox != null && adminCheckBox.isSelected();
        }
    }

    private void showDeleteConfirmation(ArticleGroup group) {
        try {
            // Get articles associated with the group
            List<HelpArticle> groupArticles = helpArticleService.getGroupArticles(group.getId());
            List<String> articlesPreventingDeletion = new ArrayList<>();

            // Check if any article is only associated with this group
            for (HelpArticle article : groupArticles) {
                List<Integer> associatedGroups = helpArticleService.getArticleGroupIds(article.getUuid());
                if (associatedGroups.size() == 1 && associatedGroups.contains(group.getId())) {
                    articlesPreventingDeletion.add(article.getTitle());
                }
            }

            if (!articlesPreventingDeletion.isEmpty()) {
                // Show warning if there are articles preventing deletion
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Delete Restricted");
                alert.setHeaderText("Cannot delete group: " + group.getName());
                alert.setContentText(
                        "The following articles are only associated with this group and prevent deletion:\n" +
                                String.join(", ", articlesPreventingDeletion) +
                                "\nPlease modify these articles to remove this restriction.");
                alert.showAndWait();
                return;
            }

            // Proceed with deletion confirmation
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete");
            alert.setHeaderText("Delete Group: " + group.getName());
            alert.setContentText("Are you sure you want to delete this group? This action cannot be undone.");

            alert.showAndWait().ifPresent(result -> {
                if (result == ButtonType.OK) {
                    try {
                        helpArticleService.deleteGroup(group.getId());
                        loadGroups(); // Refresh the table
                        EventService.getInstance().notifyHelpArticlesPage();
                    } catch (Exception e) {
                        showError("Delete Failed", "Failed to delete group: " + e.getMessage());
                    }
                }
            });
        } catch (SQLException e) {
            showError("Error", "Failed to check group articles: " + e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() {
        EventService.getInstance().removeAllArticleGroupsPageListeners();
        try {
            super.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}