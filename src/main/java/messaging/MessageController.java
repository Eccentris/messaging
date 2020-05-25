package messaging;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.net.URI;
import java.util.NoSuchElementException;


@Path("/message")
public class MessageController {

    public static final MessageDao messageDao = new MessageDao();

    @GET
    @RolesAllowed("USER")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMessages(@Context ContainerRequestContext crc) {

        String user = UserAuth.getUserFromContext(crc);

        if(user == null || user.length() == 0) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Please provide user as part of Basic Auth")
                    .build();
        }

        String map = messageDao.getAllMessages();

        return Response.status(Response.Status.OK).entity(map).build();
    }


    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMessageById(@Context ContainerRequestContext crc,
                                   @PathParam("id") long id) {

        String user = UserAuth.getUserFromContext(crc);

        if(user == null || user.length() == 0) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Please provide user as part of Basic Auth")
                    .build();
        }

        Message message = null;

        try {
            message = messageDao.get(id);

        } catch (NoSuchElementException e) {

            return  Response.status(Response.Status.NOT_FOUND)
                    .entity("Could not find a message with id=" + id)
                    .build();
        }

        return Response.status(Response.Status.OK).entity(message).build();
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMessage(@Context ContainerRequestContext crc,
                               String content) {

        String user = UserAuth.getUserFromContext(crc);

        if(user == null || user.length() == 0) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Please provide user as part of Basic Auth")
                    .build();
        }

        if(content == null || content.length() == 0 || content.length() > 500) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Please provide message content between" +
                            " 1 and 500 characters in length")
                    .build();
        }

        Message message = new Message(user, content);

        long id = messageDao.add(message);

        URI uri = null;

        try {
            uri = new URI("/message/" + String.valueOf(id));
        } catch (URISyntaxException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .build();
        }

        return Response.created(uri).build();
    }


    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response editMessage(@Context ContainerRequestContext crc,
                                  @PathParam("id") long id,
                                  String content) {

        String user = UserAuth.getUserFromContext(crc);

        if(user == null || user.length() == 0) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Please provide user as part of Basic Auth")
                    .build();
        }

        if(content == null || content.length() == 0 || content.length() > 500) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Please provide message content between" +
                            " 1 and 500 characters in length")
                    .build();
        }

        Message message = null;

        try {
            message = messageDao.get(id);
        } catch(NoSuchElementException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Could not find a message with id=" + id)
                    .build();
        }

        if(!message.getAuthor().equals(user)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Only user=" + message.getAuthor() +
                            " is allowed to edit this message")
                    .build();
        }

        message = new Message(user, content);

        try {
            messageDao.update(id, message);
        } catch (NoSuchElementException e) {
            return  Response.status(Response.Status.NOT_FOUND)
                    .entity("Could not find a message with id=" + id)
                    .build();
        }

        return Response.ok().entity(message).build();
    }


    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMessage(@Context ContainerRequestContext crc,
                                  @PathParam("id") long id) {

        String user = UserAuth.getUserFromContext(crc);

        if(user == null || user.length() == 0) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Please provide user as part of Basic Auth")
                    .build();
        }

        Message message = null;

        try {
            message = messageDao.get(id);
        } catch(NoSuchElementException e) {
            return  Response.status(Response.Status.NOT_FOUND)
                    .entity("Could not find a message with id=" + id)
                    .build();
        }

        if(!message.getAuthor().equals(user)) {
            return  Response.status(Response.Status.FORBIDDEN)
                    .entity("Only user=" + message.getAuthor() +
                            " is allowed to delete this message")
                    .build();
        }

        try {
            messageDao.remove(id);
        } catch (NoSuchElementException e) {
            return  Response.status(Response.Status.NOT_FOUND)
                    .entity("Could not find a message with id=" + id)
                    .build();
        }

        return Response.status(202)
                .entity("Message deleted successfully")
                .build();
    }

}
