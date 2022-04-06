import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

class PhoneInfo implements Serializable {
	String name;
	String phoneNumber;
	String address;

	public PhoneInfo(String name, String num, String address_str) {
		this.name = name;
		this.phoneNumber = num;
		this.address = address_str;
	}

	public void showPhoneInfo() {
		System.out.println("name: " + name);
		System.out.println("phone: " + phoneNumber);
		System.out.println("address: " + address);
	}

	public String toString() {
		return "name: " + name + '\n' + "phone: " + phoneNumber + '\n' + "address: " + address + '\n';
	}

	public int hashCode() {
		return name.hashCode();
	}

	public boolean equals(Object obj) {
		PhoneInfo cmp = (PhoneInfo) obj;
		if (name.compareTo(cmp.name) == 0)
			return true;
		else
			return false;
	}
}

class PhoneUnivInfo extends PhoneInfo {
	String major;
	int year;

	public PhoneUnivInfo(String name, String num, String address, String major, int year) {
		super(name, num, address);
		this.major = major;
		this.year = year;
	}

	public void showPhoneInfo() {
		super.showPhoneInfo();
		System.out.println("major: " + major);
		System.out.println("year: " + year);
	}

	public String toString() {
		return super.toString() + "major: " + major + '\n' + "year: " + year + '\n';
	}
}

class PhoneCompanyInfo extends PhoneInfo {
	String company;

	public PhoneCompanyInfo(String name, String num, String address, String company) {
		super(name, num, address);
		this.company = company;
	}

	public void showPhoneInfo() {
		super.showPhoneInfo();
		System.out.println("company: " + company);
	}

	public String toString() {
		return super.toString() + "company: " + company + '\n';
	}
}

class PhoneBookManager {
	private final File dataFile = new File("PhoneBook.dat");
	HashSet<PhoneInfo> infoStorage = new HashSet<PhoneInfo>();

	static PhoneBookManager inst = null;

	public static PhoneBookManager createManagerInst() {
		if (inst == null)
			inst = new PhoneBookManager();
		return inst;
	}

	private PhoneBookManager() {
		readFromFile();
	}

	public String searchData(String name) {
		if (name.length() < 1)
			return null;
		ArrayList<PhoneInfo> info = search(name);
		if (info == null)
			return null;
		else
			return info.stream().map(Object::toString).collect(Collectors.joining("\n"));
		// return info.toString();
	}
	public Boolean modify(String name,String phone,String address) {
		return modifyData( name, phone, address);
	}
	
	private Boolean modifyData(String name,String phone,String address) {
		Iterator<PhoneInfo> itr = infoStorage.iterator();
		while (itr.hasNext()) {
			PhoneInfo curInfo = itr.next();
			if (name.compareTo(curInfo.name) == 0){
				infoStorage.remove(curInfo);
				PhoneInfo newCurInfo = new PhoneInfo(name, phone, address);
				infoStorage.add(newCurInfo);
				return true;
			}
				
		}
		return false;
	}
	public String allData() {
		return allRecord().toString();
	}

	public boolean storeData() {
		return storeToFile();
	}

	public boolean deleteData(String name) {
		Iterator<PhoneInfo> itr = infoStorage.iterator();
		while (itr.hasNext()) {
			PhoneInfo curInfo = itr.next();
			if (name.compareTo(curInfo.name) == 0) {
				itr.remove();
				return true;
			}
		}
		return false;
	}

	private String allRecord() {
		Iterator<PhoneInfo> itr = infoStorage.iterator();

		String s = "";
		while (itr.hasNext()) {
			PhoneInfo curInfo = itr.next();
			s += curInfo.toString() + "\n";
		}
		return s;
	}

	private ArrayList<PhoneInfo> search(String name) {
		Iterator<PhoneInfo> itr = infoStorage.iterator();
		ArrayList<PhoneInfo> res = new ArrayList<>();
		while (itr.hasNext()) {
			PhoneInfo curInfo = itr.next();

			if (name.toLowerCase().compareTo(curInfo.name.toLowerCase()) == 0
					|| curInfo.name.toLowerCase().contains(name.toLowerCase()))
				res.add(curInfo);

			if (name.compareTo(curInfo.phoneNumber) == 0 || curInfo.phoneNumber.contains(name))
				res.add(curInfo);

			if (name.toLowerCase().compareTo(curInfo.address.toLowerCase()) == 0
					|| curInfo.address.contains(name.toLowerCase()))
				res.add(curInfo);
		}
		return res;
	}

	public Boolean storeToFile() {
		try {
			FileOutputStream file = new FileOutputStream(dataFile);
			ObjectOutputStream out = new ObjectOutputStream(file);
			Iterator<PhoneInfo> itr = infoStorage.iterator();
			while (itr.hasNext())
				out.writeObject(itr.next());
			out.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void readFromFile() {
		if (dataFile.exists()) {
			System.out.println("Found Data File! IMPORTED ALL!");
		}
		if (dataFile.exists() == false)
			return;
		try {
			FileInputStream file = new FileInputStream(dataFile);
			ObjectInputStream in = new ObjectInputStream(file);
			while (true) {
				PhoneInfo info = (PhoneInfo) in.readObject();
				if (info == null)
					break;
				infoStorage.add(info);
			}
			in.close();
		} catch (IOException e) {
			return;
		} catch (ClassNotFoundException e) {
			return;
		}
	}
}

class HintTextField extends JTextField implements FocusListener {
	private final String hint;
	private boolean showingHint;

	public HintTextField(final String hint) {
		super(hint);
		this.hint = hint;
		this.showingHint = true;
		super.addFocusListener(this);
	}

	@Override
	public void focusGained(FocusEvent e) {
		if (this.getText().isEmpty()) {
			super.setText("");
			showingHint = false;
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (this.getText().isEmpty()) {
			super.setText(hint);
			showingHint = true;
		}
	}

	@Override
	public String getText() {
		return showingHint ? "" : super.getText();
	}
}

class AllEventHandler implements ActionListener {
	JTextArea textArea;

	public AllEventHandler(JTextArea area) {
		textArea = area;
	}

	public void actionPerformed(ActionEvent e) {
		textArea.setText("");
		PhoneBookManager manager = PhoneBookManager.createManagerInst();
		String srchResult = manager.allData();
		if (srchResult == null || srchResult.length() < 2) {
			textArea.append("No info exists.\n");
		} else {
			textArea.append("All PhoneBook Data:\n");
			textArea.append(srchResult);
			textArea.append("\n");
		}
	}
}

class SearchEventHandler implements ActionListener {
	JTextField searchField;
	JTextArea textArea;

	public SearchEventHandler(JTextField field, JTextArea area) {
		searchField = field;
		textArea = area;
	}

	public void actionPerformed(ActionEvent e) {
		textArea.setText("");
		String name = searchField.getText();
		PhoneBookManager manager = PhoneBookManager.createManagerInst();
		String srchResult = manager.searchData(name);
		if (srchResult == null || srchResult.length() < 2) {
			textArea.append("Search Failed: info does not exist.\n");
		} else {
			textArea.append("Search Completed:\n");
			textArea.append(srchResult);
			textArea.append("\n");
		}
	}
}

class AddEventHandler implements ActionListener {
	JTextField name;
	JTextField phone;
	JTextField address;
	JTextField major;
	JTextField year;
	JTextField company;
	JTextArea text;
	Vector<String> inputList = new Vector<String>();

	boolean isAdded;

	PhoneInfo info;

	public AddEventHandler(JTextField nameField, JTextField phoneField, JTextField addressField, JTextField majorField,
			JTextField yearField, JTextArea textArea) {
		name = nameField;
		phone = phoneField;
		address = addressField;
		major = majorField;
		year = yearField;
		text = textArea;
	}

	public void actionPerformed(ActionEvent e) {
		text.setText("");
		PhoneBookManager manager = PhoneBookManager.createManagerInst();
		if (name.getText().length() >= 3 && phone.getText().length() == 10 && address.getText().length() >= 3) {
			if (major.getText().equals("") == false && year.getText().equals("") == true) {
				company = major;
				info = new PhoneCompanyInfo(name.getText(), phone.getText(), address.getText(), company.getText());
				isAdded = manager.infoStorage.add(info);
			} else if (major.getText().equals("") == false && year.getText().equals("") == false) {
				info = new PhoneUnivInfo(name.getText(), phone.getText(), address.getText(), major.getText(),
						Integer.parseInt(year.getText()));
				isAdded = manager.infoStorage.add(info);
			} else {
				info = new PhoneInfo(name.getText(), phone.getText(), address.getText());
				isAdded = manager.infoStorage.add(info);
			}

			if (isAdded) {
				text.append("Added to the list!\n");
			} else {
				if(manager.modify(name.getText(), phone.getText(), address.getText())){
					text.append("Modified the already existied data for "+name.getText()+".\n");	
				}else{
					text.append("Update Err.\n");
				}
			}
		} else {
			text.append("INCOMPLETE OR INVALID DATA!.\n");
			text.append("Name length should be >= 3.\n");
			text.append("Phone length should be == 10.\n");
			text.append("Address length should be >= 3.\n");
		}

	}
}

class StoreEventHandler implements ActionListener {

	JTextArea textArea;

	public StoreEventHandler(JTextArea area) {
		textArea = area;
	}

	public void actionPerformed(ActionEvent e) {
		textArea.setText("");
		PhoneBookManager manager = PhoneBookManager.createManagerInst();
		boolean isStored = manager.storeData();
		if (isStored)
			textArea.append("Exported Successfully.\n");
		else
			textArea.append("Export Error.\n");
	}
}

class DeleteEventHandler implements ActionListener {
	JTextField delField;
	JTextArea textArea;

	public DeleteEventHandler(JTextField field, JTextArea area) {
		delField = field;
		textArea = area;
	}

	public void actionPerformed(ActionEvent e) {
		textArea.setText("");
		String name = delField.getText();
		PhoneBookManager manager = PhoneBookManager.createManagerInst();
		boolean isDeleted = manager.deleteData(name);
		if (isDeleted)
			textArea.append("Remove Completed.\n");
		else
			textArea.append("Remove Failed: info does not exist.\n");
	}
}

class MainFrame extends JFrame {
	// JTextField srchField = new JTextField(15);
	JTextField srchField = new HintTextField(" Name/Phone/Address/Wild! ");
	JButton srchBtn = new JButton("SEARCH");
	JButton allBtn = new JButton("ALL");

	JButton addBtn = new JButton("ADD");
	JRadioButton rbtn1 = new JRadioButton("General");
	JRadioButton rbtn2 = new JRadioButton("University");
	JRadioButton rbtn3 = new JRadioButton("Company");
	ButtonGroup buttonGroup = new ButtonGroup();

	JLabel nameLabel = new JLabel("NAME");
	JTextField nameField = new HintTextField(" Enter Name! ");
	JLabel phoneLabel = new JLabel("PHONE NUMBER");
	JTextField phoneField = new HintTextField(" Enter Phone Number! ");
	JLabel addressLabel = new JLabel("Address");
	JTextField addressField = new HintTextField(" Enter Address! ");
	JLabel majorLabel = new JLabel("MAJOR");
	JTextField majorField = new HintTextField(" Enter Major! ");
	JLabel yearLabel = new JLabel("YEAR");
	JTextField yearField = new HintTextField(" Enter Year! ");

	JTextField delField = new HintTextField(" Enter NAME to Delete! ");
	JButton delBtn = new JButton("DEL");

	JButton storeBtn = new JButton("EXPORT");

	JTextArea textArea = new JTextArea(10, 25);

	public MainFrame(String title) {
		super(title);
		setBounds(100, 200, 330, 450);
		setSize(730, 350);
		setLayout(new GridLayout(0, 2, 0, 0));
		Border border = BorderFactory.createEtchedBorder();

		Border srchBorder = BorderFactory.createTitledBorder(border, "Search");
		JPanel srchPanel = new JPanel();
		srchPanel.setBorder(srchBorder);
		srchPanel.setLayout(new FlowLayout());
		srchPanel.add(srchField);
		srchPanel.add(srchBtn);
		srchPanel.add(allBtn);

		Border addBorder = BorderFactory.createTitledBorder(border, "Add");
		JPanel addPanel = new JPanel();
		addPanel.setBorder(addBorder);
		addPanel.setLayout(new FlowLayout());

		JPanel addInputPanel = new JPanel();
		addInputPanel.setLayout(new GridLayout(0, 2, 5, 5));

		buttonGroup.add(rbtn1);
		buttonGroup.add(rbtn2);
		buttonGroup.add(rbtn3);

		addPanel.add(rbtn1);
		addPanel.add(rbtn2);
		addPanel.add(rbtn3);
		addPanel.add(addBtn);

		addInputPanel.add(nameLabel);
		addInputPanel.add(nameField);
		addInputPanel.add(phoneLabel);
		addInputPanel.add(phoneField);
		addInputPanel.add(addressLabel);
		addInputPanel.add(addressField);
		addInputPanel.add(majorLabel);
		addInputPanel.add(majorField);
		addInputPanel.add(yearLabel);
		addInputPanel.add(yearField);

		majorLabel.setVisible(false);
		majorField.setVisible(false);
		yearLabel.setVisible(false);
		yearField.setVisible(false);

		rbtn1.setSelected(true);
		addPanel.add(addInputPanel);

		rbtn1.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					majorLabel.setVisible(false);
					majorField.setVisible(false);
					yearLabel.setVisible(false);
					yearField.setVisible(false);
					majorField.setText("");
					yearField.setText("");
				}
			}
		});

		rbtn2.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					majorLabel.setVisible(true);
					majorLabel.setText("MAJOR");
					majorField.setVisible(true);
					yearLabel.setVisible(true);
					yearField.setVisible(true);
				}
			}
		});

		rbtn3.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					majorLabel.setVisible(true);
					majorLabel.setText("COMPANY");
					majorField.setVisible(true);
					yearLabel.setVisible(false);
					yearField.setVisible(false);
					yearField.setText("");
				}
			}
		});

		Border delBorder = BorderFactory.createTitledBorder(border, "Delete/Export");
		JPanel delPanel = new JPanel();
		delPanel.setBorder(delBorder);
		delPanel.setLayout(new FlowLayout());
		delPanel.add(delField);
		delPanel.add(delBtn);
		delPanel.add(storeBtn);

		JScrollPane scrollTextArea = new JScrollPane(textArea);
		Border textBorder = BorderFactory.createTitledBorder(border, "Infomation Board");
		scrollTextArea.setBorder(textBorder);

		JPanel actionPanel = new JPanel();
		actionPanel.setLayout(new BorderLayout());
		actionPanel.add(srchPanel, BorderLayout.NORTH);
		actionPanel.add(addPanel, BorderLayout.CENTER);
		actionPanel.add(delPanel, BorderLayout.SOUTH);

		add(actionPanel);
		add(scrollTextArea);

		allBtn.addActionListener(new AllEventHandler(textArea));
		srchBtn.addActionListener(new SearchEventHandler(srchField, textArea));
		addBtn.addActionListener(
				new AddEventHandler(nameField, phoneField, addressField, majorField, yearField, textArea));
		delBtn.addActionListener(new DeleteEventHandler(delField, textArea));
		storeBtn.addActionListener(new StoreEventHandler(textArea));
		setVisible(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
}

class PhoneBook {
	public static void main(String[] args) {
		PhoneBookManager manager = PhoneBookManager.createManagerInst();
		MainFrame winFrame = new MainFrame("Phone Book | JAVA");
	}
}
