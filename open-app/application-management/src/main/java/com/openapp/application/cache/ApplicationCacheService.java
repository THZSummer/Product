package com.openapp.application.cache;

import com.openapp.application.domain.Application;
import com.openapp.application.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 应用缓存服务
 * 使用 Redis 缓存应用数据，提升查询性能
 * 
 * @author open-app
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationCacheService {

    private final ApplicationRepository applicationRepository;

    /**
     * 获取应用详情（带缓存）
     * 缓存 key: app:{id}
     * TTL: 5 分钟
     */
    @Cacheable(value = "applications", key = "'app:' + #id", unless = "#result == null")
    public Application getApplicationById(String id) {
        log.debug("Fetching application from repository: id={}", id);
        Optional<Application> optional = applicationRepository.findById(id);
        return optional.orElse(null);
    }

    /**
     * 缓存应用数据
     * 缓存 key: app:{id}
     */
    @CachePut(value = "applications", key = "'app:' + #application.id")
    public Application cacheApplication(Application application) {
        log.debug("Caching application: id={}", application.getId());
        return application;
    }

    /**
     * 从缓存中移除应用
     * 缓存 key: app:{id}
     */
    @CacheEvict(value = "applications", key = "'app:' + #id")
    public void evictApplicationCache(String id) {
        log.debug("Evicting application cache: id={}", id);
    }

    /**
     * 缓存穿透保护 - 缓存空值
     * 当应用不存在时，缓存 null 值，防止缓存穿透
     * 缓存 key: app:{id}
     * TTL: 1 分钟（较短）
     */
    @Cacheable(value = "applications", key = "'app:null:' + #id", unless = "#result != null")
    public Application getApplicationByIdWithNullCache(String id) {
        log.debug("Fetching application with null cache protection: id={}", id);
        Optional<Application> optional = applicationRepository.findById(id);
        return optional.orElse(null);
    }

    /**
     * 获取所有者应用列表缓存 key
     * 格式：app:owner:{ownerId}:page:{page}:size:{size}
     */
    public String getOwnerCacheKey(String ownerId, int page, int size) {
        return String.format("app:owner:%s:page:%d:size:%d", ownerId, page, size);
    }

    /**
     * 获取应用列表缓存 key
     * 格式：app:list:page:{page}:size:{size}:status:{status}
     */
    public String getListCacheKey(int page, int size, String status) {
        if (status != null) {
            return String.format("app:list:page:%d:size:%d:status:%s", page, size, status);
        }
        return String.format("app:list:page:%d:size:%d", page, size);
    }
}
