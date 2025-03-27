package ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Adopter;
import service.AdopterService;
import util.AlertHelper;
import util.ValidationUtils;

import java.util.List;


public class AdopterLoginScreen extends Application {

    private TextField emailField;
    private PasswordField passwordField;
    private final AdopterService adopterService = new AdopterService();

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        String imageUrl = getClass().getResource("/images/pets_background.jpg").toExternalForm();
        root.setStyle("-fx-background-image: url('" + imageUrl + "'); -fx-background-size: cover; -fx-background-position: center;");
        // Header
        Text headerText = new Text("Adopter Login");
        headerText.setFont(Font.font("System", FontWeight.BOLD, 22));

        Text subheaderText = new Text("Please login with your email or register as a new adopter");
        subheaderText.setFont(Font.font("System", FontWeight.BOLD, 16));

        VBox headerBox = new VBox(10, headerText, subheaderText);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(30, 0, 30, 0));

        // Login form
        GridPane loginGrid = new GridPane();
        loginGrid.setHgap(10);
        loginGrid.setVgap(15);
        loginGrid.setAlignment(Pos.CENTER);

        Label emailLabel = new Label("Email:");
        emailLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.setPrefWidth(250);



        loginGrid.add(emailLabel, 0, 0);
        loginGrid.add(emailField, 1, 0);


        // Buttons
        Button loginButton = new Button("LOGIN");
        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                "-fx-border-radius: 3; -fx-background-radius: 3; -fx-font-size: 14px;");
        loginButton.setPrefWidth(120);
        loginButton.setPrefHeight(40);
        loginButton.setOnAction(e -> handleLogin(primaryStage));

        Button registerButton = new Button("REGISTER");
        registerButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; " +
                "-fx-border-radius: 3; -fx-background-radius: 3; -fx-font-size: 14px;");
        registerButton.setPrefWidth(120);
        registerButton.setPrefHeight(40);
        registerButton.setOnAction(e -> handleRegister(primaryStage));

        Button backButton = new Button("GO BACK");
        backButton.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #cccccc; " +
                "-fx-border-radius: 3; -fx-background-radius: 3; -fx-font-size: 14px;");
        backButton.setPrefWidth(120);
        backButton.setPrefHeight(40);
        backButton.setOnAction(e -> {
            try {
                WelcomeScreen welcomeScreen = new WelcomeScreen();
                welcomeScreen.start(primaryStage);
            } catch (Exception ex) {
                ex.printStackTrace();
                AlertHelper.showError("Navigation Error", "Could not navigate back. Please try again.");
            }
        });

        HBox buttonBox = new HBox(20, loginButton, registerButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(30, 0, 0, 0));

        // Combine elements
        VBox contentBox = new VBox(20, headerBox, loginGrid, buttonBox);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(20));

        root.setCenter(contentBox);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Pet Adoption System - Adopter Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void handleLogin(Stage primaryStage) {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            AlertHelper.showWarning("Login Error", "Please enter your email address.");
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            AlertHelper.showWarning("Login Error", "Please enter a valid email address.");
            return;
        }

        List<Adopter> adopters = adopterService.getAllAdopters();
        Adopter matchedAdopter = null;

        for (Adopter adopter : adopters) {
            if (email.equalsIgnoreCase(adopter.getEmail())) {
                matchedAdopter = adopter;
                break;
            }
        }

        if (matchedAdopter == null) {
            AlertHelper.showWarning("Login Error",
                    "No adopter found with this email. Please register first.");
            return;
        }



        try {
            AdopterDashboard dashboard = new AdopterDashboard(matchedAdopter.getId());
            dashboard.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Login Error", "Could not log in. Please try again.");
        }
    }


    private void handleRegister(Stage primaryStage) {
        try {
            AdopterRegistrationScreen registrationScreen = new AdopterRegistrationScreen();
            registrationScreen.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Navigation Error", "Could not open registration screen. Please try again.");
        }
    }
}