package controller.staff;

import ui.WelcomeScreen;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;


public class StaffDashboardController {

    @FXML
    private Button managePetsButton;

    @FXML
    private Button manageAdoptersButton;

    @FXML
    private Button processAdoptionsButton;

    @FXML
    private Button backButton;


    @FXML
    private void handleManagePets() {
        openView("/view/staff/PetManagementView.fxml", "Pet Management");
    }


    @FXML
    private void handleManageAdopters() {
        openView("/view/staff/AdopterManagementView.fxml", "Adopter Management");
    }


    @FXML
    private void handleProcessAdoptions() {
        openView("/view/staff/AdoptionManagementView.fxml", "Adoption Management");
    }


    @FXML
    private void handleGoBack() {
        try {
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            WelcomeScreen welcomeScreen = new WelcomeScreen();
            welcomeScreen.start(currentStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void openView(String fxmlPath, String title) {
        try {
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Scene scene = new Scene(root, 1000, 700);
            currentStage.setTitle("Pet Adoption System - " + title);
            currentStage.setScene(scene);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}