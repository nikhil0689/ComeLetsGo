package com.nikhil.sdsu.comeletsgo.Pojo;

/**
 * Created by nikhilc on 12/29/2017.
 */

public class RequestDetailsPOJO {
    private String requestorContact;
    private String requestorName;
    private String posterName;
    private String posterContact;
    private boolean approvalStatus = false;

    public boolean isApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(boolean approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getRequestorContact() {
        return requestorContact;
    }

    public void setRequestorContact(String requestorContact) {
        this.requestorContact = requestorContact;
    }

    public String getRequestorName() {
        return requestorName;
    }

    public void setRequestorName(String requestorName) {
        this.requestorName = requestorName;
    }

    public String getPosterName() {
        return posterName;
    }

    public void setPosterName(String posterName) {
        this.posterName = posterName;
    }

    public String getPosterContact() {
        return posterContact;
    }

    public void setPosterContact(String posterContact) {
        this.posterContact = posterContact;
    }



}
