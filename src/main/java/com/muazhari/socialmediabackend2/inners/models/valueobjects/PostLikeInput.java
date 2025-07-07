package com.muazhari.socialmediabackend2.inners.models.valueobjects;

import com.muazhari.socialmediabackend2.inners.models.Model;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Accessors(chain = true)
@Builder
public class PostLikeInput extends Model {
    private UUID postId;
    private UUID accountId;
}

