package technicapp;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.xerces.impl.dv.util.Base64;

/**
 * Klasa stanowi jedynie opakowanie na funkcję do generacji ontologii badania.
 * @author Ciziu
 */
public class ExamOntologyGenerator {
    
    // Blokujemy możliwość skonstruowania egzemplarza klasy.
    private ExamOntologyGenerator()
    {}
   
    /* Funkcja generuje model ontologii na podstawie danych o pacjencie, techniku i
    lokalizacji binarnego pliku badania */
    public static OntModel GenerateExamOntology(PatientData selPatient,
            TechnicData techData, File examFile)
    {
        // Wczytujemy pustą ontologię badania
        OntModel examModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
        examModel.read("res/exam.owl");
        
        // Namespace ontologii badania
        String NS = examModel.getNsPrefixURI("");
        // Namespace importowanej ontologii pacjenta
        String baseNS = examModel.getNsPrefixURI("base");
        
        // Tworzymy egzemplarz technika i spisujemy jego parametry z techData
        Individual technic = examModel.createIndividual(NS + techData.idNumber, examModel.getOntClass(NS + "Technic"));
        technic.addLiteral(examModel.getDatatypeProperty(baseNS + "first_name"), techData.firstName);
        technic.addLiteral(examModel.getDatatypeProperty(baseNS + "surname"), techData.surname);
        technic.addLiteral(examModel.getDatatypeProperty(baseNS + "email"), techData.email);
        technic.addLiteral(examModel.getDatatypeProperty(baseNS + "phone_number"), techData.phoneNumber);
        technic.addLiteral(examModel.getDatatypeProperty(NS + "id_number"), techData.idNumber);
        
        // Tworzymy egzemplarz badania
        Individual exam = examModel.createIndividual(examModel.getOntClass(NS + "Exam"));
        // Ustawiamy relację z technikiem
        exam.addProperty(examModel.getObjectProperty(NS + "hasTechnic"), technic);
        // Relację z wybranym pacjentem
        exam.addProperty(examModel.getObjectProperty(NS + "hasPatient"),
                examModel.getIndividual(baseNS + selPatient.peselNumber));
        // Zapisujemy aktualną datę i czas, w formacie zgodnym z XMLSchema
        exam.addProperty(examModel.getDatatypeProperty(NS + "timestamp"),
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()));
        
        // Wczytujemy bajtową zawartość pliku, kodujemy ją za pomocą base64 i dodajemy do badania
        try {
            byte[] bytes = Files.readAllBytes(examFile.toPath());
            exam.addProperty(examModel.getDatatypeProperty(NS + "data"), Base64.encode(bytes));
        } catch (IOException ex) {
            Logger.getLogger(ExamOntologyGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return examModel;
    }
}
