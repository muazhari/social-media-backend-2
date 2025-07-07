package com.muazhari.socialmediabackend2.inners.models.valueobjects;

import com.muazhari.socialmediabackend2.inners.models.Model;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Builder
public class ChatRoomInput extends Model {
    private String name;
    private String description;
}

