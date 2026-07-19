package com.monolithinsight.infrastructure;

import com.monolithinsight.application.ProjectAnalyzer;
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
    @Override
    public ProjectAnalysis analyze(Path projectPath) {
        validateProjectPath(projectPath);
        List<JavaClassInfo> classes = new ArrayList<>();
        try (var files = Files.walk(projectPath)) {
            List<Path> javaFiles = files
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .filter(path -> !isGeneratedCode(path))
                    .toList();

            for (Path javaFile : javaFiles) {
                classes.addAll(parseFile(javaFile));
            }
            return new ProjectAnalysis(
                    projectPath.getFileName().toString(),
                    javaFiles.size(),
                    classes ); }
        catch (IOException exception) {
            throw new ProjectAnalysisException(
                    "Could not analyze project: " + projectPath, exception );
        }

    }

    private List<JavaClassInfo> parseFile(Path javaFile) {
        String filePath = javaFile.getFileSystem().toString();
        try { CompilationUnit compilationUnit = StaticJavaParser.parse(javaFile);
            String packageName = compilationUnit
                    .getPackageDeclaration()
                    .map(declaration -> declaration.getNameAsString())
                    .orElse("");
            return compilationUnit.getTypes()
                    .stream() .map(type -> toClassInfo(packageName, type,filePath))
                    .toList();
        } catch (Exception exception) {
        throw new ProjectAnalysisException( "Could not parse Java file: " + javaFile, exception );
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

    private JavaClassInfo toClassInfo( String packageName, TypeDeclaration<?> type , String filePath) {
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
                filePath);
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
