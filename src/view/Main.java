package view;

import configuration.Configuration;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Öffnet die Start View
 * 
 * @author Daniel Sogl
 *
 */

public class Main extends Application {
	/**
	 * Lade die FXML Datei und öffne diese
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("StartView.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setTitle("Vanilla Sky");
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("VanillaSky.png")));
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch (Exception e) {
			if (Configuration.DEBUG)
				System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}
