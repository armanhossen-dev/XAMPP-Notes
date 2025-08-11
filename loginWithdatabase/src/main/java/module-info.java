module com.loginwithdatabase {
    // Requires the core JavaFX modules for UI components and FXML loading.
    requires javafx.controls;
    requires javafx.fxml;

    // Requires the Java SQL module for database connectivity.
    requires java.sql;

    // Opens the package to the javafx.fxml module. This allows FXML to access
    // and create instances of your controller classes (e.g., RegistrationForm).
    opens com.loginwithdatabase to javafx.fxml;

    // Exports the package so that the main application class (and other classes
    // that might be in a separate module) can access the classes within this module.
    exports com.loginwithdatabase;
}