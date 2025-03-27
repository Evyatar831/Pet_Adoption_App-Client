package service;
import com.google.gson.reflect.TypeToken;
import model.Adopter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdopterService {
    private final ApiClient apiClient = new ApiClient();


    public List<Adopter> getAllAdopters() {
        Type responseType = new TypeToken<Response<List<Adopter>>>(){}.getType();
        Response<List<Adopter>> response = apiClient.sendRequest("adopter/getAll", null, responseType);

        if (response.isSuccess() && response.getData() != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }


    public Adopter getAdopter(String id) {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);

        Type responseType = new TypeToken<Response<Adopter>>(){}.getType();
        Response<Adopter> response = apiClient.sendRequest("adopter/get", params, responseType);

        return response.isSuccess() ? response.getData() : null;
    }


    public boolean addAdopter(Adopter adopter) {
        Type responseType = new TypeToken<Response<Adopter>>(){}.getType();
        Response<Adopter> response = apiClient.sendRequest("adopter/add", adopter, responseType);

        return response.isSuccess();
    }


    public boolean updateAdopter(Adopter adopter) {
        Type responseType = new TypeToken<Response<Adopter>>(){}.getType();
        Response<Adopter> response = apiClient.sendRequest("adopter/update", adopter, responseType);

        return response.isSuccess();
    }


    public boolean removeAdopter(String id) {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);

        Type responseType = new TypeToken<Response<Boolean>>(){}.getType();
        Response<Boolean> response = apiClient.sendRequest("adopter/delete", params, responseType);

        return response.isSuccess();
    }
}
