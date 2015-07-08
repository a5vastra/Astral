package test;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import addons.PointSystem;
import addons.QueueSystem;
import main.MyBot;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
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
		List<PointSystem.PointAccount> list;
		list = pointSystem().getTopRanked(sliderValue);
		visualization(list);
		list = pointSystem().getTopRanked(-1);
		main(list);
		settings(pointSystem().getSettings());
	}
	
	public static class _PointAccount
	{
		private final StringProperty name;
		private final StringProperty rank;
		private final StringProperty points;
		private final StringProperty delta;
		private _PointAccount(String n, String r, String p, String d)
		{
			name  = new SimpleStringProperty(n);
			rank  = new SimpleStringProperty(r);
			points= new SimpleStringProperty(p);
			delta = new SimpleStringProperty(d);
		}
		public String getName(){return name.get();}
		public void setName(String val){name.set(val);}
		public String getRank(){return rank.get();}
		public void setRank(String val){rank.set(val);}
		public String getPoints(){return points.get();}
		public void setPoints(String val){points.set(val);}
		public String getDelta(){return delta.get();}
		public void setDelta(String val){delta.set(val);}
	}
	
	private void main(List<PointSystem.PointAccount> list)
	{
		tblMain.getItems().clear();
		
		final ObservableList<_PointAccount> data = FXCollections.observableArrayList();
		for(PointSystem.PointAccount pa : list)
			data.add(new _PointAccount(pa.getName(), (list.indexOf(pa)+1)+"", pa.getPoints()+"", (pa.getPoints()-pa.initPoints)+""));

		tblMain.setItems(data);
	}
	
	public static class _Setting
	{
		private final StringProperty key;
		private final StringProperty value;
		private _Setting(String k, String v)
		{
			key = new SimpleStringProperty(k);
			value = new SimpleStringProperty(v);
		}
		public String getKey(){return key.get();}
		public void setKey(String val){key.set(val);}
		public String getValue(){return value.get();}
		public void setValue(String val){value.set(val);}
	}
	
	private void settings(HashMap<String, String> map)
	{
		if(map == null || map.isEmpty())
			return;
		
		tblSettings.getItems().clear();
		
		final ObservableList<_Setting> data = FXCollections.observableArrayList();
		for(Entry<String, String> e : map.entrySet())
			data.add(new _Setting(e.getKey(), e.getValue()));

		tblSettings.setItems(data);
	}
	
	XYChart.Series seriesViewers = new XYChart.Series();
	private void visualization(List<PointSystem.PointAccount> list)
	{
		seriesViewers.getData().clear();		
		for(PointSystem.PointAccount pa : list)
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
	private TableColumn colMainDelta;
	@FXML
	private TableColumn colSettingsSetting;
	@FXML
	private TableColumn colSettingsValue;
	@FXML
	private Slider sldrVisualization;
	@FXML
	private TableView tblMain;
	@FXML 
	private TableView tblSettings;
	@FXML 
	private Label lblVisualization;
	@Override	
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;	
		
		sldrVisualization.valueProperty().addListener((observable, oldValue, newValue) -> 
		{
		    sliderValue = newValue.intValue(); 
		    lblVisualization.setText("Top "+sliderValue+" Viewers (Points)");
		});
		
		colMainName.setCellValueFactory(
				new PropertyValueFactory<_PointAccount,String>("name"));
		colMainRank.setCellValueFactory(
				new PropertyValueFactory<_PointAccount,String>("rank"));
		colMainPoints.setCellValueFactory(
				new PropertyValueFactory<_PointAccount,String>("points"));
		colMainPoints.setCellFactory(TextFieldTableCell.forTableColumn());
		colMainPoints.setOnEditCommit(
				new EventHandler<CellEditEvent<_PointAccount, String>>() {
					@Override
					public void handle(CellEditEvent<_PointAccount, String> t) {
						try
						{
							int val = Integer.parseInt(t.getNewValue());
							String name = t.getRowValue().getName();
							pointSystem().setPointsTo(name, val);
						}
						catch(NumberFormatException e)
						{
							t.consume();
						}
					}
				}
				);
		colMainDelta.setCellValueFactory(
				new PropertyValueFactory<_PointAccount,String>("delta"));
		
		
		
		colSettingsSetting.setCellValueFactory(
				new PropertyValueFactory<_Setting,String>("key"));
		colSettingsValue.setCellValueFactory(
				new PropertyValueFactory<_Setting,String>("value"));
		colSettingsValue.setCellFactory(TextFieldTableCell.forTableColumn());
		colSettingsValue.setOnEditCommit(
				new EventHandler<CellEditEvent<_Setting, String>>() {
					@Override
					public void handle(CellEditEvent<_Setting, String> t) {
						String val = t.getNewValue();
						String name = t.getRowValue().getKey();
						pointSystem().setSettingTo(name, val+"");
					}
				}
				);
	}	
}