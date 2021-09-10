package com.amayorov.hostel.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineConfiguration {

	@Bean
	public Caffeine caffeineConfig() {
		return Caffeine.newBuilder()
				.expireAfterAccess(60, TimeUnit.MINUTES)
				.initialCapacity(25)
				.maximumSize(150)
				.removalListener(new CustomRemovalListener());
	}

	@Bean
	public CacheManager cacheManager(Caffeine caffeine) {
		CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
		caffeineCacheManager.setCaffeine(caffeine);
		return caffeineCacheManager;
	}
}
