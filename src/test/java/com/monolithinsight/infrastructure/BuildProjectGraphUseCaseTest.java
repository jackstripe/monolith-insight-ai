package com.monolithinsight.infrastructure;

import com.monolithinsight.domain.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class BuildProjectGraphUseCaseTest {


    @Test
    void shouldExecute()  {

        JavaClassInfo orderService = new JavaClassInfo(
        "com.example.orders",
        "OrderService",
        "CLASS",
        List.of("Service"),
        List.of(),
        List.of(
                new JavaFieldInfo(
                        "repository",
                        "OrderRepository"
                )
        ),
        List.of("findOrders"),
        "src/main/java/com/example/orders/OrderService.java"
        );

        JavaClassInfo inventoryService = new JavaClassInfo(
                "com.example.orders",
                "InventoryService",
                "CLASS",
                List.of("Service"),
                List.of(),
                List.of(
                        new JavaFieldInfo(
                                "repository",
                                "OrderRepository"
                        )
                ),
                List.of("findOrders"),
                "src/main/java/com/example/orders/InventoryService.java"
        );

        ProjectAnalysis analysis = new ProjectAnalysis(
                "sample-project",
                2,
                List.of(orderService,inventoryService),
                List.of()
        );

        BuildProjectGraphUseCase useCase = new BuildProjectGraphUseCase();

        ProjectGraph graph = useCase.execute(analysis);
        assertEquals(2, graph.nodes().size());

        assertThat(graph.nodes())
                .extracting(ClassNode::id)
                .containsExactlyInAnyOrder(
                        "com.example.orders.InventoryService",
                        "com.example.orders.OrderService"
                );
        assertThat(graph.nodes())
                .extracting(ClassNode::classInfo)
                .isNotNull();
        assertThat(graph.dependencies())
                .isEmpty();
  }
}
