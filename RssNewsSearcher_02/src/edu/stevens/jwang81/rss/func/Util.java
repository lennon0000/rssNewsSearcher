package edu.stevens.jwang81.rss.func;

import java.io.File;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;



public class Util {

	public static boolean isStopWords(String word) {
		if (RssReader.stopWords.containsKey(word)) {
			return true;
		} else {
			return false;
		}
	}
	public static String upperToLower(String str)
    {
    	char[] ch = str.toCharArray();
    	for(int i=0;i<ch.length;i++)
    	{
    		if(((int)ch[i]>64) && ((int)ch[i]<91))
    		{
    			ch[i] = (char)((int)ch[i]+32);
    		}
    	}
    	String childStr = String.valueOf(ch);
    	return childStr;
    }
	public static Document getDocument(String xmlPath) {
		SAXReader reader = new SAXReader();
		Document document;
		
		try {
			document = reader.read(new File(xmlPath));
			
		} catch (DocumentException e) {
			document = DocumentHelper.createDocument();
			
		}
		
		return document;
	}
	public static Element getRootElement(String xmlPath) {
		SAXReader reader = new SAXReader();
		Element root;
		Document document;
		try {
			document = reader.read(new File(xmlPath));
			root = document.getRootElement();
		} catch (DocumentException e) {
			document = DocumentHelper.createDocument();
			root = document.addElement("root");// 创建根节点
		}
		return root;
	}
}
