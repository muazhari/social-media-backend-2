package com.muazhari.socialmediabackend2.inners.models.valueobjects;

import com.muazhari.socialmediabackend2.inners.models.Model;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Accessors(chain = true)
@Builder
public class ChatRoomMemberInput extends Model {
    private UUID accountId;
    private UUID chatRoomId;
}

