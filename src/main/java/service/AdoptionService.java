package service;
import com.google.gson.reflect.TypeToken;
import model.Pet;
import model.Adoption;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdoptionService {
    private final ApiClient apiClient = new ApiClient();
    private final PetService petService = new PetService();


    public List<Adoption> getAllAdoptions() {
        Type responseType = new TypeToken<Response<List<Adoption>>>(){}.getType();
        Response<List<Adoption>> response = apiClient.sendRequest("adoption/getAll", null, responseType);

        if (response.isSuccess() && response.getData() != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }


    public List<Adoption> getAdoptionsByStatus(String status) {
        Map<String, String> params = new HashMap<>();
        params.put("status", status);

        Type responseType = new TypeToken<Response<List<Adoption>>>(){}.getType();
        Response<List<Adoption>> response = apiClient.sendRequest("adoption/getByStatus", params, responseType);

        if (response.isSuccess() && response.getData() != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }


    public Adoption getAdoption(String id) {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);

        Type responseType = new TypeToken<Response<Adoption>>(){}.getType();
        Response<Adoption> response = apiClient.sendRequest("adoption/get", params, responseType);

        return response.isSuccess() ? response.getData() : null;
    }


    public Adoption createAdoptionRequest(String petId, String adopterId) {
        // First, check if the pet is available
        Pet pet = petService.getPet(petId);
        if (pet == null || !pet.isAvailable()) {
            return null; // Pet doesn't exist or is not available
        }

        Map<String, String> params = new HashMap<>();
        params.put("petId", petId);
        params.put("adopterId", adopterId);

        Type responseType = new TypeToken<Response<Adoption>>(){}.getType();
        Response<Adoption> response = apiClient.sendRequest("adoption/create", params, responseType);

        if (response.isSuccess() && response.getData() != null) {
            // Update the pet status to Pending
            pet.setStatus("Pending");
            petService.updatePet(pet);

            return response.getData();
        }

        return null;
    }


    public Adoption updateAdoptionStatus(String id, String status) {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        params.put("status", status);

        Type responseType = new TypeToken<Response<Adoption>>(){}.getType();
        Response<Adoption> response = apiClient.sendRequest("adoption/update", params, responseType);

        if (response.isSuccess() && response.getData() != null) {
            Adoption adoption = response.getData();
            Pet pet = petService.getPet(adoption.getPetId());

            if (pet != null) {
                // Update pet status based on adoption status
                if ("Approved".equals(status)) {
                    pet.setStatus("Pending");
                    petService.updatePet(pet);
                } else if ("Rejected".equals(status)) {
                    pet.setStatus("Available");
                    petService.updatePet(pet);
                }
            }

            return adoption;
        }

        return null;
    }


    public Adoption completeAdoption(String id) {
        // First get the adoption to check the pet
        Adoption adoption = getAdoption(id);
        if (adoption == null) {
            return null;
        }

        // Check if the pet is still pending (not already adopted)
        Pet pet = petService.getPet(adoption.getPetId());
        if (pet == null) {
            return null;
        }

        if ("Adopted".equals(pet.getStatus())) {
            return null; // Pet already adopted
        }

        Map<String, String> params = new HashMap<>();
        params.put("id", id);

        Type responseType = new TypeToken<Response<Adoption>>(){}.getType();
        Response<Adoption> response = apiClient.sendRequest("adoption/complete", params, responseType);

        if (response.isSuccess() && response.getData() != null) {
            // Update pet status to Adopted
            pet.setStatus("Adopted");
            petService.updatePet(pet);

            // Cancel other pending adoptions for this pet
            cancelOtherAdoptions(pet.getId(), id);

            return response.getData();
        }

        return null;
    }


    private void cancelOtherAdoptions(String petId, String completedAdoptionId) {
        // Get all adoptions for this pet
        List<Adoption> adoptions = getAllAdoptions();

        for (Adoption adoption : adoptions) {
            // Skip the completed adoption
            if (adoption.getId().equals(completedAdoptionId)) {
                continue;
            }

            // Only process adoptions for this pet
            if (adoption.getPetId().equals(petId)) {
                // Only cancel pending or approved adoptions
                if ("Pending".equals(adoption.getStatus()) || "Approved".equals(adoption.getStatus())) {
                    updateAdoptionStatus(adoption.getId(), "Rejected");
                }
            }
        }
    }
}