package addons;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import helpers.MiniTimer;
import main.MyBot;

public class ChatExtrasSystem extends Addon{
	int flag = 0;
	public ChatExtrasSystem()
	{
		myName = ADDONS.ChatExtra;
		miniTimer = new MiniTimer(this, "", 0, 15);
	}
	MiniTimer miniTimer;
	int messagesSinceReset = 0;
	public String attemptToModify(String msg)
	{
		if(msg.contains("`"))
			return msg.replace("`", "");
		
		if(++messagesSinceReset > 10)
			return "";
		
		do
		{
			flag = 0;
			msg = random(msg);
			msg = customPointName(msg);
			breakUpIntoNewLines(msg);
			if(flag > 10)
			{
				return "";
			}
		}while(flag != 0);
		return msg;
	}
	@Override
	public
	void doneWithTime(String msg)
	{
		messagesSinceReset = 0;
	}
	String random(String msg)
	{
		Pattern p;
		Matcher m;
		p = getOrRegisterPattern("random", "\\{ran (?<min>\\d+):(?<max>\\d+)\\}");
		m = p.matcher(msg);
		if(m.find())
		{
			int min = Integer.parseInt(m.group("min"));
			int max = Integer.parseInt(m.group("max"));
			Random r = new Random();
			msg = (msg.replace(m.group(), (r.nextInt(max-min+1)+min)+""));
			flag++;
		}
		return msg;
	}
	String customPointName(String msg)
	{
		String myName = ((PointSystem)MyBot.instance.getAddon(ADDONS.Point)).pointName();
		return msg.replace("point", myName);
	}
	void breakUpIntoNewLines(String msg)
	{
		if(msg.contains("$n"))
		{
			for(String s : msg.split("\\$n"))
				MyBot.message(s);
			flag += 100;
		}
	}
}
