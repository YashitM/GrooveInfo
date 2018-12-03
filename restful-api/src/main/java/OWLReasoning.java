import java.io.File;
import java.util.Set;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import com.clarkparsia.owlapi.explanation.BlackBoxExplanation;
import com.clarkparsia.owlapi.explanation.HSTExplanationGenerator;

public class OWLReasoning {

    private final String baseIRI =
            "http://webprotege.stanford.edu/project/JwpOBJx0CbVBVRa8DvxL3";
    private OWLOntology ontology;
    private OWLOntologyManager manager;
    private OWLReasoner reasoner;
    private OWLDataFactory dataFactory;
    private ReasonerFactory reasonerFactory;

    private void loadOntologyReasoner(String ontFilePath) {
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

    public boolean checkConsistency(String ontFilePath) {
        loadOntologyReasoner(ontFilePath);
        return reasoner.isConsistent();
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
        String ontFilePath = "src/ontology/OM2.owl";
        OWLReasoning reasonerSample2 = new OWLReasoning();
        reasonerSample2.getExplanation(ontFilePath);
        boolean consistent = reasonerSample2.checkConsistency(ontFilePath);
        System.out.println("IN");
        reasonerSample2.checkConsistency(ontFilePath);

    }
}