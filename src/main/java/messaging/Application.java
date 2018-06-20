package messaging;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/")
public class Application extends ResourceConfig {

    public Application() {
        packages(this.getClass().getPackage().getName());
    }

}
