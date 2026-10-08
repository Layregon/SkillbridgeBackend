package com.inserta.gestioncompetencias.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Permite refrescar rutas de Angular cuando la SPA compilada se sirve desde Spring Boot.
 * Las rutas de API y los ficheros con extension no se redirigen.
 */
@Configuration
public class SpaConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registro) {
        registro.addViewController("/{ruta:[^\\.]*}")
                .setViewName("forward:/index.html");
        registro.addViewController("/{ruta:[^\\.]*}/{sub:[^\\.]*}")
                .setViewName("forward:/index.html");
        registro.addViewController("/{ruta:[^\\.]*}/{sub:[^\\.]*}/{tercero:[^\\.]*}")
                .setViewName("forward:/index.html");
    }
}
