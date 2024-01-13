package com.example.demo.Service;

import ch.qos.logback.core.util.FileUtil;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class ServiceClass {

    // bean initialize this
//    private POSTaggerME posTagger;


    public String print(String x) {
        System.out.println(x);
        Collection<File> f = findAllFiles(x, "png");
        processAllFiles(f, x);
        return "Reached";
    }
    public Collection<File> findAllFiles(String folderPath, String ext) {
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Invalid Path : " + folderPath);
            throw new IllegalArgumentException("Invalid Path : " + folderPath);
        }
        Collection<File> files = FileUtils.listFiles(folder, new String[]{ext}, true);
        System.out.println(files);
        System.out.println("Processed Files");
        return files;
    }

    public String getTextFromPDF(File file) {
        try(PDDocument pd = PDDocument.load(file)) {
            PDFTextStripper textStripper = new PDFTextStripper();
            String text = textStripper.getText(pd);
            System.out.println(text + "\n");
            return text;
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

        return "";
    }

    public ITesseract getTesseractInstance() {
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath("/usr/share/tesseract-ocr/5/tessdata");

        return tesseract;
    }

    private POSTaggerME getPosTaggerInstance() {
        try (InputStream modelIn = new FileInputStream("/home/rahuldogra/Downloads/en-pos-perceptron.bin")) {
            POSModel posModel = new POSModel(modelIn);
            return  new POSTaggerME(posModel);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public String extractTextFromImages(File file, ITesseract tesseract, POSTaggerME posTaggerME) {
        try {
            String text = tesseract.doOCR(file);
            System.out.println(text);
            String[] tokens = WhitespaceTokenizer.INSTANCE.tokenize(text);
            String[] tags = posTaggerME.tag(tokens);

            for (int i = 0; i < tokens.length; i++) {
                if (tags[i].startsWith("NN")) {  // NN and NNS are noun tags
//                    nouns.add(tokens[i]);
                    System.out.println(tokens[i]);
                }
            }
            return text;
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

        return "";
    }
    public void processAllFiles(Collection<File> files, String folderPath) {
        ITesseract tesseract = getTesseractInstance();
        System.out.println("Extracting Text!");
//        for(File file: files) {
//            getTextFromPDF(file);
//        }
        POSTaggerME posTaggerME = getPosTaggerInstance();
        Collection<File> images = findAllFiles(folderPath, "png");
        for(File file: images) {
            extractTextFromImages(file, tesseract, posTaggerME);
        }
    }

    //Processing text for keywords


    //use Snowmed API to search for tags

    //Convert to csv

    // DONE!!
}
