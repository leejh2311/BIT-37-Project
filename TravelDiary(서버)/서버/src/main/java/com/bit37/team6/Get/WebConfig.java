package com.bit37.team6.Get;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private String connectPath = "/image/**";
    private String resourcePath = "file:///"
            + System.getProperty("user.dir")
            + File.separator + "src"
            + File.separator + "main"
            + File.separator + "resources"
            + File.separator + "image"
            + File.separator;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(connectPath)
                .addResourceLocations(resourcePath);
    }
}