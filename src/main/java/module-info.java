module org.example.petadoptionclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires junit;



    exports ui to javafx.graphics;
    opens ui to javafx.fxml;
    opens controller to javafx.fxml;
    opens service to com.google.gson;
    opens model to com.google.gson;
    opens controller.adopter to javafx.fxml;
    opens controller.staff to javafx.fxml;
    exports app to javafx.graphics;
    opens app to javafx.fxml;



}