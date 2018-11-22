package owlpackage;
import java.io.File;
import java.util.stream.Stream;
import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

public class First {
	
	private final String baseIRI="";
	private OWLOntology ontology;
	private OWLOntologyManager manager;
	private OWLReasoner reasoner;
	private OWLDataFactory dataFactory;
	private ReasonerFactory reasonerFactory;
	
	private void loadOntologyReasoner(String ontFilePath){
		File ontFile = new File(ontFilePath);
		manager = OWLManager.createOWLOntologyManager();
	
	try {
		ontology = manager.loadOntologyFromOntologyDocument(IRI.create(ontFile));
	} catch (OWLOntologyCreationException e) {
		e.printStackTrace();
	}
	Configuration configuration = new Configuration();
	configuration.throwInconsistentOntologyException = false;
	reasonerFactory = new ReasonerFactory() {
        protected OWLReasoner createHermiTOWLReasoner(Configuration configuration, OWLOntology ontology) {
            configuration.throwInconsistentOntologyException = false;
            return new Reasoner(configuration,ontology);
        }  
	};
	reasoner = reasonerFactory.createReasoner(ontology, configuration);
	dataFactory = manager.getOWLDataFactory();
}

	
        public void checkConsistency(String ontFilePath) {
    		loadOntologyReasoner(ontFilePath);
    		System.out.println("Is the ontology consistent? " + reasoner.isConsistent());
    	}
        
        public void getExplanation(String ontFilePath) {
    		loadOntologyReasoner(ontFilePath);
    		BlackBoxExplanation explanation = 
    				new BlackBoxExplanation(ontology, reasonerFactory, reasoner);
    		HSTExplanationGenerator multiExplanator = new HSTExplanationGenerator(explanation);
    		OWLClass ac = dataFactory.getOWLClass(IRI.create(baseIRI + "AndhraCuisine"));
    		Set<OWLAxiom> axioms = multiExplanator.getExplanation(ac);
    		axioms.forEach(ax -> System.out.println(ax));
    	}

	public static void main(String[] args) {
		String ontFilePath = "src/music-ontology.owl";
		First reasonerSample=new First();
		reasonerSample.checkConsistency(ontFilePath);
		reasonerSample.getExplanation(ontFilePath);
		
	}

}
