package edu.stevens.jwang81.rss.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.stevens.jwang81.rss.func.RssService;

public class HttpPaserTest {


	@Test
	public void test() {
		String url = "http://www.apple.com/iphone/";
		RssService r = new RssService();
		r.httpPaser(url);
	}

}
