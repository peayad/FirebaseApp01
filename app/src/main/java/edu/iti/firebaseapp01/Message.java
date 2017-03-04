package edu.iti.firebaseapp01;

public class Message {

    String msgType;
    String msgTitle;
    String msgContent;

    Message(String title, String msg){
        this.msgTitle = title;
        this.msgContent = msg;
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

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }
}
