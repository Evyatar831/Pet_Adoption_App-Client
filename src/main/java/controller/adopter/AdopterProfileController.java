package controller.adopter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import model.Adopter;
import service.AdopterService;
import util.AlertHelper;
import util.BackButtonManager;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;


public class AdopterProfileController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextArea addressField;
    @FXML private ComboBox<String> speciesField;
    @FXML private TextField breedField;
    @FXML private Spinner<Integer> minAgeSpinner;
    @FXML private Spinner<Integer> maxAgeSpinner;
    @FXML private ComboBox<String> genderField;
    @FXML private Button saveButton;
    @FXML private Button backButton;

    private final AdopterService adopterService = new AdopterService();
    private String adopterId;
    private Adopter currentAdopter;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SpinnerValueFactory<Integer> minAgeFactory =
                new IntegerSpinnerValueFactory(0, 30, 0);
        minAgeSpinner.setValueFactory(minAgeFactory);

        SpinnerValueFactory<Integer> maxAgeFactory =
                new IntegerSpinnerValueFactory(0, 30, 30);
        maxAgeSpinner.setValueFactory(maxAgeFactory);

        if (speciesField.getItems().isEmpty()) {
            speciesField.getItems().addAll("Dog", "Cat", "Bird", "Small Animal", "Reptile", "Other");
        }

        if (genderField.getItems().isEmpty()) {
            genderField.getItems().addAll("Male", "Female", "Any");
        }

        saveButton.setOnAction(e -> handleSaveProfile());

    }


    public void setAdopterId(String adopterId) {
        this.adopterId = adopterId;
    }


    public void loadAdopterData() {
        if (adopterId == null || adopterId.isEmpty()) {
            AlertHelper.showError("Error", "No adopter ID provided. Cannot load profile.");
            return;
        }

        currentAdopter = adopterService.getAdopter(adopterId);

        if (currentAdopter == null) {
            AlertHelper.showError("Error", "Could not load adopter profile.");
            return;
        }

        nameField.setText(currentAdopter.getName());
        emailField.setText(currentAdopter.getEmail());
        phoneField.setText(currentAdopter.getPhone());
        addressField.setText(currentAdopter.getAddress());

        speciesField.setValue(currentAdopter.getPreferredSpecies());
        breedField.setText(currentAdopter.getPreferredBreed());

        if (currentAdopter.getPreferredAgeMin() >= 0) {
            minAgeSpinner.getValueFactory().setValue(currentAdopter.getPreferredAgeMin());
        }

        if (currentAdopter.getPreferredAgeMax() >= 0) {
            maxAgeSpinner.getValueFactory().setValue(currentAdopter.getPreferredAgeMax());
        }

        genderField.setValue(currentAdopter.getPreferredGender());

        if (backButton != null && backButton.getScene() != null) {
            BackButtonManager.configureBackButton(
                    backButton,
                    backButton.getScene(),
                    BackButtonManager.ScreenOrigin.ADOPTER_DASHBOARD,
                    adopterId
            );
        }
    }


    @FXML
    private void handleSaveProfile() {
        if (!validateForm()) {
            return;
        }

        currentAdopter.setName(nameField.getText());
        currentAdopter.setEmail(emailField.getText());
        currentAdopter.setPhone(phoneField.getText());
        currentAdopter.setAddress(addressField.getText());

        currentAdopter.setPreferredSpecies(speciesField.getValue());
        currentAdopter.setPreferredBreed(breedField.getText());
        currentAdopter.setPreferredAgeMin(minAgeSpinner.getValue());
        currentAdopter.setPreferredAgeMax(maxAgeSpinner.getValue());
        currentAdopter.setPreferredGender(genderField.getValue());

        boolean success = adopterService.updateAdopter(currentAdopter);

        if (success) {
            AlertHelper.showInformation("Success", "Your profile has been updated successfully!");
        } else {
            AlertHelper.showError("Error", "Failed to update your profile. Please try again.");
        }
    }


    private boolean validateForm() {
        StringBuilder errorMessage = new StringBuilder();

        if (nameField.getText().trim().isEmpty()) {
            errorMessage.append("Name is required.\n");
        }

        if (emailField.getText().trim().isEmpty()) {
            errorMessage.append("Email is required.\n");
        } else if (!isValidEmail(emailField.getText())) {
            errorMessage.append("Please enter a valid email address.\n");
        } else {
            String email = emailField.getText().trim();
            List<Adopter> existingAdopters = adopterService.getAllAdopters();
            boolean emailExists = existingAdopters.stream()
                    .filter(adopter -> !adopter.getId().equals(currentAdopter.getId())) // Exclude current adopter
                    .anyMatch(adopter -> email.equalsIgnoreCase(adopter.getEmail()));

            if (emailExists) {
                errorMessage.append("This email address is already in use by another adopter. Please use a different email.\n");
            }
        }

        if (phoneField.getText().trim().isEmpty()) {
            errorMessage.append("Phone number is required.\n");
        }

        if (addressField.getText().trim().isEmpty()) {
            errorMessage.append("Address is required.\n");
        }

        int minAge = minAgeSpinner.getValue();
        int maxAge = maxAgeSpinner.getValue();
        if (minAge > maxAge) {
            errorMessage.append("Minimum age cannot be greater than maximum age.\n");
        }

        if (errorMessage.length() > 0) {
            AlertHelper.showError("Validation Error", errorMessage.toString());
            return false;
        }

        return true;
    }


    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }


    public Button getBackButton() {
        return backButton;
    }
}