package addons;

import test.ChatWindow;

import java.util.*;

public class GreeterSystem extends Addon{
	public GreeterSystem()
	{
		myName = ADDONS.Greeter;
	}
	public enum STATUS{ Joined, Left, LeftRecently, Unknown }
	HashMap<String, STATUS> map = new HashMap<String, STATUS>();
	@Override
	public void onJoin(String user)
	{
		if(map.containsKey(user))
		{
			switch(map.get(user))
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
			map.put(user, STATUS.Joined);
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
		if(map.containsKey(key) && map.get(key) == STATUS.LeftRecently)
		{
			map.put(key, STATUS.Left);
		}
	}
	
	
	void welcome(String user)
	{
		
	}
	void welcomeBack(String user)
	{
		
	}
}
