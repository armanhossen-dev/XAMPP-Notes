package com.loginwithdatabase;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            System.out.println("Starting JavaFX Application...");

            // Try to initialize database (non-blocking)
            initializeDatabase();

            System.out.println("Loading FXML file...");
            // Load the FXML file for the login form
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/loginwithdatabase/login-form.fxml"));

            if (fxmlLoader.getLocation() == null) {
                System.err.println("Could not find login-form.fxml at /com/loginwithdatabase/login-form.fxml");
                showErrorAlert("Resource Error", "Could not find login-form.fxml file");
                return;
            }

            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);

            // Link the CSS file to the scene
            String cssResource = "/com/loginwithdatabase/style.css";
            var cssUrl = Main.class.getResource(cssResource);
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
                System.out.println("CSS loaded successfully");
            } else {
                System.err.println("Could not find style.css at " + cssResource);
            }

            stage.setTitle("Login and Registration System");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setMaximized(true);
            stage.centerOnScreen();

            System.out.println("Showing stage...");
            stage.show();

        } catch (IOException e) {
            System.err.println("Failed to load FXML: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Loading Error", "Failed to load the application interface: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Application Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    private void initializeDatabase() {
        try {
            System.out.println("Attempting to initialize database...");
            DatabaseManager.createUsersTable();
            System.out.println("Database initialized successfully");
        } catch (Exception e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            e.printStackTrace();

            // Show warning but don't prevent app from starting
            showWarningAlert("Database Warning",
                    "Could not connect to database. Please ensure:\n" +
                            "1. XAMPP is running\n" +
                            "2. MySQL service is started\n" +
                            "3. Database 'user_management' exists\n\n" +
                            "The application will continue but login/registration won't work until database is available.");
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarningAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        System.out.println("Application starting...");
        try {
            launch(args);
        } catch (Exception e) {
            System.err.println("Failed to launch application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}