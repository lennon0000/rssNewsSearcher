package edu.stevens.jwang81.rss.func;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author wangjingxu This class is used to crawl the source url, for example
 *         http://abcnews.go.com
 */
public class Crawler {
	private HashSet<String> allURLs;
	private LinkedList<String> notCrawlurl;// urls which have not been crawled
	private HashMap<String, Integer> depth;
	// int crawlDepth = 3;
	private int crawlDepth;
	private int threadnumber;
	private int count;
	private static final Object signal = new Object();

	public Crawler() {
		this.allURLs = new HashSet<String>();
		this.notCrawlurl = new LinkedList<String>();
		this.depth = new HashMap<String, Integer>();
		this.crawlDepth = 2;
		this.threadnumber = 10;
		this.count = 0;
	}

	/**
	 * This function is used to get all the related urls by specifing the source
	 * URL
	 * 
	 * @param url
	 * @return The Linkedlist of all the related urls
	 */
	public HashSet<String> crawl(String url) {
		Crawler cc = new Crawler();
		cc.setup(url);
		long start = System.currentTimeMillis();
		cc.begin();

		while (true) {
			if (cc.notCrawlurl.isEmpty() && Thread.activeCount() == 1
					|| cc.count == cc.threadnumber) {
				long end = System.currentTimeMillis();
				long time = end - start;
				System.out.println("[INFO] Number of pages :"
						+ cc.allURLs.size());
				System.err.println("[INFO] Running time for crawling is : "
						+ time + "ms");
				return cc.getAllURLs();
			}
		}
	}

	public HashSet<String> getAllURLs() {
		return allURLs;
	}

	private synchronized void addUrl(String url, int d) {
		notCrawlurl.add(url);
		allURLs.add(url);
		depth.put(url, d);
	}

	private synchronized String getAUrl() {
		if (notCrawlurl.isEmpty())
			return null;
		String tmpAUrl;
		tmpAUrl = notCrawlurl.get(0);
		notCrawlurl.remove(0);
		return tmpAUrl;
	}

	private void setup(String url) {
		if (!allURLs.contains(url)) {
			addUrl(url, 1);
			// TODO: this part can be used to save the source url into a xml
			// configure file
		}
	}

	private void begin() {
		for (int i = 0; i < threadnumber; i++) {
			new Thread(new Runnable() {
				public void run() {

					while (true) {
						String url = getAUrl();
						if (url != null) {
							// System.out.println("crawler：" + url);
							if (depth.get(url) < crawlDepth) {
								getChildURL(url);
							}
							// parser(tmp);
						} else {
							synchronized (signal) {
								try {
									count++;
									System.out.println("[INFO]当前有" + count
											+ "个线程在等待");
									signal.wait();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

						}
					}
				}
			}, "thread-" + i).start();
		}
	}

	/**
	 * This function can be used to get all the child url of the specified URL.
	 * This part can be updated with the searching condition which can improve
	 * the search efficiency.
	 * 
	 * @param url
	 * 
	 */
	private void getChildURL(String url) {
		try {
			Document doc = Jsoup.connect(url).get();
			Elements links = doc.select("a");
			String tmp;
			for (Element e : links) {
				if (e.attr("abs:href") != "") {
					tmp = e.attr("abs:href");// Get the abstract path
					if (allURLs.contains(tmp) == false) {
						addUrl(tmp, depth.get(url) + 1);
						if (count > 0) { // 如果有等待的线程，则唤醒
							synchronized (signal) { // ---------------------（2）
								count--;
								signal.notify();
							}
						}
					}
				}
			}
		} catch (IOException e) {
			// e.printStackTrace();
			System.out.println("[ERROR] This URL cannot be opened.");
		}
	}

}
