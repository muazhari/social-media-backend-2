package com.muazhari.socialmediabackend2.outers.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.graphql.data.federation.FederationSchemaFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;
import java.util.function.Consumer;

@Configuration
public class FederationConfig {
    @Autowired
    Environment environment;

    @Bean
    public FederationSchemaFactory schemaFactory() {
        return new FederationSchemaFactory();
    }

    @Bean
    public GraphQlSourceBuilderCustomizer customizer(FederationSchemaFactory factory) {
        return builder -> builder.schemaFactory(factory::createGraphQLSchema);
    }

    public HttpGraphQlClient getHttpGraphQlClient(Consumer<HttpHeaders> headers) {
        String url = Objects.requireNonNull(environment.getProperty("router.one.url")) + "/graphql";

        WebClient webClient = WebClient
                .builder()
                .defaultHeaders(headers)
                .baseUrl(url)
                .build();

        return HttpGraphQlClient
                .builder(webClient)
                .build();
    }

    public HttpGraphQlClient getHttpGraphQlClient() {
        String url = Objects.requireNonNull(environment.getProperty("router.one.url")) + "/graphql";

        WebClient webClient = WebClient
                .builder()
                .baseUrl(url)
                .build();

        return HttpGraphQlClient
                .builder(webClient)
                .build();
    }

}