package controller.staff;

import model.Pet;
import service.PetService;
import javafx.beans.property.SimpleIntegerProperty;
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
import java.util.stream.Collectors;


public class PetManagementController implements Initializable {

    @FXML private TextField nameField;
    @FXML private ComboBox<String> speciesField;
    @FXML private TextField breedField;
    @FXML private TextField ageField;
    @FXML private ComboBox<String> genderField;
    @FXML private ComboBox<String> statusField;

    @FXML private TextField searchField;
    @FXML private TableView<Pet> petTable;
    @FXML private TableColumn<Pet, String> idColumn;
    @FXML private TableColumn<Pet, String> nameColumn;
    @FXML private TableColumn<Pet, String> speciesColumn;
    @FXML private TableColumn<Pet, String> breedColumn;
    @FXML private TableColumn<Pet, Number> ageColumn;
    @FXML private TableColumn<Pet, String> genderColumn;
    @FXML private TableColumn<Pet, String> statusColumn;
    @FXML private Label totalPetsLabel;
    @FXML private Button backButton;

    private final PetService petService = new PetService();
    private final ObservableList<Pet> petList = FXCollections.observableArrayList();
    private Pet currentPet;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up table columns
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        speciesColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSpecies()));
        breedColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBreed()));
        ageColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getAge()));
        genderColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGender()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));

        // Format the status column with appropriate colors
        statusColumn.setCellFactory(column -> new TableCell<Pet, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);

                    switch (item) {
                        case "Available":
                            setStyle("-fx-text-fill: #4CAF50;"); // Green
                            break;
                        case "Pending":
                            setStyle("-fx-text-fill: #FF9800;"); // Orange
                            break;
                        case "Adopted":
                            setStyle("-fx-text-fill: #F44336;"); // Red
                            break;
                        default:
                            setStyle("");
                            break;
                    }
                }
            }
        });

        // Bind the table to the pet list
        petTable.setItems(petList);

        petTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                currentPet = newSelection; // Explicitly set currentPet
                displayPetDetails(newSelection);
            } else {
                currentPet = null; // Clear when no selection
            }
        });

        // Add number validation to age field
        ageField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                ageField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

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
        refreshPets();
    }


    @FXML
    private void handleRefresh() {
        refreshPets();
    }


    @FXML
    private void handleNewPet() {
        petTable.getSelectionModel().clearSelection();

        clearForm();
        currentPet = new Pet();
        statusField.setValue("Available"); // Default for new pets
    }


    @FXML
    private void handleSavePet() {
        if (!validateForm()) {
            return;
        }

        boolean isNewPet = true;

        // If no pet is being edited, create a new one
        if (currentPet == null) {
            currentPet = new Pet();

        } else {
            System.out.println("Updating existing pet: " + currentPet.getId());
            isNewPet = false;
        }

        // Update pet with form values
        currentPet.setName(nameField.getText());
        currentPet.setSpecies(speciesField.getValue());
        currentPet.setBreed(breedField.getText());
        currentPet.setAge(Integer.parseInt(ageField.getText()));
        currentPet.setGender(genderField.getValue());
        currentPet.setStatus(statusField.getValue());

        boolean success;
        if (isNewPet) {
            // Add new pet
            System.out.println("Sending add pet request for: " + currentPet.getName());
            success = petService.addPet(currentPet);
        } else {
            // Update existing pet
            System.out.println("Sending update pet request for: " + currentPet.getName());
            success = petService.updatePet(currentPet);
        }

        if (success) {
            AlertHelper.showInformation("Success",
                    "Pet saved successfully!");
            refreshPets();
            clearForm();
        } else {
            AlertHelper.showError("Error",
                    "Failed to save pet. Please check server logs for details.");
        }
    }


    @FXML
    private void handleDeletePet() {
        Pet selectedPet = petTable.getSelectionModel().getSelectedItem();

        if (selectedPet == null) {
            AlertHelper.showWarning("Warning", "No pet selected to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete " + currentPet.getName() + "?");
        confirmation.setContentText("Are you sure you want to remove this pet from the system? This action cannot be undone.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = petService.removePet(currentPet.getId());

            if (success) {
                AlertHelper.showInformation("Success",
                        "Pet deleted successfully.");
                refreshPets();
                clearForm();
            } else {
                AlertHelper.showError("Error",
                        "Failed to delete pet. Please try again.");
            }
        }
    }


    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().toLowerCase();
        if (searchTerm.isEmpty()) {
            refreshPets();
            return;
        }

        List<Pet> allPets = petService.getAllPets();
        List<Pet> filteredPets = allPets.stream()
                .filter(pet ->
                        pet.getName().toLowerCase().contains(searchTerm) ||
                                pet.getSpecies().toLowerCase().contains(searchTerm) ||
                                pet.getBreed().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());

        petList.setAll(filteredPets);
        updateTotalPetsLabel();
    }


    private void refreshPets() {
        List<Pet> pets = petService.getAllPets();
        petList.setAll(pets);
        updateTotalPetsLabel();
    }


    private void updateTotalPetsLabel() {
        totalPetsLabel.setText("Total: " + petList.size() + " pets");
    }


    private void displayPetDetails(Pet pet) {
        currentPet = pet;

        nameField.setText(pet.getName());
        speciesField.setValue(pet.getSpecies());
        breedField.setText(pet.getBreed());
        ageField.setText(String.valueOf(pet.getAge()));
        genderField.setValue(pet.getGender());
        statusField.setValue(pet.getStatus());
    }


    private void clearForm() {
        currentPet = null;
        nameField.clear();
        speciesField.setValue(null);
        breedField.clear();
        ageField.clear();
        genderField.setValue(null);
        statusField.setValue("Available");
    }


    private boolean validateForm() {
        StringBuilder errorMessage = new StringBuilder();

        // Name validation
        if (nameField.getText().trim().isEmpty()) {
            errorMessage.append("Pet name is required.\n");
        }

        // Species validation
        if (speciesField.getValue() == null || speciesField.getValue().trim().isEmpty()) {
            errorMessage.append("Species must be selected.\n");
        }

        // Breed validation
        if (breedField.getText().trim().isEmpty()) {
            errorMessage.append("Breed is required.\n");
        }

        // Age validation with more specific error messages
        if (ageField.getText().trim().isEmpty()) {
            errorMessage.append("Age is required.\n");
        } else {
            try {
                int age = Integer.parseInt(ageField.getText());

                // Specific age range validations
                if (age < 0) {
                    errorMessage.append("Age cannot be negative. Please enter a valid age.\n");
                } else if (age > 30) {
                    errorMessage.append("Age seems unrealistic. Maximum age is 30 years.\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("Age must be a whole number. Do not use letters or special characters.\n");
            }
        }

        // Gender validation
        if (genderField.getValue() == null || genderField.getValue().trim().isEmpty()) {
            errorMessage.append("Gender must be selected.\n");
        }

        // If there are any error messages, show the alert
        if (errorMessage.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setHeaderText("Please correct the following issues:");
            alert.setContentText(errorMessage.toString());
            alert.showAndWait();
            return false;
        }

        return true;
    }


    public Button getBackButton() {
        return backButton;
    }
}