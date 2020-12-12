package com.example.a277project;

public class Leave {
    String Username, dateFrom, dateTo, reason, docID;
    Boolean Approved;

    public Leave(){
    }

    public Leave(String username, String dateFrom, String dateTo, String reason) {
        Username = username;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.reason = reason;
        this.Approved = false;
    }

    public String getDocID() {
        return docID;
    }

    public String getUsername() {
        return Username;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public String getReason() {
        return reason;
    }

    public Boolean getApproved() {
        return Approved;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }
}
