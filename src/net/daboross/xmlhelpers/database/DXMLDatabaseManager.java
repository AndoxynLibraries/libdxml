package net.daboross.xmlhelpers.database;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.daboross.xmlhelpers.DXMLException;
import net.daboross.xmlhelpers.DXMLHelper;
import org.w3c.dom.Node;

/**
 *
 * @author daboross
 */
public class DXMLDatabaseManager {

    private final DXMLDatabase mainDatabase;
    private final Map<String, DXMLDatabase> dataBaseMap = new HashMap<>();

    public DXMLDatabaseManager() throws DXMLException {
        mainDatabase = new DXMLDatabase();

    }

    /**
     * This gets the data under the given name, or creates a new one if it
     * doesn't exist.
     */
    public DXMLDatabase getDatabase(String name) {
        if (dataBaseMap.containsKey(name)) {
            return dataBaseMap.get(name);
        } else {
            DXMLDatabase newDatabase = new DXMLDatabase(mainDatabase.document, mainDatabase.newNode(name));
            dataBaseMap.put(name, newDatabase);
            return newDatabase;
        }
    }

    public DXMLDatabaseManager(File file) throws DXMLException {
        mainDatabase = new DXMLDatabase(file);
        List<String> keyList = mainDatabase.getAllNodeKeys();
        for (String str : keyList) {
            Node n;
            try {
                n = mainDatabase.getNode(str);
            } catch (WrongTypeException | EntryNotFoundException ex) {
                Logger.getLogger(DXMLDatabaseManager.class.getName()).log(Level.SEVERE, "Exception While getting value from key in DXMLDatabase.getAllNodeKeys()", ex);
                continue;
            }
            DXMLDatabase dxmld = new DXMLDatabase(mainDatabase.document, n);
            dataBaseMap.put(str, dxmld);
        }
    }

    public void saveToFile(File file) throws DXMLException {
        mainDatabase.pushValuesToDocument();
        DXMLHelper.writeXML(mainDatabase.document, file);
    }
}
