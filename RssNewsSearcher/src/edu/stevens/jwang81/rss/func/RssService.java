package edu.stevens.jwang81.rss.func;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

public class RssService {

	public void addRss(String link, String name) {
		System.out.println(link + "  " + name);
		String xmlPath = "xml/rssConfig.xml";
		this.xmlGenerator(xmlPath,link,name);
	}

	private void xmlGenerator(String xmlPath, String link, String name) {
		SAXReader reader = new SAXReader();
		Document document;
		try {
			document = reader.read(new File(xmlPath));
			Element root = document.getRootElement();

			Element item = root.addElement("item");
			Element id = item.addElement("id");// RSS id
			
			if (xmlPath.equals("xml/rssConfig.xml")) {
				id.setText(RssReader.rssId + "");
				RssReader.rssId++;// TODO: rssId should be retrived from xml file
									// while the project was started.
			}else{
				id.setText(RssReader.newsId + "");
				RssReader.newsId ++;
			}

			Element rssName = item.addElement("name");// RSS name
			rssName.setText(name);

			Element rssLink = item.addElement("link");// RSS link
			rssLink.setText(link);
			
			XMLWriter writer = new XMLWriter(new FileOutputStream(
					xmlPath));
//			boolean success = xmlParser(link);
			this.xmlParser(link);
//			if (success) {
				writer.write(document);
				writer.close();
//			}

		} catch (DocumentException e) {
			document = DocumentHelper.createDocument();
			Element root = document.addElement("root");// 创建根节点
			Element item = root.addElement("item");

			Element id = item.addElement("id");
			if (xmlPath.equals("xml/rssConfig.xml")) {
				id.setText(RssReader.rssId + "");
				RssReader.rssId++;// TODO: rssId should be retrived from xml file
									// while the project was started.
			}else{
				id.setText(RssReader.newsId + "");
				RssReader.newsId ++;
			}
			
			Element rssName = item.addElement("name");
			rssName.setText(name);
			Element rssLink = item.addElement("link");
			rssLink.setText(link);

//			OutputFormat out = new OutputFormat("  ", true, "utf-8");
			XMLWriter writer;
			try {
				writer = new XMLWriter(new FileOutputStream(xmlPath));
//				boolean success = xmlParser(link);
				this.xmlParser(link);
//				if (success) {
					writer.write(document);
					writer.close();
//				}
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void xmlParser(String link) {
		boolean result = true;
			URL url ;
			try {
				url = new URL(link);
				SAXReader saxReader = new SAXReader();  	
				Document doc = saxReader.read(url); 	
				List<Element> nodes = doc.selectNodes("/rss/channel/item");
				
				for (Iterator iter = nodes.iterator(); iter.hasNext(); ) {
					Element rssElement = (Element) iter.next();
					String title = rssElement.selectSingleNode("title").getText();
					String newsUrl = rssElement.selectSingleNode("link").getText();
					this.httpPaser(newsUrl);
		        }
			} catch (MalformedURLException e) {
				System.out.println("[ERROR]This url is wrong, can't open the xml file.");
				result = false;
			} catch (DocumentException e) {
				result = false;
				System.out.println("[ERROR]There is something wrong with generate the Document Object.");
			}
	}

	public void httpPaser(String newsUrl) {//用jsoup来将获得到的HTML文件进行解析
		try {
			org.jsoup.nodes.Document doc = Jsoup.connect(newsUrl).get();//itemprop="articleBody"
			
			org.jsoup.select.Elements title = doc.select("title");
			String name = title.text();
			String newsListPath = "xml/newsPath.xml";
			/*
			 * 生成新闻的信息记录xml，用来记录新闻的id，title，url
			 * */
			this.xmlGenerator(newsListPath, newsUrl,name);
			
			String cont = "";
			Elements contents = doc.getElementsByAttributeValueContaining("class", "article-copy");
			for (int i = 0; i < contents.size(); i++) {
				org.jsoup.nodes.Element content = contents.get(i);
				cont = content.text();//Get the words of those content
			}
			this.createInvertedIndex(name+" "+cont);
			
			//获得content的string类型，
			//将这些string，进行建立inverted index操作
			//generateInvertedIndex();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void createInvertedIndex(String content) {
		// TODO Auto-generated method stub
		String[] words = content.split(" ");
		
		for (int i = 0; i < words.length; i++) {
			String word = Util.upperToLower(words[i]);
			if (!Util.isStopWords(word)) {
				//TODO:暂时写的简单些，直接取出stop words后，就直接创建倒排索引。。。还可以去除前后的特殊前后缀
				
				//判断该词在文件系统是否存在（将词，以word.xml存在文件系统，这样进行读取---这种速度？？还是将这些结果，也是保存到一个xml文件中，xml文件读写速度哪个快？？？）
				String fileName = "words/"+word+".xml";
				File file = new File(fileName);
				if (file.exists()) {
					//如果file 存在，则打开，添加新闻信息，添加ID，TF，TF-IDF等信息（现在先只加ID信息，TF，TF-IDF可以后期完善的时候做，加上那些可以将结果进行排序）
					
					
				}else {
					try {
						file.createNewFile();//如果不存在，则新建，然后插入新的新闻信息
						
						
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
				
			}
		}
	}


	private boolean isStopWords(String word) {
		// TODO Auto-generated method stub
		return false;
	}

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
	public List getRssList() {

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

	public void delRss(int delId) {
		//TODO: This method will delete the node whose id is delId,after this step was done, the window should be reloaded.
		System.out.println("this id should be delete from xml file! id- "+delId);//there is something wrong with this value.
	}

	public void updateRss(int delId) {
		// TODO 这里进行更新该rss对应的xml数据，即删除原来的，然后重新执行一遍xmlParser。。。。
		
	}
	// 检查URL是否有效  
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

	public int getNewsNum() {
		SAXReader reader = new SAXReader();
		Document document;
		try {
			document = reader.read(new File("xml/newsList.xml"));
			Element root = document.getRootElement();
			List elemement = root.elements("item");
			return elemement.size();
		} catch (DocumentException e) {
			return 0;
		}
	}  
}
