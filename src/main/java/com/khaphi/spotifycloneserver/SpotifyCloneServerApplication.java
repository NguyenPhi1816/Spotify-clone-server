package com.khaphi.spotifycloneserver;

import com.khaphi.spotifycloneserver.config.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(RsaKeyProperties.class)
@SpringBootApplication
public class SpotifyCloneServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpotifyCloneServerApplication.class, args);
	}

}
