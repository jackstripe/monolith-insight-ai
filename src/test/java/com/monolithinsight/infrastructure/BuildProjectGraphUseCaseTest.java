package com.monolithinsight.infrastructure;

import com.monolithinsight.application.BuildProjectGraphUseCase;
import com.monolithinsight.domain.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

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
    @Test
    void shouldCreateStructuralDependenciesForClass() {
        JavaClassInfo baseService = createClass(
                "com.example.common",
                "BaseService"
        );

        JavaClassInfo auditable = createClass(
                "com.example.common",
                "Auditable"
        );

        JavaClassInfo orderRepository = createClass(
                "com.example.orders",
                "OrderRepository"
        );

        JavaClassInfo order = createClass(
                "com.example.orders",
                "Order"
        );

        JavaClassInfo orderRequest = createClass(
                "com.example.orders",
                "OrderRequest"
        );

        JavaClassInfo orderService = new JavaClassInfo(
                "com.example.orders",
                "OrderService",
                "CLASS",
                Optional.of("BaseService"),
                List.of("Auditable"),
                List.of("Service"),
                List.of(
                        new JavaConstructorInfo(
                                List.of(
                                        new JavaParameterInfo("OrderRepository",
                                                "repository"
                                        )
                                )
                        )
                ),
                List.of(
                        new JavaFieldInfo(
                                "repository",
                                "OrderRepository"
                        )
                ),
                List.of(
                        new JavaMethodInfo(
                                "findOrder",
                                "Order",
                                List.of(
                                        new JavaParameterInfo(
                                                "OrderRequest",
                                                "request"
                                        )
                                )
                        )
                ),
                "src/main/java/com/example/orders/OrderService.java"
        );

        ProjectAnalysis analysis = new ProjectAnalysis(
                "sample-project",
                6,
                List.of(
                        orderService,
                        orderRepository,
                        order,
                        orderRequest,
                        baseService,
                        auditable
                ),
                List.of()
        );

        ProjectGraph graph =
                new BuildProjectGraphUseCase()
                        .execute(analysis);

        assertThat(graph.dependencies())
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
                                "com.example.orders.OrderService",
                                "com.example.orders.OrderRepository",
                                DependencyType.CONSTRUCTOR
                        ),
                        tuple(
                                "com.example.orders.OrderService",
                                "com.example.orders.Order",
                                DependencyType.RETURN_TYPE
                        ),
                        tuple(
                                "com.example.orders.OrderService",
                                "com.example.orders.OrderRequest",
                                DependencyType.METHOD_PARAMETER
                        ),
                        tuple(
                                "com.example.orders.OrderService",
                                "com.example.common.BaseService",
                                DependencyType.INHERITANCE
                        ),
                        tuple(
                                "com.example.orders.OrderService",
                                "com.example.common.Auditable",
                                DependencyType.IMPLEMENTS
                        )
                );
    }

}
