package com.mariluz.sales;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=password",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "JWT_SECRET=98PAzqkLUAElxtt+iNCg3GxPvqHI8ytzNfilW2S3RhQ=" // hash falso para que no de error el test 
})
class SalesApplicationTests {

    @Test
    void contextLoads() {}
}
