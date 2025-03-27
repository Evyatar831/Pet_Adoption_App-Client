package controller.adopter;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.Adopter;
import model.Adoption;
import model.Pet;
import service.AdopterService;
import service.AdoptionService;
import service.PetService;
import util.AlertHelper;
import util.BackButtonManager;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


public class AdopterAdoptionsController implements Initializable {

    @FXML private TableView<Adoption> adoptionsTable;
    @FXML private TableColumn<Adoption, String> petNameColumn;
    @FXML private TableColumn<Adoption, String> dateColumn;
    @FXML private TableColumn<Adoption, String> statusColumn;

    @FXML private Label petNameLabel;
    @FXML private Label petSpeciesLabel;
    @FXML private Label petBreedLabel;
    @FXML private Label petAgeLabel;
    @FXML private Label adoptionDateLabel;
    @FXML private Label adoptionStatusLabel;
    @FXML private Label statusMessageLabel;

    @FXML private Button refreshButton;
    @FXML private Button backButton;

    private final AdoptionService adoptionService = new AdoptionService();
    private final PetService petService = new PetService();
    private final AdopterService adopterService = new AdopterService();

    private final ObservableList<Adoption> adoptionsList = FXCollections.observableArrayList();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private String adopterId;
    private Adopter currentAdopter;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        petNameColumn.setCellValueFactory(cellData -> {
            Pet pet = petService.getPet(cellData.getValue().getPetId());
            return new SimpleStringProperty(pet != null ? pet.getName() : "Unknown Pet");
        });

        dateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAdoptionDate().format(dateFormatter)));

        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus()));

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

        adoptionsTable.setItems(adoptionsList);

        adoptionsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        displayAdoptionDetails(newSelection);
                    }
                });

        refreshButton.setOnAction(e -> loadAdopterAdoptions());

    }


    public void setAdopterId(String adopterId) {
        this.adopterId = adopterId;
    }


    public void loadAdopterAdoptions() {
        if (adopterId == null || adopterId.isEmpty()) {
            AlertHelper.showError("Error", "No adopter ID provided. Cannot load adoptions.");
            return;
        }

        currentAdopter = adopterService.getAdopter(adopterId);

        if (currentAdopter == null) {
            AlertHelper.showError("Error", "Could not load adopter information.");
            return;
        }

        List<Adoption> allAdoptions = adoptionService.getAllAdoptions();

        List<Adoption> myAdoptions = allAdoptions.stream()
                .filter(adoption -> adopterId.equals(adoption.getAdopterId()))
                .collect(Collectors.toList());

        adoptionsList.setAll(myAdoptions);

        if (myAdoptions.isEmpty()) {
            clearAdoptionDetails();
            statusMessageLabel.setText("You don't have any adoption applications yet.");
        } else {
            statusMessageLabel.setText("You have " + myAdoptions.size() + " adoption applications.");

            adoptionsTable.getSelectionModel().selectFirst();
        }

        if (backButton != null && backButton.getScene() != null) {
            BackButtonManager.configureBackButton(
                    backButton,
                    backButton.getScene(),
                    BackButtonManager.ScreenOrigin.ADOPTER_DASHBOARD,
                    adopterId
            );
        }
    }


    private void displayAdoptionDetails(Adoption adoption) {
        Pet pet = petService.getPet(adoption.getPetId());

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

        adoptionDateLabel.setText(adoption.getAdoptionDate().format(dateFormatter));
        adoptionStatusLabel.setText(adoption.getStatus());

        switch (adoption.getStatus()) {
            case "Pending":
                adoptionStatusLabel.setStyle("-fx-text-fill: #FF9800;"); // Orange
                break;
            case "Approved":
                adoptionStatusLabel.setStyle("-fx-text-fill: #2196F3;"); // Blue
                break;
            case "Rejected":
                adoptionStatusLabel.setStyle("-fx-text-fill: #F44336;"); // Red
                break;
            case "Completed":
                adoptionStatusLabel.setStyle("-fx-text-fill: #4CAF50;"); // Green
                break;
            default:
                adoptionStatusLabel.setStyle("");
                break;
        }
    }


    private void clearAdoptionDetails() {
        petNameLabel.setText("");
        petSpeciesLabel.setText("");
        petBreedLabel.setText("");
        petAgeLabel.setText("");
        adoptionDateLabel.setText("");
        adoptionStatusLabel.setText("");
    }


    public Button getBackButton() {
        return backButton;
    }
}