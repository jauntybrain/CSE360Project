package cse360Project;

import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/*******
 * <p>
 * LoginPage Class
 * </p>
 * 
 * <p>
 * Description: Handles user login, registration, and account setup.
 * </p>
 * 
 * <p>
 * Copyright: CSE 360 Team Th02 © 2024
 * </p>
 * 
 * @version 1.00 2024-10-09 Phase one
 * 
 */
public class LoginPage extends Application {

    // Singleton instance of UserService
    private UserService userService = UserService.getInstance();
    private InvitationCodeService invitationCodeService = InvitationCodeService.getInstance();

    /**
     * Starts the LoginScreen and determines which page to display based on user
     * status.
     * 
     * @param primaryStage The primary stage.
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Welcome");

        // Get the current user from the user service
        User currentUser = userService.getCurrentUser();

        // Determine which page to show based on user authentication status
        if (userService.isFirstUser()) {
            showRegistrationPage(primaryStage);
        } else if (currentUser != null && !(currentUser.isFullyRegistered())) {
            showFinishAccountSetupPage(primaryStage, currentUser);
        } else if (currentUser != null) {
            new HomePage().start(primaryStage);
        } else {
            showLoginPage(primaryStage);
        }
    }

    /**
     * Displays the login page.
     * 
     * @param primaryStage The primary stage.
     */
    private void showLoginPage(Stage primaryStage) {
        primaryStage.setTitle("Login");

        // Grid layout for the login form
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        // Interactive components for login
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Login");
        Label infoLabel = new Label();

        // Add components to the grid
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(loginButton, 1, 2);
        grid.add(infoLabel, 1, 3);

        // Invitation code login option
        Label codeLabel = new Label("Invitation Code:");
        TextField codeField = new TextField();
        Button submitCodeButton = new Button("Submit Code");

        grid.add(codeLabel, 0, 4);
        grid.add(codeField, 1, 4);
        grid.add(submitCodeButton, 1, 5);

        // Handle login button click
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            // Try logging in
            User user = userService.login(username, password);
            if (user != null) {
                if (user.getHasOneTimePassword()) {
                    // If the user has a one-time password, check if it is valid
                    if (userService.verifyOneTimePassword(username, password)) {
                        userService.deleteOneTimePassword(username);
                        showPasswordSetupPage(primaryStage, user);
                    } else {
                        infoLabel.setText("One-time password expired or incorrect.");
                    }
                } else {
                    // Check if the user is fully registered
                    if (user.isFullyRegistered()) {
                        infoLabel.setText("Login successful!");
                        new HomePage().start(new Stage());
                        primaryStage.close();
                    } else {
                        showFinishAccountSetupPage(primaryStage, user);
                    }
                }
            } else {
                infoLabel.setText("Invalid username or password.");
            }
        });

        // Handle invitation code button click
        submitCodeButton.setOnAction(e -> {
            String code = codeField.getText();
            // Validate the invitation code and get assigned roles
            if (invitationCodeService.validateInvitationCode(code)) {
                List<Role> roles = invitationCodeService.getRolesForInvitationCode(code);
                if (roles != null) {
                    userService.setCurrentValidationCode(code);
                    showRegistrationPage(primaryStage);
                } else {
                    infoLabel.setText("Invitation code has already been used.");
                }
            } else {
                infoLabel.setText("Invalid invitation code.");
            }
        });

        Scene scene = new Scene(grid, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Displays the registration page.
     * 
     * @param primaryStage The primary stage.
     */
    private void showRegistrationPage(Stage primaryStage) {
        primaryStage.setTitle("Register");

        // Grid layout for the registration form
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        // Interactive components for registration
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Label confirmPasswordLabel = new Label("Confirm Password:");
        PasswordField confirmPasswordField = new PasswordField();
        Button registerButton = new Button("Register");
        Label infoLabel = new Label();

        // Add components to the grid
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(confirmPasswordLabel, 0, 2);
        grid.add(confirmPasswordField, 1, 2);
        grid.add(registerButton, 1, 3);
        grid.add(infoLabel, 1, 4);

        // Handle register button click
        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            // Check if the passwords match
            if (!password.equals(confirmPassword)) {
                infoLabel.setText("Passwords do not match.");
                return;
            }
            try {
                // Try registering the user
                User registeredUser = userService.register(username, password);
                if (registeredUser == null) {
                    infoLabel.setText("Failed to create account.");
                    return;
                }

                infoLabel.setText("Account created. You can now log in.");
                usernameField.clear();
                passwordField.clear();
                confirmPasswordField.clear();

                // Show dialog notifying about account creation
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Account Created");
                alert.setHeaderText(null);
                alert.setContentText("Account created. You can now log in.");
                alert.showAndWait();

                // Navigate to the login page
                showLoginPage(primaryStage);
            } catch (IllegalArgumentException ex) {
                infoLabel.setText(ex.getMessage());
            }
        });

        Scene scene = new Scene(grid, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Displays the finish account setup page.
     * 
     * @param primaryStage The primary stage.
     * @param user         The user who is completing their account setup.
     */
    private void showFinishAccountSetupPage(Stage primaryStage, User user) {
        // Grid layout for the finish account setup form
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        // Interactive components for account setup
        Label emailLabel = new Label("Email*:");
        TextField emailField = new TextField();
        Label firstNameLabel = new Label("First Name*:");
        TextField firstNameField = new TextField();
        Label middleNameLabel = new Label("Middle Name (optional):");
        TextField middleNameField = new TextField();
        Label lastNameLabel = new Label("Last Name*:");
        TextField lastNameField = new TextField();
        Label preferredNameLabel = new Label("Preferred Name (optional):");
        TextField preferredNameField = new TextField();
        Button finishSetupButton = new Button("Finish Account Setup");
        Label infoLabel = new Label();

        // Add components to the grid
        grid.add(emailLabel, 0, 0);
        grid.add(emailField, 1, 0);
        grid.add(firstNameLabel, 0, 1);
        grid.add(firstNameField, 1, 1);
        grid.add(middleNameLabel, 0, 2);
        grid.add(middleNameField, 1, 2);
        grid.add(lastNameLabel, 0, 3);
        grid.add(lastNameField, 1, 3);
        grid.add(preferredNameLabel, 0, 4);
        grid.add(preferredNameField, 1, 4);
        grid.add(finishSetupButton, 1, 5);
        grid.add(infoLabel, 1, 6);

        // Handle finish setup button click
        finishSetupButton.setOnAction(e -> {
            String email = emailField.getText();
            String firstName = firstNameField.getText();
            String middleName = middleNameField.getText();
            String lastName = lastNameField.getText();
            String preferredName = preferredNameField.getText();

            // Check if the required fields are empty
            if (email.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                infoLabel.setText("Email, First Name, and Last Name are required.");
                return;
            }

            // Validate fields
            if (!userService.isValidEmail(email)) {
                infoLabel.setText("Invalid email format.");
                return;
            }

            if (!userService.isValidName(firstName) || !userService.isValidName(lastName) ||
                    (!middleName.isEmpty() && !userService.isValidName(middleName)) ||
                    (!preferredName.isEmpty() && !userService.isValidName(preferredName))) {
                infoLabel.setText("Names can only contain Latin letters and spaces, max length 60.");
                return;
            }

            user.setEmail(email);
            user.setFirstName(firstName);
            user.setMiddleName(middleName);
            user.setLastName(lastName);
            user.setPreferredName(preferredName);

            // Update user info
            userService.updateUser(user);
            infoLabel.setText("Account setup completed successfully!");

            // Navigate to the home page
            new HomePage().start(new Stage());
            primaryStage.close();
        });

        Scene scene = new Scene(grid, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Finish Account Setup");
    }

    /**
     * Displays the password setup page.
     * 
     * @param primaryStage The primary stage.
     * @param user         The user who is setting up their password.
     */
    private void showPasswordSetupPage(Stage primaryStage, User user) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Set Up New Password");
        dialog.setHeaderText("Please set up a new password.");

        // Action buttons for password setup
        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        // Grid layout for the password setup form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Password fields
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");

        // Add components to the grid
        grid.add(new Label("New Password:"), 0, 0);
        grid.add(newPasswordField, 1, 0);
        grid.add(new Label("Confirm Password:"), 0, 1);
        grid.add(confirmPasswordField, 1, 1);

        // Assign the grid to the dialog
        dialog.getDialogPane().setContent(grid);

        // Disable the submit button if the passwords do not match
        Node submitButton = dialog.getDialogPane().lookupButton(submitButtonType);
        submitButton.setDisable(true);

        // Add listeners to enable submit button when passwords match
        newPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            submitButton.setDisable(!newValue.equals(confirmPasswordField.getText()));
        });

        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            submitButton.setDisable(!newValue.equals(newPasswordField.getText()));
        });

        // Handle dialog result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                return newPasswordField.getText();
            }
            return null;
        });

        // After the user clicks submit, update the password and show the login page
        dialog.showAndWait().ifPresent(password -> {
            // Update user's password
            userService.updateUserPassword(user.getUsername(), password);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Password Updated");
            alert.setHeaderText(null);
            alert.setContentText("Your password is updated. Please log in again.");
            alert.showAndWait();

            // Navigate to the login page
            showLoginPage(primaryStage);
        });
    }

    /**
     * The main method to launch the JavaFX application.
     * 
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}