package owlpackage;

import java.io.File;
import java.util.stream.Stream;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
public class Second {
	private final String baseIRI = 
			""; //Put the link of the ontology as per your computer/link
	private OWLOntology ontology;
	private OWLOntologyManager manager;
	private OWLReasoner reasoner;
	private OWLDataFactory dataFactory;
	
	private void loadOntologyReasoner(String ontFilePath) {
		File ontFile = new File(ontFilePath);
		manager = OWLManager.createOWLOntologyManager();
		try {
			ontology = manager.loadOntologyFromOntologyDocument(IRI.create(ontFile));
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
		Configuration configuration = new Configuration();
		reasoner = new Reasoner(configuration, ontology);
		dataFactory = manager.getOWLDataFactory();
	}
	
	public void checkClassSubsumption(String ontFilePath) {
		loadOntologyReasoner(ontFilePath);	
		OWLClass oc = dataFactory.getOWLClass(IRI.create(baseIRI + "Genre"));
		Stream<OWLClass> superClasses = reasoner.getSuperClasses(oc).entities();
		superClasses.forEach(cl -> System.out.println(cl.getIRI().getFragment()));
	}
	
	public void checkInstanceMembership(String ontFilePath) {
		loadOntologyReasoner(ontFilePath);
		OWLNamedIndividual namedIndividual = 
				dataFactory.getOWLNamedIndividual(IRI.create(baseIRI + "Song"));
		Stream<OWLClass> instanceTypes = reasoner.getTypes(namedIndividual).entities();
		instanceTypes.forEach(cl -> System.out.println(cl.getIRI().getFragment()));
	}
	
	public static void main(String[] args) {
		String ontFilePath = "src/music-ontology.owl";
		Second intelligentChatbot = new Second();		
		intelligentChatbot.checkClassSubsumption(ont]FilePath);
//		intelligentChatbot.checkInstanceMembership(ontFilePath);
	}

}
