package technicapp;

import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.util.StoreUtils;
import java.sql.SQLException;

/**
 * Klasa startowa programu
 * @author Ciziu
 */
public class TechnicApp {

    public static void main(String[] args) throws SQLException {
        new MainWindow().setVisible(true);
    }
    
}
