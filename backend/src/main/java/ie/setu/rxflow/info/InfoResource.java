package ie.setu.rxflow.info;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Basic service information, used to check that the API is reachable.
 * Liveness and readiness (including the database connection) are exposed
 * separately by SmallRye Health at /q/health.
 */
@Path("/api/info")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Info")
public class InfoResource {

    @ConfigProperty(name = "quarkus.application.name")
    String name;

    @ConfigProperty(name = "quarkus.application.version")
    String version;

    @GET
    @Operation(summary = "Get the service name and version")
    public ServiceInfo info() {
        return new ServiceInfo(name, version);
    }

    public record ServiceInfo(String name, String version) {
    }
}
