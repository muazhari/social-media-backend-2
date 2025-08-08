package com.muazhari.socialmediabackend2.inners.models.entities;

import com.muazhari.socialmediabackend2.inners.models.Model;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Account extends Model implements UserDetails {
    UUID id;
    String imageUrl;
    String name;
    String email;
    String password;
    Double totalPostLike;
    Double totalChatMessage;
    List<String> scopes;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return scopes
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public String getUsername() {
        return id.toString();
    }
}