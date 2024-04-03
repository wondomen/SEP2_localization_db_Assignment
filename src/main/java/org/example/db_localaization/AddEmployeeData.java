package org.example.db_localaization;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

public class AddEmployeeData extends Application {

    private ResourceBundle bundle;
    private GridPane gridPane;
    private Label languageLabel;
    private ComboBox<String> languageComboBox;
    private Label firstNameLabel;
    private Label lastNameLabel;
    private Label emailLabel;
    private TextField firstNameField;
    private TextField lastNameField;
    private TextField emailField;
    private Button submitButton;
    private Label statusLabel;
    private boolean isUIInitialized = false;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(0, 0, 0, 20));

        loadResourceBundle(Locale.ENGLISH);

        languageLabel = new Label();
        languageComboBox = new ComboBox<>();
        languageComboBox.getItems().addAll("English", "Farsi", "Japanese");
        languageComboBox.setOnAction(event -> {
            loadResourceBundle(getLocale(languageComboBox.getValue()));
            updateUI();// Update UI after changing language
            updateWindowTitle();
        });

        firstNameLabel = new Label();
        firstNameField = new TextField();
        lastNameLabel = new Label();
        lastNameField = new TextField();
        emailLabel = new Label();
        emailField = new TextField();
        submitButton = new Button();
        submitButton.setOnAction(event -> addEmployee());
        statusLabel = new Label();

        Scene scene = new Scene(gridPane, 400, 250);
        primaryStage.setScene(scene);
        updateWindowTitle();
        primaryStage.show();

        // Set isUIInitialized to true after UI components have been initialized
        isUIInitialized = true;
        updateUI(); // Call updateUI to update UI if components are already initialized
    }

    private void loadResourceBundle(Locale locale) {
        // Load the resource bundle based on the locale
        switch (locale.getLanguage()) {
            case "fa":
                bundle = ResourceBundle.getBundle("messages_fa_IR", locale);
                break;
            case "ja":
                bundle = ResourceBundle.getBundle("messages_ja_JP", locale);
                break;
            default:
                bundle = ResourceBundle.getBundle("messages", locale);
                break;
        }

        updateUI();
    }

    private void updateUI() {
        if (!isUIInitialized) {
            System.err.println("UI components are not yet initialized.");
            return;
        }

        languageLabel.setText(bundle.getString("select_language"));
        firstNameLabel.setText(bundle.getString("first_name"));
        lastNameLabel.setText(bundle.getString("last_name"));
        emailLabel.setText(bundle.getString("email"));
        submitButton.setText(bundle.getString("submit"));
        statusLabel.setText(""); // Set initial text to empty string

        gridPane.getChildren().clear();
        gridPane.add(languageLabel, 0, 0);
        gridPane.add(languageComboBox, 1, 0);
        gridPane.add(firstNameLabel, 0, 1);
        gridPane.add(firstNameField, 1, 1);
        gridPane.add(lastNameLabel, 0, 2);
        gridPane.add(lastNameField, 1, 2);
        gridPane.add(emailLabel, 0, 3);
        gridPane.add(emailField, 1, 3);
        gridPane.add(submitButton, 1, 4);
        gridPane.add(statusLabel, 0, 5, 2, 1);

    }
    private void updateWindowTitle() {
            primaryStage.setTitle(bundle.getString("add_employee_data"));

    }
    private void addEmployee() {
        try {
            // Establishing a connection to the database
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/demolocalization", "root", "root");

            // Getting selected language and employee details
            String selectedLanguage = languageComboBox.getValue();
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String email = emailField.getText();

            // Prepare the SQL statement based on the selected language
            String sql;
            switch (selectedLanguage) {
                case "English":
                    sql = "INSERT INTO employee_en (first_name, last_name, email) VALUES (?, ?, ?)";
                    break;
                case "Farsi":
                    sql = "INSERT INTO employee_fa (first_name, last_name, email) VALUES (?, ?, ?)";
                    break;
                case "Japanese":
                    sql = "INSERT INTO employee_ja (first_name, last_name, email) VALUES (?, ?, ?)";
                    break;
                default:
                    showAlert("Invalid choice!", "Please select a language.");
                    return;
            }

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, email);
            // Executing the statement
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                showAlert("Success", "Employee data inserted successfully!");
            } else {
                showAlert("Error", "Failed to insert employee data.");
            }

            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to connect to database.");
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private Locale getLocale(String language) {
        return switch (language) {
            case "Farsi" -> new Locale("fa");
            case "Japanese" -> Locale.JAPANESE;
            default -> Locale.ENGLISH;
        };
    }
    public static void main(String[] args) {
        launch(args);
    }
}
