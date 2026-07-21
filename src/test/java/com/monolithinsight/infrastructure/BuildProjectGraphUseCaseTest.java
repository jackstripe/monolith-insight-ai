package com.monolithinsight.infrastructure;

import com.monolithinsight.application.BuildProjectGraphUseCase;
import com.monolithinsight.domain.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class BuildProjectGraphUseCaseTest {


    @Test
    void shouldCreateOneNodeForEachAnalyzedClass()  {

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
        assertThat(graph.nodes()).hasSize(2);

        assertThat(graph.nodes())
                .extracting(ClassNode::id)
                .containsExactlyInAnyOrder(
                        "com.example.orders.InventoryService",
                        "com.example.orders.OrderService"
                );
        assertThat(graph.nodes())
                .extracting(ClassNode::classInfo)
                .doesNotContainNull();
     //   assertThat(graph.dependencies())
       //         .isEmpty();
  }

    @Test
    void shouldCreateDependencies()  {

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
                        ),
                        new JavaFieldInfo(
                                "repository2",
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

        JavaClassInfo OrderRepository = new JavaClassInfo(
                "com.example.orders",
                "OrderRepository",
                "CLASS",
                List.of("Repository"),
                List.of(),
                List.of(
                        new JavaFieldInfo(
                                "repository",
                                "String"
                        )
                ),
                List.of("findOrdersSample"),
                "src/main/java/com/example/orders/OrderRepository.java"
        );

        BuildProjectGraphUseCase useCase = new BuildProjectGraphUseCase();

        ProjectAnalysis analysis = new ProjectAnalysis(
                "sample-project",
                3,
                List.of(orderService,inventoryService, OrderRepository),
                List.of()
        );

        ProjectGraph graph = useCase.execute(analysis);

        assertThat(graph.dependencies())
                .hasSize(2)
                .extracting(
                        ClassDependency::sourceNodeId,
                        ClassDependency::targetNodeId,
                        ClassDependency::type
                )
                .containsExactlyInAnyOrder(
                        tuple(
                                "com.example.orders.OrderService",
                                "com.example.orders.OrderRepository",
                                DependencyType.FIELD
                        ),
                        tuple(
                                "com.example.orders.InventoryService",
                                "com.example.orders.OrderRepository",
                                DependencyType.FIELD
                        )
                );
    }

    @Test
    void shouldNotResolveDependencyWhenSimpleNameIsAmbiguous() {

        BuildProjectGraphUseCase buildProjectGraphUseCase = new BuildProjectGraphUseCase();

        JavaClassInfo ordersRepository = createClass(
                "com.example.orders",
                "OrderRepository"
        );

        JavaClassInfo inventoryRepository = createClass(
                "com.example.inventory",
                "OrderRepository"
        );

        JavaClassInfo orderService = new JavaClassInfo(
                "com.example.service",
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
                List.of(),
                "src/main/java/com/example/service/OrderService.java"
        );

        ProjectAnalysis analysis = new ProjectAnalysis(
                "sample-project",
                3,
                List.of(
                        ordersRepository,
                        inventoryRepository,
                        orderService
                ),
                List.of()
        );

        ProjectGraph graph = buildProjectGraphUseCase.execute(analysis);

        assertThat(graph.nodes()).hasSize(3);

        assertThat(graph.dependencies())
                .isEmpty();
    }

    @Test
    void shouldIgnoreFieldTypeWhenItDoesNotBelongToProject() {
        JavaClassInfo service = new JavaClassInfo(
                "com.example.orders",
                "OrderService",
                "CLASS",
                List.of("Service"),
                List.of(),
                List.of(
                        new JavaFieldInfo("name", "String"),
                        new JavaFieldInfo("retryCount", "Integer")
                ),
                List.of(),
                "src/main/java/com/example/orders/OrderService.java"
        );

        ProjectAnalysis analysis = new ProjectAnalysis(
                "sample-project",
                1,
                List.of(service),
                List.of()
        );

        ProjectGraph graph = new BuildProjectGraphUseCase()
                .execute(analysis);

        assertThat(graph.dependencies()).isEmpty();
    }

    private JavaClassInfo createClass(
            String packageName,
            String className
    ) {
        return new JavaClassInfo(
                packageName,
                className,
                "CLASS",
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                "src/main/java/"
                        + packageName.replace('.', '/')
                        + "/"
                        + className
                        + ".java"
        );
    }

    @Test
    void shouldIgnoreFieldIfItsTheSameClass() {
        JavaClassInfo service = new JavaClassInfo(
                "com.example.orders",
                "OrderService",
                "CLASS",
                List.of("Service"),
                List.of(),
                List.of(
                        new JavaFieldInfo("orderService", "OrderService")
                ),
                List.of(),
                "src/main/java/com/example/orders/OrderService.java"
        );

        ProjectAnalysis analysis = new ProjectAnalysis(
                "sample-project",
                1,
                List.of(service),
                List.of()
        );

        ProjectGraph graph = new BuildProjectGraphUseCase()
                .execute(analysis);

        assertThat(graph.dependencies()).isEmpty();
    }

}
