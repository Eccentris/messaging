# messaging
A simple REST messaging API

## Building the application
**This only works (tested) with Java 8**. A JDK for 1.8 is required in order to run this application.
To build, run and test the application, use the provided gradle wrapper.
#### build
`./gradlew build`
#### run
`./gradlew jettyRun`
#### test
`./gradlew test`

## Authentication
There are two users in the system:

`    anna:password`

`    linda:password`

**All endpoints require Basic Auth**, and one of these two must be used.

## API resources
When running the application, the API can be reached at `http://localhost:8080/messaging/message`.

### GET /message
Returns all Messages

Response body example with two messages present:

    {
      "1": {
        "author": "linda",
        "content": "hello"
      },
      "2": {
        "author": "linda",
        "content": "??"
      }
    }

#### GET /message/{id}
Returns Message corresponding to {id}

Response body example:

    {
      "content": "hello",
      "author": "linda"
    }
#### POST /message
Adds a new message. Request body should be message content, between 1-500 characters in length.

Request body example:

    "this is a message"
#### PUT /message/{id}
Replaces Message contents with id {id} with the content specified in request body. Request body should be message content, between 1-500 characters in length. Note that only the user that authored the message may update it.

Request body example:

    "this will be the new message"
#### DELETE /message/{id}
Deletes the Message corresponding to {id}. Note that only the user that authored the message may delete it.