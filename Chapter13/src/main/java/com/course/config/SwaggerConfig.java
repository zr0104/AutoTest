package com.course.config;


//@Configuration
//@EnableSwagger2
//public class SwaggerConfig {
//
//    @Bean
//    public Docket api() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(apiInfo())
//                .pathMapping("/")
//                .select()
//                .paths(PathSelectors.regex("/.*"))
//                .build();
//    }
//
//    private ApiInfo apiInfo() {
//
//        return new ApiInfoBuilder().title("UserManager service API")
//                .contact(new Contact("dazhou", "", "42197393@qq.com"))
//                .description("this is UserManager service API")
//                .version("1.0")
//                .build();
//    }
//}


//@Configuration
//@OpenAPIDefinition(
//        info = @Info(
//                title = "UserManager Service API",
//                version = "1.0",
//                description = "This is UserManager Service API",
//                contact = @Contact(
//                        name = "Grayson",
//                        email = "984827201@qq.com"
//                )
//        )
//)
//public class SwaggerConfig {
//    // 无需显式配置 Docket，SpringDoc 自动扫描所有 @RestController
//}


import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("UserManager Service API")
                        .version("1.0")
                        .description("This is UserManager Service API")
                        .contact(new Contact()
                                .name("Sen")
                                .email("984827201@qq.com")
                        )
                );
    }
}

