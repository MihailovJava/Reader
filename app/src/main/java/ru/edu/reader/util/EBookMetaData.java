package ru.edu.reader.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Grinch on 15.03.2015.
 */
public class EBookMetaData {
    File file;
    Element root;

    public EBookMetaData(File file){
        this.file = file;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document document;
        try {
            builder = factory.newDocumentBuilder();
            document = builder.parse(file);
            root = document.getDocumentElement();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String getGenre(){
        NodeList nodes = root.getElementsByTagName("genre");
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }else{
            return null;
        }
    }

    public String getFirstName(){
        NodeList nodes = root.getElementsByTagName("first-name");
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }else{
            return null;
        }
    }

    public String getLastName(){
        NodeList nodes = root.getElementsByTagName("last-name");
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }else{
            return null;
        }
    }

    public String getBookID(){
        NodeList nodes = root.getElementsByTagName("id");
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }else{
            return null;
        }
    }

    public String getBookTitle(){
        NodeList nodes = root.getElementsByTagName("book-title");
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }else{
            return null;
        }
    }

    public String getProgramUsed(){
        NodeList nodes = root.getElementsByTagName("program-used");
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }else{
            return null;
        }
    }
}
