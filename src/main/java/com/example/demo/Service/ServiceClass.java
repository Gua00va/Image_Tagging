package com.example.demo.Service;

import ch.qos.logback.core.util.FileUtil;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class ServiceClass {
    public String print(String x) {
        System.out.println(x);
        Collection<File> f = findAllFiles(x, "pdf");
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
//            System.out.println(text + "\n");
            return text;
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

        return "";
    }

    public ITesseract getTesseractInstance() {
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata");

        return tesseract;
    }

    public String extractTextFromImages(File file, ITesseract tesseract) {
        try {
            String text = tesseract.doOCR(file);
            System.out.println(text);
            return text;
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

        return "";
    }
    public void processAllFiles(Collection<File> files, String folderPath) {
        ITesseract tesseract = getTesseractInstance();
        PythonScriptIntegrationService pservice = new PythonScriptIntegrationService();
        System.out.println("Extracting Text!");
        for(File file: files) {
            var text = getTextFromPDF(file);
            pservice.runPythonScript(text);
        }
//        Collection<File> images = findAllFiles(folderPath, "jpeg");
//        for(File file: images) {
//            extractTextFromImages(file, tesseract);
//        }
    }

    //Processing text for keywords


    //use Snowmed API to search for tags

    //Convert to csv

    // DONE!!
}
