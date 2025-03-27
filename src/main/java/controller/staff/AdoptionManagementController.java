package controller.staff;

import model.Adopter;
import model.Adoption;
import model.Pet;
import service.AdoptionService;
import service.AdopterService;
import service.PetService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import util.AlertHelper;
import util.BackButtonManager;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


public class AdoptionManagementController implements Initializable {

    @FXML private ComboBox<String> statusFilterComboBox;
    @FXML private TextField searchField;

    @FXML private TableView<Adoption> adoptionsTable;
    @FXML private TableColumn<Adoption, String> idColumn;
    @FXML private TableColumn<Adoption, String> petColumn;
    @FXML private TableColumn<Adoption, String> adopterColumn;
    @FXML private TableColumn<Adoption, String> dateColumn;
    @FXML private TableColumn<Adoption, String> statusColumn;
    @FXML private Label totalAdoptionsLabel;

    @FXML private Label adoptionIdLabel;
    @FXML private Label adoptionStatusLabel;
    @FXML private Label adoptionDateLabel;

    @FXML private Label petNameLabel;
    @FXML private Label petSpeciesLabel;
    @FXML private Label petBreedLabel;
    @FXML private Label petAgeLabel;

    @FXML private Label adopterNameLabel;
    @FXML private Label adopterEmailLabel;
    @FXML private Label adopterPhoneLabel;

    @FXML private ComboBox<String> statusUpdateComboBox;
    @FXML private Button backButton;

    private final AdoptionService adoptionService = new AdoptionService();
    private final PetService petService = new PetService();
    private final AdopterService adopterService = new AdopterService();

    private final ObservableList<Adoption> adoptionsList = FXCollections.observableArrayList();
    private Adoption currentAdoption;
    private Pet currentPet;
    private Adopter currentAdopter;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize status filter
        statusFilterComboBox.getItems().addAll(
                "All", "Pending", "Approved", "Rejected", "Completed");
        statusFilterComboBox.setValue("All");
        statusFilterComboBox.setOnAction(e -> handleSearch());

        // Initialize status update options
        statusUpdateComboBox.getItems().addAll(
                "Pending", "Approved", "Rejected");

        // Set up table columns
        idColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getId().substring(0, 8) + "..."));

        petColumn.setCellValueFactory(cellData -> {
            Pet pet = petService.getPet(cellData.getValue().getPetId());
            return new SimpleStringProperty(pet != null ? pet.getName() : "Unknown Pet");
        });

        adopterColumn.setCellValueFactory(cellData -> {
            Adopter adopter = adopterService.getAdopter(cellData.getValue().getAdopterId());
            return new SimpleStringProperty(adopter != null ? adopter.getName() : "Unknown Adopter");
        });

        dateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAdoptionDate().format(dateFormatter)));

        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus()));

        // Color code status cells
        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);

                    switch (item) {
                        case "Pending":
                            setStyle("-fx-text-fill: #FF9800;"); // Orange
                            break;
                        case "Approved":
                            setStyle("-fx-text-fill: #2196F3;"); // Blue
                            break;
                        case "Rejected":
                            setStyle("-fx-text-fill: #F44336;"); // Red
                            break;
                        case "Completed":
                            setStyle("-fx-text-fill: #4CAF50;"); // Green
                            break;
                        default:
                            setStyle("");
                            break;
                    }
                }
            }
        });

        // Bind the table to the adoptions list
        adoptionsTable.setItems(adoptionsList);

        // Set up selection listener
        adoptionsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        loadAdoptionDetails(newSelection);
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
        refreshAdoptions();
    }


    @FXML
    private void handleRefresh() {
        refreshAdoptions();
    }


    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().toLowerCase();
        String statusFilter = statusFilterComboBox.getValue();

        List<Adoption> allAdoptions = adoptionService.getAllAdoptions();
        List<Adoption> filteredAdoptions = allAdoptions;

        // Apply status filter
        if (!"All".equals(statusFilter)) {
            filteredAdoptions = filteredAdoptions.stream()
                    .filter(adoption -> statusFilter.equals(adoption.getStatus()))
                    .collect(Collectors.toList());
        }

        // Apply search term if present
        if (!searchTerm.isEmpty()) {
            filteredAdoptions = filteredAdoptions.stream()
                    .filter(adoption -> {
                        Pet pet = petService.getPet(adoption.getPetId());
                        Adopter adopter = adopterService.getAdopter(adoption.getAdopterId());

                        return (pet != null && pet.getName().toLowerCase().contains(searchTerm)) ||
                                (adopter != null && adopter.getName().toLowerCase().contains(searchTerm)) ||
                                adoption.getId().toLowerCase().contains(searchTerm) ||
                                adoption.getStatus().toLowerCase().contains(searchTerm);
                    })
                    .collect(Collectors.toList());
        }

        adoptionsList.setAll(filteredAdoptions);
        updateTotalAdoptionsLabel();
    }


    @FXML
    private void handleUpdateStatus() {
        if (currentAdoption == null) {
            AlertHelper.showWarning("Warning",
                    "Please select an adoption to update.");
            return;
        }

        String newStatus = statusUpdateComboBox.getValue();
        if (newStatus == null || newStatus.isEmpty()) {
            AlertHelper.showWarning("Warning",
                    "Please select a status to update to.");
            return;
        }

        if (newStatus.equals(currentAdoption.getStatus())) {
            AlertHelper.showInformation("Information",
                    "The adoption is already in the " + newStatus + " status.");
            return;
        }

        // Check if the pet is already adopted when trying to approve
        if ("Approved".equals(newStatus) && currentPet != null && "Adopted".equals(currentPet.getStatus())) {
            AlertHelper.showError("Pet Already Adopted",
                    "This pet has already been adopted by someone else. You cannot approve this adoption.");
            return;
        }

        // Validate status transitions
        String currentStatus = currentAdoption.getStatus();

        // If current status is "Rejected", only allow moving to "Pending"
        if ("Rejected".equals(currentStatus) && !"Pending".equals(newStatus)) {
            AlertHelper.showWarning("Invalid Status Change",
                    "From Rejected, can only move back to Pending for reconsideration.");
            return;
        }

        // Prevent changing status of completed adoptions
        if ("Completed".equals(currentStatus)) {
            AlertHelper.showWarning("Invalid Status Change",
                    "Completed adoptions cannot be modified.");
            return;
        }

        // Confirm status update
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Status Update");
        confirmation.setHeaderText("Update adoption status to " + newStatus + "?");
        confirmation.setContentText("Are you sure you want to update the status of this adoption?");

        if ("Approved".equals(newStatus)) {
            confirmation.setContentText("Are you sure you want to approve this adoption? " +
                    "This will mark the pet as pending and prevent other adoptions until this one is completed or rejected.");
        }

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Adoption updatedAdoption = adoptionService.updateAdoptionStatus(currentAdoption.getId(), newStatus);

            if (updatedAdoption != null) {
                AlertHelper.showInformation("Success",
                        "Adoption status updated successfully to " + newStatus + ".");
                refreshAdoptions();

                // Select the updated adoption
                for (Adoption adoption : adoptionsList) {
                    if (adoption.getId().equals(updatedAdoption.getId())) {
                        adoptionsTable.getSelectionModel().select(adoption);
                        break;
                    }
                }
            } else {
                AlertHelper.showError("Error",
                        "Failed to update adoption status. Please try again.");
            }
        }
    }


    @FXML
    private void handleCompleteAdoption() {
        if (currentAdoption == null) {
            AlertHelper.showWarning("Warning",
                    "Please select an adoption to complete.");
            return;
        }

        if (!"Approved".equals(currentAdoption.getStatus())) {
            AlertHelper.showWarning("Warning",
                    "Only approved adoptions can be completed. " +
                            "Current status: " + currentAdoption.getStatus());
            return;
        }

        // Check if the pet is already adopted
        if (currentPet != null && "Adopted".equals(currentPet.getStatus())) {
            AlertHelper.showError("Pet Already Adopted",
                    "This pet has already been adopted by someone else.");
            return;
        }

        // Confirm completion
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Adoption Completion");
        confirmation.setHeaderText("Complete this adoption?");
        confirmation.setContentText("This will finalize the adoption of " +
                (currentPet != null ? currentPet.getName() : "the pet") + " by " +
                (currentAdopter != null ? currentAdopter.getName() : "the adopter") + ".\n\n" +
                "Once completed, this pet will be marked as adopted and will no longer be available for other adopters.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Adoption completedAdoption = adoptionService.completeAdoption(currentAdoption.getId());

            if (completedAdoption != null) {
                AlertHelper.showInformation("Success",
                        "Adoption completed successfully!\n\nThe pet has been marked as adopted and is no longer available for other adopters.");
                refreshAdoptions();

                // Select the completed adoption
                for (Adoption adoption : adoptionsList) {
                    if (adoption.getId().equals(completedAdoption.getId())) {
                        adoptionsTable.getSelectionModel().select(adoption);
                        break;
                    }
                }
            } else {
                AlertHelper.showError("Error",
                        "Failed to complete the adoption. The pet may have already been adopted by someone else.");
            }
        }
    }


    private void refreshAdoptions() {
        List<Adoption> adoptions = adoptionService.getAllAdoptions();

        adoptionsList.setAll(adoptions);
        updateTotalAdoptionsLabel();
    }


    private void updateTotalAdoptionsLabel() {
        totalAdoptionsLabel.setText("Total: " + adoptionsList.size() + " adoptions");
    }


    private void loadAdoptionDetails(Adoption adoption) {
        currentAdoption = adoption;

        // Update adoption details
        adoptionIdLabel.setText(adoption.getId());
        adoptionStatusLabel.setText(adoption.getStatus());
        adoptionDateLabel.setText(adoption.getAdoptionDate().format(dateFormatter));

        // Set status update combobox to current status
        statusUpdateComboBox.setValue(adoption.getStatus());

        // Load pet and adopter details
        loadPetDetails(adoption.getPetId());
        loadAdopterDetails(adoption.getAdopterId());

        // Update UI based on pet status
        if (currentPet != null && "Adopted".equals(currentPet.getStatus()) &&
                !"Completed".equals(adoption.getStatus())) {
            // Show warning if pet is adopted but this adoption isn't completed
            adoptionStatusLabel.setText(adoption.getStatus() + " (Pet already adopted)");
            adoptionStatusLabel.setStyle("-fx-text-fill: #F44336;");
        } else {
            // Normal status display
            adoptionStatusLabel.setText(adoption.getStatus());
            switch (adoption.getStatus()) {
                case "Pending":
                    adoptionStatusLabel.setStyle("-fx-text-fill: #FF9800;");
                    break;
                case "Approved":
                    adoptionStatusLabel.setStyle("-fx-text-fill: #2196F3;");
                    break;
                case "Rejected":
                    adoptionStatusLabel.setStyle("-fx-text-fill: #F44336;");
                    break;
                case "Completed":
                    adoptionStatusLabel.setStyle("-fx-text-fill: #4CAF50;");
                    break;
                default:
                    adoptionStatusLabel.setStyle("");
                    break;
            }
        }
    }


    private void loadPetDetails(String petId) {
        Pet pet = petService.getPet(petId);
        currentPet = pet;

        if (pet != null) {
            petNameLabel.setText(pet.getName());
            petSpeciesLabel.setText(pet.getSpecies());
            petBreedLabel.setText(pet.getBreed());
            petAgeLabel.setText(String.valueOf(pet.getAge()) + " years");
        } else {
            petNameLabel.setText("Unknown");
            petSpeciesLabel.setText("Unknown");
            petBreedLabel.setText("Unknown");
            petAgeLabel.setText("Unknown");
        }
    }


    private void loadAdopterDetails(String adopterId) {
        Adopter adopter = adopterService.getAdopter(adopterId);
        currentAdopter = adopter;

        if (adopter != null) {
            adopterNameLabel.setText(adopter.getName());
            adopterEmailLabel.setText(adopter.getEmail());
            adopterPhoneLabel.setText(adopter.getPhone());
        } else {
            adopterNameLabel.setText("Unknown");
            adopterEmailLabel.setText("Unknown");
            adopterPhoneLabel.setText("Unknown");
        }
    }


    public Button getBackButton() {
        return backButton;
    }
}