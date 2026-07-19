package com.monolithinsight.infrastructure;

import com.monolithinsight.domain.JavaClassInfo;
import com.monolithinsight.domain.ProjectAnalysis;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class JavaParserProjectAnalyzerTest {

    private final JavaParserProjectAnalyzer analyzer =
            new JavaParserProjectAnalyzer();
    @TempDir
    Path tempDir;

    @Test
    void shouldAnalyzeJavaClass() throws IOException {

        Path sourceFile = tempDir.resolve("UserService.java");

        Files.writeString(sourceFile, """
                package com.example.service;

                public class UserService {

                    public void createUser() {
                    }

                    public String findUser() {
                        return "user";
                    }
                }
                """);
        ProjectAnalysis result = analyzer.analyze(tempDir);

        assertEquals(1, result.javaFileCount());
        assertEquals(1, result.classes().size());

        JavaClassInfo classInfo = result.classes().getFirst();

        assertEquals("com.example.service", classInfo.packageName());
        assertEquals("UserService", classInfo.className());
        assertEquals("CLASS", classInfo.type());
        assertEquals(
                List.of("createUser", "findUser"),
                classInfo.methods()
        );

    }

    @Test
    void shouldIgnoreTargetDirectory() throws IOException {
        Path sourceDirectory = tempDir.resolve("src/main/java");
        Path targetDirectory = tempDir.resolve("target/generated-sources");

        Files.createDirectories(sourceDirectory);
        Files.createDirectories(targetDirectory);

        Files.writeString(
                sourceDirectory.resolve("ValidClass.java"),
                "public class ValidClass {}"
        );

        Files.writeString(
                targetDirectory.resolve("GeneratedClass.java"),
                "public class GeneratedClass {}"
        );

        ProjectAnalysis result = analyzer.analyze(tempDir);

        assertEquals(1, result.javaFileCount());
        assertEquals(
                "ValidClass",
                result.classes().getFirst().className()
        );
    }

    @Test
    void shouldRejectNonExistingPath() {
        Path invalidPath = tempDir.resolve("does-not-exist");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> analyzer.analyze(invalidPath)
        );

        assertTrue(
                exception.getMessage().contains("does not exist")
        );
    }

    @Test
    void shouldRejectPathThatIsNotDirectory() throws IOException {
        Path file = tempDir.resolve("file.txt");
        Files.writeString(file, "content");

        assertThrows(
                IllegalArgumentException.class,
                () -> analyzer.analyze(file)
        );
    }
}
