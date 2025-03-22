package com.muazhari.socialmediabackend2.inners.models.valueobjects;

import com.muazhari.socialmediabackend2.inners.models.Model;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ResponseBody<T> extends Model {
    private static final boolean IS_INCLUDE_EXCEPTION = true;

    protected Throwable exception;
    private String message;
    private T data;

    public ResponseEntity<ResponseBody<T>> toEntity(HttpStatus status) {
        if (!IS_INCLUDE_EXCEPTION) {
            setException(null);
        }

        return ResponseEntity.status(status).body(this);
    }

}
