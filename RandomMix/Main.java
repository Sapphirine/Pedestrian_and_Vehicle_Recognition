import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Created by park on 12/17/16.
 */
public class Main {
    private final static String IMGPATH = "/Users/park/Desktop/test/positive/img/";
    private final static String XMLPATH = "/Users/park/Desktop/test/positive/xml/";
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, TransformerException {
        File img = new File(IMGPATH);
        File[] allimg = img.listFiles();
        int num = allimg.length;
        Random r = new Random();
        for (int i = num; i > 1; i--) {
            int idx = r.nextInt(i - 1) + 1;
            String name = allimg[idx].getName();
            System.out.println(name);
            StringBuilder sb = new StringBuilder(name);
            sb.setLength(sb.length() - 4);
            name = sb.toString();
            String newImg = IMGPATH + String.valueOf(i - 2) + ".jpg";
            allimg[idx].renameTo(new File(newImg));

            String newXml = XMLPATH + String.valueOf(i - 2) + ".xml";
            String oldXml = XMLPATH + name + ".xml";
            new File(oldXml).renameTo(new File(newXml));
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(newXml);
            NodeList list = doc.getElementsByTagName("filename");
            for (int j = 0; j < list.getLength(); j++) {
                Element ele = (Element) list.item(j);
                ele.setTextContent(String.valueOf(i - 2) + ".jpg");
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(doc);
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            StreamResult result = new StreamResult(new FileOutputStream(newXml));
            transformer.transform(domSource, result);

            File temp = allimg[idx];
            allimg[idx] = allimg[i - 1];
            allimg[i - 1] = temp;
        }
    }
}
