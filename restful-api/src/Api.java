import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/reasoner")
public class Api {
    @GET
    @Produces("text/plain")
    public String getClichedMessage(@QueryParam("query") String query) {
        return "Your query was: " + query;
    }
}