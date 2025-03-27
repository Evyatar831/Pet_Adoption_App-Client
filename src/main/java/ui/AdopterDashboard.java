package ui;

import controller.adopter.AdopterAdoptionsController;
import controller.adopter.AdopterProfileController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import model.Adopter;
import service.AdopterService;
import util.AlertHelper;
import util.BackButtonManager;


public class AdopterDashboard extends Application {

    private String userId;
    private Adopter currentAdopter;
    private final AdopterService adopterService = new AdopterService();


    public AdopterDashboard() {
    }


    public AdopterDashboard(String userId) {
        this.userId = userId;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load current adopter
        if (userId != null && !userId.isEmpty()) {
            currentAdopter = adopterService.getAdopter(userId);

            if (currentAdopter == null) {
                AlertHelper.showError("Error", "Adopter information not found. Please log in again.");
                WelcomeScreen welcomeScreen = new WelcomeScreen();
                welcomeScreen.start(primaryStage);
                return;
            }
        } else {
            AlertHelper.showError("Error", "No adopter ID provided. Please log in again.");
            WelcomeScreen welcomeScreen = new WelcomeScreen();
            welcomeScreen.start(primaryStage);
            return;
        }

        BorderPane root = new BorderPane();
        String imageUrl = getClass().getResource("/images/pets_background.jpg").toExternalForm();
        root.setStyle("-fx-background-image: url('" + imageUrl + "'); -fx-background-size: cover; -fx-background-position: center;");

        // Header
        Label greetingLabel = new Label("Hello " + currentAdopter.getName() + "!");
        greetingLabel.setFont(Font.font("System", FontWeight.BOLD, 20));

        Label instructionLabel = new Label("Please select an option below to continue");
        instructionLabel.setFont(Font.font("System", FontWeight.BOLD, 16));

        VBox headerBox = new VBox(10, greetingLabel, instructionLabel);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(30, 0, 40, 0));

        // Main action buttons
        Button searchButton = createActionButton("SEARCH PETS");
        searchButton.setPrefSize(240, 50);
        searchButton.setStyle(searchButton.getStyle() + "-fx-font-size: 16px; -fx-font-weight: bold;");
        searchButton.setOnAction(e -> openFXMLView("/view/adopter/PetBrowseView.fxml", "Pet Search", primaryStage));

        Button petMatchingButton = createActionButton("PET MATCHING");
        petMatchingButton.setOnAction(e -> openFXMLView("/view/adopter/PetMatchingView.fxml", "Pet Matching", primaryStage));

        // Secondary buttons
        Button profileButton = createActionButton("VIEW MY PROFILE");
        profileButton.setOnAction(e -> handleViewProfile(primaryStage));

        Button adoptionStatusButton = createActionButton("ADOPTION STATUS");
        adoptionStatusButton.setOnAction(e -> handleCheckStatus(primaryStage));

        Button backButton = createActionButton("LOG OUT");
        BackButtonManager.styleBackButton(backButton);
        backButton.setOnAction(e -> {
            try {
                BackButtonManager.navigateToOrigin(
                        primaryStage,
                        BackButtonManager.ScreenOrigin.WELCOME_SCREEN,
                        null
                );
            } catch (Exception ex) {
                ex.printStackTrace();
                AlertHelper.showError("Navigation Error",
                        "Could not navigate back. Please try again.");
            }
        });

        VBox buttonBox = new VBox(20, searchButton, petMatchingButton, profileButton, adoptionStatusButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox centerContent = new VBox(30, headerBox, buttonBox);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setPadding(new Insets(20));

        root.setCenter(centerContent);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Pet Adoption System - Adopter Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createActionButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(200, 40);
        button.setStyle("-fx-background-color: #e0e0e0; " +
                "-fx-border-color: #cccccc; " +
                "-fx-border-radius: 3; " +
                "-fx-background-radius: 3; " +
                "-fx-font-size: 14px;");
        return button;
    }

    private void handleViewProfile(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/adopter/AdopterProfileView.fxml"));
            Parent root = loader.load();

            AdopterProfileController controller = loader.getController();
            controller.setAdopterId(userId);

            Scene scene = new Scene(root, 1000, 700);

            BackButtonManager.configureBackButton(
                    controller.getBackButton(),
                    scene,
                    BackButtonManager.ScreenOrigin.ADOPTER_DASHBOARD,
                    userId
            );

            controller.loadAdopterData();

            primaryStage.setTitle("Pet Adoption System - My Profile");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Error", "Could not open profile view.");
        }
    }

    private void handleCheckStatus(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/adopter/AdopterAdoptionsView.fxml"));
            Parent root = loader.load();

            AdopterAdoptionsController controller = loader.getController();
            controller.setAdopterId(userId);

            Scene scene = new Scene(root, 1000, 700);

            BackButtonManager.configureBackButton(
                    controller.getBackButton(),
                    scene,
                    BackButtonManager.ScreenOrigin.ADOPTER_DASHBOARD,
                    userId
            );

            controller.loadAdopterAdoptions();

            primaryStage.setTitle("Pet Adoption System - My Adoptions");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Error", "Could not open adoptions view.");
        }
    }

    private void openFXMLView(String fxmlPath, String title, Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();

            Scene scene = new Scene(root, 1000, 700);
            primaryStage.setTitle("Pet Adoption System - " + title);
            primaryStage.setScene(scene);

            java.lang.reflect.Method setAdopterId = controller.getClass().getMethod("setAdopterId", String.class);
            setAdopterId.invoke(controller, userId);


            try {
                java.lang.reflect.Method getBackButton = controller.getClass().getMethod("getBackButton");
                Button backButton = (Button) getBackButton.invoke(controller);

                if (backButton != null) {
                    BackButtonManager.configureBackButton(
                            backButton,
                            scene,
                            BackButtonManager.ScreenOrigin.ADOPTER_DASHBOARD,
                            userId
                    );
                }
            } catch (Exception ex) {
                System.out.println("No back button found in controller: " + controller.getClass().getName());
            }

            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Error", "Could not open view: " + fxmlPath);
        }
    }
}