package cn.czc.pojo;

public class Pojo {
	private String month  ;
	private String day    ;
	private String name   ;
	private String task   ;
	private String status ;
	public Pojo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Pojo(String month, String day, String name, String task, String status) {
		super();
		this.month = month;
		this.day = day;
		this.name = name;
		this.task = task;
		this.status = status;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTask() {
		return task;
	}
	public void setTask(String task) {
		this.task = task;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
