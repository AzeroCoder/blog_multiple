package com.lh.blog;

import com.lh.blog.util.PortUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


@SpringBootApplication
@ServletComponentScan
@EnableCaching
@EnableRedisHttpSession
//@EnableJpaRepositories(basePackages = {"com.lh.blog.dao", "com.lh.blog.bean"})
public class BlogApplication extends SpringBootServletInitializer {
    static {
        PortUtil.checkPort(6379,"Redis",true);
//        PortUtil.checkPort(9300,"ElasticSearch",true);
    }
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(BlogApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);

    }

}
//@Bean
//public EmbeddedServletContainerCustomizer containerCustomizer() {
//
//    return (container -> {
//        ErrorPage error401Page = new ErrorPage(HttpStatus.UNAUTHORIZED, "/exception/401.html");
//        ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/exception/401.html");
//        ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/exception/401.html");
//
//        container.addErrorPages(error401Page, error404Page, error500Page);
//    });
//}



