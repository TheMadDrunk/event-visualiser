package swing.app;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.time.LocalDate;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class FilterDB extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField txtfieldOrdi;
	private JTextField txtfieldUser;
	private JTextField datestarttxt;
	private JTextField dateendtxt;
	private JLabel lblNewLabel;
	private JCheckBox chckbxNewCheckBox;
	private JLabel lblNewLabel_2;
	private JLabel lblNewLabel_3;
	private JPanel buttonPane;
	public JButton cancelButton;
	public JButton okButton;
	private int retVal = 1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			FilterDB dialog = new FilterDB();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	private void initFilterDB() {
		setTitle("Import From DB");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		lblNewLabel = new JLabel("ordinateur :");
		lblNewLabel.setBounds(18, 40, 69, 16);
		contentPanel.add(lblNewLabel);
		
		txtfieldOrdi = new JTextField();
		txtfieldOrdi.setBounds(92, 34, 122, 28);
		contentPanel.add(txtfieldOrdi);
		txtfieldOrdi.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Username :");
		lblNewLabel_1.setBounds(18, 82, 69, 16);
		contentPanel.add(lblNewLabel_1);
		
		txtfieldUser = new JTextField();
		txtfieldUser.setBounds(92, 74, 122, 28);
		contentPanel.add(txtfieldUser);
		txtfieldUser.setColumns(10);
		
		chckbxNewCheckBox = new JCheckBox("filter by data");
		chckbxNewCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxNewCheckBox.isSelected()) {
					datestarttxt.setEnabled(true);
					dateendtxt.setEnabled(true);
				}
				else {
					datestarttxt.setEnabled(false);
					dateendtxt.setEnabled(false);
				}
			}
		});
		chckbxNewCheckBox.setBounds(102, 120, 122, 18);
		contentPanel.add(chckbxNewCheckBox);
		
		lblNewLabel_2 = new JLabel("Start :");
		lblNewLabel_2.setBounds(18, 156, 55, 16);
		contentPanel.add(lblNewLabel_2);
		
		lblNewLabel_3 = new JLabel("End :");
		lblNewLabel_3.setBounds(18, 201, 55, 16);
		contentPanel.add(lblNewLabel_3);
		
		datestarttxt = new JTextField();
		datestarttxt.setText("YYYY-MM-DD");
		datestarttxt.setEnabled(false);
		datestarttxt.setBounds(92, 150, 122, 28);
		contentPanel.add(datestarttxt);
		datestarttxt.setColumns(10);
		
		dateendtxt = new JTextField();
		dateendtxt.setText("YYYY-MM-DD");
		dateendtxt.setEnabled(false);
		dateendtxt.setBounds(92, 195, 122, 28);
		contentPanel.add(dateendtxt);
		dateendtxt.setColumns(10);
		{
			buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	public FilterDB(JFrame owner,Boolean modal) {
		super(owner,modal);
		initFilterDB();
	}
	public FilterDB() {
		initFilterDB();
	}
	public String getOrdi() {
		return txtfieldOrdi.getText();
	}
	public String getUser() {
		return txtfieldUser.getText();
	}
	public boolean filterByDate() {
		return chckbxNewCheckBox.isSelected();
	}
	public Date[] getDates() {
		Date dts[] = new Date[2];
		LocalDate tmp = LocalDate.parse(datestarttxt.getText());
		dts[0] = Date.valueOf(tmp);
		tmp = LocalDate.parse(dateendtxt.getText());
		dts[1] = Date.valueOf(tmp);
		return  dts;
	}

	public int getRetVal() {
		return retVal;
	}

	public void setRetVal(int retVal) {
		this.retVal = retVal;
	}
}
