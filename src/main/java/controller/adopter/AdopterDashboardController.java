package controller.adopter;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import ui.WelcomeScreen;


public class AdopterDashboardController {

    @FXML
    private Button searchPetButton;

    @FXML
    private Button petMatchingButton;

    @FXML
    private Button viewProfileButton;

    @FXML
    private Button checkStatusButton;

    @FXML
    private Button backButton;

    private String adopterId;


    public void setAdopterId(String adopterId) {
        this.adopterId = adopterId;
    }


    @FXML
    private void handleSearchPets() {
        openView("/view/adopter/PetBrowseView.fxml", "Pet Search");
    }


    @FXML
    private void handlePetMatching() {
        openView("/view/adopter/PetMatchingView.fxml", "Pet Matching");
    }


    @FXML
    private void handleViewProfile() {
        openView("/view/adopter/AdopterProfileView.fxml", "My Profile");
    }


    @FXML
    private void handleCheckStatus() {
        openView("/view/adopter/AdopterAdoptionsView.fxml", "Adoption Status");
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
            Stage currentStage = (Stage) searchPetButton.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            Scene scene = new Scene(root, 1000, 700);

            try {
                java.lang.reflect.Method setAdopterId = controller.getClass().getMethod("setAdopterId", String.class);
                setAdopterId.invoke(controller, adopterId);
            } catch (Exception e) {
                System.out.println("Controller does not have setAdopterId method: " + e.getMessage());
            }

            try {
                java.lang.reflect.Method getBackButton = controller.getClass().getMethod("getBackButton");
                Button controllerBackButton = (Button) getBackButton.invoke(controller);

                if (controllerBackButton != null) {
                    util.BackButtonManager.configureBackButton(
                            controllerBackButton,
                            scene,
                            util.BackButtonManager.ScreenOrigin.ADOPTER_DASHBOARD,
                            adopterId
                    );
                }
            } catch (Exception e) {
                System.out.println("Controller does not have getBackButton method: " + e.getMessage());
            }

            currentStage.setTitle("Pet Adoption System - " + title);
            currentStage.setScene(scene);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}