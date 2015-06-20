package test;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class BaseLauncher extends ApplicationLauncher {
	@Override
	public void start(Stage primaryStage) {
		Parent root;
		try {
			FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("BaseWindow.fxml"));
			root = fxmlloader.load();
			String image = this.getClass().getResource("download1.png").toExternalForm();
			root.setStyle("-fx-background-image: url('" + image + "'); " +
			           "-fx-background-position: center center; " +
			           "-fx-background-repeat: stretch;");
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		Scene scene = new Scene(root);
		primaryStage.initStyle(StageStyle.DECORATED);
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.show();
		primaryStage.setResizable(false);
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		    @Override
		    public void handle(WindowEvent event) {
		    	Platform.exit();
		        System.exit(0);
		    }
		});
	}

	public static void begin() {
		launch(new String[]{});
	}
	
	@Override
	public void secondary(){}
}