package edu.stevens.jwang81.rss.func;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.dom4j.Element;

public class UserInterface extends JFrame implements ActionListener,
		MouseListener {

	private JTextField linkInput;
	private JTextField nameInput;
	private RssService rssService;
	private JTextField input;
	private JTextArea resultFiled;
	// private int delId;

	private JPanel cen;

	// private JFrame mainFrame ;

	public void init() {
		rssService = new RssService();
		List<Element> rsss = rssService.getRssList();
		List<Element> urls = rssService.getURLList();

		JFrame mainFrame = new JFrame("Rss Reader");
		ImageIcon image = new ImageIcon("image/icon.jpg");
		JLabel icon = new JLabel(image);
		mainFrame.setLayout(new BorderLayout());
		mainFrame.setSize(1200, 900);
		mainFrame.add(icon, BorderLayout.NORTH);

		JPanel rssList = new JPanel();
		rssList.setSize(25, 500);
		rssList.setLayout(new BorderLayout());

		linkInput = new JTextField(10);

		nameInput = new JTextField(10);
		JLabel jl = new JLabel("Link");
		JLabel jn = new JLabel("Name");
		JButton addB = new JButton("Add Rss");
		JButton addB2 = new JButton("Add URL");
		addB.setActionCommand("addRss");
		addB.addActionListener(this);

		addB2.setActionCommand("addUrl");
		addB2.addActionListener(this);

		JPanel jp = new JPanel();
		jp.setLayout(new FlowLayout());
		jp.add(jl);
		jp.add(linkInput);
		jp.add(jn);
		jp.add(nameInput);
		jp.add(addB);
		jp.add(addB2);
		rssList.add(jp, BorderLayout.NORTH);

		JPanel list = new JPanel();
		// list.setLayout(new GridLayout());
		list.setBackground(Color.gray);
		if (rsss != null) {
			for (int i = 0; i < rsss.size(); i++) {
				Element rss = rsss.get(i);
				String id = rss.element("id").getText();
				// delId = Integer.parseInt(id);
				String name = rss.element("name").getText();
				JLabel idLable = new JLabel(id);
				JLabel nameLable = new JLabel(name);

				list.add(idLable);
				list.add(nameLable);

				JButton deleteBut = new JButton("delete");

				deleteBut.setActionCommand("delete-" + id + "-rss");
				list.add(deleteBut);
				deleteBut.addActionListener(this);
				JButton updateBut = new JButton("update");
				updateBut.setActionCommand("update-" + id + "-rss");
				list.add(updateBut);
				updateBut.addActionListener(this);
			}
		}
		if (urls != null) {
			for (int i = 0; i < urls.size(); i++) {
				Element url = urls.get(i);
				String id = url.element("id").getText();
				// delId = Integer.parseInt(id);
				String name = url.element("name").getText();
				JLabel idLable = new JLabel(id);
				JLabel nameLable = new JLabel(name);

				list.add(idLable);
				list.add(nameLable);

				JButton deleteBut = new JButton("delete");
				deleteBut.setActionCommand("delete-" + id + "-url");
				list.add(deleteBut);
				deleteBut.addActionListener(this);
				JButton updateBut = new JButton("update");
				updateBut.setActionCommand("update-" + id + "-url");
				list.add(updateBut);
				updateBut.addActionListener(this);
			}
		}

		rssList.add(list, BorderLayout.CENTER);

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

		cen.add(searchBaI, BorderLayout.NORTH);

		resultFiled = new JTextArea(80, 40);
		resultFiled.setBackground(Color.lightGray);
		resultFiled.append("The result is : \r\n\r\n");
		cen.add(resultFiled, BorderLayout.CENTER);

		mainFrame.add(cen, BorderLayout.CENTER);

		mainFrame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCom = e.getActionCommand();
		int id = 0;
		String command = "";
		String tar = "";
		if (actionCom.contains("-")) {
			String sid = actionCom.split("-")[1];
			id = Integer.parseInt(sid);
			command = actionCom.split("-")[0];
			tar = actionCom.split("-")[2];
		} else {
			command = actionCom;
		}
		switch (command) {
		case "addRss":
			rssService.addRss(linkInput.getText(), nameInput.getText()); // this
																			// function
																			// will
																			// add
																			// the
																			// new
																			// rss
																			// into
																			// database
																			// or
																			// xml
																			// config
																			// file.
			break;
		case "addUrl":
			rssService.addUrl(linkInput.getText(), nameInput.getText()); // this
																			// function
																			// will
																			// add
																			// the
																			// new
																			// rss
																			// into
																			// database
																			// or
																			// xml
																			// config
																			// file.
			break;
		case "update":
			rssService.updateRss(id, tar); // this function will add the new rss
											// into
											// database or xml config file.
			break;
		case "delete":
			rssService.delRss(id, tar); // this function will add the new rss
										// into
										// database or xml config file.
			break;
		case "search":
			this.search();
			break;

		default:
			break;
		}
	}

	private void search() {

		Set<Integer> finalIndex = new HashSet<Integer>();
		// res = new JTextArea(20, 30);
		// p.removeAll();
		//
		// p.add(inputField);
		// p.add(search);
		// p.add(res);
		this.setVisible(true);
		String inputValue = input.getText();

		List<Integer> results = new ArrayList<Integer>();
		if (inputValue.contains(" ")) {
			String[] words = inputValue.split(" ");

			Map<Integer, Integer> check = new HashMap<Integer, Integer>();
			int num = words.length;
			for (int i = 0; i < words.length; i++) {
				String word = words[i];
				if (Util.isStopWords(word)) {
					num--;
				} else {
					Set<Integer> result = rssService.search(word);
					for (Integer temp : result) {
						if (check.containsKey(temp)) {
							int count = check.get(temp) + 1;

							check.put(temp, count++);
						} else {
							check.put(temp, 1);
						}
					}
				}
			}

			for (Integer index : check.keySet()) {
				if (check.get(index) == num) {
					finalIndex.add(index);
				}
			}

		} else {
			finalIndex = rssService.search(inputValue);
		}
		resultFiled.append("The results set is : \r\n\r\n");
		// for iterate all the results， output feedback.
		cen.removeAll();
		// ________________________________________________________________________________________________
		// _____________________________________________________________这部分代码有点重复
		JPanel searchBaI = new JPanel();
		searchBaI.setLayout(new FlowLayout());
		input = new JTextField(15);
		searchBaI.add(input);
		JButton searchBut = new JButton("Search");
		searchBut.setActionCommand("search");
		searchBut.addActionListener(this);
		searchBaI.add(searchBut);

		cen.add(searchBaI, BorderLayout.NORTH);
		resultFiled = new JTextArea(80, 40);
		resultFiled.setBackground(Color.lightGray);
		resultFiled.append("The result is : \r\n\r\n");

		// cen.add(resultFiled, BorderLayout.CENTER);
		// ________________________________________________________________________________________________
		JPanel result = new JPanel();
		result.setBackground(Color.lightGray);
		
		for (Integer index : finalIndex) {
			// String title = "";
			Map<String, String> titleLink = new HashMap<String, String>();

			titleLink = rssService.getTitleLink(index);
			Iterator<Map.Entry<String, String>> entries = titleLink.entrySet()
					.iterator();

			while (entries.hasNext()) {

				Map.Entry<String, String> entry = entries.next();

				JLabel title = new JLabel(entry.getKey());
				title.setName(entry.getValue());
				title.addMouseListener(this);
				result.add(title);
			}
		}
		cen.add(result);
		this.setVisible(true);
		System.out.println("-----");

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		JLabel source = (JLabel) e.getSource();
		String title = source.getText();
		String link = source.getName();

		String content = rssService.getNewsCont(link);
		JFrame frame = new JFrame(title);
		frame.setLayout(null);
		frame.setBounds(20, 20, 800, 650);
		frame.setLayout(new BorderLayout());
		
		JPanel panel = new JPanel();
		
		JTextArea contArea = new JTextArea("Content",35,63);
		contArea.setSelectedTextColor(Color.RED);
		contArea.setLineWrap(true); 
		contArea.setWrapStyleWord(true);  
		contArea.append(content);
		
		panel.add(new JScrollPane(contArea));
		frame.add(panel);
		frame.setVisible(true);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		JLabel source = (JLabel) e.getSource();
		Cursor c = new Cursor(Cursor.HAND_CURSOR);
		source.setCursor(c);
		

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
