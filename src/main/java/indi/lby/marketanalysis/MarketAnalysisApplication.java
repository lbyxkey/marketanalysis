package indi.lby.marketanalysis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaRepositories(basePackages = "indi.lby.marketanalysis.repository")
@EnableRedisRepositories(basePackages = "indi.lby.marketanalysis.redisrepository")
@SpringBootApplication
@Slf4j
public class MarketAnalysisApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarketAnalysisApplication.class, args);
	}

}
