package test;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class SetupLauncher extends ApplicationLauncher {
	private static Stage stage;
	public static Stage getSetupWindowStage(){ return stage; }
	@Override
	public void start(Stage primaryStage) {
		Parent root;
		stage = primaryStage;
		try {
			FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("SetupWindow.fxml"));
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