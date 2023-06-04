package project.babysitting.babysiting.Models;

public class Message {
    String messageId,message,senderId,reciverId;

    public Message() {
    }


    public Message(String messageId, String message, String senderId, String reciverId) {
        this.messageId = messageId;
        this.message = message;
        this.senderId = senderId;
        this.reciverId = reciverId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReciverId() {
        return reciverId;
    }

    public void setReciverId(String reciverId) {
        this.reciverId = reciverId;
    }
}
