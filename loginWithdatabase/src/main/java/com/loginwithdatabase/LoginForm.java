package com.loginwithdatabase;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.shape.SVGPath;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginForm {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField visiblePasswordField;
    @FXML
    private Button togglePasswordButton;
    @FXML
    private SVGPath togglePasswordIcon;
    @FXML
    private Button loginButton;

    private boolean isPasswordVisible = false;

    /**
     * Initializes the controller. This method is called after the FXML file
     * has been loaded. It sets up listeners to synchronize the two password fields.
     */
    @FXML
    public void initialize() {
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
     * Handles the login action when the login button is clicked.
     * Attempts to log in the user via the DatabaseManager.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Please enter both username and password.");
            return;
        }

        if (DatabaseManager.loginUser(username, password)) {
            showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome, " + username + "!");
            // Here you would navigate to the next screen.
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
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
     * Switches to the registration form.
     */
    @FXML
    private void switchToRegistration() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("registration-form.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);

        // Link the CSS file to the scene
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setMaximized(true);
        stage.setResizable(true);
        // Center the stage on screen
        stage.centerOnScreen();
        stage.setScene(scene);
    }
}
