package com.github.jvalentino.golf.dw

import groovy.transform.CompileDynamic
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * Main class
 * @author john.valentino
 */
@SpringBootApplication
@CompileDynamic
@EnableScheduling
class GolfEtlApp {

    static void main(String[] args) {
        SpringApplication.run(GolfEtlApp, args)
    }

}
