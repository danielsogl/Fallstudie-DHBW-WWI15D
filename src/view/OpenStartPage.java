package view;

import configuration.Configuration;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Öffnet die Start View
 * @author Daniel Sogl
 *
 */
public class OpenStartPage extends Stage{
	
	private Stage stage;
	
	public OpenStartPage(){
		try{
			stage = this;
			Parent root = FXMLLoader.load(getClass().getResource("StartView.fxml"));
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("Vanilla Sky");
			stage.getIcons().add(new Image(getClass().getResourceAsStream("VanillaSky.png")));
			stage.setResizable(false);
			stage.show();
		} catch (Exception e){
			if(Configuration.DEBUG)
				System.out.println(e.getMessage());
			System.out.println("Fehler aufgetreten!");
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("Bitte straten Sie die Anwendung neu.");
			alert.showAndWait();
		}
	}

}
