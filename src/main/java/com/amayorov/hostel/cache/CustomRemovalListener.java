package com.amayorov.hostel.cache;

import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;

@Slf4j
public class CustomRemovalListener implements RemovalListener<Object, Object> {
	@Override
	public void onRemoval(@Nullable Object key, @Nullable Object value, RemovalCause cause) {
		log.info("Removal listener called with key {}, cause {}, evicted {}", key, cause.toString(), cause.wasEvicted());
	}
}
