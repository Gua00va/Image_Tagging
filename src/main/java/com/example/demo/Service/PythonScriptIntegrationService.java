package com.example.demo.Service;

import java.io.*;

public class PythonScriptIntegrationService {
    public void runPythonScript(String text) {
        ProcessBuilder processBuilder = new ProcessBuilder("python3", "/home/gua00va/text_processor.py");
        try {
//            System.out.println();
            Process process = processBuilder.start();

            OutputStream outputStream = process.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream));

            writer.println(text);
            writer.flush();
            writer.close();

            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            InputStream errorStream = process.getErrorStream();
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
            while ((line = errorReader.readLine()) != null) {
                System.out.println("Error: " + line);
            }
            
            int exitCode = process.waitFor();
            System.out.println("Python script exited with code: " + exitCode);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
