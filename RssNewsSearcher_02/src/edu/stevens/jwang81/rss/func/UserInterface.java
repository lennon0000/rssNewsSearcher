package edu.stevens.jwang81.rss.func;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

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
	private int delId;
	public void init(){
		rssService = new RssService();
//		RssReader.rssId = rssService.getRssNum();
//		RssReader.newsId = rssService.getNewsNum();
		List<Element> rsss = rssService.getRssList();
		
		JFrame mainFrame = new JFrame("Rss Reader");
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
		
		JPanel cen = new JPanel();
		cen.setLayout(new BorderLayout());
		
		JPanel searchBaI = new JPanel();
		searchBaI.setLayout(new FlowLayout());
		JTextField input = new JTextField(15);
		searchBaI.add(input);
		JButton searchBut = new JButton("Search");
		searchBaI.add(searchBut);
		
		cen.add(searchBaI,BorderLayout.NORTH);
		
		JTextArea resultFiled = new JTextArea(40, 40);
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
	}
}
