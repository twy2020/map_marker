package com.map.map_marker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories("com.map.map_marker.Repository") // 确保包路径正确
@EnableScheduling  // 启用定时任务
public class MapMarkerApplication {
	public static void main(String[] args) {
		SpringApplication.run(MapMarkerApplication.class, args);
	}
}
