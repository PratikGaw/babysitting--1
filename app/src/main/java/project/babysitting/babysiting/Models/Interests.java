package project.babysitting.babysiting.Models;

public class Interests {
    String interId,uid,status,sitterUid;

    public Interests() {
    }
    public Interests(String interId, String uid, String status, String sitterUid) {
        this.interId = interId;
        this.uid = uid;
        this.status = status;
        this.sitterUid = sitterUid;
    }

    public String getInterId() {
        return interId;
    }

    public String getSitterUid() {
        return sitterUid;
    }

    public void setSitterUid(String sitterUid) {
        this.sitterUid = sitterUid;
    }

    public void setInterId(String interId) {
        this.interId = interId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
