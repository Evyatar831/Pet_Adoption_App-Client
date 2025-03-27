package app;

import ui.WelcomeScreen;
import javafx.application.Application;
import javafx.stage.Stage;



public class PetAdoptionClientApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        WelcomeScreen welcomeScreen = new WelcomeScreen();
        welcomeScreen.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }


}