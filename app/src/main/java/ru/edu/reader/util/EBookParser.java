package ru.edu.reader.util;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Grinch on 11.03.2015.
 */
public class EBookParser {
    private File book;
    private int pageCount;
    private int pageWidth;
    private int pageHeight;
    private int textSize = 14;
    private int linesInPage;
    private int charsInLine;
    private List<String> bookLines;

    public EBookParser(File book, int pageWidth, int pageHeight){
        refresh(book,pageWidth,pageHeight,14);
    }

    public void displaySize(int pageWidth, int pageHeight){
        refresh(book,pageWidth,pageHeight,textSize);
    }
    public void setTextSize(int textSize){
        refresh(book,pageWidth,pageHeight,textSize);
    }

    public String getPage(int index){
        StringBuilder page = new StringBuilder();
        int line = index * linesInPage;
        int count = 0;
        while (count < linesInPage && line < bookLines.size()){
            page.append(bookLines.get(line)).append("\n");
            count++;
            line++;
        }
        return page.toString();
    }

    private void refresh(File book, int pageWidth, int pageHeight, int textSize){
        this.book = book;
        this.pageHeight = pageHeight;
        this.pageWidth = pageWidth;
        this.textSize = textSize;
        this.bookLines = new ArrayList();
        calcParams();
        parseLines();
    }

    private void calcParams(){
        linesInPage = (int) (Math.floor(pageHeight / (2.5*textSize)));
        charsInLine = (int) (Math.floor(pageWidth / textSize));
    }

    private void parseLines(){
        List<String> lines = null;
        try {
            lines = getXMLStrings();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        for (String line : lines){
            StringTokenizer stringTokenizer = new StringTokenizer(line," !@#$%^&*()-=+|/?,.:;'\"\\", true);
            StringBuilder newString = new StringBuilder();

            while (stringTokenizer.hasMoreTokens()){
                String currentWord = stringTokenizer.nextToken();
                newString.append(currentWord);

                if (newString.length() >= charsInLine){
                    newString.delete(newString.lastIndexOf(currentWord),newString.length());
                    bookLines.add(newString.toString());
                    newString = new StringBuilder();
                    newString.append(currentWord);
                }
            }

            if (newString.length() > 0){
                bookLines.add(newString.toString());
            }
        }
        pageCount = (int) (Math.floor(bookLines.size() / linesInPage));
    }

    private List<String> getXMLStrings() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(book);
        Element root = document.getDocumentElement();
        NodeList nodes = root.getElementsByTagName("p");
        List<String> strings = new ArrayList<>();

        for (int i = 0; i < nodes.getLength(); i++){
            strings.add(nodes.item(i).getTextContent());
        }
        return strings;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("File: " + book.getAbsolutePath()).append("\n");
        sb.append("Page width: " + pageWidth).append("\n");
        sb.append("Page height: " + pageHeight).append("\n");
        sb.append("Pages count: " + pageCount).append("\n");
        sb.append("Lines in page: " + linesInPage).append("\n");
        sb.append("Text size: " + textSize).append("\n");
        return sb.toString();
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
}
