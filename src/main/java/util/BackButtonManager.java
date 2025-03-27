package util;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import ui.WelcomeScreen;
import ui.StaffDashboard;
import ui.AdopterDashboard;


public class BackButtonManager {


    public enum ScreenOrigin {
        WELCOME_SCREEN,
        STAFF_DASHBOARD,
        ADOPTER_DASHBOARD
    }


    public static void configureBackButton(Button backButton, Scene scene, ScreenOrigin origin, String userId) {
        // Apply consistent styling
        styleBackButton(backButton);

        // Set action based on origin
        backButton.setOnAction(e -> {
            try {
                Stage stage = (Stage) scene.getWindow();
                navigateToOrigin(stage, origin, userId);
            } catch (Exception ex) {
                ex.printStackTrace();
                AlertHelper.showError("Navigation Error",
                        "Could not navigate back. Please try again.");
            }
        });
    }


    public static void setupBackButton(Button backButton, Stage stage, NavigationType navigationType, String userId) {
        // Apply consistent styling
        styleBackButton(backButton);

        // Set the action based on navigation type
        backButton.setOnAction(e -> {
            try {
                switch (navigationType) {
                    case TO_WELCOME:
                        new WelcomeScreen().start(stage);
                        break;
                    case TO_STAFF_DASHBOARD:
                        new StaffDashboard().start(stage);
                        break;
                    case TO_ADOPTER_DASHBOARD:
                        new AdopterDashboard(userId).start(stage);
                        break;
                    case PREVIOUS_SCREEN:
                        // This would require tracking navigation history
                        // For simplicity, we'll default to welcome screen
                        new WelcomeScreen().start(stage);
                        break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                AlertHelper.showError("Navigation Error", "Could not navigate back. Please try again.");
            }
        });
    }





    public static void styleBackButton(Button button) {
        button.setText("GO BACK");
        button.setStyle("-fx-background-color: #f5f5f5; " +
                "-fx-border-color: #cccccc; " +
                "-fx-border-radius: 3; " +
                "-fx-background-radius: 3; " +
                "-fx-font-size: 14px;");
        button.setPrefWidth(120);
        button.setPrefHeight(40);
    }


    public static void navigateToOrigin(Stage stage, ScreenOrigin origin, String userId) throws Exception {
        switch (origin) {
            case WELCOME_SCREEN:
                new WelcomeScreen().start(stage);
                break;

            case STAFF_DASHBOARD:
                new StaffDashboard().start(stage);
                break;

            case ADOPTER_DASHBOARD:
                if (userId == null || userId.isEmpty()) {
                    AlertHelper.showError("Navigation Error",
                            "Cannot navigate to adopter dashboard without user ID.");
                    // Fall back to welcome screen
                    new WelcomeScreen().start(stage);
                } else {
                    new AdopterDashboard(userId).start(stage);
                }
                break;

            default:
                // Default to welcome screen
                new WelcomeScreen().start(stage);
                break;
        }
    }


    public enum NavigationType {
        TO_WELCOME,
        TO_STAFF_DASHBOARD,
        TO_ADOPTER_DASHBOARD,
        PREVIOUS_SCREEN
    }
}