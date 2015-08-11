package test;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import addons.CommandSystem;
import addons.PointSystem;
import addons.CommandSystem.Command;
import main.MyBot;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;

public class CommandWindow extends BorderPane implements javafx.fxml.Initializable {
	private static CommandWindow instance;
	public static CommandWindow getInstance(){return instance;}
	private CommandSystem commandSystem;
	public CommandSystem commandSystem()
	{ 
		if(commandSystem == null) 
			commandSystem = (CommandSystem)MyBot.instance.getAddon("CommandSystem");
		return commandSystem; 
	}
	@FXML private TableColumn colMainIsRegex ;
	@FXML private ChoiceBox chcAddCommandAccessibility ;
	@FXML private TextField txtAddRegexMessage ;
	@FXML private TextField txtAddRegexRegex ;
	@FXML private TableColumn colMainKey ;
	@FXML private TableColumn colMainMessage ;
	@FXML private TableColumn colMainAccessibility ;
	@FXML private TableView tblMain ;
	@FXML private Label lblAddRegexMatch ;
	@FXML private TextField txtAddCommandCommand ;
	@FXML private Label lblAddRegexValid ;
	@FXML private Label lblAddRegexInvalid ;
	@FXML private TextField txtAddCommandMessage ;
	@FXML private ChoiceBox chcAddRegexAccessibility ;
	@FXML private Label lblAddRegexNotMatch ;

	@Override	
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;
		lblAddRegexMatch.setVisible(false);
		lblAddRegexNotMatch.setVisible(false);
		lblAddRegexValid.setVisible(false);
		lblAddRegexInvalid.setVisible(false);
		chcAddCommandAccessibility.getItems().add("all");
		chcAddCommandAccessibility.getItems().add("mod");
		chcAddCommandAccessibility.getItems().add("admin");
		chcAddRegexAccessibility.getItems().add("all");
		chcAddRegexAccessibility.getItems().add("mod");
		chcAddRegexAccessibility.getItems().add("admin");
		
		colMainIsRegex.setCellValueFactory(
				new PropertyValueFactory<_Command,String>("isRegex"));
		colMainKey.setCellValueFactory(
				new PropertyValueFactory<_Command,String>("key"));
		colMainMessage.setCellValueFactory(
				new PropertyValueFactory<_Command,String>("message"));
		colMainAccessibility.setCellValueFactory(
				new PropertyValueFactory<_Command,String>("accessibility"));
		colMainAccessibility.setCellFactory(TextFieldTableCell.forTableColumn());
		colMainAccessibility.setOnEditCommit(
				new EventHandler<CellEditEvent<_Command, String>>() {
					@Override
					public void handle(CellEditEvent<_Command, String> t) {
						try
						{
							
						}
						catch(NumberFormatException e)
						{
							t.consume();
						}
					}
				}
				);
		refresh();
	}	
	
	
	
	List<CommandSystem.Command> commands;
	public static void forceRefresh()
	{
		if(instance != null)
		{
			instance.refresh();
		}
	}
	public void refresh()
	{
		commands = commandSystem().getCommands();
		
		tblMain.getItems().clear();
		
		final ObservableList<_Command> data = FXCollections.observableArrayList();
		for(Command c : commands)
			data.add(new _Command(c));

		tblMain.setItems(data);
		
	}
	public static class _Command
	{
		private final StringProperty isRegex;
		private final StringProperty key;
		private final StringProperty message;
		private final StringProperty accessibility;
		private _Command(Command c)
		{
			isRegex       = new SimpleStringProperty(c.isRegex()+"");
			key           = new SimpleStringProperty(c.getCommandOrRegex());
			message       = new SimpleStringProperty(c.getMessage());
			accessibility = new SimpleStringProperty(c.getAccessibility());
		}
		public String getIsRegex(){return isRegex.get();}
		public void setIsRegex(String val){isRegex.set(val);}
		public String getKey(){return key.get();}
		public void setKey(String val){key.set(val);}
		public String getMessage(){return message.get();}
		public void setMessage(String val){message.set(val);}
		public String getAccessibility(){return accessibility.get();}
		public void setAccessibility(String val){accessibility.set(val);}
	}
	@FXML private void addCommand(){
		String command = txtAddCommandCommand.getText();
		String message = txtAddCommandMessage.getText();
		String accessibility = "all";
		if(chcAddCommandAccessibility.getValue() != null)
			accessibility = chcAddCommandAccessibility.getValue().toString();
		if((command + message).length() == 0) return;
		MyBot.message(
				String.format("!commandadd <command='%s'/> <message='%s'/> <accessibility='%s'/>",
						command, message, accessibility));
	}
	@FXML private void addRegex(){
		String regex = txtAddRegexRegex.getText();
		String message = txtAddRegexMessage.getText();
		String accessibility = "all";
		if((regex + message).length() == 0) return;
		if(chcAddRegexAccessibility.getValue() != null)
			accessibility = chcAddRegexAccessibility.getValue().toString();
		try
		{
			Pattern.compile(regex);
			Command c = ((CommandSystem)MyBot.instance.getAddon("CommandSystem")).new Command("<regex='"+regex+"'/> <message='"+message+"'/>");
			if(!c.checkCommandRegexForMatch(regex))
				throw new Exception();
		}
		catch(Exception e)
		{
			lblAddRegexValid.setVisible(false);
			lblAddRegexInvalid.setVisible(true);
			lblAddRegexInvalid.setTooltip(new Tooltip(e.getMessage()));
			return;
		}
		lblAddRegexValid.setVisible(true);
		lblAddRegexInvalid.setVisible(false);
		MyBot.message(
				String.format("!commandadd <regex='%s'/> <message='%s'/> <accessibility='%s'/>",
						regex, message, accessibility));
	}
	@FXML private void removeCommand()
	{
		if(tblMain.getSelectionModel() != null)
		{
			_Command c = (_Command)tblMain.getSelectionModel().getSelectedItem();
			if(c==null)
				return;
			
			MyBot.message(String.format("!commandremove %s", c.key.get()));
		}
				
	}
	@FXML private void tryOutRegex(ActionEvent e){
		try
		{
			Pattern p = Pattern.compile(txtAddRegexRegex.getText());
			Matcher m = p.matcher(((TextField)e.getSource()).getText());
			boolean b = m.find();
			lblAddRegexMatch.setVisible(b);
			lblAddRegexNotMatch.setVisible(!b);
		}
		catch(Exception ex)
		{
			lblAddRegexValid.setVisible(false);
			lblAddRegexInvalid.setVisible(true);
			lblAddRegexInvalid.setTooltip(new Tooltip(ex.getMessage()));
			return;
		}
		lblAddRegexValid.setVisible(true);
		lblAddRegexInvalid.setVisible(false);
	}

}