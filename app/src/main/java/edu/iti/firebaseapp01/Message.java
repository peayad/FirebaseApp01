package edu.iti.firebaseapp01;

public class Message {

    String msgLink;
    String msgTitle;
    String msgContent;

    Message(String title, String msg, String link){
        this.msgTitle = title;
        this.msgContent = msg;
        this.msgLink = link;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getMsgTitle() {
        return msgTitle;
    }

    public void setMsgTitle(String msgTitle) {
        this.msgTitle = msgTitle;
    }

    public String getMsgLink() {
        return msgLink;
    }

    public void setMsgLink(String msgLink) {
        this.msgLink = msgLink;
    }
}
