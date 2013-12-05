package technicapp;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.util.StoreUtils;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Klasa odpowiedzialna za wczytanie (lub przygotowanie) i zarządzanie ontologią pacjentów.
 * Wewnętrznie korzysta z API Jeny o nazwie SDB, przeznaczonego do przechowywania ontologii
 * w relacyjnej bazie danych.
 * @author Ciziu
 */
public class PatientStorage {
    Store dbStore;
    Model baseModel;
    OntModel ontModel;
    
    String NS;
    OntClass patientClass;
    DatatypeProperty firstNameProp, surnameProp, emailProp, phoneNumberProp,
            peselNumberProp;
    
    public PatientStorage() {
        try {
            dbStore = SDBFactory.connectStore("res/dbconf.ttl");
            
            // Jeżeli baza jest niesformatowana, wczytujemy ontologię z pliku
            if (!StoreUtils.isFormatted(dbStore))
            {
                dbStore.getTableFormatter().format();
                baseModel = SDBFactory.connectDefaultModel(dbStore);
                ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, baseModel);
                ontModel.read("res/base.owl");
            }
            // W przeciwnym wypadku czytamy model z bazy
            else
            {
                baseModel = SDBFactory.connectDefaultModel(dbStore);
                ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, baseModel);
            }
            
            // Zapisujemy namespace tej ontologii
            NS = ontModel.getNsPrefixURI("");
            
            /* Wymuszamy na Jenie wrzucenie tej ontologii do cache'a (czego,
            nie wiadomo dlaczego, nie robi tutaj automatycznie) */
            ontModel.getDocumentManager().getFileManager().addCacheModel(NS.substring(0, NS.length() - 1), ontModel);
            
            /* Pobieramy klasę pacjenta i datatype properties na potrzeby
            dalszych operacji na bazie. */
            patientClass = ontModel.getOntClass(NS + "Patient");
            firstNameProp = ontModel.getDatatypeProperty(NS + "first_name");
            surnameProp = ontModel.getDatatypeProperty(NS + "surname");
            emailProp = ontModel.getDatatypeProperty(NS + "email");
            phoneNumberProp = ontModel.getDatatypeProperty(NS + "phone_number");
            peselNumberProp = ontModel.getDatatypeProperty(NS + "pesel");
        } catch (SQLException ex) {
            Logger.getLogger(PatientStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Funkcja wczytuje aktualną bazę pacjentów z ontologii
    public ArrayList<PatientData> GetPatientsList()
    {
        ArrayList<PatientData> patientsList = new ArrayList<>();
        for (Iterator<Individual> i = ontModel.listIndividuals(patientClass); i.hasNext(); )
        {
            Individual patient = i.next();
            PatientData patientData = new PatientData();
            patientData.firstName = patient.getProperty(firstNameProp).getString();
            patientData.surname = patient.getProperty(surnameProp).getString();
            patientData.email = patient.getProperty(emailProp).getString();
            patientData.phoneNumber = patient.getProperty(phoneNumberProp).getString();
            patientData.peselNumber = patient.getProperty(peselNumberProp).getString();
            patientsList.add(patientData);
        }
        return patientsList;
    }
    
    // Funkcja dodaje nowego pacjenta do ontologii na podstawie paczki z danymi
    public void AddPatient(PatientData newPatient)
    {
        Individual ontPatient =
                ontModel.createIndividual(NS + newPatient.peselNumber, patientClass);
        ontPatient.addLiteral(firstNameProp, newPatient.firstName);
        ontPatient.addLiteral(surnameProp, newPatient.surname);
        ontPatient.addLiteral(emailProp, newPatient.email);
        ontPatient.addLiteral(phoneNumberProp, newPatient.phoneNumber);
        ontPatient.addLiteral(peselNumberProp, newPatient.peselNumber);
    }
}
