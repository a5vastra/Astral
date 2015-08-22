package helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import main.MyBot;
import addons.PointSystem;

public class OldPointConversion {
	public static void Convert(String path) throws NumberFormatException, IOException
	{
		List<PointSystem.PointAccount> list = new ArrayList<PointSystem.PointAccount>();
		PointSystem ps = new PointSystem();
		
		BufferedReader br = new BufferedReader(new FileReader(new File(path)));
		String line;
		Pattern p = Pattern.compile("^(?<key>\\w+)\t(?<value>\\d+)$");
		while ((line = br.readLine()) != null) {
			Matcher m = p.matcher(line);
			System.out.println(line);
			if(m.find())
				list.add(ps.new PointAccount(m.group("key"), Integer.parseInt(m.group("value"))));
		}
		ps.setPointAccounts(list);
		ps.Save();
	}
}
