package messaging;

import org.json.JSONObject;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.NoSuchElementException;

public class MessageDao {

    private final ConcurrentMap<Long, Message> messages;
    private final AtomicLong counter = new AtomicLong();

    public MessageDao() {
        this.messages = new ConcurrentHashMap<>();
    }

    public String getAllMessages() {

        JSONObject mapRepresentation = new JSONObject(messages);

        return mapRepresentation.toString();
    }

    public Message get(long id) throws NoSuchElementException {

        if( ! messages.containsKey(id)) {
            throw new NoSuchElementException();
        }

        return messages.get(id);
    }

    public long add(Message message) {

        long id = counter.incrementAndGet();
        messages.put(id, message);

        return id;
    }

    public void update(long id, Message message) throws NoSuchElementException {

        if( ! messages.containsKey(id)) {
            throw new NoSuchElementException();
        }

        messages.put(id, message);
    }


    public void remove(long id) throws NoSuchElementException {

        if( ! messages.containsKey(id)) {
            throw new NoSuchElementException();
        }

        messages.remove(id);
    }

    public void clear() {

        messages.clear();
        counter.set(0);
    }
}
