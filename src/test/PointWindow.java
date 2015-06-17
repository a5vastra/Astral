package test;
import java.net.URL;
import java.util.ResourceBundle;

import addons.PointSystem;
import addons.QueueSystem;
import main.MyBot;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

public class PointWindow extends BorderPane implements javafx.fxml.Initializable {
	private static PointWindow instance;
	public static PointWindow getInstance(){return instance;}
	private long counter = 0;
	private PointSystem pointSystem;
	public PointSystem pointSystem()
	{ 
		if(pointSystem == null) 
			pointSystem = (PointSystem)MyBot.instance.getAddon("PointSystem");
		return pointSystem; 
	}
	public PointWindow()
	{
		final Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1000),
				new EventHandler<ActionEvent>()
				{
			@Override
			public void handle(ActionEvent event) {
				if(counter%30 == 0)
				{
					refresh();
				}
				counter++;
			}
				}));
		timeline.setCycleCount(Animation.INDEFINITE);
		
		timeline.play();
		 
	}
	public static void forceRefresh(){
		if(instance != null)
			instance.refresh();
	}
	private int sliderValue = 1;
	private void refresh()
	{		
		visualization();
		
		
	}

	XYChart.Series seriesViewers = new XYChart.Series();
	private void visualization()
	{
		seriesViewers.getData().clear();		
		for(PointSystem.PointAccount pa : pointSystem().getTopRanked(sliderValue))
			seriesViewers.getData().add(new XYChart.Data<String, Number>(pa.getName(), pa.getPoints()));
		if(chtVisualization.getData().size() == 0)
			chtVisualization.getData().add(seriesViewers);			
	}
	
	@FXML
	private BarChart chtVisualization;
	@FXML
	private TableColumn colMainName;
	@FXML
	private TableColumn colMainPoints;
	@FXML
	private TableColumn colMainRank;
	@FXML
	private TableColumn colSettingsSetting;
	@FXML
	private TableColumn colSettingsValue;
	@FXML
	private Slider sldrVisualization;
	@FXML
	private TableView tblMain;
	@FXML 
	private TableView tblSetting;
	@FXML 
	private Label lblVisualization;
	@Override	
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;	
		
		sldrVisualization.valueProperty().addListener((observable, oldValue, newValue) -> {
		    sliderValue = newValue.intValue(); 
		    lblVisualization.setText("Top "+sliderValue+" Viewers (Points)");
		});
	}	
}