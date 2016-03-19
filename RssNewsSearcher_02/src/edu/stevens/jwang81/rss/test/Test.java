package edu.stevens.jwang81.rss.test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import edu.stevens.jwang81.rss.func.RssService;
import edu.stevens.jwang81.rss.func.Util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String url = "http://abcnews.go.com/Politics/analysis-president-obamas-supreme-court-nomination-means-2016/story?id=37701942";
//		RssService s = new RssService();
//		s.httpPaser(url);
		
//		String s = "big apple test good/";
//		String s2 = "bad";
//		System.out.println(s+s2);
//		String xmlPath = "xml/testXML.xml";
//		Document d = Util.getDocument(xmlPath);
//		System.out.println("....");
//		String content = "Trump also discussed his request that his team look into the details surrounding a 78-year-old supporter who allegedly punched a protester at a Trump rally in North Carolina last week.";
//		r.createInvertedIndex(content, 1);
//		System.out.println("-----");
		
		String link = "http://feeds.abcnews.com/abcnews/topstories";
		RssService r = new RssService();
//		r.xmlParser(link);
		
		
	}
	private static void xmlParser(String xmlLink) {
		boolean result = true;
			URL url ;
			try {
				url = new URL(xmlLink);
				
				SAXReader saxReader = new SAXReader();  	
				Document doc = saxReader.read(url); 	
				if (doc != null) {
					List<Element> items = doc.selectNodes("//item");
					for (int i = 0; i < items.size(); i++) {
						Element item = items.get(i);
						Element title = item.element("title");
						Element link = item.element("link");
						String tit = title.getText();
						String lin = link.getText();
						
						System.out.println(tit);
					}
					
					System.out.println("....");
				}
				
			} catch (MalformedURLException e) {
				System.out.println("[ERROR]This url is wrong, can't open the xml file.");
				result = false;
			} catch (DocumentException e) {
				result = false;
				System.out.println("[ERROR]There is something wrong with generate the Document Object.");
			}
	}
}
