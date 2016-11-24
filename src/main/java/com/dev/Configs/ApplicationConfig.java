package com.dev.Configs;

import com.dev.Utils.FileLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by sonicmaster on 23.11.16.
 */
@Configuration
public class ApplicationConfig {

    @Bean
    public FileLoader getFileLoaderBean(){
        return new FileLoader();
    }
}
