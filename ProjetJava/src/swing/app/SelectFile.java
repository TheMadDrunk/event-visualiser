package swing.app;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorListener;
import javax.swing.event.AncestorEvent;


public class SelectFile extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private JLabel lblNewLabel;
	private JButton btnNewButton;
	private int retVal=1;
	private SelectFile ptrSF = this;
	private JPanel buttonPane;
	public JButton okButton;
	public JButton cancelButton;
	private JCheckBox chckbxNewCheckBox;
	private JTextField textField_1;
	private JLabel lblNewLabel_1;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			SelectFile dialog = new SelectFile();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Create the dialog.
	 */
	public SelectFile(JFrame owner,Boolean modal) {
		super(owner,modal);
		initDialog();
	}
	
	public SelectFile() {
		initDialog();
	}
	
	private void initDialog() {
		setTitle("Open file");
		setBounds(100, 100, 360, 216);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			lblNewLabel = new JLabel("Choose file : ");
		}
		{
			textField = new JTextField();
			textField.setColumns(20);
		}
		{
			btnNewButton = new JButton("Open a file");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser FC = new JFileChooser();
					if(FC.showOpenDialog(FC) == JFileChooser.APPROVE_OPTION && MatchExtention(FC.getSelectedFile().getPath(),"xml")) {
						textField.setText(FC.getSelectedFile().getPath());
					}
				}
			});
		}
		{
			lblNewLabel_1 = new JLabel("Username");
			
		}
		{
			textField_1 = new JTextField();
			textField_1.setColumns(20);
		}
		contentPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		contentPanel.add(lblNewLabel);
		contentPanel.add(textField);
		contentPanel.add(btnNewButton);
		contentPanel.add(lblNewLabel_1);
		contentPanel.add(textField_1);
		{
			chckbxNewCheckBox = new JCheckBox("upload to DB");
			contentPanel.add(chckbxNewCheckBox);
		}
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
	
	public int getRetVal() {
		return retVal;
	}

	public void setRetVal(int retVal) {
		this.retVal = retVal;
	}
	
	public String getPath() {
		return textField.getText();
	}
	
	public String getUsername() {
		return textField_1.getText();
	}
	
	public Boolean checkDB() {
		return chckbxNewCheckBox.isSelected();
	}
	
	private Boolean MatchExtention(String path,String extcmp) {
		String ext;
		int index = path.lastIndexOf('.');
		ext = path.substring(index+1);
		System.out.println(ext);
		return ext.contains(extcmp);
	}
}
