package technicapp;

/**
 * Struktura danych zawierająca dane pojedynczej osoby.
 * Pola klasy odzwierciedlają 1:1 data properties w ontologii.
 * @author Ciziu
 */
public class PersonData {
    public String firstName;
    public String surname;
    public String phoneNumber;
    public String email;
    
    // Potrzebne do poprawnego wyświetlania w oknie
    public String toString()
    {
        return firstName + " " + surname;
    }
}
