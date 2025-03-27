package controller.adopter;

import model.Adopter;
import model.Adoption;
import model.Pet;
import service.AdopterService;
import service.AdoptionService;
import service.PetService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import util.AlertHelper;
import util.BackButtonManager;

import java.net.URL;
import java.util.*;


public class PetMatchingController implements Initializable {

    @FXML private Button refreshButton;
    @FXML private Spinner<Integer> matchCountSpinner;

    @FXML private Label adopterNameLabel;
    @FXML private Label adopterEmailLabel;
    @FXML private Label adopterPhoneLabel;

    @FXML private Label speciesPreferenceLabel;
    @FXML private Label breedPreferenceLabel;
    @FXML private Label agePreferenceLabel;
    @FXML private Label genderPreferenceLabel;

    @FXML private Label matchCountLabel;
    @FXML private TableView<MatchResult> matchesTable;
    @FXML private TableColumn<MatchResult, String> matchNameColumn;
    @FXML private TableColumn<MatchResult, String> matchSpeciesColumn;
    @FXML private TableColumn<MatchResult, String> matchBreedColumn;
    @FXML private TableColumn<MatchResult, Number> matchAgeColumn;
    @FXML private TableColumn<MatchResult, String> matchGenderColumn;
    @FXML private TableColumn<MatchResult, Number> matchScoreColumn;
    @FXML private Button backButton;

    private final AdopterService adopterService = new AdopterService();
    private final PetService petService = new PetService();
    private final AdoptionService adoptionService = new AdoptionService();

    private final ObservableList<MatchResult> matchResults = FXCollections.observableArrayList();
    private String adopterId;
    private Adopter currentAdopter;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 3);
        matchCountSpinner.setValueFactory(valueFactory);

        matchNameColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getPet().getName()));
        matchSpeciesColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getPet().getSpecies()));
        matchBreedColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getPet().getBreed()));
        matchAgeColumn.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(cellData.getValue().getPet().getAge()));
        matchGenderColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getPet().getGender()));
        matchScoreColumn.setCellValueFactory(
                cellData -> new SimpleDoubleProperty(cellData.getValue().getMatchScore()));

        matchScoreColumn.setCellFactory(column -> new TableCell<MatchResult, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.0f%%", item.doubleValue()));

                    double score = item.doubleValue();
                    if (score >= 80) {
                        setStyle("-fx-text-fill: #4CAF50;"); // Green for high matches
                    } else if (score >= 60) {
                        setStyle("-fx-text-fill: #2196F3;"); // Blue for good matches
                    } else if (score >= 40) {
                        setStyle("-fx-text-fill: #FF9800;"); // Orange for medium matches
                    } else {
                        setStyle("-fx-text-fill: #F44336;"); // Red for low matches
                    }
                }
            }
        });

        matchesTable.setItems(matchResults);

        if (backButton != null) {
            backButton.sceneProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    BackButtonManager.configureBackButton(
                            backButton,
                            newValue,
                            BackButtonManager.ScreenOrigin.ADOPTER_DASHBOARD,
                            adopterId
                    );
                }
            });
        }
    }


    public void setAdopterId(String adopterId) {
        this.adopterId = adopterId;
        loadAdopterDetails();
    }


    private void loadAdopterDetails() {
        if (adopterId == null || adopterId.isEmpty()) {
            return;
        }

        currentAdopter = adopterService.getAdopter(adopterId);
        if (currentAdopter == null) {
            AlertHelper.showError("Error", "Could not load your profile. Please try again.");
            return;
        }

        adopterNameLabel.setText(currentAdopter.getName());
        adopterEmailLabel.setText(currentAdopter.getEmail());
        adopterPhoneLabel.setText(currentAdopter.getPhone());

        speciesPreferenceLabel.setText(currentAdopter.getPreferredSpecies() != null && !currentAdopter.getPreferredSpecies().isEmpty() ?
                currentAdopter.getPreferredSpecies() : "Any");

        breedPreferenceLabel.setText(currentAdopter.getPreferredBreed() != null && !currentAdopter.getPreferredBreed().isEmpty() ?
                currentAdopter.getPreferredBreed() : "Any");

        agePreferenceLabel.setText(String.format("%d to %d years",
                currentAdopter.getPreferredAgeMin(), currentAdopter.getPreferredAgeMax()));

        genderPreferenceLabel.setText(currentAdopter.getPreferredGender() != null && !currentAdopter.getPreferredGender().isEmpty() ?
                currentAdopter.getPreferredGender() : "Any");
    }


    @FXML
    private void handleFindMatches() {
        if (currentAdopter == null) {
            AlertHelper.showWarning("Warning", "Your profile could not be loaded. Please try again later.");
            return;
        }

        int matchCount = matchCountSpinner.getValue();

        List<Pet> matches = petService.findMatches(currentAdopter.getId(), matchCount);

        matchResults.clear();

        if (matches.isEmpty()) {
            matchCountLabel.setText("0 matches found");
            AlertHelper.showInformation("No Matches",
                    "No matching pets were found. Try adjusting your preferences in your profile.");
            return;
        }

        for (int i = 0; i < matches.size(); i++) {
            Pet pet = matches.get(i);

            double score = calculateEstimatedScore(pet, currentAdopter, i, matches.size());

            matchResults.add(new MatchResult(pet, score));
        }

        matchCountLabel.setText(matchResults.size() + " matches found");
    }


    private double calculateEstimatedScore(Pet pet, Adopter adopter, int position, int totalMatches) {

        double baseScore = 100.0 - ((double)position / totalMatches * 20.0);

        double score = baseScore;

        if (adopter.getPreferredSpecies() != null && !adopter.getPreferredSpecies().isEmpty() &&
                !adopter.getPreferredSpecies().equalsIgnoreCase(pet.getSpecies())) {
            score -= 30;
        }

        if (adopter.getPreferredBreed() != null && !adopter.getPreferredBreed().isEmpty() &&
                !adopter.getPreferredBreed().equalsIgnoreCase(pet.getBreed())) {
            score -= 20;
        }

        if (pet.getAge() < adopter.getPreferredAgeMin() || pet.getAge() > adopter.getPreferredAgeMax()) {
            score -= 15;
        }

        if (adopter.getPreferredGender() != null && !adopter.getPreferredGender().isEmpty() &&
                !adopter.getPreferredGender().equalsIgnoreCase(pet.getGender()) &&
                !adopter.getPreferredGender().equalsIgnoreCase("Any")) {
            score -= 10;
        }

        return Math.max(20, score);
    }

    @FXML
    private void handleViewPetDetails() {
        MatchResult selectedMatch = matchesTable.getSelectionModel().getSelectedItem();
        if (selectedMatch == null) {
            AlertHelper.showWarning("Warning",
                    "Please select a pet to view details.");
            return;
        }

        Pet pet = selectedMatch.getPet();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Pet Details");
        alert.setHeaderText(pet.getName() + " - " + pet.getBreed() + " " + pet.getSpecies());

        VBox content = new VBox(10);

        Label ageLabel = new Label("Age: " + pet.getAge() + " years");
        Label genderLabel = new Label("Gender: " + pet.getGender());
        Label statusLabel = new Label("Status: " + pet.getStatus());

        Label matchScoreLabel = new Label(String.format("Match Score: %.0f%%", selectedMatch.getMatchScore()));
        matchScoreLabel.setStyle("-fx-font-weight: bold;");

        VBox explanationBox = new VBox(5);
        Label explanationTitle = new Label("Match Details:");
        explanationTitle.setStyle("-fx-font-weight: bold;");

        HBox speciesMatch = new HBox(5);
        speciesMatch.getChildren().addAll(
                new Label("Species:"),
                createMatchLabel(
                        currentAdopter.getPreferredSpecies() == null ||
                                currentAdopter.getPreferredSpecies().isEmpty() ||
                                currentAdopter.getPreferredSpecies().equalsIgnoreCase(pet.getSpecies())
                )
        );

        HBox breedMatch = new HBox(5);
        breedMatch.getChildren().addAll(
                new Label("Breed:"),
                createMatchLabel(
                        currentAdopter.getPreferredBreed() == null ||
                                currentAdopter.getPreferredBreed().isEmpty() ||
                                currentAdopter.getPreferredBreed().equalsIgnoreCase(pet.getBreed())
                )
        );

        HBox ageMatch = new HBox(5);
        ageMatch.getChildren().addAll(
                new Label("Age:"),
                createMatchLabel(
                        pet.getAge() >= currentAdopter.getPreferredAgeMin() &&
                                pet.getAge() <= currentAdopter.getPreferredAgeMax()
                )
        );

        HBox genderMatch = new HBox(5);
        genderMatch.getChildren().addAll(
                new Label("Gender:"),
                createMatchLabel(
                        currentAdopter.getPreferredGender() == null ||
                                currentAdopter.getPreferredGender().isEmpty() ||
                                "Any".equalsIgnoreCase(currentAdopter.getPreferredGender()) ||
                                currentAdopter.getPreferredGender().equalsIgnoreCase(pet.getGender())
                )
        );

        explanationBox.getChildren().addAll(
                explanationTitle, speciesMatch, breedMatch, ageMatch, genderMatch
        );

        content.getChildren().addAll(ageLabel, genderLabel, statusLabel, matchScoreLabel, explanationBox);

        if ("Available".equals(pet.getStatus())) {
            Button adoptButton = new Button("Request Adoption");
            adoptButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            adoptButton.setOnAction(e -> {
                alert.close();
                handleCreateAdoption();
            });
            content.getChildren().add(adoptButton);
        }

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setContent(content);
        dialogPane.setPrefWidth(400);

        alert.showAndWait();
    }


    private Label createMatchLabel(boolean isMatch) {
        Label label = new Label(isMatch ? "Match ✓" : "Mismatch ✗");
        label.setStyle(isMatch ?
                "-fx-text-fill: #4CAF50; -fx-font-weight: bold;" :
                "-fx-text-fill: #F44336;");
        return label;
    }

    @FXML
    private void handleCreateAdoption() {
        MatchResult selectedMatch = matchesTable.getSelectionModel().getSelectedItem();

        if (currentAdopter == null) {
            AlertHelper.showWarning("Warning",
                    "Your profile could not be loaded. Please try again later.");
            return;
        }

        if (selectedMatch == null) {
            AlertHelper.showWarning("Warning",
                    "Please select a pet to create an adoption request.");
            return;
        }

        Pet pet = selectedMatch.getPet();

        if (!"Available".equals(pet.getStatus())) {
            AlertHelper.showWarning("Pet Not Available",
                    "Sorry, " + pet.getName() + " is currently " + pet.getStatus().toLowerCase() +
                            " and cannot be adopted at this time.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Adoption Request");
        confirmation.setHeaderText("Create adoption request for " + pet.getName() + "?");
        confirmation.setContentText("This will submit an adoption request for " + pet.getName() + ".");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Adoption adoption = adoptionService.createAdoptionRequest(pet.getId(), currentAdopter.getId());

            if (adoption != null) {
                AlertHelper.showInformation("Success",
                        "Adoption request created successfully!");
                handleFindMatches();
            } else {
                AlertHelper.showError("Error",
                        "Failed to create adoption request. The pet may no longer be available.");
            }
        }
    }


    @FXML
    private void handleRefresh() {
        loadAdopterDetails();

        matchResults.clear();
        matchCountLabel.setText("0 matches found");
    }


    public Button getBackButton() {
        return backButton;
    }


    public static class MatchResult {
        private final Pet pet;
        private final double matchScore;

        public MatchResult(Pet pet, double matchScore) {
            this.pet = pet;
            this.matchScore = matchScore;
        }

        public Pet getPet() {
            return pet;
        }

        public double getMatchScore() {
            return matchScore;
        }
    }
}