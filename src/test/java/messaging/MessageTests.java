package messaging;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class MessageTests {

    /**
     * getAuthor should return Message author
     * @result returns String author
     */
    @Test
    public void getAuthorShouldReturnAuthor() {

        Message message = new Message("username", "hello");

        assertEquals("username", message.getAuthor());
    }

    /**
     * getContent should return Message content
     * @result returns String content
     */
    @Test
    public void getContentShouldReturnContent() {

        Message message = new Message("username", "hello");

        assertEquals("hello", message.getContent());
    }

    /**
     * toString should return Json representation of Message as String
     * @result returns Json representation of Message as String
     */
    @Test
    public void toStringShouldReturnJsonRepresentationContent() {

        Message message = new Message("username", "hello");

        String expect = "{\"author\":\"username\",\"content\":\"hello\"}";

        assertEquals(expect, message.toString());
    }

}
