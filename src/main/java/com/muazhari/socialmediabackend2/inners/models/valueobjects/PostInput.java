package com.muazhari.socialmediabackend2.inners.models.valueobjects;

import com.muazhari.socialmediabackend2.inners.models.Model;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
@Accessors(chain = true)
@Builder
public class PostInput extends Model {
    private UUID accountId;
    private String title;
    private String content;
    private MultipartFile image;
}

