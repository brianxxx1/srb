package site.wenshuo.srb.base.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket AdminApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2).groupName("adminApi").apiInfo(adminApiInfo()).select().paths(
                Predicates.and(PathSelectors.regex("/admin/.*"))
        ).build();
    }

    @Bean
    public Docket WebApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2).groupName("webApi").apiInfo(webApiInfo()).select().paths(
                Predicates.and(PathSelectors.regex("/api/.*"))
        ).build();
    }

    private ApiInfo adminApiInfo(){
        return new ApiInfoBuilder().title("admin interface doc").contact(new Contact("Wenshuo","http://wenshuo.site","xuw57@mcmaster.ca")).build();
    }
    private ApiInfo webApiInfo(){
        return new ApiInfoBuilder().title("web interface doc").contact(new Contact("Wenshuo","http://wenshuo.site","xuw57@mcmaster.ca")).build();
    }
}

