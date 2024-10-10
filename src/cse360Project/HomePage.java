package cse360Project;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/*******
 * <p>
 * HomePage Class
 * </p>
 * 
 * <p>
 * Description: Displays the home page for the user based on their role.
 * </p>
 * 
 * <p>
 * Copyright: CSE 360 Team Th02 © 2024
 * </p>
 * 
 * @version 1.00 2024-10-09 Phase one
 * 
 */
public class HomePage extends Application {

    // Singleton instance of UserService
    private UserService userService = UserService.getInstance();

    /**
     * Starts the HomePage and displays the page based on user role.
     * 
     * @param primaryStage The primary stage.
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Home Page");

        // Vertical box layout for the home page
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        // Get the current user
        User currentUser = userService.getCurrentUser();

        // Check if the user has multiple roles
        // In Phase One, STUDENT and INSTRUCTOR have the same home page with only the
        // logout button
        if (currentUser.getRoles().size() > 1) {
            Label roleLabel = new Label("Select your role for this session:");
            ComboBox<Role> roleComboBox = new ComboBox<>();
            roleComboBox.getItems().addAll(currentUser.getRoles());
            roleComboBox.setValue(currentUser.getRoles().get(0));

            Button selectRoleButton = new Button("Select Role");
            selectRoleButton.setOnAction(e -> {
                Role selectedRole = roleComboBox.getValue();
                if (selectedRole == Role.ADMIN) {
                    new AdminHomePage().start(new Stage());
                    primaryStage.close();
                } else {
                    vbox.getChildren().clear();
                    Label selectedRoleLabel = new Label("Welcome! Your current role: " + selectedRole);
                    vbox.getChildren().add(selectedRoleLabel);
                    showLogoutButton(vbox, primaryStage);
                }
            });

            vbox.getChildren().addAll(roleLabel, roleComboBox, selectRoleButton);
        } else {
            // If the user has only one role, navigate to the corresponding home page
            Role singleRole = currentUser.getRoles().get(0);
            if (singleRole == Role.ADMIN) {
                primaryStage.close();
                new AdminHomePage().start(new Stage());
                return;
            } else {
                Label roleLabel = new Label("Welcome! Your current role: " + singleRole);
                vbox.getChildren().add(roleLabel);
                showLogoutButton(vbox, primaryStage);
            }
        }

        Scene scene = new Scene(vbox, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Displays the logout button.
     * 
     * @param vbox         The VBox to add the button to.
     * @param primaryStage The primary stage.
     */
    private void showLogoutButton(VBox vbox, Stage primaryStage) {
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            userService.setCurrentUser(null);

            // Navigate to the login page
            new LoginPage().start(new Stage());
            primaryStage.close();
        });
        vbox.getChildren().add(logoutButton);
    }

    /**
     * Launches the application.
     * 
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}