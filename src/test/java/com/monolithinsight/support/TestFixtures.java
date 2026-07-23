package com.monolithinsight.support;

import com.monolithinsight.domain.JavaClassInfo;
import com.monolithinsight.domain.JavaFieldInfo;
import com.monolithinsight.domain.ProjectAnalysis;
import com.monolithinsight.domain.ProjectGraph;

import java.util.List;

public class TestFixtures {

    public static JavaClassInfo createClass( String packageName,
                                                        String className){

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

    public static JavaClassInfo createOrderService(){
        return new JavaClassInfo(
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
}

    public static JavaClassInfo createInventoryService () {
        return new JavaClassInfo("com.example.orders",
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
    }
    public static JavaClassInfo createOrderRepository () {
        return new JavaClassInfo(
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
    }

    public static JavaClassInfo createUserService(){

        return new JavaClassInfo(
                "com.example",
                "UserService",
                "CLASS",
                List.of("Service"),
                List.of(""),
                List.of(),
                List.of("createUser"),
                "src/main/java/com/example/service/UserService.java"
        );
    }

}
