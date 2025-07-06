package com.muazhari.socialmediabackend2.outers.configs.datastores;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Objects;

@Configuration
public class FourDatastore {

    @Autowired
    private Environment environment;

    @Bean
    public MinioClient minioClient() {
        return MinioClient
                .builder()
                .endpoint(
                        Objects.requireNonNull(environment.getProperty("datastore.four.host")),
                        Integer.parseInt(Objects.requireNonNull(environment.getProperty("datastore.four.port"))),
                        false
                )
                .credentials(
                        Objects.requireNonNull(environment.getProperty("datastore.four.root.user")),
                        Objects.requireNonNull(environment.getProperty("datastore.four.root.password"))
                )
                .build();
    }

}

