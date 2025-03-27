package controller.adopter;

import model.Adopter;
import model.Pet;
import model.Adoption;
import service.AdopterService;
import service.PetService;
import service.AdoptionService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.geometry.Pos;
import util.AlertHelper;
import util.BackButtonManager;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.io.InputStream;


public class PetBrowseController implements Initializable {

    @FXML private ComboBox<String> speciesFilter;
    @FXML private ComboBox<String> breedFilter;
    @FXML private ComboBox<String> ageFilter;
    @FXML private ComboBox<String> genderFilter;
    @FXML private TextField searchField;
    @FXML private TilePane petTilePane;
    @FXML private Label resultCountLabel;
    @FXML private Button backButton;

    private final PetService petService = new PetService();
    private final AdoptionService adoptionService = new AdoptionService();
    private final AdopterService adopterService = new AdopterService();

    private List<Pet> allPets = new ArrayList<>();
    private String adopterId;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize filter options
        speciesFilter.getItems().addAll("All", "Dog", "Cat", "Bird", "Other");
        speciesFilter.setValue("All");

        ageFilter.getItems().addAll("All", "Baby", "Young", "Adult", "Senior");
        ageFilter.setValue("All");

        genderFilter.getItems().addAll("All", "Male", "Female");
        genderFilter.setValue("All");

        speciesFilter.setOnAction(e -> applyFilters());
        breedFilter.setOnAction(e -> applyFilters());
        ageFilter.setOnAction(e -> applyFilters());
        genderFilter.setOnAction(e -> applyFilters());

        speciesFilter.setOnAction(e -> {
            updateBreedOptions();
            applyFilters();
        });

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

        refreshPets();
    }


    public void setAdopterId(String adopterId) {
        this.adopterId = adopterId;
    }


    private void updateBreedOptions() {
        breedFilter.getItems().clear();
        breedFilter.getItems().add("All");

        String selectedSpecies = speciesFilter.getValue();
        if (!"All".equals(selectedSpecies)) {
            Set<String> breeds = allPets.stream()
                    .filter(pet -> selectedSpecies.equals(pet.getSpecies()))
                    .map(Pet::getBreed)
                    .collect(Collectors.toSet());

            breedFilter.getItems().addAll(breeds);
        }

        breedFilter.setValue("All");
    }


    @FXML
    private void handleRefresh() {
        refreshPets();
    }


    @FXML
    private void handleResetFilters() {
        speciesFilter.setValue("All");
        breedFilter.setValue("All");
        ageFilter.setValue("All");
        genderFilter.setValue("All");
        searchField.clear();
        applyFilters();
    }


    @FXML
    private void handleSearch() {
        applyFilters();
    }


    private void refreshPets() {
        allPets = petService.getAvailablePets();

        updateBreedOptions();

        applyFilters();
    }


    private void applyFilters() {
        String species = speciesFilter.getValue();
        String breed = breedFilter.getValue();
        String age = ageFilter.getValue();
        String gender = genderFilter.getValue();
        String searchTerm = searchField.getText().toLowerCase();

        List<Pet> filteredPets = allPets.stream()
                .filter(pet -> "All".equals(species) || species.equals(pet.getSpecies()))
                .filter(pet -> "All".equals(breed) ||
                        (breed != null && pet.getBreed() != null && breed.equals(pet.getBreed())))
                .filter(pet -> filterByAgeGroup(pet, age))
                .filter(pet -> "All".equals(gender) || gender.equals(pet.getGender()))
                .filter(pet -> searchTerm.isEmpty() ||
                        (pet.getName() != null && pet.getName().toLowerCase().contains(searchTerm)) ||
                        (pet.getBreed() != null && pet.getBreed().toLowerCase().contains(searchTerm)))
                .collect(Collectors.toList());

        displayPets(filteredPets);

        resultCountLabel.setText("Showing " + filteredPets.size() + " of " + allPets.size() + " pets");
    }


    private boolean filterByAgeGroup(Pet pet, String ageGroup) {
        if ("All".equals(ageGroup)) return true;

        int age = pet.getAge();

        switch (ageGroup) {
            case "Baby": return age <= 1;
            case "Young": return age > 1 && age <= 3;
            case "Adult": return age > 3 && age <= 8;
            case "Senior": return age > 8;
            default: return true;
        }
    }


    private void displayPets(List<Pet> pets) {
        petTilePane.getChildren().clear();

        for (Pet pet : pets) {
            petTilePane.getChildren().add(createPetCard(pet));
        }
    }


    private VBox createPetCard(Pet pet) {
        VBox card = new VBox(10);
        card.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 10; -fx-background-color: white;");
        card.setPrefWidth(280);
        card.setAlignment(Pos.CENTER);

        ImageView imageView = createPetImage(pet);
        imageView.setFitWidth(250);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);

        Label nameLabel = new Label(pet.getName());
        nameLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        Label statusLabel = new Label(pet.getStatus());
        if ("Available".equals(pet.getStatus())) {
            statusLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
        } else if ("Pending".equals(pet.getStatus())) {
            statusLabel.setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold;");
        } else {
            statusLabel.setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
        }

        VBox detailsBox = new VBox(5);
        detailsBox.setAlignment(Pos.CENTER_LEFT);

        Label speciesBreedLabel = new Label(pet.getSpecies() + " - " + pet.getBreed());
        Label ageGenderLabel = new Label(pet.getAge() + " years old - " + pet.getGender());

        detailsBox.getChildren().addAll(speciesBreedLabel, ageGenderLabel, statusLabel);

        Button viewDetailsBtn = new Button("View Details");
        viewDetailsBtn.setMaxWidth(Double.MAX_VALUE);
        viewDetailsBtn.setOnAction(e -> showPetDetails(pet));

        Button adoptBtn = new Button("Adopt Me");
        adoptBtn.setMaxWidth(Double.MAX_VALUE);
        adoptBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        if (!"Available".equals(pet.getStatus())) {
            adoptBtn.setDisable(true);
            adoptBtn.setText(pet.getStatus());
        }

        adoptBtn.setOnAction(e -> initiateAdoption(pet));

        HBox buttonBox = new HBox(10, viewDetailsBtn, adoptBtn);
        buttonBox.setAlignment(Pos.CENTER);

        card.getChildren().addAll(imageView, nameLabel, detailsBox, buttonBox);

        return card;
    }


    private ImageView createPetImage(Pet pet) {
        String imagePath;

        if ("Dog".equalsIgnoreCase(pet.getSpecies())) {
            imagePath = "/images/dog_default.jpg";
        } else if ("Cat".equalsIgnoreCase(pet.getSpecies())) {
            imagePath = "/images/cat_default.jpg";
        } else if ("Bird".equalsIgnoreCase(pet.getSpecies())) {
            imagePath = "/images/bird_default.jpg";
        } else {
            imagePath = "/images/pet_default.jpg";
        }

        InputStream is = getClass().getResourceAsStream(imagePath);
        Image image;
        if (is != null) {
            image = new Image(is);
        } else {
            image = null;
        }

        ImageView imageView = new ImageView();
        if (image != null) {
            imageView.setImage(image);
        } else {
            imageView.setStyle("-fx-background-color: #f0f0f0;");
        }

        return imageView;
    }


    private void showPetDetails(Pet pet) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Pet Details");
        alert.setHeaderText(pet.getName() + " - " + pet.getBreed() + " " + pet.getSpecies());

        VBox content = new VBox(10);

        Label ageLabel = new Label("Age: " + pet.getAge() + " years");
        Label genderLabel = new Label("Gender: " + pet.getGender());
        Label statusLabel = new Label("Status: " + pet.getStatus());
        statusLabel.setStyle("-fx-font-weight: bold;");

        switch (pet.getStatus()) {
            case "Available":
                statusLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                break;
            case "Pending":
                statusLabel.setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold;");
                break;
            case "Adopted":
                statusLabel.setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
                break;
        }

        content.getChildren().addAll(ageLabel, genderLabel, statusLabel);

        if ("Available".equals(pet.getStatus())) {
            Button adoptButton = new Button("Request Adoption");
            adoptButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            adoptButton.setOnAction(e -> {
                alert.close();
                initiateAdoption(pet);
            });
            content.getChildren().add(adoptButton);
        }

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setContent(content);
        dialogPane.setPrefWidth(400);

        alert.showAndWait();
    }


    private void initiateAdoption(Pet pet) {
        // Check if pet is available
        if (!"Available".equals(pet.getStatus())) {
            AlertHelper.showWarning("Pet Not Available",
                    "Sorry, " + pet.getName() + " is currently " + pet.getStatus().toLowerCase() +
                            " and cannot be adopted at this time.");
            return;
        }

        if (adopterId == null || adopterId.isEmpty()) {
            AlertHelper.showError("Error", "Your adopter information could not be found. Please try logging in again.");
            return;
        }

        Adopter adopter = adopterService.getAdopter(adopterId);
        if (adopter == null) {
            AlertHelper.showError("Error", "Your adopter profile could not be loaded. Please try again.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Adopt " + pet.getName());
        confirmation.setHeaderText("Would you like to submit an adoption request for " + pet.getName() + "?");
        confirmation.setContentText("This will create an adoption application that shelter staff will review.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Adoption adoption = adoptionService.createAdoptionRequest(pet.getId(), adopter.getId());

            if (adoption != null) {
                AlertHelper.showInformation("Adoption Request Submitted",
                        "Your adoption request has been submitted! The shelter will review your application and contact you soon.");

                refreshPets();
            } else {
                AlertHelper.showError("Error",
                        "Failed to submit adoption request. Please try again later.");
            }
        }
    }


    public Button getBackButton() {
        return backButton;
    }
}