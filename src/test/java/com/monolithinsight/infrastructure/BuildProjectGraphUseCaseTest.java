package com.monolithinsight.infrastructure;

import com.monolithinsight.application.BuildProjectGraphUseCase;
import com.monolithinsight.domain.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.monolithinsight.support.TestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class BuildProjectGraphUseCaseTest {



    @Test
    void shouldCreateOneNodeForEachAnalyzedClass()  {

        JavaClassInfo orderService = createOrderService();
        JavaClassInfo inventoryService = createInventoryService();


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

        BuildProjectGraphUseCase useCase = new BuildProjectGraphUseCase();

        ProjectAnalysis analysis = new ProjectAnalysis(
                "sample-project",
                3,
                List.of(createOrderService(),createInventoryService(), createOrderRepository()),
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

        JavaClassInfo orderService = createOrderService();

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
        JavaClassInfo service = createOrderService();

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



    @Test
    void shouldIgnoreFieldIfItsTheSameClass() {
        JavaClassInfo service = createOrderService();

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
