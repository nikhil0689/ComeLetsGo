package com.nikhil.sdsu.comeletsgo.Pojo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nikhilc on 1/6/2018.
 */

public class MyRideDetailsPOJO {
    private String posterName,posterContact,source,destination,date,time,requestorName,uid;
    private boolean rideStatus,approvalStatus=false;
    private Map<String,String> joinee = new HashMap();

    public Map<String, String> getJoinee() {
        return joinee;
    }

    public void setJoinee(Map<String, String> joinee) {
        this.joinee = joinee;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(boolean approvalStatus) {
        this.approvalStatus = approvalStatus;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isRideStatus() {
        return rideStatus;
    }

    public void setRideStatus(boolean rideStatus) {
        this.rideStatus = rideStatus;
    }
}
