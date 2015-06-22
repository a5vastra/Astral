package test;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CommandLauncher extends ApplicationLauncher {
	Stage stage;
	@Override
	public void secondary()
	{
		if(ChatWindow.getInstance() != null)
		{
			stage.show();
			stage.setAlwaysOnTop(true);
			stage.setAlwaysOnTop(false);
			return;
		}
		
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource("CommandWindow.fxml"));
			String image = this.getClass().getResource("download1.png").toExternalForm();
			root.setStyle("-fx-background-image: url('" + image + "'); " +
			           "-fx-background-position: center center; " +
			           "-fx-background-repeat: stretch;");
			stage = new Stage();
			stage.initStyle(StageStyle.DECORATED);
			stage.setScene(new Scene(root));
			
			stage.show();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
	}
}