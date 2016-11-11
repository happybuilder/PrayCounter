package edu.wisdom.praycounter2;

import java.util.Date;

/**
 * Created by Chan Chu Man on 11/11/2016.
 */

public class PrayHistoryBean {

    // PrayHistory record.
    public long id;				// Record ID.
    public int roundSize;		// 一輪經文為多少遍.
    public String name;			// 經文名稱.
    public String notes;		// 筆記.
    public Date createDttm;		// 建立 record 的日期時間.

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Date getCreateDttm() {
        return createDttm;
    }

    public void setCreateDttm(Date createDttm) {
        this.createDttm = createDttm;
    }

}
