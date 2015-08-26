package test;
import helpers._StringToString;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import test.CommandWindow._Command;
import addons.GreeterSystem;
import addons.PointSystem;
import addons.QueueSystem;
import addons.Addon.ADDONS;
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

public class GreeterWindow extends BorderPane implements javafx.fxml.Initializable {
	private static GreeterWindow instance;
	public static GreeterWindow getInstance(){return instance;}
	private GreeterSystem system;
	public GreeterSystem greeterSystem()
	{ 
		if(system == null) 
			system = (GreeterSystem)MyBot.instance.getAddon(ADDONS.Greeter);
		return system; 
	}
	public GreeterWindow()
	{
		 
	}
	@Override	
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;
	}	
	
	@FXML
	TableView tblGreetingGroups;
	@FXML
	TableView tblUserGroups;
	@FXML
	TableColumn tblColGreetingGroups;
	@FXML
	TableColumn tblColGreetingID;
	@FXML
	TableColumn tblColUserUsername;
	@FXML
	TableColumn tblColUserID;
	@FXML
	void OnUserAdd()
	{
		
	}
	@FXML
	void OnUserRemove()
	{
		if(tblUserGroups.getSelectionModel() != null)
		{
			_StringToString s = (_StringToString)tblUserGroups.getSelectionModel().getSelectedItem();
			if(s==null)
				return;

			MyBot.message(String.format("!greeterremove %s %s", GreeterSystem.MyInformation.UserGroup.name(), s.getKey()));
		}
	}
	@FXML
	void OnGreetingAdd()
	{
		
	}
	@FXML
	void OnGreetingRemove()
	{
		if(tblGreetingGroups.getSelectionModel() != null)
		{
			_StringToString s = (_StringToString)tblGreetingGroups.getSelectionModel().getSelectedItem();
			if(s==null)
				return;
			
			MyBot.message(String.format("!greeterremove %s %s", GreeterSystem.MyInformation.GreetingGroup.name(), s.getKey()));
		}
	}
	public static void forceRefresh(){
		if(instance != null)
			instance.refresh();
	}
	private void refresh()
	{		
		
	}
}