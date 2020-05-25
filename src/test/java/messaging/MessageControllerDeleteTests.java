package messaging;

import io.restassured.response.Response;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import javax.ws.rs.core.Application;

import static io.restassured.RestAssured.given;
import static messaging.MessageController.messageDao;

public class MessageControllerDeleteTests extends JerseyTest {

    // Need to set port as the tests run on a different port than
    // the standard application
    private static final int PORT = 9998;

    @Override
    protected Application configure() {
        return new ResourceConfig(MessageController.class);
    }

    @After
    public void after() throws Exception {
        super.tearDown();
        messageDao.clear();
    }

    /**
     * When user matches message author
     * and a message exists, delete should succeed
     * @result Status 202, Expected success message
     */
    @Test
    public void deleteShouldSucceedWith202WhenSameUserTest() {

        //Add message so there is one to delete
        Message message = new Message("username", "hello");
        messageDao.add(message);

        Response response = given()
                                .port(PORT)
                                .auth()
                                .preemptive()
                                .basic("username", "password")
                            .when()
                                .delete("/message/1");

        String successMsg = "Message deleted successfully";

        Assert.assertEquals(202, response.getStatusCode());
        Assert.assertEquals(successMsg, response.getBody().asString());

    }

    /**
     * Delete should fail if requested Message does not exist
     * @result Status 404, error message
     */
    @Test
    public void deleteShouldFailWith404WhenMessageDoesNotExistTest() {

        Response response = given()
                                .port(PORT)
                                .auth()
                                .preemptive()
                                .basic("username", "password")
                            .when()
                                .delete("/message/1");

        String errorMsg = "Could not find a message with id=1";

        Assert.assertEquals(404, response.getStatusCode());
        Assert.assertEquals(errorMsg, response.getBody().asString());

    }

    /**
     * Delete should fail if user does not match Message author
     * @result Status 403, error message
     */
    @Test
    public void deleteShouldFailWith403WhenUserDoesNotMatchAuthorTest() {

        //Add message so there is one to delete
        Message message = new Message("username", "hello");
        messageDao.add(message);

        Response response = given()
                                .port(PORT)
                                .auth()
                                .preemptive()
                                .basic("another", "password")
                            .when()
                                .delete("/message/1");

        String errorMsg = "Only user=username is allowed to delete this message";

        Assert.assertEquals(403, response.getStatusCode());
        Assert.assertEquals(errorMsg, response.getBody().asString());

    }

}
