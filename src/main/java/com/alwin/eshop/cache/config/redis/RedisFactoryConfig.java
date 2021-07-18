package com.alwin.eshop.cache.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

//@Configuration
public class RedisFactoryConfig {

    /**
     * Type safe representation of application.properties
     */
    private final ClusterConfigurationProperties clusterProperties;

    public RedisFactoryConfig(ClusterConfigurationProperties clusterProperties) {
        this.clusterProperties = clusterProperties;
    }

    @Bean
    public RedisConnectionFactory connectionFactory() {

        return new JedisConnectionFactory(
                new RedisClusterConfiguration(clusterProperties.getNodes()));
    }

}
