package com.thd.mapserver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class MapServerConfiguration implements WebMvcConfigurer {
	@Value("${mapserver.connection-string}")
	private String connectionString;

	//Code for setting up the Repositories, Services, ...
}
