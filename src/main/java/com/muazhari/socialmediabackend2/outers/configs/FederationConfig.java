package com.muazhari.socialmediabackend2.outers.configs;

import graphql.kickstart.servlet.apollo.ApolloScalars;
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.data.federation.FederationSchemaFactory;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class FederationConfig {

    @Bean
    public FederationSchemaFactory schemaFactory() {
        return new FederationSchemaFactory();
    }

    @Bean
    public GraphQlSourceBuilderCustomizer customizer(FederationSchemaFactory factory) {
        return builder -> builder.schemaFactory(factory::createGraphQLSchema);
    }

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder.scalar(ApolloScalars.Upload);
    }
}