package addons;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.MyBot;

public class ChatExtrasSystem extends Addon{
	int flag = 0;
	public ChatExtrasSystem()
	{
		myName = "ChatExtrasSystem";
	}
	public String attemptToModify(String msg)
	{
		if(msg.contains("`"))
			return msg.replace("`", "");
		do
		{
			flag = 0;
			msg = random(msg);
			msg = customPointName(msg);
			if(flag > 25)
			{
				return "";
			}
		}while(flag != 0);
		return msg;
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
		String myName = ((PointSystem)MyBot.instance.getAddon("PointSystem")).pointName();
		return msg.replace("point", myName);
	}
}
