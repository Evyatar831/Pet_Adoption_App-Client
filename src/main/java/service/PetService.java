package service;
import com.google.gson.reflect.TypeToken;
import model.Pet;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class PetService {
    private final ApiClient apiClient = new ApiClient();


    public List<Pet> getAllPets() {
        Type responseType = new TypeToken<Response<List<Pet>>>(){}.getType();
        Response<List<Pet>> response = apiClient.sendRequest("pet/getAll", null, responseType);

        if (response.isSuccess() && response.getData() != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }


    public List<Pet> getAvailablePets() {
        List<Pet> allPets = getAllPets();
        return allPets.stream()
                .filter(pet -> !"Adopted".equals(pet.getStatus()))
                .collect(Collectors.toList());
    }


    public Pet getPet(String id) {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);

        Type responseType = new TypeToken<Response<Pet>>(){}.getType();
        Response<Pet> response = apiClient.sendRequest("pet/get", params, responseType);

        return response.isSuccess() ? response.getData() : null;
    }


    public boolean addPet(Pet pet) {
        // Ensure new pets have Available status
        if (pet.getStatus() == null) {
            pet.setStatus("Available");
        }

        Type responseType = new TypeToken<Response<Pet>>(){}.getType();
        Response<Pet> response = apiClient.sendRequest("pet/add", pet, responseType);

        return response.isSuccess();
    }


    public boolean updatePet(Pet pet) {
        Type responseType = new TypeToken<Response<Pet>>(){}.getType();
        Response<Pet> response = apiClient.sendRequest("pet/update", pet, responseType);

        return response.isSuccess();
    }


    public boolean removePet(String id) {
        System.out.println("Client: Removing pet with ID: " + id);

        Map<String, String> params = new HashMap<>();
        params.put("id", id);

        try {
            Type responseType = new TypeToken<Response<Boolean>>(){}.getType();
            Response<Boolean> response = apiClient.sendRequest("pet/delete", params, responseType);

            if (!response.isSuccess()) {
                System.err.println("Server returned error status: " + response.getMessage());
                return false;
            }

            // Check the actual data value which indicates operation success
            Boolean result = response.getData();
            if (result == null || !result) {
                System.err.println("Server indicated deletion operation failed");
                return false;
            }

            return true;
        } catch (Exception e) {
            System.err.println("Exception during remove pet request: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public List<Pet> findMatches(String adopterId, int limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("adopterId", adopterId);
        params.put("limit", limit);

        Type responseType = new TypeToken<Response<List<Pet>>>(){}.getType();
        Response<List<Pet>> response = apiClient.sendRequest("pet/match", params, responseType);

        if (response.isSuccess() && response.getData() != null) {
            // Filter out adopted pets from the results
            return response.getData()
                    .stream()
                    .filter(pet -> !"Adopted".equals(pet.getStatus()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}