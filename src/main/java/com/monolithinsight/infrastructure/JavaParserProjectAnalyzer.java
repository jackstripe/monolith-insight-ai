package com.monolithinsight.infrastructure;

import com.monolithinsight.application.ProjectAnalyzer;
import com.monolithinsight.domain.AnalysisError;
import com.monolithinsight.domain.JavaClassInfo;
import com.monolithinsight.domain.JavaFieldInfo;
import com.monolithinsight.domain.ProjectAnalysis;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

@Component
public class JavaParserProjectAnalyzer implements ProjectAnalyzer {

    private record FileAnalysisResult(
            List<JavaClassInfo> classes,
            List<AnalysisError> errors
    ) {
    }
    @Override
    public ProjectAnalysis analyze(Path projectPath) {
        validateProjectPath(projectPath);
        List<JavaClassInfo> classes = new ArrayList<>();
        List<AnalysisError> errors = new ArrayList<>();

        try (var files = Files.walk(projectPath)) {
            List<Path> javaFiles = files
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .filter(path -> !isGeneratedCode(path))
                    .toList();

            for (Path javaFile : javaFiles) {
                FileAnalysisResult result =
                        parseFile(projectPath, javaFile);

                classes.addAll(result.classes());
                errors.addAll(result.errors());
            }
            return new ProjectAnalysis(
                    projectPath.getFileName().toString(),
                    javaFiles.size(),
                    classes,
                    errors  ); }
        catch (IOException exception) {
            throw new ProjectAnalysisException(
                    "Could not analyze project: " + projectPath, exception );
        }

    }

    private FileAnalysisResult  parseFile(Path projectPath,
                                          Path javaFile) {

        String relativePath = projectPath
                .relativize(javaFile)
                .normalize()
                .toString()
                .replace('\\', '/');

        try {
            CompilationUnit compilationUnit = StaticJavaParser.parse(javaFile);
            String packageName = compilationUnit
                    .getPackageDeclaration()
                    .map(declaration -> declaration.getNameAsString())
                    .orElse("");
            List<JavaClassInfo> classes = compilationUnit
                    .getTypes()
                    .stream()
                    .map(type -> toClassInfo(
                            packageName,
                            type,
                            relativePath
                    ))
                    .toList();

            return new FileAnalysisResult(
                    classes,
                    List.of()
            );
        } catch (Exception exception) {
            return new FileAnalysisResult(
                    List.of(),
                    List.of(
                            new AnalysisError(
                                    relativePath,
                                    exception.getClass().getSimpleName(),
                                    exception.getMessage()
                            )
                    )
            );
        }
    }

    private String determineType(TypeDeclaration<?> type) {
        if (type.isAnnotationDeclaration()) {
            return "ANNOTATION";
        }
        if (type.isEnumDeclaration()) {
            return "ENUM";
        }
        if (type.isRecordDeclaration()) {
            return "RECORD";
        }
        if (type.isClassOrInterfaceDeclaration() && type.asClassOrInterfaceDeclaration().isInterface()) {
            return "INTERFACE";
        }
        return "CLASS";
    }

    private JavaClassInfo toClassInfo( String packageName, TypeDeclaration<?> type , String relativePath) {
        List<String> methods = type.getMethods()
                .stream()
                .map(method -> method.getNameAsString())
                .toList();
        List<String> constructors = extractConstructors(type);


        List<JavaFieldInfo> fields = type.getFields()
                .stream()
                .flatMap(fieldDeclaration ->
                        fieldDeclaration.getVariables().stream()
                )
                .map(variable -> new JavaFieldInfo(
                        variable.getNameAsString(),
                        variable.getTypeAsString()

                ))
                .toList();
        List<String> annotations =  type.getAnnotations()
                .stream()
                .map(annotation ->annotation.getNameAsString())
                .toList();

        return new JavaClassInfo( packageName, type.getNameAsString(),
                determineType(type),
                annotations,
                constructors,
                fields,
                methods,
                relativePath
        );
    }

    private List<String> extractConstructors(TypeDeclaration<?> type) {
        if (type.isClassOrInterfaceDeclaration()
                && !type.asClassOrInterfaceDeclaration().isInterface()) {

            return type.asClassOrInterfaceDeclaration()
                    .getConstructors()
                    .stream()
                    .map(constructor -> constructor.getDeclarationAsString())
                    .toList();
        }

        if (type.isEnumDeclaration()) {
            return type.asEnumDeclaration()
                    .getConstructors()
                    .stream()
                    .map(constructor -> constructor.getDeclarationAsString())
                    .toList();
        }

        return List.of();
    }

    private void validateProjectPath(Path projectPath) {
        if (!Files.exists(projectPath)) {
            throw new IllegalArgumentException( "Project path does not exist: " + projectPath );
        }
        if (!Files.isDirectory(projectPath)) {
            throw new IllegalArgumentException( "Project path is not a directory: " + projectPath );
        }
    }

    private boolean isGeneratedCode(Path path) {
        String normalizedPath = path.toString().replace('\\', '/');
        return normalizedPath.contains("/target/")
                || normalizedPath.contains("/build/")
                || normalizedPath.contains("/.git/");
    }
}
