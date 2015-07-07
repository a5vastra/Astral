package main;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.List;

import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

import test.ChatWindow;
import addons.Addon;
import addons.ChatExtrasSystem;
import addons.ChatSystem;
import addons.CommandSystem;
import addons.PointSystem;
import addons.QueueSystem;


public class MyBot extends PircBot{
	public static MyBot instance;
	private TreeMap<String, Addon> addonHash = new TreeMap<String, Addon>();
	private static String ownerName; private static String botName; private static String botOperator;
	 public MyBot(String _ownerName, String _botName, String _botOperator)
	 {
		 ownerName = _ownerName;
		 botName = _botName;
		 botOperator = _botOperator;
		 this.setLogin(botName);
		 this.setName(botName);
		 this.setMessageDelay(0);
		 instance = this;
		 hash(new PointSystem());
		 hash(new CommandSystem());
		 hash(new ChatSystem());
		 hash(new QueueSystem());
		 hash(new ChatExtrasSystem());
		 directMessage("hello world");
	 }
	 private boolean messageFound = false;
	 public void messageFound(){ messageFound = true; System.out.println("Message matched!"); }
	 @Override
	 public void onMessage(String channel, String sender, String login, String hostname, String message)
	 {
		 onMessage(sender, message);
	 }
	 public boolean onMessage(String sender, String message)
	 {
		 messageFound = false;
		 System.out.println(message);
		 for(Addon a : addonHash.values())
		 {
			 if(messageFound)
				break;
			 //if(!a.getName().equals("ChatSystem"))
				 a.onMessage(sender, message);
		 }
		 messages ++;
		 return messageFound;
	 }
	 public User[] getUsers(){return getUsers("#"+ownerName);}
	 private int messages = 0;
	 public int getResetMessages(){int temp = messages; messages = 0; return temp;}
	 @Override
	 public void onJoin(String channel, String user, String login, String hostname)
	 {
		 for(Addon a : addonHash.values())
		 {
			 a.onJoin(user);
		 }
	 }
	 
	 @Override
	 public void onPart(String channel, String user, String login, String hostname)
	 {
		 for(Addon a : addonHash.values())
		 {
			 a.onExit(user);
		 }
	 }
	 
	 public void hash(Addon a)
	 {
		 addonHash.put(a.getName(), a);
	 }
	 public Addon getAddon(String name)
	 {
		 if(addonHash.containsKey(name))
			 return addonHash.get(name);
		 return null;
	 }
	 public static boolean isOwner(String user)
	 {
		 return user.equalsIgnoreCase(ownerName) || user.equalsIgnoreCase(botName) || user.equalsIgnoreCase(botOperator);
	 }
	 public static boolean isMod(String user)
	 {
		 return false;
	 }
	 public static boolean isFollower(String user)
	 {
		 return false;
	 }
	 public static void message(String msg)
	 {
		 if(instance == null) return;
		 
		 msg = ((ChatExtrasSystem)instance.getAddon("ChatExtrasSystem")).attemptToModify(msg);
		 
		 instance.sendMessage("#"+ownerName, msg);
		 instance.onMessage(botName, msg);
	 }
	 public static void directMessage(String msg)
	 {
		 if(instance == null) return;
		 instance.sendMessage("#"+ownerName, msg);
	 }
}

