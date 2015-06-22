package addons;

import helpers.FileManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import test.CommandWindow;
import main.MyBot;

public class CommandSystem extends Addon{
	List<Command> commands = new ArrayList<Command>();
	public List<Command> getCommands(){return new ArrayList<Command>(commands);}
	public CommandSystem()
	{
		myName = "CommandSystem";
		Load();
		//commands.add(new Command(""));
		//commands.add(new Command("<command='!test1'/><message='this is a test'/>"));
		//commands.add(new Command("<regex='^\\+\\d+$'/><message='this is another test'/>"));
		Save();
	}
	@Override
	public void Load()
	{
		FileManager fm = new FileManager(getName());
		map = fm.Load(getName());
		settings = mapGet(MyInformation.Commands.name());
		{
			for(Entry<String, String> e : mapGet(MyInformation.Commands.name()).entrySet())
			{
				Command c = new Command(e.getValue());
				commands.add(c);
			}
		}
	}
	@Override
	public void Save()
	{
		FileManager fm = new FileManager(getName());
		{
			HashMap<String, String> commandsHash = new HashMap<String, String>();
			int i = 0;
			for(Command c : commands)
			{
				commandsHash.put("_"+(i++)+"", c.toString());
			}
			map.put(MyInformation.Commands.name(), commandsHash);
		}
		fm.Create(getName(), map);
	}
	@Override
	public void onMsg(String sender, String message)
	{
		boolean changeMade = false;
		if(MyBot.isOwner(sender))
		{
			Pattern p;
			Matcher m;
			p = getOrRegisterPattern("addcommand", "^!commandadd (?<command>.+)$");
			m = p.matcher(message);
			if(find(m))
			{
				Command c = new Command(m.group("command"));
				if(c.isValid())
				{
					commands.add(c);
					changeMade = true;
					msg("New command created: "+c.toString());
				}
				else
				{
					msg("Invalid command entered, try again");
				}
			}
			p = getOrRegisterPattern("removecommand", "^!command(remove|delete) (?<command>.+)$");
			m = p.matcher(message);
			if(find(m))
			{
				Command toRemove = null;
				for(Command c : commands)
				{
					if(c.getCommandOrRegex().equals(m.group("command")))
					{
						toRemove = c;
						break;
					}
				}
				if(toRemove != null)
				{
					commands.remove(toRemove);
					changeMade = true;
				}
				else
				{
					msg("Could not find that command, check your spelling and try again?");
				}
			}
			p = getOrRegisterPattern("showall", "^!command(s|)showall$");
			m = p.matcher(message);
			if(find(m))
			{
				String s = "All commands: ";
				for(Command c : commands)
				{
					s += "{"+c.toString()+"}     ";
				}
				msg(s);
			}
		}
		for(Command c : commands)
		{
			if(c.isMatch(sender, message))
			{
				msg(c.message);
			}
		}
		if(changeMade)
		{
			Save();
			CommandWindow.forceRefresh();
		}
	}
	public class Command
	{
		private String command = "";
		private String message = "";
		private String regex = "";
		private String accessibility = "all";
		public boolean isRegex(){ return !regex.equals(""); }
		public String getMessage(){ return message; }
		public String getAccessibility(){ return accessibility; }
		protected Command(String input)
		{
			Pattern p;
			Matcher m;
			p = Pattern.compile("<command='(?<command>.+?)'/>");
			m = p.matcher(input);
			if(m.find())
			{
				command = m.group("command");
			}
			p = Pattern.compile("<message='(?<message>.+?)'/>");
			m = p.matcher(input);
			if(m.find())
			{
				message = m.group("message");
			}
			p = Pattern.compile("<regex='(?<regex>.+?)'/>");
			m = p.matcher(input);
			if(m.find())
			{
				regex = m.group("regex");
				try
				{
					pattern = Pattern.compile(regex);
				}
				catch(PatternSyntaxException pme)
				{
					error("Invalid regex attempt: '"+regex+"'");
				}
			}
			p = Pattern.compile("<accessibility='(?<accessibility>all|mod|admin)'/>");
			m = p.matcher(input);
			if(m.find())
			{
				accessibility = m.group("accessibility");
			}
		}
		protected boolean isValid()
		{
			return (!command.equals("") || !regex.equals("")) && (!message.equals(""));
		}
		Pattern pattern;
		protected boolean isMatch(String sender, String matchTo)
		{
			if(!isValid())
				return false;
			switch(accessibility)
			{
			case "all":
				break;
			case "mod":
				if(!MyBot.isMod(sender))
					return false;
				break;
			case "admin":
				if(!MyBot.isOwner(sender))
					return false;
				break;
			}
			if(command != "")
			{
				return check(command.equalsIgnoreCase(matchTo));
			}
			if(regex != "")
			{
				Matcher m = pattern.matcher(matchTo);
				return find(m);
			}
			return false;
		}
		public String getCommandOrRegex()
		{
			if(isValid())
				return command.equals("")?regex:command;
			else
				return "";
		}
		@Override
		public String toString()
		{
			String s = "";
			if(!command.equals(""))
				s += "<command='"+command+"'/> ";
			if(!message.equals(""))
				s += "<message='"+message+"'/> ";
			if(!regex.equals(""))
				s += "<regex='"+regex+"'/> ";
			s += "<accessibility='"+accessibility+"'/> ";
			return (s);
		}
	}
	enum MyTasks
	{
		
	}
	enum MyInformation
	{
		Commands
	}
}
