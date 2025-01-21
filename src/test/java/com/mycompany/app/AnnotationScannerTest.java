package com.mycompany.app;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class AnnotationScannerTest {

    @Test
    public void testOpenJarFile() throws IOException {
        // Assuming a valid JAR file path is provided
        AnnotationScanner scanner = new AnnotationScanner();
        String jarPath = "commons-lang3-3.17.0.jar";
        JarFile jarFile = scanner.openJarFile(jarPath);
        assertNotNull(jarFile);
        jarFile.close();
    }

    @Test
    public void testOpenJarFileWithInvalidPath() {
        AnnotationScanner scanner = new AnnotationScanner();
        String invalidJarPath = "invalid.jar";
        assertThrows(IOException.class, () -> scanner.openJarFile(invalidJarPath));
    }

    @Test
    public void testCreateClassLoader() throws IOException {
        AnnotationScanner scanner = new AnnotationScanner();
        String jarPath = "commons-lang3-3.17.0.jar";
        assertNotNull(scanner.createClassLoader(jarPath));
    }

    @Test
    public void testScanJarForAnnotations() throws IOException {
        // Mock or use a real JAR file with known annotations
        AnnotationScanner scanner = new AnnotationScanner();
        String jarPath = "commons-lang3-3.17.0.jar";
        JarFile jarFile = scanner.openJarFile(jarPath);
        URLClassLoader classLoader = scanner.createClassLoader(jarPath);

        StringBuilder report = scanner.scanJarForAnnotations(jarFile, classLoader);
        assertNotNull(report);
        assertTrue(report.toString().contains("Class Name,Annotation(s)"));
    }

    @Test
    public void testWriteReportToFile(@TempDir Path tempDir) throws IOException {
        AnnotationScanner scanner = new AnnotationScanner();
        StringBuilder report = new StringBuilder("Class Name,Annotation(s)\n");
        String reportFilePath = tempDir.resolve("annotation_report.csv").toString();

        scanner.writeReportToFile(report, reportFilePath);

        File reportFile = new File(reportFilePath);
        assertTrue(reportFile.exists());
        String content = Files.readString(reportFile.toPath());
        assertEquals("Class Name,Annotation(s)\n", content);
    }
}