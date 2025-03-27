package controller.staff;

import model.Adopter;
import service.AdopterService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import util.AlertHelper;
import util.BackButtonManager;

import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class AdopterManagementController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextArea addressField;

    @FXML private ComboBox<String> speciesField;
    @FXML private TextField breedField;
    @FXML private Spinner<Integer> minAgeSpinner;
    @FXML private Spinner<Integer> maxAgeSpinner;
    @FXML private ComboBox<String> genderField;

    @FXML private TextField searchField;
    @FXML private TableView<Adopter> adopterTable;
    @FXML private TableColumn<Adopter, String> idColumn;
    @FXML private TableColumn<Adopter, String> nameColumn;
    @FXML private TableColumn<Adopter, String> emailColumn;
    @FXML private TableColumn<Adopter, String> phoneColumn;
    @FXML private Label totalAdoptersLabel;
    @FXML private Button backButton;

    private final AdopterService adopterService = new AdopterService();
    private final ObservableList<Adopter> adopterList = FXCollections.observableArrayList();
    private Adopter currentAdopter;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        phoneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));

        adopterTable.setItems(adopterList);

        adopterTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displayAdopterDetails(newSelection);
            }
        });

        SpinnerValueFactory<Integer> minAgeFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 30, 0);
        minAgeSpinner.setValueFactory(minAgeFactory);

        SpinnerValueFactory<Integer> maxAgeFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 30, 30);
        maxAgeSpinner.setValueFactory(maxAgeFactory);

        // Set up species options
        if (!speciesField.getItems().contains("Dog")) {
            speciesField.getItems().addAll("Dog", "Cat", "Bird", "Small Animal", "Reptile", "Other");
        }

        // Set up gender options
        if (!genderField.getItems().contains("Male")) {
            genderField.getItems().addAll("Male", "Female", "Any");
        }

        // Configure back button once the scene is available
        if (backButton != null) {
            backButton.sceneProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    BackButtonManager.configureBackButton(
                            backButton,
                            newValue,
                            BackButtonManager.ScreenOrigin.STAFF_DASHBOARD,
                            null
                    );
                }
            });
        }

        // Load initial data
        refreshAdopters();
    }


    @FXML
    private void handleRefresh() {
        refreshAdopters();
    }


    @FXML
    private void handleNewAdopter() {
        adopterTable.getSelectionModel().clearSelection();
        clearForm();
        currentAdopter = new Adopter();
    }


    @FXML
    private void handleSaveAdopter() {
        if (!validateForm()) {
            return;
        }

        boolean isNewAdopter = true;
        if (currentAdopter == null) {
            currentAdopter = new Adopter();

        }
        else {
            System.out.println("Updating existing pet: " + currentAdopter.getId());
            isNewAdopter = false;
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

        boolean success;
        if (isNewAdopter) {
            // Add new adopter
            success = adopterService.addAdopter(currentAdopter);
        } else {
            // Update existing adopter
            success = adopterService.updateAdopter(currentAdopter);
        }

        if (success) {
            AlertHelper.showInformation("Success",
                    "Adopter saved successfully!");
            refreshAdopters();
            clearForm();
        } else {
            AlertHelper.showError("Error",
                    "Failed to save adopter. Please try again.");
        }
    }

    @FXML
    private void handleDeleteAdopter() {
        Adopter selectedAdopter = adopterTable.getSelectionModel().getSelectedItem();
        if (selectedAdopter == null) {
            AlertHelper.showWarning("No Selection", "Please select an adopter to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete " + currentAdopter.getName() + "?");
        confirmation.setContentText("Are you sure you want to remove this adopter from the system? This action cannot be undone.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = adopterService.removeAdopter(currentAdopter.getId());

            if (success) {
                AlertHelper.showInformation("Success",
                        "Adopter deleted successfully.");
                refreshAdopters();
                clearForm();
            } else {
                AlertHelper.showError("Error",
                        "Failed to delete adopter. Please try again.");
            }
        }
    }


    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().toLowerCase();
        if (searchTerm.isEmpty()) {
            refreshAdopters();
            return;
        }

        List<Adopter> allAdopters = adopterService.getAllAdopters();
        List<Adopter> filteredAdopters = allAdopters.stream()
                .filter(adopter ->
                        adopter.getName().toLowerCase().contains(searchTerm) ||
                                adopter.getEmail().toLowerCase().contains(searchTerm) ||
                                adopter.getPhone().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());

        adopterList.setAll(filteredAdopters);
        updateTotalAdoptersLabel();
    }


    private void refreshAdopters() {
        List<Adopter> adopters = adopterService.getAllAdopters();
        adopterList.setAll(adopters);
        updateTotalAdoptersLabel();
    }


    private void updateTotalAdoptersLabel() {
        totalAdoptersLabel.setText("Total: " + adopterList.size() + " adopters");
    }


    private void displayAdopterDetails(Adopter adopter) {
        currentAdopter = adopter;

        nameField.setText(adopter.getName());
        emailField.setText(adopter.getEmail());
        phoneField.setText(adopter.getPhone());
        addressField.setText(adopter.getAddress());

        speciesField.setValue(adopter.getPreferredSpecies());
        breedField.setText(adopter.getPreferredBreed());

        if (adopter.getPreferredAgeMin() >= 0) {
            minAgeSpinner.getValueFactory().setValue(adopter.getPreferredAgeMin());
        }

        if (adopter.getPreferredAgeMax() >= 0) {
            maxAgeSpinner.getValueFactory().setValue(adopter.getPreferredAgeMax());
        }

        genderField.setValue(adopter.getPreferredGender());
    }


    private void clearForm() {
        currentAdopter = null;
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        addressField.clear();

        speciesField.setValue(null);
        breedField.clear();
        minAgeSpinner.getValueFactory().setValue(0);
        maxAgeSpinner.getValueFactory().setValue(30);
        genderField.setValue(null);
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

            if (currentAdopter == null) {
                boolean emailExists = existingAdopters.stream()
                        .anyMatch(adopter -> email.equalsIgnoreCase(adopter.getEmail()));

                if (emailExists) {
                    errorMessage.append("This email address is already in use. Please use a different email.\n");
                }
            } else {
                boolean emailExists = existingAdopters.stream()
                        .filter(adopter -> !adopter.getId().equals(currentAdopter.getId()))
                        .anyMatch(adopter -> email.equalsIgnoreCase(adopter.getEmail()));

                if (emailExists) {
                    errorMessage.append("This email address is already in use by another adopter. Please use a different email.\n");
                }
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