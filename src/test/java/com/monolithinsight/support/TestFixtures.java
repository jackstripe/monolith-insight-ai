package com.monolithinsight.support;

import com.monolithinsight.domain.ClassNode;
import com.monolithinsight.domain.JavaClassInfo;
import com.monolithinsight.domain.JavaFieldInfo;
import com.monolithinsight.domain.JavaMethodInfo;

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
}
