package messaging;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.util.NoSuchElementException;

public class MessageDaoTests {

    /**
     * When no messages are present, getAllMessages should return empty
     * @result returns empty Json object
     */
    @Test
    public void getAllMessagesShouldReturnNoMessages() {

        MessageDao messageDao = new MessageDao();

        assertEquals("{}", messageDao.getAllMessages());
    }

    /**
     * When one message is present, getAllMessages should return Json
     * representation containing the id of the message and the message
     * @result returns Json representation of message and its id
     */
    @Test
    public void getAllMessagesShouldReturnSingleMessage() {

        MessageDao messageDao = new MessageDao();
        Message message = new Message("username", "hello");
        messageDao.add(message);

        String expect = "{\"1\":{\"author\":\"username\",\"content\":\"hello\"}}";

        assertEquals(expect, messageDao.getAllMessages());
    }

    /**
     * When two message are present, getAllMessages should return Json
     * representation containing the ids of the messages and the messages
     * @result returns Json representation of messages and their ids
     */
    @Test
    public void getAllMessagesShouldReturnTwoMessages() {

        MessageDao messageDao = new MessageDao();
        Message message = new Message("username", "hello");
        messageDao.add(message);
        message = new Message("another", "other");
        messageDao.add(message);

        String expect = "{\"1\":" +
                "{\"author\":\"username\",\"content\":\"hello\"}," +
                "\"2\":" +
                "{\"author\":\"another\",\"content\":\"other\"}}";

        assertEquals(expect, messageDao.getAllMessages());
    }

    /**
     * get should successfully return a message if specified message exists
     * @result returns specified message
     */
    @Test
    public void getShouldReturnOneMessageWhenOneExists() {

        MessageDao messageDao = new MessageDao();
        Message message = new Message("username", "hello");
        messageDao.add(message);

        assertEquals(message, messageDao.get(1));
    }

    /**
     * get should throw Exception if specified message does not exist
     * @result throws NoSuchElementException
     */
    @Test(expected = NoSuchElementException.class)
    public void getShouldThrowExceptionWhenRequiredMessageNotFound() {

        MessageDao messageDao = new MessageDao();
        messageDao.get(1);
    }

    /**
     * add should successfully add a message with incremented id to map
     * @result message added to map
     */
    @Test
    public void addShouldAddMessageWithIncrementedIdToMap() {

        MessageDao messageDao = new MessageDao();
        Message message = new Message("username", "hello");
        messageDao.add(message);

        assertEquals(message, messageDao.get(1));
    }

    /**
     * update should successfully replace original message with the specified
     * new message if the original message is found
     * @result replaces original message with new message
     */
    @Test
    public void updateShouldReplaceMessageIfFound() {

        MessageDao messageDao = new MessageDao();
        Message firstMessage = new Message("username", "hello");
        messageDao.add(firstMessage);

        assertEquals(firstMessage, messageDao.get(1));

        Message editedMessage = new Message("username", "another");
        messageDao.update(1, editedMessage);

        assertEquals(editedMessage, messageDao.get(1));
    }

    /**
     * update should throw Exception if message wished to be replaced can't
     * be found
     * @result throws NoSuchElementException
     */
    @Test(expected = NoSuchElementException.class)
    public void updateShouldThrowExceptionWhenRequiredMessageNotFound() {

        MessageDao messageDao = new MessageDao();
        Message message = new Message("username", "hello");
        messageDao.update(1, message);
    }

    /**
     * remove should remove specified message successfully, and throw
     * exception when later attempting to get the message
     * @result message is removed, NoSuchElementException thrown on attempting
     * to get the message
     */
    @Test(expected = NoSuchElementException.class)
    public void removeShouldDeleteMessageIfFound() {

        MessageDao messageDao = new MessageDao();
        Message message = new Message("username", "hello");
        messageDao.add(message);

        assertEquals(message, messageDao.get(1));

        messageDao.remove(1);

        messageDao.get(1);
    }


    /**
     * remove should throw Exception if message wished to be removed can't
     * be found
     * @result throws NoSuchElementException
     */
    @Test(expected = NoSuchElementException.class)
    public void removeShouldThrowExceptionWhenRequiredMessageNotFound() {
        MessageDao messageDao = new MessageDao();
        messageDao.remove(1);
    }

    /**
     * clear should remove all messages successfully, and return empty
     * Json object when getting all messages afterwards
     * @result messages are cleared from map
     */
    @Test
    public void clearShouldEmptyMap() {

        MessageDao messageDao = new MessageDao();
        Message firstMessage = new Message("username", "hello");
        messageDao.add(firstMessage);

        assertEquals(firstMessage, messageDao.get(1));

        messageDao.clear();

        assertEquals("{}", messageDao.getAllMessages());
    }

    /**
     * clear should reset the incremental counter. When clear has been used,
     * adding new messages will restart count at 1.
     * @result counter is reset
     */
    @Test
    public void clearShouldResetCounter() {

        MessageDao messageDao = new MessageDao();
        Message firstMessage = new Message("username", "hello");
        messageDao.add(firstMessage);

        assertEquals(firstMessage, messageDao.get(1));

        messageDao.clear();

        Message secondMessage = new Message("other", "another");
        messageDao.add(secondMessage);
        assertEquals(secondMessage, messageDao.get(1));

    }

}