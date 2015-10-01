package helpers;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.CopyOption;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.apache.commons.lang3.StringEscapeUtils;


public class FileManager {
	String name;
	public FileManager(String name)
	{
		this.name = name;
	}
	public HashMap<String, HashMap<String, String>> Load(String className)
	{
		try {
			HashMap<String, HashMap<String, String>> map = new HashMap<String, HashMap<String, String>>();
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			File file = new File(className+".xml");
			if(!file.exists())
			{
				//file.createNewFile();
				return new HashMap<String, HashMap<String, String>>();
			}
			else
			{
				Date today;
				String dateOut;
				DateFormat dateFormatter;

				dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT);
				today = new Date();
				dateOut = dateFormatter.format(today);
				
				String newDirPath = "backup"+
						File.separator+
						dateOut;
				
				new File(newDirPath).mkdirs();
				
				Files.copy(
						new File(className+".xml").toPath(),
						new File(newDirPath+File.separator+className+".xml").toPath(),
						new CopyOption[]{
						      StandardCopyOption.REPLACE_EXISTING,
						      StandardCopyOption.COPY_ATTRIBUTES
						    }
					);
			}
			
			Document doc = dBuilder.parse(file);
			if(doc.hasChildNodes())
			{
				NodeList outerNodeList = doc.getChildNodes().item(0).getChildNodes();
				for(int i = 0; i < outerNodeList.getLength(); i++)
				{
					Node outerNode = outerNodeList.item(i);
					if(outerNode.getNodeType() == Node.ELEMENT_NODE && outerNode.hasChildNodes())
					{
						System.out.println(outerNode.getNodeName());
						NodeList innerNodeList = outerNode.getChildNodes();
						HashMap<String, String> innerMap = new HashMap<String, String>();
						for(int j = 0; j < innerNodeList.getLength(); j++)
						{
							Node innerNode = innerNodeList.item(j);
							if(innerNode.getNodeType() == Node.ELEMENT_NODE)
							{
								System.out.println("\t"+innerNode.getNodeName()+"] ["+innerNode.getTextContent());
								String key = innerNode.getNodeName();
								String value = StringEscapeUtils.unescapeXml(innerNode.getTextContent());
								if(key.startsWith("_"))
									key = key.substring(1);
								innerMap.put(key, value);
							}
						}
						map.put(StringEscapeUtils.unescapeXml(outerNode.getNodeName()), innerMap);
					}
				}
			}
			return map;
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			return new HashMap<String, HashMap<String, String>>();
		}
		return null;
	}
	public void Create(String className, HashMap<String, HashMap<String, String>> map)
	{
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	 
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement(className);
			doc.appendChild(rootElement);
			
			// staff elements
			for(Entry<String, HashMap<String, String>> outer : map.entrySet())
			{
				System.out.println("outer:"+outer.getKey());
				Element eOuter = doc.createElement(outer.getKey());
				for(Entry<String, String> inner : outer.getValue().entrySet())
				{
					System.out.println("inner:"+inner.getKey()+":"+inner.getValue());
					Element eInner = doc.createElement("_"+inner.getKey());
					eInner.appendChild(doc.createTextNode(StringEscapeUtils.escapeXml10(inner.getValue())));
					eOuter.appendChild(eInner);
				}
				rootElement.appendChild(eOuter);
			}
			
			StreamResult result = new StreamResult(new File(className+".xml"));
			 
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer;
				transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(source, result);
	 
			//System.out.println("File saved!");
		} catch (ParserConfigurationException | TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
