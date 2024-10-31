package cse360Project.screens;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

/*******
 * <p>
 * InstructorHomePage Class.
 * </p>
 * 
 * <p>
 * Description: Displays the instructor home page.
 * </p>
 * 
 * <p>
 * Copyright: CSE 360 Team Th02 © 2024
 * </p>
 * 
 * @version 1.00 2024-10-30 Phase two
 * 
 */
public class InstructorHomePage extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Instructor Home Page");

        TabPane tabPane = new TabPane();

        Tab helpTab = new Tab("Help Articles");
        helpTab.setClosable(false);

        // Load HelpArticlesPage
        HelpArticlesPage helpArticlesPage = new HelpArticlesPage();
        try {
            helpArticlesPage.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        helpTab.setContent(helpArticlesPage.getRootNode());
        tabPane.getTabs().add(helpTab);

        Scene scene = new Scene(tabPane, 900, 450);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Launches the InstructorHomePage.
     * 
     * @param args The arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}