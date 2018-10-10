package serializers;
/**
 * Created by Makoiedov.H on 9/1/2017.
 */

import org.w3c.dom.*;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import treerealization.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;


/**
 * Created by Makoiedov.H on 8/30/2017.
 */
public class XMLSerializer {
    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;

    /**
     * default constructor
     * sets up DocumentBuilderFactory and
     * DocumentBuilder
     */
    public XMLSerializer() {
        factory = DocumentBuilderFactory.newInstance();
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * This is support recursive method for serialize a tree
     * into document using XML approach
     * @param node current node
     * @param elementStack temporary stack of elements to build a document
     * @param document under construction document
     */
    private void recSerialize(treerealization.Node node, Stack<Element> elementStack, Document document) {
        if (node == null) return;

        Element currentElement = document.createElement(node.getName());

        for(Map.Entry<String, String> pair : node.getAttributes().entrySet()) {
            currentElement.setAttribute(pair.getKey(), pair.getValue());
        }
        Text text = document.createTextNode(node.getText());
        currentElement.appendChild(text);

        if (elementStack.empty()) {
            document.appendChild(currentElement);
        } else {
           elementStack.peek().appendChild(currentElement);
        }

        elementStack.push(currentElement);
        for(int i = 0; i < node.getChildren().size(); i++) {
            recSerialize(node.getChildren().get(i), elementStack, document);
        }
        elementStack.pop();
    }

    /**
     * Method serialize current tree into you file
     * using XML approach
     * @param tree selected tree that will be serialized
     * @param file file that will contain your serialized tree
     * @throws ParserConfigurationException
     * @throws TransformerException
     * @throws IOException
     */
    public void serialize(Tree tree, File file) throws ParserConfigurationException, TransformerException, IOException{
        Document document = builder.newDocument();

        Stack<Element> elementStack = new Stack<>();
        recSerialize(tree.getRoot(), elementStack, document);

        //display setting
        DOMImplementation impl = document.getImplementation();
        DOMImplementationLS implLS = (DOMImplementationLS)impl.getFeature("LS", "3.0");
        LSSerializer serializer = implLS.createLSSerializer();;
        serializer.getDomConfig().setParameter("format-pretty-print", true);
        LSOutput output = implLS.createLSOutput();
        output.setEncoding("UTF-8");
        ////////////////////////////////////////////////////////////////////////////
        output.setByteStream(new FileOutputStream(file));
        serializer.write(document, output);
    }

    /**
     * This is support recursive method for deserialize a tree
     * from a file using XML approach
     * @param currentElement current XML element
     * @param node current node
     */
    private void recDeserialize(Element currentElement, treerealization.Node node) {
        if (node == null) {
            return;
        }
        Map<String, String> nodeAttributes = new HashMap<String, String>();
        List<treerealization.Node> listNodes = new NodeChildrenList(node);

        node.setName(currentElement.getTagName());
        node.setAttributes(nodeAttributes);
        node.setChildren(listNodes);

        //set node text
        if (currentElement.hasChildNodes()) {
            String text = currentElement.getFirstChild().getNodeValue();
            String[] split = text.split("\n");
            if (split.length > 0) {
                if (split[0].equals("")) {
                    node.setText(null);
                } else {
                    node.setText(split[0]);
                }
            }
        }

        //set node attributes
        NamedNodeMap elementAttributes = currentElement.getAttributes();
        for(int i = 0; i < elementAttributes.getLength(); i++) {
            Node attribute = elementAttributes.item(i);
            nodeAttributes.put(attribute.getNodeName(), attribute.getNodeValue());
        }

        //set node children
        NodeList nodeList = currentElement.getChildNodes();
        for(int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i) instanceof Element){
                treerealization.Node tmpNode = new treerealization.Node();
                listNodes.add(tmpNode);

                Element tmpElement = (Element)nodeList.item(i);
                recDeserialize(tmpElement, tmpNode);
            }
        }
    }

    /**
     * Method deserialize tree from you file
     * using XML approach
     * @param file file with serialized tree
     * @return deserialize tree
     * @throws IOException
     * @throws SAXException
     */
    public Tree deserialize(File file) throws IOException, SAXException{
        Document document;
        try {
            document = builder.parse(file);
        } catch (SAXParseException e) {
            return null;
        }
        treerealization.Node rootNode = new treerealization.Node();
        Element rootElement = document.getDocumentElement();


        recDeserialize(rootElement, rootNode);

        return new Tree(rootNode);
    }
}
