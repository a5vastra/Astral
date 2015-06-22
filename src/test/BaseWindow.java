package test;
import helpers.MiniTimer;

import java.net.URL;
import java.util.HashMap;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import addons.Addon;
import main.MyBot;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.chart.*;
import javafx.util.Duration;
import javafx.animation.AnimationTimer;
import javafx.application.Application;

public class BaseWindow extends BorderPane implements javafx.fxml.Initializable {
	private static BaseWindow instance;
	private HashMap<Integer, ApplicationLauncher> launcherMap = new HashMap<Integer, ApplicationLauncher>();
	private long counter = 0;
	public BaseWindow()
	{
		instance = this;
		
		final Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1000),
				new EventHandler<ActionEvent>()
				{
			@Override
				public void handle(ActionEvent event) {
					if(counter%10 == 0)
					{
						updateViewers(counter);
						updateMessages(counter);
					}
					counter++;
				}
				}));
		timeline.setCycleCount(Animation.INDEFINITE);
		
		timeline.play();
		
		
	}
	public static void OnMessage(String sender, String message)
	{
		
	}
	@FXML
	private CheckBox chkSystem1;
	@FXML
	private Button btnSystem1;
	@FXML
	private CheckBox chkSystem2;
	@FXML
	private Button btnSystem2;
	@FXML
	private CheckBox chkSystem3;
	@FXML
	private Button btnSystem3;
	@FXML
	private CheckBox chkSystem4;
	@FXML
	private Button btnSystem4;
	@FXML
	private CheckBox chkSystem5;
	@FXML
	private Button btnSystem5;
	@FXML
	private CheckBox chkSystem6;
	@FXML
	private Button btnSystem6;
	@FXML
	private CheckBox chkSystem7;
	@FXML
	private Button btnSystem7;
	@FXML
	private CheckBox chkSystem8;
	@FXML
	private Button btnSystem8;
	@FXML
	private LineChart chrtViewers;
	@FXML
	private LineChart chrtMessages;
	
	@Override	
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;
		
		{
			register(1, "ChatSystem", new ChatLauncher());
			register(2, "QueueSystem", new QueueLauncher());
			register(3, "PointSystem", new PointLauncher());
			register(4, "CommandSystem", new CommandLauncher());
			for(int i = 5; i <= 8; i++)
				register(i, "", null);
		}
		
	}	
	private void register(Integer index, String name, ApplicationLauncher launcher)
	{
		if(launcher == null)
		{
			numberToButton(index).setVisible(false);
			numberToCheckBox(index).setVisible(false);
		}
		else
		{
			numberToCheckBox(index).setText(name);
			numberToCheckBox(index).setSelected(MyBot.instance.getAddon(name).enabled());
			launcherMap.put(index, launcher);
		}
	}
	private Integer buttonToNumber(Button b)
	{
		Integer num = 0;
		if(b == btnSystem1)
			num = 1;
		else if (b == btnSystem2)
			num = 2;
		else if (b == btnSystem3)
			num = 3;
		else if (b == btnSystem4)
			num = 4;
		else if (b == btnSystem5)
			num = 5;
		else if (b == btnSystem6)
			num = 6;
		else if (b == btnSystem7)
			num = 7;
		else if (b == btnSystem8)
			num = 8;
		return num;
	}
	private Button numberToButton(Integer i)
	{
		switch(i)
		{
		case 1:
			return btnSystem1;
		case 2:
			return btnSystem2;
		case 3:
			return btnSystem3;
		case 4:
			return btnSystem4;
		case 5:
			return btnSystem5;
		case 6:
			return btnSystem6;
		case 7:
			return btnSystem7;
		case 8:
			return btnSystem8;
		}
		return null;
	}
	private Integer checkBoxToNumber(CheckBox c)
	{
		Integer num = 0;
		if(c == chkSystem1)
			num = 1;
		else if (c == chkSystem2)
			num = 2;
		else if (c == chkSystem3)
			num = 3;
		else if (c == chkSystem4)
			num = 4;
		else if (c == chkSystem5)
			num = 5;
		else if (c == chkSystem6)
			num = 6;
		else if (c == chkSystem7)
			num = 7;
		else if (c == chkSystem8)
			num = 8;
		return num;
	}
	private CheckBox numberToCheckBox(Integer i)
	{
		switch(i)
		{
		case 1:
			return chkSystem1;
		case 2:
			return chkSystem2;
		case 3:
			return chkSystem3;
		case 4:
			return chkSystem4;
		case 5:
			return chkSystem5;
		case 6:
			return chkSystem6;
		case 7:
			return chkSystem7;
		case 8:
			return chkSystem8;
		}
		return null;
	}
	@FXML
	private void system(ActionEvent e) 
	{
		Integer num = buttonToNumber((Button)e.getSource());
		if(launcherMap.containsKey(num))
		{
			launcherMap.get(num).secondary();
		}
	}
	@FXML
	private void enableDisable(ActionEvent e)
	{
		
	}
	XYChart.Series seriesViewers = new XYChart.Series();
	private void updateViewers(long iteration)
	{
		int nViewers = MyBot.instance.getUsers().length;
		seriesViewers.getData().add(new XYChart.Data<String, Number>(iteration+"", nViewers));
		if(chrtViewers.getData().size() == 0)
			chrtViewers.getData().add(seriesViewers);
		
	}
	XYChart.Series seriesMessages = new XYChart.Series();
	private void updateMessages(long iteration)
	{
		int nMessages = MyBot.instance.getResetMessages();
		seriesMessages.getData().add(new XYChart.Data<String, Number>(iteration+"", nMessages));
		if(chrtMessages.getData().size() == 0)
			chrtMessages.getData().add(seriesMessages);
	}
}