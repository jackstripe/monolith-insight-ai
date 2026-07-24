package com.monolithinsight.infrastructure;

import com.monolithinsight.domain.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

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
        JavaMethodInfo createUser = new JavaMethodInfo(
                        "createUser",
                        "void",
                        List.of()
                );
        JavaMethodInfo findUser = new JavaMethodInfo(
                "findUser",
                "String",
                List.of()
        );
        assertEquals(
                List.of(createUser, findUser),
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

    @Test
    void shouldFindOnlyJavaFiles() throws IOException {
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
        Path sourceFileAnotherJavaFile = tempDir.resolve("UserServic2e.java");
        Files.writeString(sourceFileAnotherJavaFile, """
                package com.example.service;

                public class UserServic2e {

                    public void createUser() {
                    }

                    public String findUser() {
                        return "user";
                    }
                }
                """);
        Path sourceFileTxt = tempDir.resolve("UserService.txt");
        Path sourceFileBin = tempDir.resolve("UserService.bin");
        Path sourceFileReadMe = tempDir.resolve("UserService.md");

        Files.writeString(sourceFileTxt, "not Java");
        Files.writeString(sourceFileBin, "binary-ish content");
        Files.writeString(sourceFileReadMe, "# README");

        ProjectAnalysis result = analyzer.analyze(tempDir);

        assertEquals(2, result.javaFileCount());
        assertEquals(2, result.classes().size());


    }
    @Test
    void shouldExtractConstructors() throws IOException {
        Path sourceFile = tempDir.resolve("OrderService.java");

        Files.writeString(sourceFile, """
            package com.example;

            public class OrderService {

                public OrderService() {
                }

                public OrderService(String name, int retryCount) {
                }
            }
            """);

        ProjectAnalysis result = analyzer.analyze(tempDir);

        JavaClassInfo classInfo = result.classes().getFirst();

        assertThat(classInfo.constructors())
                .hasSize(2);

        assertThat(classInfo.constructors())
                .anySatisfy(constructor ->
                        assertThat(constructor.parameters()).isEmpty()
                );

        assertThat(classInfo.constructors())
                .anySatisfy(constructor ->
                        assertThat(constructor.parameters())
                                .extracting(
                                        JavaParameterInfo::name,
                                        JavaParameterInfo::type
                                )
                                .containsExactly(
                                        tuple("name", "String"),
                                        tuple("retryCount", "int")
                                )
                );
    }

    @Test
    void shouldExtractFieldsAndTheirTypes() throws IOException {
        Path sourceFile = tempDir.resolve("OrderService.java");

        Files.writeString(sourceFile, """
            package com.example;

            public class OrderService {

                private final OrderRepository repository;
                private String serviceName;
                private int minimum, maximum;
            }
            """);

        ProjectAnalysis result = analyzer.analyze(tempDir);

        JavaClassInfo classInfo = result.classes().getFirst();

        assertEquals(4, classInfo.fields().size());

        assertTrue(classInfo.fields().contains(
                new  JavaFieldInfo("repository", "OrderRepository")
        ));

        assertTrue(classInfo.fields().contains(
                new JavaFieldInfo("serviceName", "String")
        ));

        assertTrue(classInfo.fields().contains(
                new JavaFieldInfo("minimum", "int")
        ));

        assertTrue(classInfo.fields().contains(
                new JavaFieldInfo("maximum", "int")
        ));
    }

    @Test
    void shouldExtractAnnotations() throws IOException {
        Path sourceFile = tempDir.resolve("OrderService.java");

        Files.writeString(sourceFile, """
            package com.example;

            @Service
            public class OrderService {

                public OrderService() {
                }
            }
            """);

        ProjectAnalysis result = analyzer.analyze(tempDir);

        JavaClassInfo classInfo = result.classes().getFirst();

        assertEquals(1, classInfo.annotations().size());
        assertTrue(
                classInfo.annotations()
                        .contains("Service")
        );
    }

    @Test
    void shouldIncludeRelativeFilePath() throws IOException {
        Path sourceDirectory = tempDir.resolve(
                "src/main/java/com/example/service"
        );

        Files.createDirectories(sourceDirectory);

        Path sourceFile = sourceDirectory.resolve("OrderService.java");

        Files.writeString(sourceFile, """
            package com.example.service;

            public class OrderService {

                public void createOrder() {
                }
            }
            """);

        ProjectAnalysis result = analyzer.analyze(tempDir);

        assertEquals(1, result.classes().size());

        JavaClassInfo classInfo = result.classes().getFirst();

        assertEquals(
                "src/main/java/com/example/service/OrderService.java",
                classInfo.filePath()
        );
    }
    @Test
    void shouldContinueAnalysisWhenOneFileCannotBeParsed()
            throws IOException {

        Files.writeString(
                tempDir.resolve("ValidClass.java"),
                """
                public class ValidClass {
                }
                """
        );

        Files.writeString(
                tempDir.resolve("InvalidClass.java"),
                """
                public class InvalidClass {
                """
        );

        Files.writeString(
                tempDir.resolve("AnotherInvalidClass.java"),
                """
                public class AnotherInvalidClass {
                """
        );


        ProjectAnalysis result =
                analyzer.analyze(tempDir);

        assertEquals(3, result.javaFileCount());
        assertEquals(1, result.classes().size());
        assertEquals("ValidClass",
                result.classes().getFirst().className());

        assertEquals(2, result.errors().size());

        assertTrue(result.errors().stream()
                .map(AnalysisError::filePath)
                .toList()
                .containsAll(List.of(
                        "InvalidClass.java",
                        "AnotherInvalidClass.java"
                )
                )
        );
    }
}
