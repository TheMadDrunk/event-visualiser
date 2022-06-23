package swing.app;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.table.DefaultTableModel;

import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.XChartPanel;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;


public class MainWindow extends JFrame   {

	/**S
	 * 
	 */
	private static final long serialVersionUID = -8721827713435558122L;
	private JPanel contentPane;
	private JFrame ptrThis = this; //x
	private EventLogList ELLOpen = new EventLogList(); //x
	private JTable table; //x
	private Connection dbcon; //x
	private Statement stmt; //x
	private JTable summarytable; //x
	private PieChart piechart = new PieChartBuilder().width(600).height(600).title("Level Proportions").build();
	private CategoryChart histchart = new CategoryChartBuilder().width(600).height(600).title("Event per day").build();
	/**
	 * Launch the application.
	 * @throws UnsupportedLookAndFeelException 
	 */
	public static void main(String[] args) throws UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(new NimbusLookAndFeel());
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 480);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu importfile = new JMenu("Import File");
		menuBar.add(importfile);
		
		JMenuItem fromdb = new JMenuItem("From DB...");
		importfile.add(fromdb);
		fromdb.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				FilterDB FDB = new FilterDB(ptrThis,true);
				
				FDB.okButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						FDB.setRetVal(1);
						FDB.setVisible(false);
					}
				});
				
				FDB.cancelButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						FDB.setRetVal(0);
						FDB.setVisible(false);
					}
				});
				
				FDB.setVisible(true);
				String ordi,user;
				Date dts[] = null;
				if(FDB.getRetVal()==1) {
					ordi = FDB.getOrdi();
					user = FDB.getUser();
					if(FDB.filterByDate()) {
						dts = FDB.getDates();
						ELLOpen.selectFromDB(stmt, ordi, user, dts);
					}
					else ELLOpen.selectFromDB(stmt, ordi, user);
				}
				FDB.dispose();
				updateDashboard();
			}
			
		});
		
		JMenuItem selFile = new JMenuItem("Select File...");
		importfile.add(selFile);
		selFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SelectFile SF = new SelectFile(ptrThis,true);
				
				SF.okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						SF.setRetVal(1);
						SF.setVisible(false);
					}
				});

				SF.cancelButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						SF.setRetVal(0);
						SF.setVisible(false);	
					}
				});
				SF.setVisible(true);
				String path;
				String username;
				if(SF.getRetVal()==1) {
					path = SF.getPath();
					username = SF.getUsername();
					ELLOpen.parseXML(path,username);
					if(SF.checkDB())
						ELLOpen.insertListIntoDB(stmt);
				}
				SF.dispose();
				updateDashboard();
				
			}
			
		});
		
		Component horizontalStrut = Box.createHorizontalStrut(705);
		menuBar.add(horizontalStrut);
		
		JMenuItem noctifs = new JMenuItem("Noctifications...");
		noctifs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Clicked");
				ShowNoctifs SN = new ShowNoctifs(stmt);
				SN.setVisible(true);
			}
		});
		
		menuBar.add(noctifs);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		table = new JTable();
		table.setEnabled(false);
		table.setCellSelectionEnabled(true);
		table.setShowVerticalLines(true);
		table.setShowGrid(false);
		
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Level", "Date", "Source", "EventID"
			}
		));
		table.getColumnModel().getColumn(0).setPreferredWidth(129);
		table.getColumnModel().getColumn(1).setPreferredWidth(153);
		table.getColumnModel().getColumn(2).setPreferredWidth(165);
		table.getColumnModel().getColumn(3).setPreferredWidth(171);
		tabbedPane.addTab("table", null, new JScrollPane(table), null);
		
		summarytable = new JTable();
		summarytable.setEnabled(false);
		summarytable.setModel(new DefaultTableModel(
			new Object[][] {},
			new String[] {
				"Last Day", "Last Week", "Last Month"
			}
		));
		summarytable.setBounds(181, 222, 469, -143);
		JScrollPane scrollPane = new JScrollPane(summarytable);
		tabbedPane.addTab("summary", null, scrollPane, null);
		
		JPanel panel = new JPanel();
		scrollPane.setRowHeaderView(panel);
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("70px"),},
			new RowSpec[] {
				FormSpecs.LABEL_COMPONENT_GAP_ROWSPEC,
				RowSpec.decode("15px"),
				RowSpec.decode("15px"),
				RowSpec.decode("15px"),}));
		
		JLabel labelErr = new JLabel("Error");
		panel.add(labelErr, "1, 2, fill, fill");
		
		JLabel labelwar = new JLabel("Warning");
		panel.add(labelwar, "1, 3, fill, fill");
		
		JLabel labelinfo = new JLabel("Information");
		panel.add(labelinfo, "1, 4, fill, fill");
		
		JPanel PiePanel = new XChartPanel<PieChart>(piechart);
		piechart.addSeries("Error", null);
		piechart.addSeries("Warning", null);
		piechart.addSeries("Information", null);
		tabbedPane.addTab("Pie", null, PiePanel, null);
		
		JPanel HistPanel = new XChartPanel<CategoryChart>(histchart);
		histchart.addSeries("Error", new int[] {0},new int[] {0});
		histchart.addSeries("Warning",new int[] {0},new int[] {0});
		histchart.addSeries("Information", new int[] {0},new int[] {0});
		tabbedPane.addTab("Histogram", null, HistPanel, null);
		
		
		try {
			dbcon = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaprj", "root","");
			stmt = dbcon.createStatement();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		
	}

	private void updateTable() {
		table.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
					"Level", "Date", "Source", "EventID"
				}
			));
		for(int i = 0;i<ELLOpen.list.size();i++) {
			((DefaultTableModel)table.getModel()).addRow(ELLOpen.list.get(i).toTable());
		}
	}
	private void updateSummary() {
		
		Integer arr[][] = new Integer[3][3];
		
		Date sqlDate = new Date(System.currentTimeMillis());
		LocalDate ldate = sqlDate.toLocalDate();
		
		arr[0][0] = ELLOpen.countLevelData("Error", Date.valueOf(ldate.minusDays(1)));
		arr[0][1] = ELLOpen.countLevelData("Error", Date.valueOf(ldate.minusDays(7)));
		arr[0][2] = ELLOpen.countLevelData("Error", Date.valueOf(ldate.minusDays(30)));
		
		
		arr[1][0] = ELLOpen.countLevelData("Warning", Date.valueOf(ldate.minusDays(1)));
		arr[1][1] = ELLOpen.countLevelData("Warning", Date.valueOf(ldate.minusDays(7)));
		arr[1][2] = ELLOpen.countLevelData("Warning", Date.valueOf(ldate.minusDays(30)));
		
		
		arr[2][0] = ELLOpen.countLevelData("Information", Date.valueOf(ldate.minusDays(1)));
		arr[2][1] = ELLOpen.countLevelData("Information", Date.valueOf(ldate.minusDays(7)));
		arr[2][2] = ELLOpen.countLevelData("Information", Date.valueOf(ldate.minusDays(30)));
		
		((DefaultTableModel)summarytable.getModel()).setDataVector(arr,new String[] {
				"Last Day", "Last Week", "Last Month"
			});
		
	}
	
	private void updatePie() {
		piechart.updatePieSeries("Error", ELLOpen.countLevel("Error"));
		piechart.updatePieSeries("Warning", ELLOpen.countLevel("Warning"));
		piechart.updatePieSeries("Information", ELLOpen.countLevel("Information"));
	}
	
	private void updateHist() {
		int[][] error = ELLOpen.countLevelPerDay("Error");
		int[][] warning = ELLOpen.countLevelPerDay("Warning");
		int[][] info = ELLOpen.countLevelPerDay("Information");
		histchart.removeSeries("Error");
		histchart.removeSeries("Warning");
		histchart.removeSeries("Information");
		histchart.addSeries("Error", error[1],error[0]);
		histchart.addSeries("Warning", warning[1],warning[0]);
		histchart.addSeries("Information", info[1],info[0]);
	}
	
	private void updateDashboard() {
		updateTable();
		updateSummary();
		updatePie();
		updateHist();
	}

	
}
