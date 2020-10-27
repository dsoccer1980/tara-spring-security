package ee.aktors.tara.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "ee.aktors.tara")
@PropertySources({
    @PropertySource("classpath:tara.properties"),
    @PropertySource(value = "${tara.properties.location}", ignoreResourceNotFound = true),
    @PropertySource("classpath:jdbc.properties"),
    @PropertySource(value = "${jdbc.properties.location}", ignoreResourceNotFound = true)})
public class WebConfig implements WebMvcConfigurer {

  @Bean
  public ViewResolver getViewResolver() {
    InternalResourceViewResolver resolver = new InternalResourceViewResolver();
    resolver.setPrefix("/WEB-INF/view/");
    resolver.setSuffix(".jsp");
    return resolver;
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/resources/**", "/css/**", "/js/**", "/fonts/**", "/img/**")
        .addResourceLocations("/WEB-INF/resources/", "/WEB-INF/resources/css/",
            "/WEB-INF/resources/js/", "/WEB-INF/resources/fonts/", "/WEB-INF/resources/img/");
  }

}
