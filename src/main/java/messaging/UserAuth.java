package messaging;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.xml.bind.DatatypeConverter;

public class UserAuth {

    public static String getUserFromContext(ContainerRequestContext crc) {

        String auth = crc.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (auth == null || auth.length() == 0) {
            return null;
        }

        //Decode authentication credentials
        auth = auth.replaceFirst("[B|b]asic ", "");
        byte[] decodedBytes = DatatypeConverter.parseBase64Binary(auth);

        if (decodedBytes == null || decodedBytes.length == 0) {
            return null;
        }

        //Convert the byte[] into a split array
        //first element is user, second is password
        String[] credentials = new String(decodedBytes).split(":", 2);

        return credentials[0];
    }
}
