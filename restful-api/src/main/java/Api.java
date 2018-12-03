import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.io.File;
import java.util.Scanner;
import java.util.Set;

@Path("/reasoner")
public class Api {
    @GET
    @Produces("text/plain")
    public String getClichedMessage(@QueryParam("query") String query) {
        String ontFilePath = "src/ontology/OM2.owl";
        Scanner in = new Scanner(System.in);
        OWLReasoning reasonerSample2 = new OWLReasoning();
        return "Your query was: " + query + String.valueOf(true);
//        boolean consistent = reasonerSample2.checkConsistency(ontFilePath);
//        reasonerSample2.getExplanation(ontFilePath);
    }

}

