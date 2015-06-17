package test;
import java.net.URL;
import java.util.ResourceBundle;

import main.MyBot;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class ChatWindow extends BorderPane implements javafx.fxml.Initializable {
	private static ChatWindow instance;
	public static ChatWindow getInstance(){return instance;}
	public static void OnMessage(String sender, String message)
	{
		if(instance != null)
			instance.addMessage(sender, message);
	}
	@FXML
	private TextField txfInput;
	@FXML
	private Button btnEnter;
	@FXML
	private TextArea txaAllMessages;

	@Override	
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;	
	}	
	@FXML
	private void enterButton(ActionEvent e) 
	{
		String s = txfInput.getText();
		txfInput.clear();
		s = s.trim();
		if(s.length() != 0)
		{
			//addMessage(MyBot.instance.getNick(), s);
			MyBot.message(s);
		}
	}
	public void addMessage(String sender, String message)
	{
		String s = message;
		s = s.trim();
		if(s.length() != 0)
			txaAllMessages.setText(sender+" > "+s+"\n"+txaAllMessages.getText());
	}
}