package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


public class ApiClient {
    public static String SERVER_HOST = "localhost";
    public static int SERVER_PORT = 34567;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT)
            .create();


    public <T> Response<T> sendRequest(String action, Object body, Type responseType) {
        System.out.println("Sending request to server: " + action);

        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            Map<String, String> headers = new HashMap<>();
            headers.put("action", action);
            Request<Object> request = new Request<>(headers, body);

            String requestJson = gson.toJson(request);
            System.out.println("Request payload: " + requestJson);
            writer.println(requestJson);

            String responseJson = reader.readLine();
            System.out.println("Response received: " + responseJson);

            return gson.fromJson(responseJson, responseType);

        } catch (Exception e) {
            System.err.println("ERROR in API request: " + e.getMessage());
            e.printStackTrace();
            return Response.error("Error connecting to server: " + e.getMessage());
        }
    }

    public ApiClient() {
    }

    public ApiClient(String host, int port) {
        SERVER_HOST = host;
        SERVER_PORT = port;
    }

    public static void configure(String host, int port) {
        SERVER_HOST = host;
        SERVER_PORT = port;
    }

    private static class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.toString());
            }
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return LocalDateTime.parse(in.nextString());
        }
    }
}