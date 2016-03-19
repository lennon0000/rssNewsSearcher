package edu.stevens.jwang81.rss.func;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.dom4j.Element;



public class UserInterface extends JFrame implements ActionListener {

	private JTextField linkInput;
	private JTextField nameInput;
	private RssService rssService;
	private JTextField input; 
	private JTextArea resultFiled;
	private int delId;
	
	
	private JPanel cen ;
//	private JFrame mainFrame ;
	
	public void init(){
		rssService = new RssService();
		List<Element> rsss = rssService.getRssList();
		
		JFrame mainFrame = new JFrame("Rss Reader");
		mainFrame = new JFrame("Rss Reader");
		ImageIcon image = new ImageIcon("image/icon.jpg");
		JLabel icon = new JLabel(image);
		mainFrame.setLayout(new BorderLayout());
		mainFrame.setSize(1200, 900);
		mainFrame.add(icon,BorderLayout.NORTH);
		
		JPanel rssList = new JPanel();
		rssList.setSize(25, 500);
		rssList.setLayout(new BorderLayout());
		
		linkInput = new JTextField(10);
		
		
		nameInput = new JTextField(10);
		JLabel jl = new JLabel("Link");
		JLabel jn = new JLabel("Name");
		JButton addB = new JButton("add");
		addB.setActionCommand("add");
		addB.addActionListener(this);
		
		JPanel jp = new JPanel();
		jp.setLayout(new FlowLayout());
		jp.add(jl);
		jp.add(linkInput);
		jp.add(jn);
		jp.add(nameInput);
		jp.add(addB);
		rssList.add(jp,BorderLayout.NORTH);
		
		JPanel list = new JPanel();
//		list.setLayout(new GridLayout());
		list.setBackground(Color.gray);
		if (rsss !=null) {
			for (int i = 0; i < rsss.size(); i++) {
				Element rss = rsss.get(i);
				String id = rss.element("id").getText();
				delId = Integer.parseInt(id);
				String name = rss.element("name").getText();
				JLabel idLable = new JLabel(id);
				JLabel nameLable = new JLabel(name);
				
				list.add(idLable);
				list.add(nameLable);
				
				JButton deleteBut = new JButton("delete");
				deleteBut.setActionCommand("delete");
				list.add(deleteBut);
				deleteBut.addActionListener(this);
				JButton updateBut = new JButton("update");
				updateBut.setActionCommand("update");
				list.add(updateBut);
				updateBut.addActionListener(this);
			}
		}
		
		rssList.add(list,BorderLayout.CENTER);
		
		mainFrame.add(rssList, BorderLayout.WEST);
		
		cen = new JPanel();
		cen.setLayout(new BorderLayout());
		
		JPanel searchBaI = new JPanel();
		searchBaI.setLayout(new FlowLayout());
		input = new JTextField(15);
		searchBaI.add(input);
		JButton searchBut = new JButton("Search");
		searchBut.setActionCommand("search");
		searchBut.addActionListener(this);
		searchBaI.add(searchBut);
		
		cen.add(searchBaI,BorderLayout.NORTH);
		
		resultFiled = new JTextArea(80, 40);
		resultFiled.setBackground(Color.lightGray);
		resultFiled.append("The result is : \r\n\r\n");
		cen.add(resultFiled,BorderLayout.CENTER);
		
		mainFrame.add(cen,BorderLayout.CENTER);
		
		mainFrame.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("add")) {
			rssService.addRss(linkInput.getText(),nameInput.getText()); //this function will add the new rss into database or xml config file.
		}
		if (e.getActionCommand().equals("delete")) {
			rssService.delRss(delId); //this function will add the new rss into database or xml config file.
		}
		if (e.getActionCommand().equals("update")) {
			rssService.updateRss(delId); //this function will add the new rss into database or xml config file.
		}
		if (e.getActionCommand().equals("search")) {
			Set<Integer> finalIndex = new HashSet<Integer>();
//			res = new JTextArea(20, 30);
//			p.removeAll();
//			
//			p.add(inputField);
//			p.add(search);
//			p.add(res);
			this.setVisible(true);
			String inputValue = input.getText();
			
			List<Integer> results = new ArrayList<Integer>();
			if (inputValue.contains(" ")) {
				String[] words = inputValue.split(" ");
				
				Map<Integer,Integer> check = new HashMap<Integer,Integer>();
				int num = words.length;
				for (int i = 0; i < words.length; i++) {
					Set<Integer> result = rssService.search(words[i]);
					for(Integer temp:result){
						if (check.containsKey(temp)) {
							int count = check.get(temp) +1;
							
							check.put(temp, count++);
						}else {
							check.put(temp, 1);
						}
					}
				}
				
				for (Integer index:check.keySet()) {
					if (check.get(index) == num) {
						finalIndex.add(index);
					}
				}
				
			}else {
				finalIndex = rssService.search(inputValue);
			}
			resultFiled.append("The results set is : \r\n\r\n");
			// for iterate all the results， output feedback.
			cen.removeAll();
			//________________________________________________________________________________________________
			//_____________________________________________________________这部分代码有点重复
			JPanel searchBaI = new JPanel();
			searchBaI.setLayout(new FlowLayout());
			input = new JTextField(15);
			searchBaI.add(input);
			JButton searchBut = new JButton("Search");
			searchBut.setActionCommand("search");
			searchBut.addActionListener(this);
			searchBaI.add(searchBut);
			
			cen.add(searchBaI,BorderLayout.NORTH);
			resultFiled = new JTextArea(80, 40);
			resultFiled.setBackground(Color.lightGray);
			resultFiled.append("The result is : \r\n\r\n");
			cen.add(resultFiled,BorderLayout.CENTER);
			//________________________________________________________________________________________________
			
			for (Integer index: finalIndex) {
//				String title = "";
				Map<String,String> titleLink =new HashMap<String, String>(); 
			
				titleLink = rssService.getTitleLink(index);
				Iterator<Map.Entry<String, String>> entries = titleLink.entrySet().iterator();  
				  
				while (entries.hasNext()) {  
				  
				    Map.Entry<String, String> entry = entries.next();  
				  
//				    System.out.println("name = " + entry.getKey() + ", link = " + entry.getValue());  
//				    resultFiled.append("Title: " + entry.getKey() + "  Link: "+entry.getValue()+ "\r\n\r\n");
				    resultFiled.append("Title: " + entry.getKey() + "\r\n\r\n");
//				    JLabel title = new JLabel(entry.getKey());
//				    this.add(title);
//				    this.repaint();
				} 
				
//				resultFiled.append(title + "\r\n\r\n");
			}
			this.setVisible(true);
			System.out.println("-----");
			
		}
	}
}
