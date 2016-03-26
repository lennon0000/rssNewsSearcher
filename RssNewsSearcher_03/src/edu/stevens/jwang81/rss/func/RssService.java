package edu.stevens.jwang81.rss.func;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

/**
 * @author wangjingxu
 *
 */
/**
 * @author wangjingxu
 *
 */
public class RssService {

	/**
	 * This function is used to add rss source into the system, all the related News HTML will be parsed and generate a inverted index.
	 * @param link the link of rss source
	 * @param name define a name for this resource
	 */
	public void addRss(String link, String name) {
		System.out.println(link + "  " + name);
		String xmlPath = "xml/rssConfig.xml";
		this.xmlGenerator(xmlPath, link, name);
		System.out.println("[INFO] rss config fiel was updated!");
		this.xmlParser(link);
		System.out
				.println("[INFO] all the related news was updated successfully!");
	}

	/**This function is used to add source URL into this system, after this was added, the system will crawl all the related webpages. 
	 * @param link 
	 * @param name
	 */
	public void addUrl(String link, String name) {
		System.out.println(link + "  " + name);
		String xmlPath = "xml/urlConfig.xml";
		this.xmlGenerator(xmlPath, link, name);
		System.out.println("[INFO] URL config fiel was updated!");
		//Use crawler to get all the urls.
		Crawler crawler = new Crawler();
		HashSet<String> links = crawler.crawl(link);
		//Iterator the urls, parse each of single url.
		for (Iterator iterator = links.iterator(); iterator.hasNext();) {
			String url = (String) iterator.next();
			this.httpPaser(url);
		}
		System.out
				.println("[INFO] all the related news was crawled successfully!");

	}

	/**This can be used to generate the xml file for rss configuration or url configuration.
	 * @param xmlPath
	 * @param link
	 * @param name
	 * @return return the id of recently add item
	 */
	private int xmlGenerator(String xmlPath, String link, String name) {
		int id = 0;
		Element root = Util.getRootElement(xmlPath);
		try {
			Element item = root.addElement("item");
			Element eid = item.addElement("id");// RSS id
			/*
			 * rssId should be retrived from xml file while the project was
			 * started.
			 */
			if (xmlPath.equals("xml/rssConfig.xml")) {
				eid.setText(RssReader.rssId + "");
				id = RssReader.rssId++;
			} else if (xmlPath.equals("xml/urlConfig.xml")) {
				eid.setText(RssReader.urlId + "");
				id = RssReader.urlId++;
			} else {
				eid.setText(RssReader.newsId + "");
				id = RssReader.newsId++;
			}

			Element rssName = item.addElement("name");// RSS name
			rssName.setText(name);

			Element rssLink = item.addElement("link");// RSS link
			rssLink.setText(link);

			XMLWriter writer = new XMLWriter(new FileOutputStream(xmlPath));

			writer.write(root.getDocument());
			writer.close();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return id;
	}

	/**parse the xml link, get all the titles and its corresponding links
	 * @param link
	 */
	public void xmlParser(String link) {
		boolean result = true;
		URL url;
		try {
			url = new URL(link);
			SAXReader saxReader = new SAXReader();
			Document doc = saxReader.read(url);
			List<Element> items = doc.selectNodes("//item");

			for (Iterator item = items.iterator(); item.hasNext();) {
				Element it = (Element) item.next();
				String title = it.selectSingleNode("title").getText();
				String newsUrl = it.selectSingleNode("link").getText();
				this.httpPaser(newsUrl);
			}
		} catch (MalformedURLException e) {
			System.out
					.println("[ERROR]This url is wrong, can't open the xml file.");
			result = false;
		} catch (DocumentException e) {
			result = false;
			System.out
					.println("[ERROR]There is something wrong with generate the Document Object.");
		}
	}

	/**Parse the html file, get all the content of the news link
	 * @param newsUrl
	 */
	public void httpPaser(String newsUrl) {// 用jsoup来将获得到的HTML文件进行解析
		try {
			org.jsoup.nodes.Document doc = Jsoup.connect(newsUrl).get();// itemprop="articleBody"

			org.jsoup.select.Elements title = doc.select("title");
			if (title != null) {
				String name = title.text();
				String newsListPath = "xml/newsPath.xml";
				/*
				 * 生成新闻的信息记录xml，用来记录新闻的id，title，url
				 */
				int newsId = this.xmlGenerator(newsListPath, newsUrl, name);

				String cont = "";
				Elements contents = doc.getElementsByAttributeValueContaining(
						"class", "article-copy");
				for (int i = 0; i < contents.size(); i++) {
					org.jsoup.nodes.Element content = contents.get(i);
					cont = content.text();// Get the words of those content
				}
				this.createInvertedIndex(name + " " + cont, newsId);
				System.out.println("[LOADING] : adding news Id: " + newsId);
				// 获得content的string类型，
				// 将这些string，进行建立inverted index操作
				// generateInvertedIndex();
			} else {
				System.out
						.println("[ERROR] this webpage is not a news webpage.");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**Using each word to create inverted index, the index was saved into a xml file, the name of the file is consisted like "word.xml"
	 * @param content
	 * @param newsId
	 */
	public void createInvertedIndex(String content, int newsId) {
		// TODO Auto-generated method stub
		String[] words = content.split("[^a-zA-Z]+");

		for (int i = 0; i < words.length; i++) {
			String word = Util.upperToLower(words[i]);
			if (!Util.isStopWords(word)) {
				// TODO:暂时写的简单些，直接取出stop words后，就直接创建倒排索引。。。还可以去除前后的特殊前后缀

				// 判断该词在文件系统是否存在（将词，以word.xml存在文件系统，这样进行读取---这种速度？？还是将这些结果，也是保存到一个xml文件中，xml文件读写速度哪个快？？？）
				String fileName = "words/" + word + ".xml";

				Element root = Util.getRootElement(fileName);

				boolean flag = true;
				if (root.hasContent()) {
					List<Element> elements = root.elements();
					// List<Node> nodes = root.selectNodes("item");

					for (int j = 0; j < elements.size(); j++) {
						Element node = elements.get(j);

						if (node.element("newsId").getText()
								.equals(newsId + "")) {
							flag = false;
						}
					}
				}

				if (flag) {
					Element item = root.addElement("item");
					Element nId = item.addElement("newsId");
					nId.setText(newsId + "");
					XMLWriter writer;
					try {
						writer = new XMLWriter(new FileOutputStream(fileName));
						writer.write(root.getDocument());
						writer.close();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**Get the list of all the Rss source
	 * @return
	 */
	public List<Element> getRssList() {

		SAXReader reader = new SAXReader();
		Document document;
		try {
			document = reader.read(new File("xml/rssConfig.xml"));
			Element root = document.getRootElement();
			List<Element> rsss = root.elements("item");

			return rsss;

		} catch (DocumentException e) {
			return null;
		}
	}
	/**Get the list of all the URL source
	 * @return
	 */
	public List<Element> getURLList() {
		SAXReader reader = new SAXReader();
		Document document;
		try {
			document = reader.read(new File("xml/urlConfig.xml"));
			Element root = document.getRootElement();
			List<Element> rsss = root.elements("item");

			return rsss;

		} catch (DocumentException e) {
			return null;
		}
	}

	/**Delete the chosen rss from rssConfig.xml
	 * @param delId
	 * @param tar
	 */
	public void delRss(int delId, String tar) {
		String xmlPath = "xml/" + tar + "Config.xml";
		Document doc = Util.getDocument(xmlPath);
		// TODO: This method will delete the node whose id is delId,after this
		// step was done, the window should be reloaded.
		Element root = doc.getRootElement();
		List<Element> items = root.elements("item");
		Element delEle = null;
		if (items != null) {
			for (int i = 0; i < items.size(); i++) {
				Element tmp = items.get(i);
				int id = Integer.parseInt(tmp.element("id").getText());
				if (id == delId) {
					delEle = tmp;
				}
			}
		}

		root.remove(delEle);
		XMLWriter writer;
		try {
			writer = new XMLWriter(new FileOutputStream(xmlPath));
			writer.write(root.getDocument());
			writer.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void updateRss(int delId, String tar) {
		// TODO 这里进行更新该rss对应的xml数据，即删除原来的，然后重新执行一遍xmlParser。。。。
		System.out.println("This function is not completed yet.");
	}

	
	/**Check if this url can be connected
	 * @param url
	 * @return
	 */
	private boolean isConnect(String url) {
		boolean flag = false;
		int counts = 0;
		if (url == null || url.length() <= 0) {
			return flag;
		}
		while (counts < 5) {
			try {
				HttpURLConnection connection = (HttpURLConnection) new URL(url)
						.openConnection();
				int state = connection.getResponseCode();
				if (state == 200) {

					flag = true;
				}
				break;
			} catch (Exception ex) {
				counts++;
				continue;
			}
		}
		return flag;
	}

	/**Get the total number of news
	 * @return
	 */
	public int getNewsNum() {
		SAXReader reader = new SAXReader();
		Document document;
		try {
			document = reader.read(new File("xml/newsPath.xml"));
			Element root = document.getRootElement();
			List elemement = root.elements("item");
			return elemement.size();
		} catch (DocumentException e) {
			return 0;
		}
	}
	/**Get the total number of URLs
	 * @return
	 */
	public int getURLNum() {
		SAXReader reader = new SAXReader();
		Document document;
		try {
			document = reader.read(new File("xml/urlConfig.xml"));
			Element root = document.getRootElement();
			List elemement = root.elements("item");
			return elemement.size();
		} catch (DocumentException e) {
			return 0;
		}
	}
	/**Get the total number of rss
	 * @return
	 */
	public int getRssNum() {

		SAXReader reader = new SAXReader();
		Document document;
		try {
			document = reader.read(new File("xml/rssConfig.xml"));
			Element root = document.getRootElement();
			List elemement = root.elements("item");

			return elemement.size();

		} catch (DocumentException e) {
			return 0;
		}
	}

	/**This function is used to search the news by the input key word.
	 * @param inputValue
	 * @return
	 */
	public Set<Integer> search(String inputValue) {
		Set<Integer> results = new HashSet<Integer>();
		String path = "words/" + inputValue + ".xml";
		Document doc = Util.getDocument(path);
		if (doc != null) {
			List<Element> ids = doc.selectNodes("//newsId");
			if (ids != null) {
				for (int i = 0; i < ids.size(); i++) {
					Element newsId = ids.get(i);
					results.add(Integer.parseInt(newsId.getText()));
				}
			}
		}

		return results;
	}

	/**Get the title and link of the news by specifying the index of news
	 * @param index
	 * @return
	 */
	public Map<String, String> getTitleLink(Integer index) {
		Map<String, String> results = new HashMap<String, String>();
		String xmlPath = "xml/newsPath.xml";

		Document doc = Util.getDocument(xmlPath);
		if (doc != null) {
			List<Element> items = doc.selectNodes("//item");
			if (items != null) {
				for (int i = 0; i < items.size(); i++) {
					Element item = items.get(i);
					int id = Integer.parseInt(item.element("id").getText());
					if (index == id) {
						String name = item.element("name").getText();
						String link = item.element("link").getText();
						results.put(name, link);
					}
				}
			}
		}
		return results;
	}

	/**Get the content of the news
	 * @param link
	 * @return
	 */
	public String getNewsCont(String link) {
		String cont = "";
		try {
			org.jsoup.nodes.Document doc = Jsoup.connect(link).get();// itemprop="articleBody"

			Elements contents = doc.getElementsByAttributeValueContaining(
					"class", "article-copy");
			for (int i = 0; i < contents.size(); i++) {
				org.jsoup.nodes.Element content = contents.get(i);
				cont = content.text();// Get the words of those content
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cont;
	}

}
