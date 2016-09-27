package edu.wisdom.praycounter2;

import java.util.Date;

public class CounterBean {

	// Current counter record.
	public long id;				// Record ID.
	public int current;			// 已誦了多少遍經文.
	public int round;			// 已誦了多少輪經文.
	public int roundSize;		// 一輪經文為多少遍.
	public String name;			// 經文名稱.
	public String notes;		// 筆記.
	public boolean isCurrent;  	// 是不是目前背誦中的經文.
	public Date lastUpdate;		// 最後更新日期時間.
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getCurrent() {
		return current;
	}
	public void setCurrent(int current) {
		this.current = current;
	}
	public int getRound() {
		return round;
	}
	public void setRound(int round) {
		this.round = round;
	}
	public int getRoundSize() {
		return roundSize;
	}
	public void setRoundSize(int roundSize) {
		this.roundSize = roundSize;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public boolean isCurrent() {
		return isCurrent;
	}
	public void setCurrent(boolean isCurrent) {
		this.isCurrent = isCurrent;
	}
	public Date getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
}
