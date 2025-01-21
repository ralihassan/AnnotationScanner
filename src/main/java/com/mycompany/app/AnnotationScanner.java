package com.mycompany.app;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AnnotationScanner {

    public static void main(String[] args) {
        String jarPath = "commons-lang3-3.17.0.jar";
        AnnotationScanner scanner = new AnnotationScanner();
        scanner.generateAnnotationReport(jarPath, "annotation_report.csv");
    }

    public void generateAnnotationReport(String jarPath, String reportFilePath) {
        try {
            JarFile jarFile = openJarFile(jarPath);
            URLClassLoader classLoader = createClassLoader(jarPath);
            StringBuilder report = scanJarForAnnotations(jarFile, classLoader);
            writeReportToFile(report, reportFilePath);
        } catch (IOException e) {
            System.err.println("I/O error occurred: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    public JarFile openJarFile(String jarPath) throws IOException {
        return new JarFile(jarPath);
    }

    public URLClassLoader createClassLoader(String jarPath) throws IOException {
        URL[] urls = {new URL("jar:file:" + jarPath + "!/")};
        return URLClassLoader.newInstance(urls);
    }

    public StringBuilder scanJarForAnnotations(JarFile jarFile, URLClassLoader classLoader) throws IOException {
        Enumeration<JarEntry> entries = jarFile.entries();
        StringBuilder report = new StringBuilder();
        report.append("Class Name,Annotation(s)\n");

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();

            if (entry.isDirectory() || !entryName.endsWith(".class") || entryName.contains("META-INF")) {
                continue;
            }

            String className = entryName.replace("/", ".").replace(".class", "");

            try {
                Class<?> clazz = classLoader.loadClass(className);
                Annotation[] annotations = clazz.getAnnotations();
                if (annotations.length > 0) {
                    report.append(className).append(",");
                    for (Annotation annotation : annotations) {
                        report.append(annotation.annotationType().getName()).append(" ");
                    }
                    report.append("\n");
                }
            } catch (NoClassDefFoundError | ClassNotFoundException e) {
                System.out.println("Could not load class: " + className + " - " + e.getMessage());
            }
        }

        jarFile.close();
        return report;
    }

    public void writeReportToFile(StringBuilder report, String reportFilePath) throws IOException {
        File reportFile = new File(reportFilePath);
        try (FileWriter writer = new FileWriter(reportFile)) {
            writer.write(report.toString());
            System.out.println("Annotation report generated: " + reportFile.getAbsolutePath());
        }
    }
}