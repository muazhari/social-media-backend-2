package com.muazhari.socialmediabackend2.outers.configs.datastores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.util.Objects;


@Configuration
public class OneDatastore {

    @Autowired
    private Environment environment;

    @Bean
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(Objects.requireNonNull(environment.getProperty("datastore.one.host")));
        config.setPort(Integer.parseInt(Objects.requireNonNull(environment.getProperty("datastore.one.port"))));
        config.setPassword(Objects.requireNonNull(environment.getProperty("datastore.one.password")));

        return new LettuceConnectionFactory(config);
    }

}
