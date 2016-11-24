package com.dev.Utils;

import org.springframework.boot.SpringApplication;

import java.io.File;

/**
 * Created by sonicmaster on 23.11.16.
 */
public class FileLoader {

    private final String FILE_NAME = "RoutesMap.txt";


    public File loadFile() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(FILE_NAME).getFile());
        return file;
    }
}
