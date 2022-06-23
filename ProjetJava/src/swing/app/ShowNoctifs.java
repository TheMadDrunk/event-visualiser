package swing.app;

import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ShowNoctifs extends JDialog {
	private JTable table;
	private List<String> noctifs = new ArrayList<String>();
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		try {
			
			Connection dbcon = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaprj", "root","");
			Statement stmt = dbcon.createStatement();
			ShowNoctifs dialog = new ShowNoctifs(stmt);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Create the dialog.
	 */
	public ShowNoctifs(Statement stmt) {
		setTitle("Noctifications");
		makeNoctifsLate(stmt);
		makeNoctifsLastDay(stmt);
		setBounds(100, 100, 338, 172);
		getContentPane().setLayout(new BorderLayout());
		{
			JScrollPane scrollPane = new JScrollPane();
			getContentPane().add(scrollPane, BorderLayout.CENTER);
			{
				table = new JTable();
				table.setModel(new DefaultTableModel());
				
				((DefaultTableModel)table.getModel()).addColumn("Notifications", noctifs.toArray());
				scrollPane.setViewportView(table);
			}
		}
	}
	
	private void makeNoctifsLastDay(Statement stmt) {
		Date sqlDate = new Date(System.currentTimeMillis());
		LocalDate ldate = sqlDate.toLocalDate();
		
		String query = "select nom from utilisateur where last_log >= '"+Date.valueOf(ldate.minusDays(1))+"'";
		try {
			ResultSet res = stmt.executeQuery(query);
			while(res.next()) {
				noctifs.add("the user "+res.getString("nom")+" just submited!");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void makeNoctifsLate(Statement stmt) {
		Date sqlDate = new Date(System.currentTimeMillis());
		LocalDate ldate = sqlDate.toLocalDate();
		
		String query = "select nom from utilisateur where last_log <= '"+Date.valueOf(ldate.minusDays(15))+"'";
		try {
			ResultSet res = stmt.executeQuery(query);
			while(res.next()) {
				noctifs.add("the user "+res.getString("nom")+" didn't submit for more than 15 days");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
