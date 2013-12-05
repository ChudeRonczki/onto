package technicapp;

/**
 * Struktura danych zawierająca dane pojedynczego pacjenta.
 * Pola klasy odzwierciedlają 1:1 data properties w ontologii.
 * @author Ciziu
 */
public class PatientData extends PersonData {
    public String peselNumber; // Służy również jako unikalny identyfikator
}
