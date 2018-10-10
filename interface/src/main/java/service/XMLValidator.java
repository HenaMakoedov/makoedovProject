package service;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Makoiedov.H on 11/30/2017.
 */
public class XMLValidator {
    /**
     * Method checks the file for validity
     * @param xmlFile
     * @throws SAXException
     * @throws IOException
     */
    public static void validateXMLFile(File xmlFile) throws SAXException, IOException {
        ClassLoader classLoader = XMLValidator.class.getClassLoader();
        InputStream XSDFile = classLoader.getResourceAsStream("checker.xsd");
        Source xmlSource = new StreamSource(xmlFile);
        Source xsdSource = new StreamSource(XSDFile);
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(xsdSource);
            Validator validator = schema.newValidator();
            validator.validate(xmlSource);
        } finally {
            XSDFile.close();
        }
    }
}
