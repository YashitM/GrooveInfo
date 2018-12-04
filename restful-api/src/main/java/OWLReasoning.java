import java.io.File;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Stream;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;

public class OWLReasoning {

    private static final String baseIRI =
            "http://www.semanticweb.org/nirmita/ontologies/2018/11/untitled-ontology-14#";
    private static OWLOntology ontology;
    private static OWLOntologyManager manager;
    private static OWLReasoner reasonerCon, reasoner;
    private static OWLDataFactory dataFactory, dataFactoryCon;

    private static ReasonerFactory reasonerFactoryCon;

    private static void loadOntologyReasoner(String ontFilePath) {
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

    public static ArrayList<String> checkClassSubsumption(String ontFilePath, String classItem) {
        loadOntologyReasoner(ontFilePath);
        ArrayList<String> classes = new ArrayList<>();
        OWLClass oc = dataFactory.getOWLClass(IRI.create(baseIRI + classItem));
        Stream<OWLClass> superClasses = reasoner.getSuperClasses(oc).entities();
        superClasses.forEach(cl -> classes.add(cl.getIRI().getFragment()));
        return classes;
    }

    public static ArrayList<String> checkClassSupersumption(String ontFilePath, String classItem) {
        loadOntologyReasoner(ontFilePath);
        ArrayList<String> classes = new ArrayList<>();
        OWLClass oc = dataFactory.getOWLClass(IRI.create(baseIRI + classItem));
        Stream<OWLClass> superClasses = reasoner.getSubClasses(oc).entities();
        superClasses.forEach(cl -> classes.add(cl.getIRI().getFragment()));
        return classes;
    }

    public static ArrayList<String> checkPropertySupersumption(String ontFilePath, String classItem) {
        loadOntologyReasoner(ontFilePath);
        ArrayList<String> classes = new ArrayList<>();
        OWLObjectProperty oc = dataFactory.getOWLObjectProperty(IRI.create(baseIRI + classItem));
        Stream<OWLObjectPropertyExpression> subProperties = reasoner.getSubObjectProperties(oc).entities();
        subProperties.forEach(cl -> classes.add(cl.getNamedProperty().toString().replace(baseIRI,"").replace("<","").replace(">","")));
        return classes;
    }

    public static ArrayList<String> checkPropertySubsumption(String ontFilePath, String classItem) {
        loadOntologyReasoner(ontFilePath);
        ArrayList<String> classes = new ArrayList<>();
        OWLObjectProperty oc = dataFactory.getOWLObjectProperty(IRI.create(baseIRI + classItem));
        Stream<OWLObjectPropertyExpression> subProperties = reasoner.getSuperObjectProperties(oc).entities();
        subProperties.forEach(cl -> classes.add(cl.getNamedProperty().toString().replace(baseIRI,"").replace("<","").replace(">","")));
        return classes;
    }

//    public static ArrayList<String> getExplanation(String ontFilePath, String classItem) {
//        loadConsistencyOntologyReader(ontFilePath);
//        ArrayList<String> classes = new ArrayList<>();
//        BlackBoxExplanation explanation =
//                new BlackBoxExplanation(ontology, reasonerFactoryCon, reasonerCon);
//        HSTExplanationGenerator multiExplanator = new HSTExplanationGenerator(explanation);
////        OWLClass ac = dataFactoryCon.getOWLClass(IRI.create(baseIRI + classItem));
//        OWLAxiom axiom = new OWLAxiom("asdf");
//        Set<OWLAxiom> axioms = multiExplanator.getExplanation(ac);
//        axioms.forEach(ax -> classes.add(String.valueOf(ax)));
//        return classes;
//    }

    public static ArrayList<String> getClassInstances(String ontFilePath, String classItem) {
        loadOntologyReasoner(ontFilePath);
        ArrayList<String> instances = new ArrayList<>();
        OWLClass oc = dataFactory.getOWLClass(IRI.create(baseIRI + classItem));
        Stream<OWLNamedIndividual> namedIndividualStream = reasoner.getInstances(oc).entities();
        namedIndividualStream.forEach(cl -> instances.add(cl.getIRI().getFragment()));
        return instances;
    }

    public static ArrayList<String> checkInstanceMembership(String ontFilePath, String instance) {
        loadOntologyReasoner(ontFilePath);
        ArrayList<String> classes = new ArrayList<>();
        OWLNamedIndividual namedIndividual =
                dataFactory.getOWLNamedIndividual(IRI.create(baseIRI + instance));
        Stream<OWLClass> instanceTypes = reasoner.getTypes(namedIndividual).entities();
        instanceTypes.forEach(cl -> classes.add(cl.getIRI().getFragment()));
        return classes;
    }

    private static void loadConsistencyOntologyReader(String ontFilePath) {
        File ontFile = new File(ontFilePath);
        manager = OWLManager.createOWLOntologyManager();
        try {
            ontology = manager.loadOntologyFromOntologyDocument(IRI.create(ontFile));
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
        Configuration configuration = new Configuration();
        configuration.throwInconsistentOntologyException = false;
        reasonerFactoryCon = new ReasonerFactory() {
            protected OWLReasoner createHermiTOWLReasoner(Configuration configuration, OWLOntology ontology) {
                configuration.throwInconsistentOntologyException = false;
                return new Reasoner(configuration,ontology);
            }
        };
        // Reasoner created using ReasonerFactory. This is different from how Reasoner was
        // created in ReasonerSample1
        reasonerCon = reasonerFactoryCon.createReasoner(ontology, configuration);
        dataFactoryCon = manager.getOWLDataFactory();
    }

    public static void add(String ontFilePath, String classItem) {
        loadOntologyReasoner(ontFilePath);
        Set<OWLOntology> importsClosure = ontology.getImportsClosure();
        OWLEntityChecker entityChecker = new ShortFormEntityChecker(
                new BidirectionalShortFormProviderAdapter(manager, importsClosure,
                        new SimpleShortFormProvider()));
        ManchesterOWLSyntaxParser parser = OWLManager.createManchesterParser();
        parser.setDefaultOntology(ontology);
        parser.setOWLEntityChecker(entityChecker);
        parser.setStringToParse(classItem);
        OWLAxiom axiom = parser.parseAxiom();
        manager.addAxiom(ontology, axiom);
    }

    public static boolean checkConsistency(String ontFilePath) {
        loadConsistencyOntologyReader(ontFilePath);
        return reasonerCon.isConsistent();
    }

    public static String parseQuery(String query, String ontPath) {
        if (query.contains("SubClassOf")) {
            String[] classes = query.split(" SubClassOf ");

            if (classes[0].contains("?")) {
                ArrayList<String> subClasses = checkClassSupersumption(ontPath, classes[1]);
                String output = subClasses.toString() + " is/are a SubClassOf " + classes[1];
                return output;
            } else if (classes[1].contains("?")) {
                ArrayList<String> superClasses = checkClassSubsumption(ontPath, classes[0]);
                String output = classes[0] + " is a SubClassOf " + superClasses.toString();
                return output;
            } else {
                ArrayList<String> superClasses = checkClassSubsumption(ontPath, classes[0]);

                for (String superClass : superClasses) {
                    if (superClass.equals(classes[1])) {

                        String output = classes[0] + " is a SubClassOf " + classes[1];
                        System.out.println(output);
                        return output;
                    }
                }
                String output = classes[0] + " is not a SubClassOf " + classes[1];
                System.out.println(output);
                return output;
            }
        } else if (query.contains("InstanceOf")) {
            String[] classes = query.split(" InstanceOf ");

            if (classes[0].contains("?")) {
                ArrayList<String> instances = getClassInstances(ontPath, classes[1]);
                String output = instances.toString() + " is/are InstanceOf " + classes[1];
                return output;
            } else if (classes[1].contains("?")) {
                ArrayList<String> instanceClass = checkInstanceMembership(ontPath, classes[0]);
                String output = classes[0] + " is an InstanceOf " + instanceClass.toString();
                return output;
            } else {
                ArrayList<String> instanceClass = checkInstanceMembership(ontPath, classes[0]);

                for (String superClass : instanceClass) {
                    if (superClass.equals(classes[1])) {
                        String output = classes[0] + " is an InstanceOf " + classes[1];
                        System.out.println(output);
                        return output;
                    }
                }
                String output = classes[0] + " is not an InstanceOf " + classes[1];
                System.out.println(output);
                return output;
            }
        } else if (query.contains("Consistent")) {
            String output = "Ontology is ";
            if (checkConsistency(ontPath)) {
                output += "Consistent";
            } else {
                output += "Inconsistent";
            }
            return output;
        } else if (query.contains("Add:")) {
            String[] classes = query.split("Add: ");
            System.out.println(classes[1]);
            add(ontPath, classes[1]);
            String output = "Axiom: " + classes[1] + " executed";
            return output;
        } else if (query.contains("GetTypes")) {
            String[] classes = query.split("GetTypes ");
            ArrayList<String> types = checkInstanceMembership(ontPath, classes[1]);
            String output = types.toString() + " is/are Types of " + classes[1];
            return output;
        } else if (query.contains("SubPropertyOf")) {
            String[] classes = query.split(" SubPropertyOf ");

            if (classes[0].contains("?")) {
                ArrayList<String> instances = checkPropertySupersumption(ontPath, classes[1]);
                String output = instances.toString() + " is/are SubPropertyOf " + classes[1];
                return output;
            } else if (classes[1].contains("?")) {
                ArrayList<String> instanceClass = checkPropertySubsumption(ontPath, classes[0]);
                String output = classes[0] + " is a SubPropertyOf " + instanceClass.toString();
                return output;
            } else {
                ArrayList<String> instanceClass = checkPropertySubsumption(ontPath, classes[0]);

                for (String superClass : instanceClass) {
                    if (superClass.equals(classes[1])) {
                        String output = classes[0] + " is a SubPropertyOf " + classes[1];
                        System.out.println(output);
                        return output;
                    }
                }
                String output = classes[0] + " is not a SubPropertyOf " + classes[1];
                System.out.println(output);
                return output;
            }
        }
        String output = "LEL";
        return output;
    }

    public static void main(String[] args) {
        String ontFilePath = "src/ontology/OM2.owl";

//        String query = "Male SubClassOf ?";
//        String query = "? SubClassOf Gender";
//        String query = "? InstanceOf Albums";
//        String query = "Vaaqif InstanceOf ?";
//        String query = "OneThing InstanceOf Songs";
//        String query = "IsConsistent?";
//        String query = "Add: Song and (fromAlbum some {UpAllNight})";
//        String query = "GetTypes OneDirection";
//        String query = "? SubPropertyOf realizedBy";
//        String query = "hasGenre SubPropertyOf ?";
        String query = "hasGenre SubPropertyOf realizedBy";
//        String query = "CharacteristicsOf realizedBy";
        System.out.println(parseQuery(query, ontFilePath));
    }
}