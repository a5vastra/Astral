package test;
import java.net.URL;
import java.util.ResourceBundle;

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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

public class QueueWindow extends BorderPane implements javafx.fxml.Initializable {
	private static QueueWindow instance;
	public static QueueWindow getInstance(){return instance;}
	private long counter = 0;
	private QueueSystem queueSystem;
	public QueueSystem queueSystem()
	{ 
		if(queueSystem == null) 
			queueSystem = (QueueSystem)MyBot.instance.getAddon("QueueSystem");
		return queueSystem; 
	}
	public QueueWindow()
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
	private void refresh()
	{		
		
		listView.getItems().clear();
		for(String s : queueSystem().getQueue())
		{
			listView.getItems().add(s);
		}
		
		lblLength.setText("Length: "+listView.getItems().size());
		lblStatus.setText("Status: "+(queueSystem().getIsOpen()?"Open":"Closed"));
		btnOpenClose.setText(!queueSystem().getIsOpen()?"Open":"Close");
		
	}
	@FXML
	private Button btnNext;
	@FXML
	private Button btnOpenClose;
	@FXML
	private Label lblStatus;
	@FXML
	private Label lblLength;
	@FXML
	private ListView listView;

	@Override	
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;	
	}	
	@FXML
	private void button(ActionEvent e) 
	{
		System.out.println(e.getSource().equals(btnNext));
		if(e.getSource().equals(btnNext))
		{
			MyBot.message("!queuenext");
		}
		else if(e.getSource().equals(btnOpenClose))
		{
			MyBot.message("!queue"+(queueSystem().getIsOpen()?"close":"open"));
		}
	}
}