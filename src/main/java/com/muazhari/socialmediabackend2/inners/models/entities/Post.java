package com.muazhari.socialmediabackend2.inners.models.entities;

import com.muazhari.socialmediabackend2.inners.models.Model;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
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
@ToString(callSuper = true)
public class Post extends Model {
    @Id
    UUID id;
    String title;
    String content;
    UUID accountId;
    UUID imageId;

    @Transient
    String imageUrl;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    @ToString.Exclude
    Set<PostLike> postLikes;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Post post = (Post) o;
        return getId() != null && Objects.equals(getId(), post.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}