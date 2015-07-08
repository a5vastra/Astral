package test;
import helpers.FileManager;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import addons.QueueSystem;
import main.MyBot;
import main.Starter;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class SetupWindow extends BorderPane implements javafx.fxml.Initializable {
	@FXML AnchorPane P1;
	@FXML SplitPane P2;
	@FXML SplitPane P3;
	@FXML SplitPane P4;
	@FXML SplitPane P5;
	@FXML AnchorPane P6;
	@FXML ProgressBar Progress;
	@FXML Hyperlink linkGithub;
	@FXML Hyperlink linkSignup;
	@FXML Hyperlink linkOauth;
	@FXML StackPane stackPane;
	@FXML TextField fldBotName;
	@FXML TextField fldBotOperator;
	@FXML TextField fldBotOauth;
	@FXML TextField fldChannelOwner;
	@FXML TextArea finalPage;
	@Override	
	public void initialize(URL location, ResourceBundle resources) {
		//refresh();
		refresh();
	}	
	public SetupWindow()
	{
	}
	int stage = 1;
	@FXML private void next()
	{
		stage++;
		
		System.out.println("next");
		refresh();
	}
	@FXML private void prev()
	{
		if(stage > 1)
			stage--;
		System.out.println("prev");
		refresh();
	}
	private void refresh()
	{
		if(stackPane == null)
			return;
		stackPane.getChildren().clear();
		Node n = null;
		switch(stage)
		{
		case 1:n = ((Node)P1);break;
		case 2:n = ((Node)P2);break;
		case 3:n = ((Node)P3);break;
		case 4:n = ((Node)P4);break;
		case 5:n = ((Node)P5);break;
		case 6:n = ((Node)P6);
		finalPage.setText("Press next if the info is correct. Then reopen this .jar file, and if you've done everything right, it should boot up!\n\n"+
				"Channel Name:\n\t >"+fldChannelOwner.getText()+"\n"+
				"Bot Operator:\n\t >"+fldBotOperator.getText()+"\n"+
				"Bot Name:\n\t >"+fldBotName.getText()+"\n"+
				"Bot Oauth:\n\t >"+fldBotOauth.getText());
		break;
		case 7:
			Pattern p;
			Matcher m;
			String channelOwner = fldChannelOwner.getText();
			String botOperator = fldBotOperator.getText();
			String botName = fldBotName.getText();
			String botOauth = fldBotOauth.getText();
			boolean b = false;
			{
				p = Pattern.compile("\\w+");
				m = p.matcher(channelOwner);
				b |= !m.find();
				p = Pattern.compile("\\w+");
				m = p.matcher(botOperator);
				b |= !m.find();
				p = Pattern.compile("\\w+");
				m = p.matcher(botName);
				b |= !m.find();
				p = Pattern.compile("oauth:\\w+");
				m = p.matcher(botOauth);
				b |= !m.find();
			}
			if(b)
			{
				stage = 6;
				refresh();
				return;
			}
			else
			{
				HashMap map = new HashMap<String, HashMap<String, String>>();
				HashMap map2 = new HashMap<String, String>();
				map2.put("ChannelOwner", channelOwner);
				map2.put("BotOperator", botOperator);
				map2.put("BotName", botName);
				map2.put("BotOauth", botOauth);
				map.put("Settings", map2);
				new FileManager("Settings").Create("Settings", map);
				Platform.exit();
				System.exit(0);
			}
			return;
		
		default: return;
		}
		Progress.setProgress(stage/6f);
		System.out.println(stage+ " "+n);
		if(n != null)
		{
			stackPane.getChildren().add(n);
		}
		
	}
	@FXML private void HyperLink(ActionEvent e)
	{ 
		String url = "";
		url = ((Hyperlink)e.getSource()).getText();
		if(url.equals("here"))
			url = "http://twitchapps.com/tmi";
		
		try {
		    Desktop.getDesktop().browse(new URL(url).toURI());
		} catch (Exception ex) {ex.printStackTrace();}
	}
	
}