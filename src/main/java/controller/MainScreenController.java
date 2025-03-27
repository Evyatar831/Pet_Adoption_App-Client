package controller;

import ui.AdopterDashboard;
import ui.StaffDashboard;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;


public class MainScreenController {

    @FXML
    private Button staffButton;

    @FXML
    private Button adopterButton;


    @FXML
    private void handleStaffLogin() {
        try {
            // Get the current stage
            Stage currentStage = (Stage) staffButton.getScene().getWindow();

            // Open the staff dashboard
            StaffDashboard dashboard = new StaffDashboard();
            dashboard.start(currentStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleAdopterLogin() {
        try {
            // Get the current stage
            Stage currentStage = (Stage) adopterButton.getScene().getWindow();

            // Open the adopter dashboard
            AdopterDashboard dashboard = new AdopterDashboard();
            dashboard.start(currentStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}