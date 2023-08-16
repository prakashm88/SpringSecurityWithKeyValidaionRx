package com.itechgenie.apps.jdk11.sbfilter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages= {"com.itechgenie.framework","com.itechgenie.apps"})
public class SbFiltersTestingRxApplication {

	public static void main(String[] args) {
		SpringApplication.run(SbFiltersTestingRxApplication.class, args);
	}

}
