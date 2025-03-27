package ui;

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
import util.AlertHelper;
import util.BackButtonManager;


public class StaffDashboard extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = new BorderPane();
        String imageUrl = getClass().getResource("/images/pets_background.jpg").toExternalForm();
        root.setStyle("-fx-background-image: url('" + imageUrl + "'); -fx-background-size: cover; -fx-background-position: center;");

        // Header
        Label greetingLabel = new Label("Hello Staff!");
        greetingLabel.setFont(Font.font("System", FontWeight.BOLD, 20));

        Label instructionLabel = new Label("Please choose what would you like to do:");
        instructionLabel.setFont(Font.font("System", FontWeight.BOLD, 16));

        VBox headerBox = new VBox(10, greetingLabel, instructionLabel);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(30, 0, 40, 0));

        // Action buttons - added Manage Pets button
        Button managePetsButton = createActionButton("MANAGE PETS");
        managePetsButton.setOnAction(e -> openFXMLView("/view/staff/PetManagementView.fxml", "Pet Management", primaryStage));

        Button manageAdoptersButton = createActionButton("MANAGE ADOPTERS");
        manageAdoptersButton.setOnAction(e -> openFXMLView("/view/staff/AdopterManagementView.fxml", "Adopter Management", primaryStage));

        Button processAdoptionsButton = createActionButton("PROCESS ADOPTIONS");
        processAdoptionsButton.setOnAction(e -> openFXMLView("/view/staff/AdoptionManagementView.fxml", "Adoption Management", primaryStage));

        Button backButton = createActionButton("GO BACK");
        // Apply consistent styling through the BackButtonManager
        BackButtonManager.styleBackButton(backButton);
        // Set the action to navigate back to the welcome screen
        backButton.setOnAction(e -> {
            try {
                BackButtonManager.navigateToOrigin(
                        primaryStage,
                        BackButtonManager.ScreenOrigin.WELCOME_SCREEN,
                        null
                );
            } catch (Exception ex) {
                ex.printStackTrace();
                AlertHelper.showError("Navigation Error", "Could not navigate back. Please try again.");
            }
        });

        // Layout buttons in a vertical arrangement with spacing
        VBox buttonBox = new VBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        // Three rows of action buttons
        VBox rowOne = new VBox(15, managePetsButton);
        rowOne.setAlignment(Pos.CENTER);

        VBox rowTwo = new VBox(15, manageAdoptersButton);
        rowTwo.setAlignment(Pos.CENTER);

        // Third row with adoptions button
        VBox rowThree = new VBox(15, processAdoptionsButton);
        rowThree.setAlignment(Pos.CENTER);

        // Fourth row with back button
        VBox rowFour = new VBox(15, backButton);
        rowFour.setAlignment(Pos.CENTER);

        buttonBox.getChildren().addAll(rowOne, rowTwo, rowThree, rowFour);

        // Combine all elements
        VBox centerContent = new VBox(20, headerBox, buttonBox);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setPadding(new Insets(20));

        root.setCenter(centerContent);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Pet Adoption System - Staff Dashboard");
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

    private void openFXMLView(String fxmlPath, String title, Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();

            Scene scene = new Scene(root, 1000, 700);
            primaryStage.setTitle("Pet Adoption System - " + title);
            primaryStage.setScene(scene);

            try {
                java.lang.reflect.Method getBackButton = controller.getClass().getMethod("getBackButton");
                Button backButton = (Button) getBackButton.invoke(controller);

                if (backButton != null) {
                    BackButtonManager.configureBackButton(
                            backButton,
                            scene,
                            BackButtonManager.ScreenOrigin.STAFF_DASHBOARD,
                            null
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