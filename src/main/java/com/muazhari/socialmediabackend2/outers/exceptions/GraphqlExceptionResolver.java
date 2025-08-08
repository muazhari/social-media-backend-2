package com.muazhari.socialmediabackend2.outers.exceptions;

import graphql.GraphQLError;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GraphqlExceptionResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    protected List<GraphQLError> resolveToMultipleErrors(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof AuthenticationException) {
            return List.of(
                    GraphQLError
                            .newError()
                            .errorType(ErrorType.BAD_REQUEST)
                            .message("Authentication failed, please log in.")
                            .path(env.getExecutionStepInfo().getPath())
                            .location(env.getField().getSourceLocation())
                            .build()
            );
        } else if (ex instanceof AuthorizationException) {
            return List.of(
                    GraphQLError
                            .newError()
                            .errorType(ErrorType.FORBIDDEN)
                            .message("Authorization failed, you do not have permission.")
                            .path(env.getExecutionStepInfo().getPath())
                            .location(env.getField().getSourceLocation())
                            .build()
            );
        }

        return super.resolveToMultipleErrors(ex, env);
    }
}