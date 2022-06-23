package swing.app;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;





public class EventLogList {
	public List<EventLog> list = new ArrayList<EventLog>();
	

	public EventLogList() {
		super();
	}
	public EventLogList(List<EventLog> list) {
		super();
		this.list = list;
	}
	@Override
	public String toString() {
		return "EventLogList [list=" + list.toString() + "]";
	}
	
	public void parseXML(String path,String user) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = null;
			
			//make new file and parse the XML insides it
			document = builder.parse(new File(path));

			document.getDocumentElement().normalize();

			NodeList nodelist = document.getElementsByTagName("Event");

			List<EventLog> listTmp = new ArrayList<EventLog>();
			
			for (int i = 0; i < nodelist.getLength(); i++) {
				Node node = nodelist.item(i);

				Element event = (Element) node;
				Element system = (Element) event.getElementsByTagName("System").item(0);
				EventLog EL = new EventLog();
				NodeList childslist = system.getChildNodes();
				for (int j = 0; j < childslist.getLength(); j++) {
					Element El = (Element) childslist.item(j);
					
					EL.setUser(user);
					if (El.getTagName() == "Provider") {
						EL.setSource(El.getAttribute("Name")); 
					} else if (El.getTagName() == "TimeCreated") {
						
						String datatime=El.getAttribute("SystemTime");
						EL.setTimelog(datatime);
						java.util.Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(datatime);
						java.sql.Date datelog = new java.sql.Date(utilDate.getTime());
						EL.setDatelog(datelog);
						
					} else if (El.getTagName() == "Channel") {
						EL.setTypelog(El.getTextContent());
					} else if (El.getTagName() == "Computer") {
						EL.setComputer(El.getTextContent());
					} else if (El.getTagName() == "Level") {
						int lvl = Integer.parseInt(El.getTextContent());
						if(lvl == 2)
							EL.setLevel("Error");
						if(lvl == 3)
							EL.setLevel("Warning");
						if(lvl == 4)
							EL.setLevel("Information");
					} else if (El.getTagName() == "EventID") {
						EL.setEventID(Integer.parseInt(El.getTextContent()));
					}
					
				}
				listTmp.add(EL);
				
			}
			this.list.clear();//vider la liste actuel
			this.list.addAll(listTmp);//ajouter les nouveau EL
			
			this.list.sort(new Comparator<EventLog>() { //on trie par date de creation pour cree histogram
				@Override
				public int compare(EventLog o1, EventLog o2) {
					return o1.getDatelog().compareTo(o2.getDatelog());
				}
			});

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (SAXException e) {
			 e.printStackTrace();
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println("File not found");
			return;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// insert ou update utilisateur et ordinateur ET insert les eventlog dans la list
	public void insertListIntoDB(Statement stmt) {
		EventLog ev = list.get(0);
		
		//insert utilisateur
		String query = "select id from utilisateur where nom='"+ev.getUser()+"'";
		ResultSet res;
		try {
			res = stmt.executeQuery(query);
		
			if(res.next()) {
				int id = res.getInt("id");
				String update = "update utilisateur set last_log = '"+ev.getDatelog()+"' where id='"+id+"'";
				stmt.executeUpdate(update);
			}
			else {
				String insert = "insert into utilisateur (nom,last_log) values ('"+ev.getUser()+"','"+ev.getDatelog()+"')";
				stmt.executeUpdate(insert);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//insert ordinateur
		query = "select id from ordinateur where nom='"+ev.getComputer()+"'";
		try {
			res = stmt.executeQuery(query);
		
			if(res.next()) {
				int id = res.getInt("id");
				String update = "update ordinateur set n_logs = n_logs + 1 where id='"+id+"'";
				stmt.executeUpdate(update);
			}
			else {
				String insert = "insert into ordinateur (nom,n_logs) values ('"+ev.getComputer()+"',1)";
				stmt.executeUpdate(insert);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		//insert EventLog
		for(int i = 0;i<list.size();i++) {
			list.get(i).insertIntoDB(stmt);
		}
	}
	
	public void selectFromDB(Statement stmt,String ordi,String user,Date[] dts) {
		String query = "Select * from eventlogs where nom_ordi='"+ordi+"' nom_user='"+user+"' and datelog between '"+dts[0]+"' and '"+dts[1]+"'";
		List<EventLog> listTmp = new ArrayList<EventLog>();
		try {
			ResultSet res = stmt.executeQuery(query);
			while(res.next()) {
				listTmp.add(new EventLog(res));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.list.clear();
		this.list = listTmp;
	}
	
	public void selectFromDB(Statement stmt,String ordi,String user) {
		String query = "Select * from eventlogs where nom_ordi='"+ordi+"'and nom_user='"+user+"'";
		List<EventLog> listTmp = new ArrayList<EventLog>();
		try {
			ResultSet res = stmt.executeQuery(query);
			while(res.next()) {
				listTmp.add(new EventLog(res));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.list.clear();
		this.list = listTmp;
	}

	public int countLevel(String lvl) {
		int cnt=0;
		for(int i = 0;i<list.size();i++) {
			if(list.get(i).getLevel().contains(lvl))
				cnt++;
		}
		return cnt;
	}
	
	public int[][]  countLevelPerDay(String lvl){
		int[][] LL = null;
		//get last and first day in the list
		int f = list.get(0).getDatelog().toLocalDate().getDayOfYear(), l = list.get(list.size()-1).getDatelog().toLocalDate().getDayOfYear();
		//n number of days in the list
		int n =  l-f + 1;
	    LocalDate ld;
	    
	    LL = new int[2][n];
	    int llptr = -1;
	    for(int i = 0;i<list.size();i++) {
	    	ld = list.get(i).getDatelog().toLocalDate();
	    	
	    	if(ld.getDayOfYear() > f+llptr) {
	    		llptr++;
	    		LL[1][llptr] = f+llptr;
	    	}
	    	if(list.get(i).getLevel().contains(lvl))
	    		LL[0][llptr]++;
	    }
	    
		return LL;
	}
	
	public int countLevelData(String lvl,java.sql.Date d) {
		int cnt = 0;
		
		for(int i = 0;i<list.size() && d.compareTo(list.get(i).getDatelog()) <=0 ;i++) {
			if(list.get(i).getLevel().contains(lvl))
				cnt++;
		}
		return cnt;
	}
}
