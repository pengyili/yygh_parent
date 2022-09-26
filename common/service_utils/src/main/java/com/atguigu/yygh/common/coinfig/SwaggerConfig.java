package com.atguigu.yygh.common.coinfig;



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
    public Docket adminDocket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(adminApiInfo())
                .groupName("管理员")
                .select()
                //只显示admin路径下的页面
                .paths(PathSelectors.regex("/admin/.*"))
                .build();

    }
    @Bean
    public Docket userDocket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("用户")
                .apiInfo(adminApiInfo())
                .select()
                //只显示admin路径下的页面
                .paths(PathSelectors.regex("/user/.*"))
                .build();

    }
    @Bean
    public Docket apiDocket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("第三方api")
                .apiInfo(adminApiInfo())
                .select()
                //只显示admin路径下的页面
                .paths(PathSelectors.regex("/api/.*"))
                .build();

    }

    private ApiInfo adminApiInfo(){
        return new ApiInfoBuilder()
                .title("后台管理系统-API文档")
                .description("本文档描述了后台管理系统微服务接口定义")
                .version("1.0")
                .contact(new Contact("atguigu", "http://atguigu.com", "49321112@qq.com"))
                .build();
    }  private ApiInfo userApiInfo(){
        return new ApiInfoBuilder()
                .title("用户管理系统-API文档")
                .description("本文档描述了前端用户访问系统微服务接口定义")
                .version("1.0")
                .contact(new Contact("atguigu", "http://atguigu.com", "49321112@qq.com"))
                .build();
    }  private ApiInfo apiApiInfo(){
        return new ApiInfoBuilder()
                .title("后台管理系统-API文档")
                .description("本文档提供供第三方接口定义")
                .version("1.0")
                .contact(new Contact("atguigu", "http://atguigu.com", "49321112@qq.com"))
                .build();
    }
}
