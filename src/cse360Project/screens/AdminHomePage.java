package cse360Project.screens;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cse360Project.models.Role;
import cse360Project.models.User;
import cse360Project.services.InvitationCodeService;
import cse360Project.services.UserService;

/*******
 * <p>
 * AdminHomePage Class
 * </p>
 * 
 * <p>
 * Description: Admin dashboard to manage users.
 * and roles.
 * </p>
 * 
 * <p>
 * Copyright: CSE 360 Team Th02 © 2024
 * </p>
 * 
 * @version 1.01 2024-10-30 Phase two
 *          1.00 2024-10-09 Phase one
 * 
 */
public class AdminHomePage extends Application {

    // Singleton instance of UserService
    private UserService userService = UserService.getInstance();
    private InvitationCodeService invitationCodeService = InvitationCodeService.getInstance();

    // TableView to show all user accounts
    private TableView<User> userTable = new TableView<>();

    /**
     * Starts the AdminHomePage and sets up the dashboard interface.
     * 
     * @param primaryStage The primary stage.
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Admin Home Page");

        // Create a TabPane
        TabPane tabPane = new TabPane();

        // Create the Admin Home Page tab
        Tab adminTab = new Tab("Manage Users");
        adminTab.setClosable(false);
        VBox adminVBox = new VBox(10);
        adminVBox.setPadding(new Insets(10));

        // Get the current user
        User currentUser = userService.getCurrentUser();

        // Setup user table
        userTable.setEditable(true);
        setupUserTableColumns(primaryStage);

        // Add observable (stateful) users to the table
        ObservableList<User> observableUsers = FXCollections.observableArrayList(userService.getAllUsers());
        userTable.setItems(observableUsers);

        adminVBox.getChildren().add(userTable);

        // Create an HBox for the invite and logout buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setSpacing(10);
        buttonBox.setPrefWidth(Double.MAX_VALUE);

        // Add invite button
        Button inviteButton = new Button("Invite User");
        inviteButton.setOnAction(e -> inviteUser());
        buttonBox.getChildren().add(inviteButton);

        // Add a spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        buttonBox.getChildren().add(spacer);

        // Add logout button
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            userService.setCurrentUser(null);
            new LoginPage().start(new Stage());
            primaryStage.close();
        });
        buttonBox.getChildren().add(logoutButton);

        adminVBox.getChildren().add(buttonBox);

        adminTab.setContent(adminVBox);

        // Create the Help Articles Page tab
        Tab helpTab = new Tab("Help Articles");
        helpTab.setClosable(false);

        // Load content from HelpArticlesPage
        HelpArticlesPage helpArticlesPage = new HelpArticlesPage();
        try {
            helpArticlesPage.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Add the HelpArticlesPage's root node to the tab
        helpTab.setContent(helpArticlesPage.getRootNode());

        Tab articleGroupsTab = new Tab("Article Groups");
        articleGroupsTab.setClosable(false);

        // Load content from ArticleGroupsPage
        ArticleGroupsPage articleGroupsPage = new ArticleGroupsPage();
        try {
            articleGroupsPage.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Add the ArticleGroupsPage's root node to the tab
        articleGroupsTab.setContent(articleGroupsPage.getRootNode());

        // Add tabs to the TabPane
        tabPane.getTabs().addAll(adminTab, helpTab, articleGroupsTab);

        // Set the scene with the TabPane
        Scene scene = new Scene(tabPane, 900, 450);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Sets up the user table.
     */
    private void setupUserTableColumns(Stage primaryStage) {
        // Define columns
        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<User, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<User, String> middleNameCol = new TableColumn<>("Middle Name");
        middleNameCol.setCellValueFactory(new PropertyValueFactory<>("middleName"));

        TableColumn<User, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<User, String> preferredNameCol = new TableColumn<>("Preferred Name");
        preferredNameCol.setCellValueFactory(new PropertyValueFactory<>("preferredName"));

        // Define roles column with checkboxes
        TableColumn<User, HBox> rolesCol = new TableColumn<>("Roles");
        rolesCol.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            HBox roleBox = new HBox(5);
            for (Role role : Role.values()) {
                CheckBox roleCheckBox = new CheckBox(role.name());
                // Select roles
                roleCheckBox.setSelected(user.getRoles().contains(role));

                roleCheckBox.setOnAction(e -> handleRoleChange(user, role, roleCheckBox));
                roleBox.getChildren().add(roleCheckBox);
            }
            return new SimpleObjectProperty<>(roleBox);
        });

        // Define actions column
        TableColumn<User, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button resetButton = new Button("Reset");
            private final Button deleteButton = new Button("Delete");

            {
                resetButton.setOnAction(e -> resetUser(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(e -> deleteUser(getTableView().getItems().get(getIndex()), primaryStage));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, resetButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });

        // Add columns to the table
        userTable.getColumns().addAll(usernameCol, firstNameCol, middleNameCol, lastNameCol, preferredNameCol, rolesCol,
                actionCol);
    }

    /**
     * Handles role changes for a user.
     * 
     * @param user         The user whose role is being changed.
     * @param role         The role being changed.
     * @param roleCheckBox The role checkbox.
     */
    private void handleRoleChange(User user, Role role, CheckBox roleCheckBox) {
        if (roleCheckBox.isSelected()) {
            userService.addRole(user.getUuid(), role);
        } else {
            // Check if removing the admin role would leave no admins
            if (role == Role.ADMIN) {
                long adminCount = userService.getAllUsers().stream()
                        .filter(u -> u.getRoles().contains(Role.ADMIN))
                        .count();
                if (adminCount <= 1) {
                    roleCheckBox.setSelected(true); // Revert the change
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Role Change Denied");
                    alert.setHeaderText(null);
                    alert.setContentText("Cannot remove the last admin role.");
                    alert.showAndWait();
                    return;
                }
            }
            userService.removeRole(user.getUuid(), role);
        }
    }

    /**
     * Creates a user invitation code with assigned roles.
     */
    private void inviteUser() {
        List<Role> roles = Arrays.asList(Role.values());

        // Create role checkboxes UI
        VBox roleCheckBoxContainer = new VBox(5);
        List<CheckBox> roleCheckBoxes = new ArrayList<>();

        for (Role role : roles) {
            CheckBox checkBox = new CheckBox(role.name());
            roleCheckBoxes.add(checkBox);
            roleCheckBoxContainer.getChildren().add(checkBox);
        }

        // Select roles dialog (can pick multiple)
        Dialog<List<Role>> dialog = new Dialog<>();
        dialog.setTitle("Select Roles");
        dialog.setHeaderText("Select roles for the new user");
        dialog.getDialogPane().setContent(roleCheckBoxContainer);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                List<Role> selectedRoles = new ArrayList<>();
                for (int i = 0; i < roles.size(); i++) {
                    if (roleCheckBoxes.get(i).isSelected()) {
                        selectedRoles.add(roles.get(i));
                    }
                }
                return selectedRoles;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(selectedRoles -> {
            // Generate one-time code
            String oneTimeCode = invitationCodeService.generateOneTimeCode(selectedRoles);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Invitation Code");
            alert.setHeaderText("Invitation generated. Selected roles: " + selectedRoles);

            // Add Copy button to store the code in the clipboard
            Button copyButton = new Button("Copy invitation code to clipboard");
            copyButton.setOnAction(event -> {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(oneTimeCode);
                clipboard.setContent(content);
            });

            alert.getDialogPane().setContent(copyButton);
            alert.showAndWait();
        });
    }

    /**
     * Resets the user's account by assigning a one-time password.
     * 
     * @param user The user whose account is being reset.
     */
    private void resetUser(User user) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Password Reset");
        confirmAlert.setHeaderText("Are you sure?");
        confirmAlert.setContentText("Do you really want to reset the password for " + user.getUsername() + "?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String oneTimePassword = userService.setOneTimePassword(user.getUuid());

                if (oneTimePassword != null) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Password Reset");
                    alert.setHeaderText("A one-time password has been set for " + user.getUsername());

                    // Add Copy button to store the password in the clipboard
                    Button copyButton = new Button("Copy password to clipboard");
                    copyButton.setOnAction(event -> {
                        Clipboard clipboard = Clipboard.getSystemClipboard();
                        ClipboardContent content = new ClipboardContent();
                        content.putString(oneTimePassword);
                        clipboard.setContent(content);
                    });

                    alert.getDialogPane().setContent(copyButton);
                    alert.showAndWait();
                } else {
                    // Show error message on password reset failure
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText("Password Reset Failed");
                    errorAlert.setContentText(
                            "Something went wrong while resetting the password for " + user.getUsername() + ".");
                    errorAlert.showAndWait();
                }
            }
        });
    }

    /**
     * Deletes the user account.
     * 
     * @param user The user whose account is being deleted.
     */
    private void deleteUser(User user, Stage primaryStage) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");

        // Check if the user is deleting their own account
        boolean isSelfDeletion = userService.getCurrentUser().getUuid().equals(user.getUuid());
        String additionalText = isSelfDeletion ? "\n\nNote: You will be logged out if you delete your own account."
                : "";

        confirmAlert.setHeaderText("Are you sure?");
        confirmAlert.setContentText(
                "Do you really want to delete the account for " + user.getUsername() + "?" + additionalText);

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                userService.deleteUser(user.getUuid());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Account Deleted");
                alert.setHeaderText(null);
                alert.setContentText("The account for " + user.getUsername() + " has been deleted.");
                alert.showAndWait();

                ObservableList<User> observableUsers = FXCollections.observableArrayList(userService.getAllUsers());
                userTable.setItems(observableUsers);
                userTable.refresh();

                // Log out the user if they deleted their own account
                if (isSelfDeletion) {
                    userService.setCurrentUser(null);
                    new LoginPage().start(new Stage());
                    primaryStage.close();
                }
            }
        });
    }
}