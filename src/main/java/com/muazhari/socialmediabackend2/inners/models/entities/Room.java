package com.muazhari.socialmediabackend2.inners.models.entities;

import com.muazhari.socialmediabackend2.inners.models.Model;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Set;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "post")
@Entity
public class Room extends Model {
    @Id
    UUID id;
    String name;
    String description;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    Set<Chat> chats;
}