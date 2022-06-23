package swing.app;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class EventLog {
	private int EventID;
	private String source;
	private String Level;
	private java.sql.Date datelog;
	private String timelog;
	private String typelog;
	private String computer;
	private String user;
	
	public EventLog() {}
	
	public EventLog(int eventID, String source, String level, Date datalog, String typelog, String computer,
			String user) {
		super();
		EventID = eventID;
		this.source = source;
		this.Level = level;
		this.datelog = datalog;
		this.typelog = typelog;
		this.computer = computer;
		this.user = user;
	}
	
	public EventLog(EventLog EL) {
		super();
		EventID = EL.EventID;
		this.source = EL.source;
		this.Level = EL.Level;
		this.datelog = EL.datelog;
		this.typelog = EL.typelog;
		this.computer = EL.computer;
		this.user = EL.user;
	}

	public EventLog(ResultSet res) {
		//select
		try {
			this.EventID = res.getInt("eventid");
			this.source = res.getString("source");
			this.Level = res.getString("level");
			this.datelog = res.getDate("datelog");
			//System.out.println(res.getDate("datelog"));
			this.typelog = res.getString("typelog");
			this.computer = res.getString("nom_ordi");
			this.user = res.getString("nom_user");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getEventID() {
		return EventID;
	}

	public void setEventID(int eventID) {
		EventID = eventID;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getLevel() {
		return Level;
	}

	public void setLevel(String level) {
		Level = level;
	}

	public Date getDatelog() {
		return datelog;
	}

	public void setDatelog(Date datelog) {
		this.datelog = datelog;
	}

	public String getTypelog() {
		return typelog;
	}

	public void setTypelog(String typelog) {
		this.typelog = typelog;
	}

	public String getComputer() {
		return computer;
	}

	public void setComputer(String computer) {
		this.computer = computer;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	

	@Override
	public String toString() {
		return "EventLog [EventID=" + EventID + ", source=" + source + ", Level=" + Level + ", datelog=" + datelog
				+ ", timelog=" + timelog + ", typelog=" + typelog + ", computer=" + computer + ", user=" + user + "]";
	}
	
	public String[] toTable() {
		String[] arrstr = new String[4];
		//System.out.println(this.toString());
		arrstr[0] = this.Level;
		arrstr[1] = this.datelog.toString();
		arrstr[2] = this.source;
		arrstr[3] = Integer.toString(this.EventID);
		return arrstr;
	}

	public String getTimelog() {
		return timelog;
	}

	public void setTimelog(String timelog) {
		this.timelog = timelog;
	}
	
	public void insertIntoDB(Statement stmt) {
		String request = "INSERT INTO eventlogs (nom_ordi, nom_user, typelog, level, datelog, source, eventid) VALUES ";
		//request += (values)
		request += "('"+this.computer+"','"+this.user+"','"+this.typelog+"','"+this.Level+"','"+this.datelog+"','"+this.source+"','"+Integer.toString(EventID) +"')";
		//System.out.println(request);
		try {
			stmt.executeUpdate(request);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
