package net.daboross.xmlhelpers.database;

import java.util.List;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.daboross.xmlhelpers.DXMLException;
import net.daboross.xmlhelpers.DXMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author daboross
 */
public class DXMLDatabase {

    protected final Document document;
    private Node root;
    private final Node parentOfRoot;
    private final Map<String, Node> elementMap = new HashMap<>();

    public DXMLDatabase() throws DXMLException {
        try {
            document = DXMLHelper.newDocument();
        } catch (DXMLException ex) {
            Logger.getLogger(DXMLDatabase.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        parentOfRoot = document;
        root = document.createElement("dataroot");
        document.appendChild(root);
    }

    public DXMLDatabase(File file) throws DXMLException {
        try {
            document = DXMLHelper.readDocument(file);
        } catch (DXMLException ex) {
            Logger.getLogger(DXMLDatabase.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        parentOfRoot = document;
        {
            NodeList nodeList = document.getChildNodes();
            Node rootTemp = null;
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node current = nodeList.item(i);
                if (current.getNodeName().equals("dataroot")) {
                    rootTemp = current;
                    break;
                }
            }
            root = rootTemp;
            if (rootTemp == null) {
                throw new DXMLException("File not XML DATABASE File");
            }
        }
        {
            NodeList list = root.getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                if (node.getNodeName().equals("#text")) {
                    continue;
                }
                elementMap.put(node.getNodeName(), node);
            }
        }
    }

    public DXMLDatabase(Document mainDoc, Node parentNode) {
        document = mainDoc;
        Node rootTemp = null;
        {
            NodeList nodeList = parentNode.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node current = nodeList.item(i);
                if (current.getNodeName().equals("dataroot")) {
                    rootTemp = current;
                    break;
                }
            }
        }
        parentOfRoot = parentNode;
        if (rootTemp != null) {
            root = rootTemp;
            {
                NodeList list = root.getChildNodes();
                for (int i = 0; i < list.getLength(); i++) {
                    Node node = list.item(i);
                    if (node.getNodeName().equals("#text")) {
                        continue;
                    }
                    elementMap.put(node.getNodeName(), node);
                }
            }
        } else {
            root = document.createElement("dataroot");
            parentNode.appendChild(root);
        }
    }

    public void add(String key, String text) {
        if (key == null) {
            key = "null";
        }
        if (text == null) {
            text = "null";
        }
        elementMap.put(key, DXMLHelper.createElement(document, key, text));
    }

    public void add(String key, Node node) {
        if (key == null || node == null) {
            throw new IllegalArgumentException("Null Argument");
        }
        Node n = document.createElement(key);
        n.appendChild(node);
        elementMap.put(key, n);
    }

    /**
     * This creates a new Node and then stores it under the given key.
     */
    public Node newNode(String key) {
        Node n = document.createElement(key);
        add(key, n);
        return n;
    }

    public String getString(String key) throws WrongTypeException, EntryNotFoundException {
        Node n = elementMap.get(key);
        NodeList list = n.getChildNodes();
        if (list.getLength() != 1) {
            throw new EntryNotFoundException("Entry Not Valid");
        }
        Node child = list.item(0);
        if (!child.getNodeName().equals("#text")) {
            throw new WrongTypeException("Entry Not String");
        }
        return child.getNodeValue();
    }

    public Node getNode(String key) throws WrongTypeException, EntryNotFoundException {
        Node n = elementMap.get(key);
        NodeList list = n.getChildNodes();
        Node child = null;
        for (int i = 0; i < list.getLength(); i++) {
            Node current = list.item(i);
            if (current.getNodeName().equals("#text")) {
                continue;
            }
            child = current;
            break;
        }
        if (child == null) {
            throw new WrongTypeException("Entry Not Node");
        }
        return child;
    }

    public boolean isString(String key) {
        return elementMap.containsKey(key) ? getInternalString(elementMap.get(key)) != null : false;
    }

    public boolean contains(String key) {
        return elementMap.containsKey(key);
    }

    private String getInternalString(Node n) {
        NodeList list = n.getChildNodes();
        if (list.getLength() != 1) {
            return null;
        }
        Node child = list.item(0);
        if (child.getNodeName().equals("#text")) {
            return child.getNodeValue();
        }
        return null;
    }

    public void pushValuesToDocument() {
        parentOfRoot.removeChild(root);
        root = document.createElement("dataroot");
        parentOfRoot.appendChild(root);
        for (Node n : elementMap.values()) {
            root.appendChild(n);
        }
    }

    public void clearvalues() {
        elementMap.clear();
        parentOfRoot.removeChild(root);
        root = document.createElement("dataroot");
        parentOfRoot.appendChild(root);
    }

    public List<String> getAllNodeKeys() {
        List<String> list = new ArrayList<>();
        for (String str : elementMap.keySet()) {
            if (!isString(str)) {
                list.add(str);
            }
        }
        return list;
    }
}
