package com.monolithinsight.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public Map<String, String> health(){
        return Map.of(
                "status", "UP",
                "application", "Monoltih Insight AI",
                "mission", "Analize Java monoliths"
        );
    }
}
