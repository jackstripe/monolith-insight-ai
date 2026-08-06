package com.monolithinsight.support;

import com.monolithinsight.domain.*;

import java.util.List;
import java.util.Optional;

public class TestFixtures {

    private TestFixtures() {
    }
    public static JavaClassInfo createClass( String packageName,
                                                        String className){

        return new JavaClassInfo(
                packageName,
                className,
                "CLASS",
                Optional.empty(),
                List.of(),
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

    public static JavaClassInfo createOrderService(){
        return new JavaClassInfo(
                "com.example.orders",
                "OrderService",
                "CLASS",
                Optional.empty(),
                List.of(),
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
                List.of(
                        new JavaMethodInfo(
                                "findOrders",
                                "void",
                                List.of()
                        )
                ),
                "src/main/java/com/example/orders/OrderService.java"
        );
}

    public static JavaClassInfo createInventoryService () {
        return new JavaClassInfo("com.example.orders",
                "InventoryService",
                "CLASS",
                Optional.empty(),
                List.of(),
                List.of("Service"),
                List.of(),
                List.of(
                        new JavaFieldInfo(
                                "repository",
                                "OrderRepository"
                        )
                ),
                List.of(
                        new JavaMethodInfo(
                                "findOrders",
                                "void",
                                List.of()
                        )
                ),
                "src/main/java/com/example/orders/InventoryService.java"
        );
    }
    public static JavaClassInfo createOrderRepository () {
        return new JavaClassInfo(
                "com.example.orders",
                "OrderRepository",
                "CLASS",
                Optional.empty(),
                List.of(),
                List.of("Repository"),
                List.of(),
                List.of(
                        new JavaFieldInfo(
                                "repository",
                                "String"
                        )
                ),
                List.of(
                        new JavaMethodInfo(
                                "findOrdersSample",
                                "void",
                                List.of()
                        )
                ),
                "src/main/java/com/example/orders/OrderRepository.java"
        );
    }

    public static JavaClassInfo createUserService(){

        return new JavaClassInfo(
                "com.example",
                "UserService",
                "CLASS",
                Optional.empty(),
                List.of(),
                List.of("Service"),
                List.of(),
                List.of(),
                List.of(
                        new JavaMethodInfo(
                                "createUser",
                                "void",
                                List.of()
                        )
                ),
                "src/main/java/com/example/service/UserService.java"
        );
    }

    public static ClassNode createNode(String id){
        return new ClassNode(id,null);
    }

    public static ProjectGraph createReachabilityGraph(){

        ClassNode orderService =
                ClassNode.from(
                        TestFixtures.createClass(
                                "com.example.orders",
                                "OrderService"
                        )
                );
        ClassNode auditService =
                ClassNode.from(
                        TestFixtures.createClass(
                                "com.example.shared",
                                "AuditService"
                        )
                );
        ClassNode orderController =
                ClassNode.from(
                        TestFixtures.createClass(
                                "com.example.orders",
                                "OrderController"
                        )
                );

        ClassNode inventoryService =
                ClassNode.from(
                        TestFixtures.createClass(
                                "com.example.inventory",
                                "InventoryService"
                        )
                );
        ClassNode legacyHelper =
                ClassNode.from(
                        TestFixtures.createClass(
                                "com.example.legacy",
                                "LegacyHelper"
                        )
                );


        return new ProjectGraph(
                List.of(
                        orderController,
                        orderService,
                        inventoryService,
                        auditService,
                        legacyHelper
                ),
                List.of(
                        new ClassDependency(
                                orderController.id(),
                                orderService.id(),
                                DependencyType.FIELD
                        ),
                        new ClassDependency(
                                orderController.id(),
                                inventoryService.id(),
                                DependencyType.FIELD
                        ),
                        new ClassDependency(
                                orderService.id(),
                                auditService.id(),
                                DependencyType.FIELD
                        ),
                        new ClassDependency(
                                inventoryService.id(),
                                auditService.id(),
                                DependencyType.CONSTRUCTOR
                        ),
                        new ClassDependency(
                                auditService.id(),
                                legacyHelper.id(),
                                DependencyType.CONSTRUCTOR
                        ),
                        new ClassDependency(
                                orderService.id(),
                                legacyHelper.id(),
                                DependencyType.CONSTRUCTOR
                        )
                )
        );
    }

    public static ProjectGraph createCycleReachabilityGraph(){

        ClassNode orderService =
                ClassNode.from(
                        TestFixtures.createClass(
                                "com.example.orders",
                                "OrderService"
                        )
                );
        ClassNode auditService =
                ClassNode.from(
                        TestFixtures.createClass(
                                "com.example.shared",
                                "AuditService"
                        )
                );

        ClassNode legacyHelper =
                ClassNode.from(
                        TestFixtures.createClass(
                                "com.example.legacy",
                                "LegacyHelper"
                        )
                );


        return new ProjectGraph(
                List.of(
                        orderService,
                        auditService,
                        legacyHelper
                ),
                List.of(

                        new ClassDependency(
                                orderService.id(),
                                auditService.id(),
                                DependencyType.FIELD
                        ),
                        new ClassDependency(
                                auditService.id(),
                                legacyHelper.id(),
                                DependencyType.FIELD
                        ),
                        new ClassDependency(
                                legacyHelper.id(),
                                orderService.id(),
                                DependencyType.CONSTRUCTOR
                        )
                )
        );
    }

    public static ProjectGraph createReachabilityGraphWithOneNonReachable(){

        ClassNode orderService =
                ClassNode.from(
                        TestFixtures.createClass(
                                "com.example.orders",
                                "OrderService"
                        )
                );
        ClassNode auditService =
                ClassNode.from(
                        TestFixtures.createClass(
                                "com.example.shared",
                                "AuditService"
                        )
                );
        ClassNode orderController =
                ClassNode.from(
                        TestFixtures.createClass(
                                "com.example.orders",
                                "OrderController"
                        )
                );

        ClassNode inventoryService =
                ClassNode.from(
                        TestFixtures.createClass(
                                "com.example.inventory",
                                "InventoryService"
                        )
                );
        ClassNode legacyHelper =
                ClassNode.from(
                        TestFixtures.createClass(
                                "com.example.legacy",
                                "LegacyHelper"
                        )
                );
        ClassNode reportingService =
                ClassNode.from(
                        TestFixtures.createClass(
                                "com.example.reporting",
                                "ReportingService"
                        )
                );


        return new ProjectGraph(
                List.of(
                        orderController,
                        orderService,
                        inventoryService,
                        auditService,
                        legacyHelper,
                        reportingService
                ),
                List.of(
                        new ClassDependency(
                                orderController.id(),
                                orderService.id(),
                                DependencyType.FIELD
                        ),
                        new ClassDependency(
                                orderController.id(),
                                inventoryService.id(),
                                DependencyType.FIELD
                        ),
                        new ClassDependency(
                                orderService.id(),
                                auditService.id(),
                                DependencyType.FIELD
                        ),
                        new ClassDependency(
                                inventoryService.id(),
                                auditService.id(),
                                DependencyType.CONSTRUCTOR
                        ),
                        new ClassDependency(
                                auditService.id(),
                                legacyHelper.id(),
                                DependencyType.CONSTRUCTOR
                        ),
                        new ClassDependency(
                                orderService.id(),
                                legacyHelper.id(),
                                DependencyType.CONSTRUCTOR
                        )
                )
        );
    }
}
