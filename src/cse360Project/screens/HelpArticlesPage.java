package cse360Project.screens;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import cse360Project.models.HelpArticle;
import cse360Project.models.Topic;
import cse360Project.services.*;
import javafx.application.Platform;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/*******
 * <p>
 * HelpArticlesPage Class.
 * </p>
 * 
 * <p>
 * Description: Displays articles and provides article management UI.
 * </p>
 * 
 * <p>
 * Copyright: CSE 360 Team Th02 © 2024
 * </p>
 * 
 * @version 1.00 2024-10-30 Phase two
 * 
 */
public class HelpArticlesPage extends Application {

    private TableView<HelpArticleRow> tableView;
    private HelpArticleService helpArticleService;
    private UserService userService = UserService.getInstance();

    private List<HelpArticle> articles;
    private Set<String> selectedGroups = new HashSet<>();
    private List<String> allGroups = new ArrayList<>();

    private Button backupButton;
    private VBox rootNode;
    private Button groupFilterButton;

    private boolean isUpdatingGroups = false;

    /**
     * Gets the root node.
     * 
     * @return the root node.
     */
    public Parent getRootNode() {
        return rootNode;
    }

    /**
     * The main entry point for the JavaFX application.
     * 
     * @param primaryStage the primary stage.
     * @throws Exception if application error occurs.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        helpArticleService = new HelpArticleService();
        helpArticleService.connectToDatabase();

        tableView = new TableView<>();
        setupTableView();

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            userService.setCurrentUser(null);
            new LoginPage().start(new Stage());
            primaryStage.close();
        });

        Button createButton = new Button("Create Article");
        createButton.setOnAction(e -> showModifyArticleDialog(null));

        backupButton = new Button("Backup Articles");
        backupButton.setOnAction(e -> backupArticles());

        Button restoreButton = new Button("Restore Articles");
        restoreButton.setOnAction(e -> restoreArticles());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox buttonsRow = new HBox(10, createButton, backupButton, restoreButton, spacer, logoutButton);

        groupFilterButton = new Button("Filter by Groups");
        groupFilterButton.setOnAction(e -> showGroupFilterDialog());

        HBox filterRow = new HBox(10, new Label("Filter:"), groupFilterButton);

        rootNode = new VBox(10, filterRow, tableView, buttonsRow);
        rootNode.setPadding(new Insets(10));

        Scene scene = new Scene(rootNode, 900, 450);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Article Dashboard");
        primaryStage.show();

        loadArticles();
    }

    /**
     * Sets up the TableView.
     */
    @SuppressWarnings("unchecked")
    private void setupTableView() {
        TableColumn<HelpArticleRow, String> seqCol = new TableColumn<>("Sequence");
        seqCol.setCellValueFactory(new PropertyValueFactory<>("sequence"));
        seqCol.setPrefWidth(50);

        TableColumn<HelpArticleRow, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(150);

        TableColumn<HelpArticleRow, String> abstractTextCol = new TableColumn<>("Abstract");
        abstractTextCol.setCellValueFactory(new PropertyValueFactory<>("abstractText"));
        abstractTextCol.setPrefWidth(150);

        TableColumn<HelpArticleRow, String> authorsCol = new TableColumn<>("Author(s)");
        authorsCol.setCellValueFactory(new PropertyValueFactory<>("authors"));
        authorsCol.setPrefWidth(150);

        TableColumn<HelpArticleRow, String> groupsCol = new TableColumn<>("Group(s)");
        groupsCol.setCellValueFactory(new PropertyValueFactory<>("groups"));
        groupsCol.setPrefWidth(150);

        TableColumn<HelpArticleRow, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(param -> new TableCell<HelpArticleRow, Void>() {
            private final Button viewButton = new Button("View");
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                viewButton.setOnAction(e -> {
                    HelpArticleRow data = getTableView().getItems().get(getIndex());
                    showArticleDetails(data.getId());
                });

                deleteButton.setOnAction(e -> {
                    HelpArticleRow data = getTableView().getItems().get(getIndex());
                    deleteArticle(data.getId());
                });

                editButton.setOnAction(e -> {
                    HelpArticleRow data = getTableView().getItems().get(getIndex());
                    editArticle(data.getId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(10, viewButton, editButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });
        actionCol.setPrefWidth(220);

        tableView.getColumns().addAll(seqCol, titleCol, abstractTextCol, authorsCol, groupsCol, actionCol);
    }

    /**
     * Fetches articles from the database and adds them to the TableView.
     */
    private void loadArticles() {
        try {
            if (selectedGroups.isEmpty()) {
                articles = helpArticleService.getAllArticles();
            } else {
                articles = helpArticleService.getArticlesByGroups(new ArrayList<>(selectedGroups));
            }

            ObservableList<HelpArticleRow> data = FXCollections.observableArrayList();
            int sequence = 1;
            for (HelpArticle article : articles) {
                String title = new String(article.getTitle()).trim();
                String abstractText = new String(article.getAbstractText());
                String authors = article.getAuthorsString();
                String groups = article.getGroupsString();
                data.add(new HelpArticleRow(article.getUuid(), sequence++, title, abstractText, authors, groups));
            }

            tableView.setItems(data);
            backupButton.setDisable(articles.isEmpty());

            // Update groups list if not already updating
            if (!isUpdatingGroups) {
                Platform.runLater(this::updateGroupsList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows a dialog to create a new article.
     * Gets user input and creates a new article in the database.
     */
    private void showModifyArticleDialog(HelpArticle existingArticle) {
        Dialog<HelpArticle> dialog = new Dialog<>();
        dialog.setTitle(existingArticle == null ? "Create New Article" : "Edit Article");

        ButtonType createButtonType = new ButtonType(existingArticle == null ? "Create" : "Update",
                ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        boolean alreadyExists = existingArticle != null;

        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        if (alreadyExists) {
            titleField.setText(new String(existingArticle.getTitle()).trim());
        }

        TextField authorsField = new TextField();
        authorsField.setPromptText("Authors (comma-separated)");
        if (alreadyExists) {
            authorsField.setText(existingArticle.getAuthorsString());
        }

        TextArea abstractField = new TextArea();
        abstractField.setPromptText("Abstract");
        if (alreadyExists) {
            abstractField.setText(new String(existingArticle.getAbstractText()).trim());
        }

        TextField keywordsField = new TextField();
        keywordsField.setPromptText("Keywords (comma-separated)");
        if (alreadyExists) {
            keywordsField.setText(existingArticle.getKeywordsString());
        }

        TextArea bodyField = new TextArea();
        bodyField.setPromptText("Body of the article");
        if (alreadyExists) {
            bodyField.setText(new String(existingArticle.getBody()).trim());
        }

        TextField referencesField = new TextField();
        referencesField.setPromptText("References (comma-separated)");
        if (alreadyExists) {
            referencesField.setText(existingArticle.getReferencesString());
        }

        TextField groupsField = new TextField();
        groupsField.setPromptText("Groups (comma-separated)");
        if (alreadyExists) {
            groupsField.setText(existingArticle.getGroupsString());
        }

        ComboBox<Topic> levelComboBox = new ComboBox<>();
        levelComboBox.getItems().addAll(Topic.values());
        levelComboBox.setPromptText("Choose level");
        if (alreadyExists) {
            levelComboBox.setValue(existingArticle.getLevel());
        }

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.getChildren().addAll(
                new Label("Title:"), titleField,
                new Label("Authors:"), authorsField,
                new Label("Abstract:"), abstractField,
                new Label("Keywords:"), keywordsField,
                new Label("Body:"), bodyField,
                new Label("References:"), referencesField,
                new Label("Groups:"), groupsField,
                new Label("Level:"), levelComboBox,
                errorLabel);

        dialog.getDialogPane().setContent(vbox);

        Node createButton = dialog.getDialogPane().lookupButton(createButtonType);
        createButton.addEventFilter(ActionEvent.ACTION, event -> {
            String title = titleField.getText().trim();
            String authorsText = authorsField.getText().trim();
            String abstractText = abstractField.getText().trim();
            String keywordsText = keywordsField.getText().trim();
            String bodyText = bodyField.getText().trim();
            String referencesText = referencesField.getText().trim();
            String groupsText = groupsField.getText().trim();
            Topic level = levelComboBox.getValue();

            StringBuilder validationMessage = new StringBuilder();
            if (title.isEmpty())
                validationMessage.append("- Title is required\n");
            if (authorsText.isEmpty())
                validationMessage.append("- Authors are required\n");
            if (abstractText.isEmpty())
                validationMessage.append("- Abstract is required\n");
            if (keywordsText.isEmpty())
                validationMessage.append("- Keywords are required\n");
            if (bodyText.isEmpty())
                validationMessage.append("- Body is required\n");
            if (referencesText.isEmpty())
                validationMessage.append("- References are required\n");
            if (groupsText.isEmpty())
                validationMessage.append("- Groups are required\n");
            if (level == null)
                validationMessage.append("- Level is required\n");

            if (validationMessage.length() > 0) {
                errorLabel.setText(validationMessage.toString());
                event.consume(); // Prevent the dialog from closing
            } else {
                // Process the validated data
                String[] authorsArray = authorsText.split(",");
                char[][] authors = new char[authorsArray.length][];
                for (int i = 0; i < authorsArray.length; i++) {
                    authors[i] = authorsArray[i].trim().toCharArray();
                }

                String[] keywordsArray = keywordsText.split(",");
                char[][] keywords = new char[keywordsArray.length][];
                for (int i = 0; i < keywordsArray.length; i++) {
                    keywords[i] = keywordsArray[i].trim().toCharArray();
                }

                String[] referencesArray = referencesText.split(",");
                char[][] references = new char[referencesArray.length][];
                for (int i = 0; i < referencesArray.length; i++) {
                    references[i] = referencesArray[i].trim().toCharArray();
                }

                String[] groupsArray = groupsText.split(",");
                char[][] groups = new char[groupsArray.length][];
                for (int i = 0; i < groupsArray.length; i++) {
                    groups[i] = groupsArray[i].trim().toCharArray();
                }

                HelpArticle article = new HelpArticle(alreadyExists ? existingArticle.getUuid() : null,
                        title.toCharArray(),
                        authors,
                        abstractText.toCharArray(),
                        keywords,
                        bodyText.toCharArray(),
                        references,
                        groups,
                        level);

                try {
                    helpArticleService.modifyArticle(article, alreadyExists);
                    loadArticles();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        dialog.showAndWait();
    }

    /**
     * Deletes an article from the database and refreshes the table.
     * 
     * @param id the ID of the article to delete.
     */
    private void deleteArticle(String id) {
        try {
            helpArticleService.deleteArticle(id);
            loadArticles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editArticle(String articleId) {
        HelpArticle article = articles.stream()
                .filter(a -> a.getUuid().equals(articleId))
                .findFirst()
                .orElse(null);

        if (article != null) {
            showModifyArticleDialog(article);
        }
    }

    private void showArticleDetails(String articleId) {
        try {
            HelpArticle article = articles.stream()
                    .filter(a -> a.getUuid().equals(articleId))
                    .findFirst()
                    .orElse(null);

            if (article != null) {
                Dialog<Void> dialog = new Dialog<>();
                dialog.setTitle("Article Details");

                VBox content = new VBox(10);
                content.setPadding(new Insets(10));

                content.getChildren().addAll(
                        new Label("Title: " + new String(article.getTitle()).trim()),
                        new Label("Authors: " + article.getAuthorsString()),
                        new Label("Abstract: " + new String(article.getAbstractText()).trim()),
                        new Label("Keywords: " + article.getKeywordsString()),
                        new Label("Body: " + new String(article.getBody()).trim()),
                        new Label("References: " + article.getReferencesString()),
                        new Label("Groups: " + article.getGroupsString()),
                        new Label("Level: " + article.getLevel().name()));

                dialog.getDialogPane().setContent(content);
                dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

                // Set minimum width for the dialog
                dialog.getDialogPane().setMinWidth(400); // Set to desired minimum width

                dialog.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Backs up articles to a user-specified file.
     */
    private void backupArticles() {
        // Show group selection dialog
        Dialog<Set<String>> dialog = new Dialog<>();
        dialog.setTitle("Backup Articles");
        dialog.setHeaderText("Select groups to backup");

        // Create checkboxes for each group
        VBox checkboxContainer = new VBox(5);
        Map<String, CheckBox> checkboxes = new HashMap<>();

        // Add "All Groups" checkbox
        CheckBox allGroupsCheckbox = new CheckBox("All Groups");
        allGroupsCheckbox.setSelected(true);
        checkboxContainer.getChildren().add(allGroupsCheckbox);

        // Add separator
        checkboxContainer.getChildren().add(new Separator());

        // Add other group checkboxes
        for (String group : allGroups) {
            CheckBox cb = new CheckBox(group);
            cb.setDisable(true); // Initially disabled since "All Groups" is selected
            checkboxes.put(group, cb);
            checkboxContainer.getChildren().add(cb);
        }

        // Handle "All Groups" checkbox logic
        allGroupsCheckbox.setOnAction(e -> {
            checkboxes.values().forEach(cb -> {
                cb.setDisable(allGroupsCheckbox.isSelected());
                if (allGroupsCheckbox.isSelected()) {
                    cb.setSelected(false);
                }
            });
        });

        dialog.getDialogPane().setContent(checkboxContainer);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                if (allGroupsCheckbox.isSelected()) {
                    return new HashSet<>();
                }
                Set<String> selectedGroups = new HashSet<>();
                for (Map.Entry<String, CheckBox> entry : checkboxes.entrySet()) {
                    if (entry.getValue().isSelected()) {
                        selectedGroups.add(entry.getKey());
                    }
                }
                return selectedGroups;
            }
            return null;
        });

        Optional<Set<String>> result = dialog.showAndWait();
        if (result.isPresent()) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Backup");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Backups", "*.bak"));
            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                try {
                    Set<String> selectedGroups = result.get();
                    helpArticleService.backupArticles(file.getAbsolutePath(), selectedGroups);

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Backup Successful");
                    alert.setHeaderText(null);
                    alert.setContentText("Articles have been successfully backed up.");
                    alert.showAndWait();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Backup Failed");
                    alert.setHeaderText(null);
                    alert.setContentText("Failed to backup articles: " + e.getMessage());
                    alert.showAndWait();
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Restores articles from a user-specified backup file.
     */
    private void restoreArticles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Backup");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Backups", "*.bak"));

        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
            dialog.setTitle("Restore Options");
            dialog.setHeaderText("Choose Restore Method");
            dialog.setContentText("Would you like to merge with existing articles or replace them?");

            ButtonType mergeButton = new ButtonType("Merge", ButtonBar.ButtonData.LEFT);
            ButtonType replaceButton = new ButtonType("Replace All", ButtonBar.ButtonData.LEFT);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            dialog.getButtonTypes().setAll(mergeButton, replaceButton, cancelButton);

            dialog.showAndWait().ifPresent(response -> {
                try {
                    boolean merge = response == mergeButton;
                    if (response != cancelButton) {
                        helpArticleService.restoreArticles(file.getAbsolutePath(), merge);
                        loadArticles();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Restore Successful");
                        alert.setHeaderText(null);
                        alert.setContentText("Articles have been successfully restored.");
                        alert.showAndWait();
                    }
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Restore Failed");
                    alert.setHeaderText(null);
                    alert.setContentText("Failed to restore articles: " + e.getMessage());
                    alert.showAndWait();
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Shows the group filter dialog.
     */
    private void showGroupFilterDialog() {
        Dialog<Set<String>> dialog = new Dialog<>();
        dialog.setTitle("Filter by Groups");
        dialog.setHeaderText("Select groups to filter by");

        VBox checkboxContainer = new VBox(5);
        Map<String, CheckBox> checkboxes = new HashMap<>();

        // Add "All Groups" checkbox
        CheckBox allGroupsCheckbox = new CheckBox("All Groups");
        allGroupsCheckbox.setSelected(selectedGroups.isEmpty());
        checkboxContainer.getChildren().add(allGroupsCheckbox);

        checkboxContainer.getChildren().add(new Separator());

        for (String group : allGroups) {
            CheckBox cb = new CheckBox(group);
            cb.setSelected(selectedGroups.contains(group));
            checkboxes.put(group, cb);
            checkboxContainer.getChildren().add(cb);
        }

        allGroupsCheckbox.setOnAction(e -> {
            if (allGroupsCheckbox.isSelected()) {
                checkboxes.values().forEach(cb -> {
                    cb.setSelected(false);
                    cb.setDisable(true);
                });
            } else {
                checkboxes.values().forEach(cb -> cb.setDisable(false));
            }
        });

        checkboxes.values().forEach(cb -> {
            cb.setOnAction(e -> {
                boolean anySelected = checkboxes.values().stream().anyMatch(CheckBox::isSelected);
                allGroupsCheckbox.setSelected(!anySelected);
                allGroupsCheckbox.setDisable(anySelected);
            });
        });

        ScrollPane scrollPane = new ScrollPane(checkboxContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);

        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                if (allGroupsCheckbox.isSelected()) {
                    return new HashSet<>();
                }
                Set<String> selectedGroups = new HashSet<>();
                for (Map.Entry<String, CheckBox> entry : checkboxes.entrySet()) {
                    if (entry.getValue().isSelected()) {
                        selectedGroups.add(entry.getKey());
                    }
                }
                return selectedGroups;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            selectedGroups = result;
            updateGroupFilterButtonText();
            loadArticles();
        });
    }

    /**
     * Updates the group filter button text.
     */
    private void updateGroupFilterButtonText() {
        if (selectedGroups.isEmpty()) {
            groupFilterButton.setText("Filter by Groups (All)");
        } else {
            groupFilterButton.setText(String.format("Filter by Groups (%d selected)", selectedGroups.size()));
        }
    }

    /**
     * Updates the groups list.
     */
    private void updateGroupsList() {
        try {
            isUpdatingGroups = true;

            allGroups = helpArticleService.getAllGroups();

            if (!allGroups.isEmpty()) {
                groupFilterButton.setDisable(false);

                Set<String> validGroups = new HashSet<>(selectedGroups);
                validGroups.retainAll(allGroups);
                selectedGroups = validGroups;

                if (selectedGroups.isEmpty()) {
                    updateGroupFilterButtonText();
                }
            } else {
                groupFilterButton.setDisable(true);
                selectedGroups.clear();
                updateGroupFilterButtonText();
            }

        } catch (Exception e) {
            e.printStackTrace();
            groupFilterButton.setDisable(true);
            selectedGroups.clear();
            updateGroupFilterButtonText();
        } finally {
            isUpdatingGroups = false;
        }
    }

    /**
     * Entry point of the application.
     * 
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /*******
     * <p>
     * HelpArticleRow class.
     * </p>
     * 
     * <p>
     * Description: Represents a row in the help article table.
     * </p>
     * 
     * <p>
     * Copyright: CSE 360 Team Th02 © 2024
     * </p>
     * 
     * @version 1.00 Phase two
     * 
     */
    public static class HelpArticleRow {
        private final SimpleStringProperty id;
        private final int sequence;
        private final SimpleStringProperty title;
        private final SimpleStringProperty abstractText;
        private final SimpleStringProperty authors;
        private final SimpleStringProperty groups;

        /**
         * Creates an HelpArticleRow with the given parameters.
         * 
         * @param string   unique id of the article.
         * @param sequence sequence number.
         * @param title    title of the article.
         * @param authors  authors of the article.
         */
        public HelpArticleRow(String id, int sequence, String title, String abstractText, String authors,
                String groups) {
            this.id = new SimpleStringProperty(id);
            this.sequence = sequence;
            this.title = new SimpleStringProperty(title);
            this.abstractText = new SimpleStringProperty(abstractText);
            this.authors = new SimpleStringProperty(authors);
            this.groups = new SimpleStringProperty(groups);
        }

        /**
         * Gets the ID of the article.
         * 
         * @return the article ID.
         */
        public String getId() {
            return id.get();
        }

        /**
         * Gets the sequence number of the article.
         * 
         * @return the sequence number.
         */
        public int getSequence() {
            return sequence;
        }

        /**
         * Gets the title of the article.
         * 
         * @return the title.
         */
        public String getTitle() {
            return title.get();
        }

        /**
         * Gets the abstract text of the article.
         * 
         * @return the abstract text.
         */
        public String getAbstractText() {
            return abstractText.get();
        }

        /**
         * Gets the authors of the article.
         * 
         * @return the authors.
         */
        public String getAuthors() {
            return authors.get();
        }

        /**
         * Gets the groups of the article.
         * 
         * @return the groups.
         */
        public String getGroups() {
            return groups.get();
        }
    }
}
