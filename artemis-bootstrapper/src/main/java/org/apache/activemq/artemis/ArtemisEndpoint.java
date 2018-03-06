package org.apache.activemq.artemis;


import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;


@Path("/artemis")
@ApplicationScoped
public class ArtemisEndpoint {

	@Inject
	private ArtemisBootstrapService artemisBootstrapService;

	@GET
	@Produces("text/plain")
	public Response doGet() {
		return Response.ok("Bootstrap Available!").build();
	}

    @GET
    @Produces("text/plain")
    @Path("/start")
    public Response doStart(@PathParam("clean") Boolean clean) {
	    artemisBootstrapService.start(clean);
        return Response.ok("starting Artemis Broker").build();
    }

    @GET
    @Produces("text/plain")
    @Path("/stop")
    public Response doStop() {
        artemisBootstrapService.stop();
        return Response.ok("stopping Artemis Broker").build();
    }

    @GET
    @Produces("text/plain")
    @Path("/kill")
    public Response doKill() {
        artemisBootstrapService.kill();
        return Response.ok("killing Artemis Broker").build();
    }
}