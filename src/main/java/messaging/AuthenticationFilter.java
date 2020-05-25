package messaging;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.internal.util.Base64;

@Provider
public class AuthenticationFilter implements javax.ws.rs.container.ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    private static final String AUTH_SCHEME = "Basic";

    @Override
    public void filter(ContainerRequestContext crc) {

        Method method = resourceInfo.getResourceMethod();

        // Only check access if PermitAll is not present
        if( ! method.isAnnotationPresent(PermitAll.class)) {

            if(method.isAnnotationPresent(DenyAll.class)) {
                crc.abortWith(Response
                        .status(Response.Status.FORBIDDEN)
                        .entity("Access to this resource is forbidden")
                        .build());
                return;
            }

            String[] usernameAndPassword = getUserAndPwFromContext(crc);

            if(usernameAndPassword == null) {
                crc.abortWith(Response
                        .status(Response.Status.UNAUTHORIZED)
                        .entity("Access to this resource is denied")
                        .build());
                return;
            }

            final String username = usernameAndPassword[0];
            final String password = usernameAndPassword[1];

            if(method.isAnnotationPresent(RolesAllowed.class)) {
                RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
                Set<String> rolesSet = new HashSet<>(Arrays.asList(rolesAnnotation.value()));

                if( ! isUserAllowed(username, password, rolesSet)) {
                    crc.abortWith(Response
                            .status(Response.Status.UNAUTHORIZED)
                            .entity("Access to this resource is denied")
                            .build());
                    return;
                }
            }
        }
    }

    private boolean isUserAllowed(final String username, final String password, final Set<String> rolesSet) {

        /*
         * This method would be used to check if the supplied user has one of the roles part of the `rolesSet`
         * against a user management service or a database or something like that.
         *
         * Now it only has some hardcoded values for testing purposes.
         *
         */

        if((username.equals("linda") || username.equals("anna")) && password.equals("password")) {

            String userRole = "USER";

            if(rolesSet.contains(userRole)) {
                return true;
            }
        }
        return false;
    }

    public static String[] getUserAndPwFromContext(ContainerRequestContext crc) {

        final MultivaluedMap<String, String> headers = crc.getHeaders();

        final List<String> auth = headers.get(HttpHeaders.AUTHORIZATION);

        if(auth == null || auth.isEmpty()) {
            return null;
        }

        // Get encoded username and password
        final String encodedPassword = auth.get(0).replaceFirst(AUTH_SCHEME + " ", "");

        String[] usernameAndPassword = new String(Base64
                .decode(encodedPassword.getBytes()))
                .split(":", 2);

        return usernameAndPassword;
    }

}
