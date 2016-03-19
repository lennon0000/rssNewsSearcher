package edu.stevens.jwang81.rss.func;

import java.util.HashMap;
import java.util.Map;


public class RssReader {
	
public static int rssId;//TODO: rssId should be retrived from xml file while the project was started.
public static int newsId;
public static Map<String,String> stopWords = new HashMap<String, String>();

public static void main(String[] args) {
	    StopWords.loadStopWords();
		System.out.println("sss");
		UserInterface ui = new UserInterface();
		ui.init();
	}

}
