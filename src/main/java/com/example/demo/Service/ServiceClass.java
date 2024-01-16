package com.example.demo.Service;

import ch.qos.logback.core.util.FileUtil;
import com.opencsv.CSVWriter;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.io.StringWriter;
import java.util.*;

@Service
public class ServiceClass {
    public ResponseEntity<byte[]> print(String x) {
        System.out.println(x);
        Collection<File> f = findAllFiles(x, "pdf");
        HashMap<String, List<String>> keywords = new HashMap<>();
        keywords = processAllFiles(f, x);
        ChatGPTAPIService  cs = new ChatGPTAPIService();
        List<String[]> csvData = new ArrayList<>();

        for(Map.Entry<String, List<String>> e: keywords.entrySet()) {
            String key = e.getKey();
            String value = e.getValue().toString();

            String response = cs.chatgpt("GIVE me 3 related words, only words and dont number them, to each "  + value);
            csvData.add(new String[]{key, response});
        }
        System.out.println(csvData.get(0)[1]);
//        getCSV();
        return downloadCSV(csvData);
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
            text += ". " + file.getName();
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
            text += ". " + file.getName();
            System.out.println(text);
            return text;
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

        return "";
    }
    public HashMap<String, List<String>> processAllFiles(Collection<File> files, String folderPath) {
        ITesseract tesseract = getTesseractInstance();
        PythonScriptIntegrationService pservice = new PythonScriptIntegrationService();
        HashMap<String, List<String>> data = new HashMap<>();

//        System.out.println("Extracting Text!");
//        for(File file: files) {
//            var text = getTextFromPDF(file);
//            List<String> keywords = pservice.runPythonScript(text);
//            data.put(file.getName(), keywords);
//        }
        List<String> keywords = pservice.runPythonScript("HEH");
        data.put("testing", keywords);
//        Collection<File> images = findAllFiles(folderPath, "jpeg");List<String> keywords = pservice.runPythonScript(text);
//            data.put(file.getName(), keywords);
//        for(File file: images) {
//            var text = extractTextFromImages(file, tesseract);
//            List<String> keywords = pservice.runPythonScript(text);
//            data.put(file.getName(), keywords);
//        }

        return data;
    }

    public ResponseEntity<byte[]> downloadCSV(List<String[]> data) {


        try (StringWriter stringWriter = new StringWriter();
             CSVWriter csvWriter = new CSVWriter(stringWriter)) {

            csvWriter.writeAll(data);

            byte[] csvBytes = stringWriter.toString().getBytes();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "output.csv");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvBytes);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error generating CSV file.".getBytes());
        }
    }
}
