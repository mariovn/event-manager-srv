package com.mariovn.eventmanager.config;

import java.time.Duration;

import org.ehcache.config.builders.*;
import org.ehcache.jsr107.Eh107Configuration;

import org.hibernate.cache.jcache.ConfigSettings;
import io.github.jhipster.config.JHipsterProperties;

import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.*;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.Cache.Ehcache ehcache = jHipsterProperties.getCache().getEhcache();

        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class,
                ResourcePoolsBuilder.heap(ehcache.getMaxEntries()))
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.getTimeToLiveSeconds())))
                .build());
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, com.mariovn.eventmanager.repository.UserRepository.USERS_BY_LOGIN_CACHE);
            createCache(cm, com.mariovn.eventmanager.repository.UserRepository.USERS_BY_EMAIL_CACHE);
            createCache(cm, com.mariovn.eventmanager.domain.User.class.getName());
            createCache(cm, com.mariovn.eventmanager.domain.Authority.class.getName());
            createCache(cm, com.mariovn.eventmanager.domain.User.class.getName() + ".authorities");
            createCache(cm, com.mariovn.eventmanager.domain.Event.class.getName());
            createCache(cm, com.mariovn.eventmanager.domain.Event.class.getName() + ".expenses");
            createCache(cm, com.mariovn.eventmanager.domain.Event.class.getName() + ".participants");
            createCache(cm, com.mariovn.eventmanager.domain.Expense.class.getName());
            createCache(cm, com.mariovn.eventmanager.domain.Expense.class.getName() + ".justifies");
            createCache(cm, com.mariovn.eventmanager.domain.UserExtra.class.getName());
            createCache(cm, com.mariovn.eventmanager.domain.UserExtra.class.getName() + ".participes");
            createCache(cm, com.mariovn.eventmanager.domain.Participant.class.getName());
            createCache(cm, com.mariovn.eventmanager.domain.UserExtra.class.getName() + ".friends");
            createCache(cm, com.mariovn.eventmanager.domain.Event.class.getName() + ".contains");
            createCache(cm, com.mariovn.eventmanager.domain.Expense.class.getName() + ".contains");
            createCache(cm, com.mariovn.eventmanager.domain.UserExtra.class.getName() + ".participants");
            createCache(cm, com.mariovn.eventmanager.domain.Expense.class.getName() + ".tickets");
            createCache(cm, com.mariovn.eventmanager.domain.Currency.class.getName());
            createCache(cm, com.mariovn.eventmanager.domain.Participant.class.getName() + ".expenses");
            createCache(cm, com.mariovn.eventmanager.domain.UserExtra.class.getName() + ".users");
            // jhipster-needle-ehcache-add-entry
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache == null) {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }

}
