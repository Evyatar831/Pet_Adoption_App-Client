package ui;

import javafx.application.Application;
import javafx.collections.FXCollections;
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


public class AdopterRegistrationScreen extends Application {

    // Form fields
    private TextField nameField;
    private TextField emailField;
    private TextField phoneField;
    private TextArea addressField;
    private ComboBox<String> speciesField;
    private TextField breedField;
    private Spinner<Integer> minAgeSpinner;
    private Spinner<Integer> maxAgeSpinner;
    private ComboBox<String> genderField;

    private final AdopterService adopterService = new AdopterService();

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        String imageUrl = getClass().getResource("/images/pets_background.jpg").toExternalForm();
        root.setStyle("-fx-background-image: url('" + imageUrl + "'); -fx-background-size: cover; -fx-background-position: center;");
        // Header
        Text headerText = new Text("Adopter Registration");
        headerText.setFont(Font.font("System", FontWeight.BOLD, 22));

        Text subheaderText = new Text("Please fill in your information to create an account");
        subheaderText.setFont(Font.font("System", FontWeight.NORMAL, 16));

        VBox headerBox = new VBox(10, headerText, subheaderText);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(20, 0, 20, 0));

        // Registration form
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(15);
        formGrid.setPadding(new Insets(20));

        // Personal Information Section
        Label personalInfoLabel = new Label("Personal Information");
        personalInfoLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        formGrid.add(personalInfoLabel, 0, 0, 2, 1);

        // Name
        Label nameLabel = new Label("Name:");
        nameField = new TextField();
        nameField.setPromptText("Enter your full name");
        formGrid.add(nameLabel, 0, 1);
        formGrid.add(nameField, 1, 1);

        // Email
        Label emailLabel = new Label("Email:");
        emailField = new TextField();
        emailField.setPromptText("Enter your email address");
        formGrid.add(emailLabel, 0, 2);
        formGrid.add(emailField, 1, 2);

        // Phone
        Label phoneLabel = new Label("Phone:");
        phoneField = new TextField();
        phoneField.setPromptText("Enter your phone number");
        formGrid.add(phoneLabel, 0, 3);
        formGrid.add(phoneField, 1, 3);

        // Address
        Label addressLabel = new Label("Address:");
        addressField = new TextArea();
        addressField.setPromptText("Enter your address");
        addressField.setPrefRowCount(3);
        formGrid.add(addressLabel, 0, 4);
        formGrid.add(addressField, 1, 4);

        // Pet Preferences Section
        Label preferencesLabel = new Label("Pet Preferences");
        preferencesLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        formGrid.add(preferencesLabel, 0, 5, 2, 1);

        // Preferred Species
        Label speciesLabel = new Label("Preferred Species:");
        speciesField = new ComboBox<>(FXCollections.observableArrayList(
                "Dog", "Cat", "Bird", "Small Animal", "Reptile", "Other"));
        formGrid.add(speciesLabel, 0, 6);
        formGrid.add(speciesField, 1, 6);

        // Preferred Breed
        Label breedLabel = new Label("Preferred Breed:");
        breedField = new TextField();
        breedField.setPromptText("Enter preferred breed (if any)");
        formGrid.add(breedLabel, 0, 7);
        formGrid.add(breedField, 1, 7);

        // Age Range
        Label ageRangeLabel = new Label("Age Range (years):");

        minAgeSpinner = new Spinner<>(0, 30, 0);
        minAgeSpinner.setEditable(true);
        minAgeSpinner.setPrefWidth(80);

        maxAgeSpinner = new Spinner<>(0, 30, 30);
        maxAgeSpinner.setEditable(true);
        maxAgeSpinner.setPrefWidth(80);

        HBox ageRangeBox = new HBox(10, minAgeSpinner, new Label("to"), maxAgeSpinner);

        formGrid.add(ageRangeLabel, 0, 8);
        formGrid.add(ageRangeBox, 1, 8);

        // Preferred Gender
        Label genderLabel = new Label("Preferred Gender:");
        genderField = new ComboBox<>(FXCollections.observableArrayList("Male", "Female", "Any"));
        formGrid.add(genderLabel, 0, 9);
        formGrid.add(genderField, 1, 9);

        scrollPane.setContent(formGrid);

        // Buttons
        Button registerButton = new Button("REGISTER");
        registerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                "-fx-border-radius: 3; -fx-background-radius: 3; -fx-font-size: 14px;");
        registerButton.setPrefWidth(120);
        registerButton.setPrefHeight(40);
        registerButton.setOnAction(e -> handleRegister(primaryStage));

        Button cancelButton = new Button("CANCEL");
        cancelButton.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #cccccc; " +
                "-fx-border-radius: 3; -fx-background-radius: 3; -fx-font-size: 14px;");
        cancelButton.setPrefWidth(120);
        cancelButton.setPrefHeight(40);
        cancelButton.setOnAction(e -> {
            try {
                AdopterLoginScreen loginScreen = new AdopterLoginScreen();
                loginScreen.start(primaryStage);
            } catch (Exception ex) {
                ex.printStackTrace();
                AlertHelper.showError("Navigation Error", "Could not navigate back. Please try again.");
            }
        });

        HBox buttonBox = new HBox(20, registerButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 20, 0));

        // Combine elements
        VBox contentBox = new VBox(10, headerBox, scrollPane, buttonBox);
        contentBox.setPadding(new Insets(20));

        root.setCenter(contentBox);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Pet Adoption System - Adopter Registration");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void handleRegister(Stage primaryStage) {
        if (!validateForm()) {
            return;
        }

        // Create new adopter
        Adopter adopter = new Adopter();
        adopter.setName(nameField.getText().trim());
        adopter.setEmail(emailField.getText().trim());
        adopter.setPhone(phoneField.getText().trim());
        adopter.setAddress(addressField.getText().trim());

        // Set preferences
        adopter.setPreferredSpecies(speciesField.getValue());
        adopter.setPreferredBreed(breedField.getText().trim());
        adopter.setPreferredAgeMin(minAgeSpinner.getValue());
        adopter.setPreferredAgeMax(maxAgeSpinner.getValue());
        adopter.setPreferredGender(genderField.getValue());

        // Add adopter to system
        boolean success = adopterService.addAdopter(adopter);

        if (success) {
            AlertHelper.showInformation("Registration Successful",
                    "Your account has been created successfully!");

            try {
                // Navigate to adopter dashboard
                AdopterDashboard dashboard = new AdopterDashboard(adopter.getId());
                dashboard.start(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
                AlertHelper.showError("Navigation Error",
                        "Could not navigate to dashboard. Please try logging in.");
            }
        } else {
            AlertHelper.showError("Registration Error",
                    "Could not create account. Please try again.");
        }
    }


    private boolean validateForm() {
        StringBuilder errorMessage = new StringBuilder();

        // Validate name
        if (nameField.getText().trim().isEmpty()) {
            errorMessage.append("Name is required.\n");
        }

        // Validate email with uniqueness check
        if (emailField.getText().trim().isEmpty()) {
            errorMessage.append("Email is required.\n");
        } else if (!ValidationUtils.isValidEmail(emailField.getText().trim())) {
            errorMessage.append("Please enter a valid email address.\n");
        } else {
            // Check if email is already in use by another adopter
            String email = emailField.getText().trim();
            List<Adopter> existingAdopters = adopterService.getAllAdopters();
            boolean emailExists = existingAdopters.stream()
                    .anyMatch(adopter -> email.equalsIgnoreCase(adopter.getEmail()));

            if (emailExists) {
                errorMessage.append("This email address is already in use. Please use a different email.\n");
            }
        }

        // Validate phone
        if (phoneField.getText().trim().isEmpty()) {
            errorMessage.append("Phone number is required.\n");
        }

        // Validate address
        if (addressField.getText().trim().isEmpty()) {
            errorMessage.append("Address is required.\n");
        }

        // Validate age range
        int minAge = minAgeSpinner.getValue();
        int maxAge = maxAgeSpinner.getValue();
        if (minAge > maxAge) {
            errorMessage.append("Minimum age cannot be greater than maximum age.\n");
        }

        // Show errors if any
        if (errorMessage.length() > 0) {
            AlertHelper.showError("Validation Error", errorMessage.toString());
            return false;
        }

        return true;
    }
}