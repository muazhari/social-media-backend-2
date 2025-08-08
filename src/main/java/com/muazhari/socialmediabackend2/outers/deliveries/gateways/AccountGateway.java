package com.muazhari.socialmediabackend2.outers.deliveries.gateways;

import com.muazhari.socialmediabackend2.inners.models.entities.Account;
import com.muazhari.socialmediabackend2.outers.configs.FederationConfig;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

@Component
public class AccountGateway {

    @Autowired
    FederationConfig federationConfig;

    public Account getAccountById(UUID accountId) {
        String document = """
                query Account($id: ID!) {
                    account(id: $id) {
                        id
                        imageUrl
                        name
                        email
                        totalPostLike
                        totalChatMessage
                        scopes
                    }
                }
                """;

        RequestAttributes attributes = Objects.requireNonNull(RequestContextHolder.getRequestAttributes());
        HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
        Consumer<HttpHeaders> headers = httpHeaders -> {
            httpHeaders.set(HttpHeaders.AUTHORIZATION, request.getHeader(HttpHeaders.AUTHORIZATION));
        };
        HttpGraphQlClient.RequestSpec requestSpec = federationConfig
                .getHttpGraphQlClient(headers)
                .document(document)
                .variable("id", accountId);

        Account account = requestSpec
                .retrieve("account")
                .toEntity(Account.class)
                .block();

        if (account == null) {
            throw new UsernameNotFoundException("account not found");
        }

        return account;
    }

}
