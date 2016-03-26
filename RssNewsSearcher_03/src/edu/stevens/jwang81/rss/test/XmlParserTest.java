package edu.stevens.jwang81.rss.test;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import edu.stevens.jwang81.rss.func.RssService;

public class XmlParserTest {


	@Test
	public void test() {
		RssService r = new RssService();
		String link = "http://feeds.abcnews.com/abcnews/topstories";
		r.xmlParser(link);
	}

}
