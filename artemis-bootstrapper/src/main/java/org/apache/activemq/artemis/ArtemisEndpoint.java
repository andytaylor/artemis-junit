package org.apache.activemq.artemis;


import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLDecoder;


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
   public Response doStart(@DefaultValue("true") @QueryParam("clean") Boolean clean,
                           @QueryParam("configuration") String configuration,
                           @QueryParam("artemisCreateCommand") String artemisCreateCommand) {
      try {
         //not sure why but xml isn't decoded
         artemisBootstrapService.start(clean, URLDecoder.decode(configuration, "UTF-8"), artemisCreateCommand);
      } catch (Throwable e) {
         return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
      }
      return Response.ok("starting Artemis Broker").build();
   }

   @GET
   @Produces("text/plain")
   @Path("/stop")
   public Response doStop(@DefaultValue("true") @PathParam("wait") Boolean wait) {
      try {
         artemisBootstrapService.stop(wait);
      } catch (Throwable e) {
         return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
      }
      return Response.ok("stopping Artemis Broker").build();
   }

   @GET
   @Produces("text/plain")
   @Path("/kill")
   public Response doKill() {
      try {
         artemisBootstrapService.kill();
      } catch (Throwable e) {
         return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
      }
      return Response.ok("killing Artemis Broker").build();
   }
}