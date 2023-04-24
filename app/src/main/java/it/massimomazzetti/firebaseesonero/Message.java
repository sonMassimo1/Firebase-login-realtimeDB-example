package it.massimomazzetti.firebaseesonero;

//Message class used to insert data into firebase database
public class Message {

    private String text;
    private String timeStamp;
    private String user;

    public Message() {
        this("", "", "");
    }

    public Message(String text, String timeStamp, String user) {
        this.text = text;
        this.timeStamp = timeStamp;
        this.user = user;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getUser() {
        return user;
    }
}
