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

/**
 * A utility class to scan JAR files for classes and their associated annotations,
 * and generate a CSV report summarizing the findings.
 */
public class AnnotationScanner {

    /**
     * The main method serves as the entry point of the application.
     * It specifies the JAR file to scan and the output report file.
     *
     * @param args Command-line arguments (not used in this implementation).
     */
    public static void main(String[] args) {
        String jarPath = "commons-lang3-3.17.0.jar"; // Path to the JAR file to scan.
        AnnotationScanner scanner = new AnnotationScanner();
        scanner.generateAnnotationReport(jarPath, "annotation_report.csv"); // Generates the annotation report.
    }

    /**
     * Generates a report of all annotations used in the classes of a given JAR file.
     *
     * @param jarPath        The path to the JAR file to scan.
     * @param reportFilePath The path to the output CSV file to save the report.
     */
    public void generateAnnotationReport(String jarPath, String reportFilePath) {
        try {
            JarFile jarFile = openJarFile(jarPath); // Opens the JAR file for reading.
            URLClassLoader classLoader = createClassLoader(jarPath); // Creates a class loader for the JAR.
            StringBuilder report = scanJarForAnnotations(jarFile, classLoader); // Scans the JAR for annotations.
            writeReportToFile(report, reportFilePath); // Writes the report to the output file.
        } catch (IOException e) {
            System.err.println("I/O error occurred: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Opens a JAR file for processing.
     *
     * @param jarPath The path to the JAR file.
     * @return A JarFile object representing the JAR.
     * @throws IOException If an error occurs while opening the file.
     */
    public JarFile openJarFile(String jarPath) throws IOException {
        return new JarFile(jarPath);
    }

    /**
     * Creates a URLClassLoader for the specified JAR file.
     *
     * @param jarPath The path to the JAR file.
     * @return A URLClassLoader for loading classes from the JAR.
     * @throws IOException If an error occurs while creating the class loader.
     */
    public URLClassLoader createClassLoader(String jarPath) throws IOException {
        URL[] urls = {new URL("jar:file:" + jarPath + "!/")}; // URL pointing to the JAR file.
        return URLClassLoader.newInstance(urls);
    }

    /**
     * Scans the classes in a JAR file for annotations and generates a report.
     *
     * @param jarFile     The JarFile object representing the JAR.
     * @param classLoader The class loader to load classes from the JAR.
     * @return A StringBuilder containing the CSV-formatted report.
     * @throws IOException If an error occurs while processing the JAR.
     */
    public StringBuilder scanJarForAnnotations(JarFile jarFile, URLClassLoader classLoader) throws IOException {
        Enumeration<JarEntry> entries = jarFile.entries(); // Retrieves all entries in the JAR.
        StringBuilder report = new StringBuilder();
        report.append("Class Name,Annotation(s)\n"); // CSV header.

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();

            // Skip directories, non-class files, and META-INF entries.
            if (entry.isDirectory() || !entryName.endsWith(".class") || entryName.contains("META-INF")) {
                continue;
            }

            String className = entryName.replace("/", ".").replace(".class", ""); // Convert path to class name.

            try {
                Class<?> clazz = classLoader.loadClass(className); // Load the class using the class loader.
                Annotation[] annotations = clazz.getAnnotations(); // Get annotations for the class.
                if (annotations.length > 0) {
                    report.append(className).append(","); // Add class name to the report.
                    for (Annotation annotation : annotations) {
                        report.append(annotation.annotationType().getName()).append(" "); // List annotations.
                    }
                    report.append("\n"); // New line for each class.
                }
            } catch (NoClassDefFoundError | ClassNotFoundException e) {
                System.out.println("Could not load class: " + className + " - " + e.getMessage());
            }
        }

        jarFile.close(); // Close the JAR file to release resources.
        return report;
    }

    /**
     * Writes the generated report to a file.
     *
     * @param report         The report content as a StringBuilder.
     * @param reportFilePath The path to the output file.
     * @throws IOException If an error occurs while writing to the file.
     */
    public void writeReportToFile(StringBuilder report, String reportFilePath) throws IOException {
        File reportFile = new File(reportFilePath); // Create a File object for the report.
        try (FileWriter writer = new FileWriter(reportFile)) { // FileWriter with try-with-resources for auto-closing.
            writer.write(report.toString()); // Write the report content to the file.
            System.out.println("Annotation report generated: " + reportFile.getAbsolutePath());
        }
    }
}
