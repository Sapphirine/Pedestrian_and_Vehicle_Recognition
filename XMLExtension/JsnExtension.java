package com.yuan;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by park on 12/1/16.
 */
public class JsnExtension {
    //absolute path to file
    final private static String INPUTFILE = "/Users/park/Desktop/test/src/label.txt";
    //absolute path to folder
    final private static String OUTPUTPATH = "/Users/park/Desktop/test/";
    //folder
    final private static String OUTPUTFOLDER = "output";
    final private static String FILEPOSTFIX1 = "jpg";
    final private static String FILEPOSTFIX2 = "xml";
    final private static String DATABASE = "YUAN";
    final private static String WIDTH = "640";
    final private static String HEIGHT = "360";
    final private static String DEPTH = "3";
    final private static String SEGMENTED = "0";
    final private static String[] OBJECT = {"background", "car", "pedestrian", "bicycle", "sign"};
    private static final double DEFAULT_MAG = 0.8;

    public static void main(String[] args) throws IOException {
        JsnExtension solution = new JsnExtension();
        solution.extension();
    }


    public void extension() throws IOException {
        File input = new File(INPUTFILE);
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(input)));
        String line = br.readLine();
        Random random = new Random();
        while (line != null) {
            List<List<Double>> info = new ArrayList<>();
            String outputFilename = extract(line, info);
            //write 10 file
            if (info.size() != 0) {
                for (int i = -1; i < 9; i++) {
                    createXML(revisedName(outputFilename, i), revisedInfo(info, i), i);
                }
            } else {
                for (int i = -1; i < 9; i++) {
                    String oldName = revisedName(outputFilename, i);
                    BufferedImage neg = ImageIO.read(new File(OUTPUTPATH + "positive/img/" + oldName + '.' + FILEPOSTFIX1));
                    createXML(oldName, randomInfo(i, random), i);
                    //copy image and create xml
                    for (int j = 10; j < 19; j++) {
                        String newName = revisedName(oldName, j);
                        ImageIO.write(neg, "jpg", new File(OUTPUTPATH + "positive/img/" + newName + '.' + FILEPOSTFIX1));
                        createXML(newName, randomInfo(i, random), i);
                    }
                }
            }
            //write 1 file
            //File output = new File(outputPath);
            //BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
            //bw.write(new String(sb[i]));
            //bw.flush();
            //bw.close();
            line = br.readLine();
        }
    }

    public List<List<Double>> randomInfo(int i, Random r) {
        List<List<Double>> info = new ArrayList<>();
        double w = 640;
        double h = 360;
        switch(i) {
            case 0:
                h = 360;
                w = 256;
                break;
            case 1:
                h = 360;
                w = 512;
                break;
            case 2:
                h = 144;
                w = 640;
                break;
            case 3:
                h = 288;
                w = 640;
                break;
            case 4:
                h = 144;
                w = 256;
                break;
            case 5:
                h = 288;
                w = 512;
                break;
            default:
                break;
        }
        double[] x = new double[2];
        x[0] = r.nextDouble() * w;
        x[1] = r.nextDouble() * w;
        if (x[0] > x[1]) {
            double temp = x[0];
            x[0] = x[1];
            x[1] = temp;
        }
        double[] y = new double[2];
        y[0] = r.nextDouble() * h;
        y[1] = r.nextDouble() * h;
        if (y[0] > y[1]) {
            double temp = y[0];
            y[0] = y[1];
            y[1] = temp;
        }
        List<Double> temp = new ArrayList<>();
        temp.add(x[0]);
        temp.add(y[0]);
        temp.add(x[1]);
        temp.add(y[1]);
        temp.add(0.);
        info.add(temp);
        return info;
    }


    public String extract(String line, List<List<Double>> info) {
        StringBuilder sb = new StringBuilder();
        boolean hasName = false;
        String name = null;
        List<Double> bndbox = new ArrayList<>();
        int ct = 0;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            //found a start
            if (c <= '9' && c >= '0') {
                while (c <= '9' && c >= '0' || c == '.') {
                    sb.append(c);
                    c = line.charAt(++i);
                }
                if (!hasName) {
                    sb.setLength(sb.length() - 1);
                    name = new String(sb);
                    sb.setLength(0);
                    hasName = true;
                } else {
                    bndbox.add(Double.parseDouble(new String(sb)));
                    sb.setLength(0);
                    ct++;
                    if (ct == 5) {
                        info.add(new ArrayList<>(bndbox));
                        bndbox.clear();
                        ct = 0;
                    }
                }
            }
        }
        return name;
    }

    public String revisedName(String name, int i) {
        if (i == -1) {
            return name;
        } else {
            return new String(name + '_' + String.valueOf(i));
        }
    }

    public List<List<Double>> revisedInfo(List<List<Double>> info, int i) {
        List<List<Double>> res = new ArrayList<>();
        for (List<Double> list : info) {
            res.add(new ArrayList<>(list));
        }
        if (i == 0) {
            double mag = DEFAULT_MAG / 2;
            for (List<Double> list : res) {
                list.set(0, list.get(0) * mag);
                list.set(2, list.get(2) * mag);
            }
        } else if (i == 1) {
            for (List<Double> list : res) {
                list.set(0, list.get(0) * DEFAULT_MAG);
                list.set(2, list.get(2) * DEFAULT_MAG);
            }
        } else if (i == 2) {
            double mag = DEFAULT_MAG / 2;
            for (List<Double> list : res) {
                list.set(1, list.get(1) * mag);
                list.set(3, list.get(3) * mag);
            }
        } else if (i == 3) {
            for (List<Double> list : res) {
                list.set(1, list.get(1) * DEFAULT_MAG);
                list.set(3, list.get(3) * DEFAULT_MAG);
            }
//            double rad = Math.toRadians(-DEFAULT_DEGREE);
//            for (List<Double> list : res) {
//                double xmin = list.get(0);
//                double ymin = list.get(1);
//                double xmax = list.get(2);
//                double ymax = list.get(3);
//                list.set(0, Math.sqrt(Math.pow(xmin, 2) + Math.pow(ymax,2)) * Math.sin((rad + Math.atan(xmin / ymax))));
//                list.set(1, Math.sqrt(Math.pow(xmin, 2) + Math.pow(ymin,2)) * Math.cos((rad + Math.atan(xmin / ymin))));
//                list.set(2, Math.sqrt(Math.pow(xmax, 2) + Math.pow(ymin,2)) * Math.sin((rad + Math.atan(xmax / ymin))));
//                list.set(3, Math.sqrt(Math.pow(xmax, 2) + Math.pow(ymax,2)) * Math.cos((rad + Math.atan(xmax / ymax))));
//            }
        } else if (i == 4) {
            double mag = DEFAULT_MAG / 2;
            for (List<Double> list : res) {
                list.set(0, list.get(0) * mag);
                list.set(1, list.get(1) * mag);
                list.set(2, list.get(2) * mag);
                list.set(3, list.get(3) * mag);
            }
        } else if (i == 5) {
            for (List<Double> list : res) {
                list.set(0, list.get(0) * DEFAULT_MAG);
                list.set(1, list.get(1) * DEFAULT_MAG);
                list.set(2, list.get(2) * DEFAULT_MAG);
                list.set(3, list.get(3) * DEFAULT_MAG);
            }
        }
        return res;
    }

    public String nameOf(double i) {
        return OBJECT[(int)i];
    }

    public DocumentBuilder getDocumentBuilder(){
        // 创建一个DocumentBuilderFactory的对象
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        // 创建DocumentBuilder对象
        DocumentBuilder db =null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return db;
    }

    /**
     * 生成xml
     */
    public void createXML(String filename, List<List<Double>> info, int n) throws IOException {
        int w = 640;
        int h = 360;
        switch(n) {
            case 0:
                h = 360;
                w = 256;
                break;
            case 1:
                h = 360;
                w = 512;
                break;
            case 2:
                h = 144;
                w = 640;
                break;
            case 3:
                h = 288;
                w = 640;
                break;
            case 4:
                h = 144;
                w = 256;
                break;
            case 5:
                h = 288;
                w = 512;
                break;
            default:
                break;
        }
        DocumentBuilder db = getDocumentBuilder();
        Document document = db.newDocument();
        document.setXmlStandalone(true);
        Element annotation = document.createElement("annotation");
        //folder
        Element folder = document.createElement("folder");
        folder.setTextContent(OUTPUTFOLDER);
        annotation.appendChild(folder);
        //filename
        Element fileName = document.createElement("filename");
        fileName.setTextContent(filename + '.' + FILEPOSTFIX1);
        annotation.appendChild(fileName);
        //source
        Element source = document.createElement("source");

        Element database = document.createElement("database");
        database.setTextContent(DATABASE);
        source.appendChild(database);

        annotation.appendChild(source);

        //size
        Element size = document.createElement("size");

        Element width = document.createElement("width");
        width.setTextContent(String.valueOf(w));
        size.appendChild(width);

        Element height = document.createElement("height");
        height.setTextContent(String.valueOf(h));
        size.appendChild(height);

        Element depth = document.createElement("depth");
        depth.setTextContent(DEPTH);
        size.appendChild(depth);

        annotation.appendChild(size);

        //segmented
        Element segmented = document.createElement("segmented");
        segmented.setTextContent(SEGMENTED);
        annotation.appendChild(segmented);

        //objects
        for (int i = 0; i < info.size(); i++) {
            Element object = document.createElement("object");

            //name
            Element name = document.createElement("name");
            name.setTextContent(nameOf(info.get(i).get(4)));
            object.appendChild(name);

            //boundingbox
            Element bndbox = document.createElement("bndbox");

            Element xmin = document.createElement("xmin");
            xmin.setTextContent(String.valueOf(info.get(i).get(0)));
            bndbox.appendChild(xmin);

            Element ymin = document.createElement("ymin");
            ymin.setTextContent(String.valueOf(info.get(i).get(1)));
            bndbox.appendChild(ymin);

            Element xmax = document.createElement("xmax");
            xmax.setTextContent(String.valueOf(info.get(i).get(2)));
            bndbox.appendChild(xmax);

            Element ymax = document.createElement("ymax");
            ymax.setTextContent(String.valueOf(info.get(i).get(3)));
            bndbox.appendChild(ymax);

            object.appendChild(bndbox);

            annotation.appendChild(object);
        }
        //将bookstore节点（已经包含了book）添加到dom树中
        document.appendChild(annotation);
        //创建TransformerFactory对象
        TransformerFactory tff = TransformerFactory.newInstance();
        try {
            //创建Transformer对象
            Transformer tf = tff.newTransformer();
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
                tf.transform(new DOMSource(document), new StreamResult(new File(OUTPUTPATH + "positive/xml/" + filename + '.' + FILEPOSTFIX2)));
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}
