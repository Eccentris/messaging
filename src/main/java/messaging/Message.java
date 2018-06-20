package messaging;

import org.json.JSONObject;

public class Message {

    private String content;
    private String author;

    public Message(String user, String content) {
        this.author = user;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        JSONObject object = new JSONObject();
        object.put("author", this.author);
        object.put("content", this.content);
        return object.toString();
    }
}
