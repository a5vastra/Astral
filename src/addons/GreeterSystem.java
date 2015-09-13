package addons;

import test.ChatWindow;
import helpers.FileManager;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.MyBot;

public class GreeterSystem extends Addon{
	public GreeterSystem()
	{
		myName = ADDONS.Greeter;
	}
	public enum STATUS{ Joined, Left, LeftRecently, Unknown }
	//Format: USER_GROUP_1, { hey $u, welcome $u }
	HashMap<String, List<String>> greetingGroups= new HashMap<String, List<String>>();
	//Format: USER_GROUP_1, { a5vastra, jmangroup }
	HashMap<String, List<String>> userGroups =  new HashMap<String, List<String>>();
	HashMap<String, STATUS> statusMap = new HashMap<String, STATUS>();
	@Override
	public void Load()
	{
		FileManager fm = new FileManager(getSystemName());
		String miName;
		
		miName = MyInformation.GreetingGroup.name();
		map = fm.Load(getSystemName());
		settings = mapGet(miName);
		{
			for(Entry<String, String> e : settings.entrySet())
			{
				addGreeting(e.getValue().substring(1), e.getKey());
			}
		}
		
		miName = MyInformation.UserGroup.name();
		settings = mapGet(miName);
		{
			for(Entry<String, String> e : settings.entrySet())
			{
				addUser(e.getValue().substring(1), e.getKey());
			}
		}
		
	}
	@Override
	public void Save()
	{
		FileManager fm = new FileManager(getSystemName());
		String miName;
		
		miName = MyInformation.GreetingGroup.name();
		map = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> temp = new HashMap<String, String>();
		
		temp.clear();
		for(String e : greetingGroups.keySet())
		{
			for(String e2: greetingGroups.get(e))
			{
				temp.put(e, e2);
			}
		}
		map.put(miName, temp);
		
		miName = MyInformation.UserGroup.name();
		temp.clear();
		for(String e : userGroups.keySet())
		{
			for(String e2: userGroups.get(e))
			{
				temp.put(e, e2);
			}
		}
		map.put(miName, temp);
		
		
		fm.Create(getSystemName(), map);
	}
	@Override
	public void onJoin(String user)
	{
		if(statusMap.containsKey(user))
		{
			switch(statusMap.get(user))
			{
			case Left:
				welcomeBack(user);			
				break;
			case Joined:
				//ignore
				break;
			case LeftRecently:
				//ignore
				break;
			case Unknown:
				//ignore
				break;
			}
		}
		else
		{
			welcome(user);
			statusMap.put(user, STATUS.Joined);
		}
	}
	@Override
	public void onExit(String user)
	{
		addTimer(user, 5*60, 0);
	}
	@Override
	public void doneWithTime(String key)
	{
		if(statusMap.containsKey(key) && statusMap.get(key) == STATUS.LeftRecently)
		{
			statusMap.put(key, STATUS.Left);
		}
	}
	@Override
	public void onMsg(String user, String message)
	{
		boolean wasChanged = false;
		if(MyBot.instance.isOwner(user))
		{
			Pattern p;
			Matcher m;
			p = getOrRegisterPattern("GreeterAdd","^!greeter(?:add|modify) (?<type>"+MyInformation.UserGroup.name()+"|"+MyInformation.GreetingGroup.name()+") (?<key>\\w+) (?<val>.+)$");
			m = p.matcher(message);
			if(m.find())
			{
				wasChanged = true;
				String type, key, val;
				type = m.group("type");
				key  = m.group("key" );
				val  = m.group("val" );
				
				
				if(type.equalsIgnoreCase(MyInformation.GreetingGroup.name()))
				{
					addGreeting(key, val);
				}
				else if(type.equalsIgnoreCase(MyInformation.UserGroup.name()))
				{
					addUser(key, val);
				}
				else
				{
					return;
				}
				msg("Successfully changed "+type+": "+key+" = "+val);
			}
			
			 p = getOrRegisterPattern("GreeterRemove","^!greeter(remove) (?<type>"+MyInformation.UserGroup.name()+"|"+MyInformation.GreetingGroup.name()+") (?<key>\\w++)$");
			 m = p.matcher(message);
			 if(m.find())
			 {
				 wasChanged = true;
				 String type, key; 
				 type = m.group("type");
				 key  = m.group("key");
				 if(type.equalsIgnoreCase(MyInformation.GreetingGroup.name()))
					{
						removeGreeting(key);
					}
					else if(type.equalsIgnoreCase(MyInformation.UserGroup.name()))
					{
						removeUser(key);
					}
					else
					{
						return;
					}
					msg("Successfully removed "+type+": "+key);
			 }
			 
			 if(wasChanged)
			 {
				 Save();
			 }
		}
	}
	//key = name, val = groupID
	void addGreeting(String key, String val)
	{
		removeGreeting(key);
		if(!greetingGroups.containsKey(key))
			greetingGroups.put(key, new ArrayList<String>());
		greetingGroups.get(key).add(val);
	}
	void addUser(String key, String val)
	{
		removeUser(key);
		if(!userGroups.containsKey(key))
			userGroups.put(key, new ArrayList<String>());
		userGroups.get(key).add(val);
	}
	void removeGreeting(String key)
	{
		for(String e : greetingGroups.keySet())
			greetingGroups.get(e).remove(key);
	}
	void removeUser(String key)
	{
		for(String e : userGroups.keySet())
			userGroups.get(e).remove(key);
	}
	void welcome(String user)
	{
		String out = "";
		int count;
		final String EMPTY_KEY = "";
		Random r = new Random();
		
		String group = null;
		for(String userGroup : userGroups.keySet())
		{
			if(userGroups.get(userGroup).contains(user))
			{
				group = userGroup;
				break;
			}
		}
		
		if(group != null)
		{			
			count = greetingGroups.get(group).size();
			out = greetingGroups.get(group).get(r.nextInt(count));
		}
		else if(userGroups.containsKey(EMPTY_KEY))
		{
			count = greetingGroups.get(EMPTY_KEY).size();
			out = greetingGroups.get(EMPTY_KEY).get(r.nextInt(count));
		}
		msg(out);
	}
	void welcomeBack(String user)
	{
		
	}
	public enum MyInformation
	{
		UserGroup, GreetingGroup
	}
}
