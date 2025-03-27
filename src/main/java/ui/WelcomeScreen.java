package ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


public class WelcomeScreen extends Application {

    @Override
    public void start(Stage primaryStage) {
       BorderPane root = new BorderPane();
        String imageUrl = getClass().getResource("/images/pets_background.jpg").toExternalForm();
        root.setStyle("-fx-background-image: url('" + imageUrl + "'); -fx-background-size: cover; -fx-background-position: center;");

        // Title
        Label titleLabel = new Label("Welcome to Ben & Michelle's");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));

        Label subtitleLabel = new Label("Pet Adoption Management Service!");
        subtitleLabel.setFont(Font.font("System", FontWeight.NORMAL, 18));

        VBox titleBox = new VBox(10, titleLabel, subtitleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(30, 0, 50, 0));

        // User selection prompt
        Label promptLabel = new Label("Please choose a user:");
        promptLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
//        promptLabel.setStyle("-fx-padding: 15px; -fx-background-color: #E0E0E0; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #B0B0B0; -fx-border-width: 2px;");

        // Buttons
        Button staffButton = createRoleButton("STAFF");
        staffButton.setOnAction(e -> openStaffDashboard(primaryStage));

        Button adopterButton = createRoleButton("ADOPTER");
        adopterButton.setOnAction(e -> openAdopterLogin(primaryStage)); // Changed to login screen

        HBox buttonBox = new HBox(40, staffButton, adopterButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Combine elements
        VBox centerContent = new VBox(30, titleBox, promptLabel, buttonBox);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setPadding(new Insets(20));

        root.setCenter(centerContent);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Pet Adoption System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createRoleButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(150, 50);
        button.setStyle("-fx-background-color: #e0e0e0; " +
                "-fx-border-color: #cccccc; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold;");
        return button;
    }

    private void openStaffDashboard(Stage primaryStage) {
        StaffDashboard dashboard = new StaffDashboard();
        try {
            dashboard.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openAdopterLogin(Stage primaryStage) {
        AdopterLoginScreen loginScreen = new AdopterLoginScreen();
        try {
            loginScreen.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}