package com.monolithinsight.application;

import com.monolithinsight.domain.ProjectAnalysis;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class AnalyzeProjectUseCaseTest {

    @Mock
    private ProjectAnalyzer projectAnalyzer;

    @InjectMocks
    private AnalyzeProjectUseCase useCase;

    @Test
    void shouldDelegateAnalysisToProjectAnalyzer() {
        ProjectAnalysis expected = new ProjectAnalysis(
                "sample",
                0,
                List.of(),
                List.of()
        );

        when(projectAnalyzer.analyze(any(Path.class)))
                .thenReturn(expected);

        ProjectAnalysis result =
                useCase.execute("C:/projects/sample");

        assertSame(expected, result);

        verify(projectAnalyzer)
                .analyze(Path.of("C:/projects/sample"));
    }
}
