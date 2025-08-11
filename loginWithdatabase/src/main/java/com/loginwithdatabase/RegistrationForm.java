package com.loginwithdatabase;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.shape.SVGPath;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class RegistrationForm {

    @FXML
    private ChoiceBox<String> userTypeChoiceBox;
    @FXML
    private TextField nameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField visiblePasswordField;
    @FXML
    private SVGPath togglePasswordIcon;
    @FXML
    private TextField locationField;
    @FXML
    private Button registerButton;

    private boolean isPasswordVisible = false;

    /**
     * Initializes the controller. This method is called after the FXML file
     * has been loaded. It sets up the ChoiceBox, synchronizes password fields,
     * and adds a listener for the user type selection.
     */
    @FXML
    public void initialize() {
        // Populate the ChoiceBox with user types
        userTypeChoiceBox.setItems(FXCollections.observableArrayList("Student", "House Owner"));
        userTypeChoiceBox.setValue("Student"); // Default value

        // Add a listener to handle the visibility of the location field
        userTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            boolean isHouseOwner = "House Owner".equals(newValue);
            locationField.setVisible(isHouseOwner);
            locationField.setManaged(isHouseOwner);
        });

        // Initially, the visible password field is not shown.
        visiblePasswordField.setVisible(false);
        // Bind the text of the two fields so they always have the same value.
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());

        // Update the icon based on the initial state (hidden).
        updateToggleIcon();
    }

    /**
     * Toggles the visibility of the password between a PasswordField and a TextField.
     */
    @FXML
    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            passwordField.setVisible(false);
            visiblePasswordField.setVisible(true);
            visiblePasswordField.requestFocus();
            // Move the caret to the end of the text
            visiblePasswordField.positionCaret(visiblePasswordField.getText().length());
        } else {
            visiblePasswordField.setVisible(false);
            passwordField.setVisible(true);
            passwordField.requestFocus();
        }
        updateToggleIcon();
    }

    /**
     * Updates the SVG icon for the password toggle button based on visibility.
     * The icon changes from an open eye to a closed eye.
     */
    private void updateToggleIcon() {
        // SVG paths for an open eye and a closed eye
        String openEye = "M12 4.5C7 4.5 2.73 7.61 0 12c2.73 4.39 7 7.5 12 7.5s9.27-3.11 12-7.5c-2.73-4.39-7-7.5-12-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z";
        String closedEye = "M12 7c-2.76 0-5 2.24-5 5 0 .65.13 1.26.36 1.83l-1.57 1.57C4.19 13.56 3 12 3 12c-2.73-4.39 7-7.5 12-7.5 1.55 0 3.03.3 4.39.88l-1.55-1.55c-1.36-.45-2.82-.71-4.24-.83V3h2v2H12c-4.99 0-9.27 3.11-12 7.5 2.73 4.39 7 7.5 12 7.5s9.27-3.11 12-7.5c-.32-.51-.66-1.02-1.01-1.51l-1.52-1.52c.3.56.55 1.15.75 1.78.27.86.43 1.76.43 2.75 0 2.76-2.24 5-5 5s-5-2.24-5-5zm-5 5c0-1.66 1.34-3 3-3 .65 0 1.26.13 1.83.36l1.57-1.57C13.56 4.19 12 3 12 3c-4.39 2.73-7.5 7-7.5 12s3.11 9.27 7.5 12c.51-.32 1.02-.66 1.51-1.01l1.52-1.52c-.56.3-1.15.55-1.78.75-.86.27-1.76.43-2.75.43-2.76 0-5-2.24-5-5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5z";
        togglePasswordIcon.setContent(isPasswordVisible ? closedEye : openEye);
    }

    /**
     * Handles the registration action when the register button is clicked.
     * Validates input and attempts to register the user via the DatabaseManager.
     */
    @FXML
    private void handleRegistration() {
        String userType = userTypeChoiceBox.getValue();
        String name = nameField.getText();
        String phone = phoneField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String location = locationField.getText();

        if (name.isEmpty() || phone.isEmpty() || username.isEmpty() || password.isEmpty() || ("House Owner".equals(userType) && location.isEmpty())) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Please fill in all required fields.");
            return;
        }

        if (DatabaseManager.registerUser(userType, name, phone, username, password, location)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "User registered successfully!");
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Username may already exist or there was a database error.");
        }
    }

    /**
     * Helper method to display an Alert dialog.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Switches to the login form.
     */
    @FXML
    private void switchToLogin() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login-form.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);

        // Link the CSS file to the scene
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        Stage stage = (Stage) registerButton.getScene().getWindow();

        stage.setTitle("Login");

        stage.setResizable(true);

// Fullscreen look
        stage.setMaximized(true);

// OR for half-screen centered
        stage.setWidth(Screen.getPrimary().getVisualBounds().getWidth() / 2);
        stage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
        stage.setX((Screen.getPrimary().getVisualBounds().getWidth() - stage.getWidth()) / 2);
        stage.setY(0);

        stage.setScene(scene);
    }
}
