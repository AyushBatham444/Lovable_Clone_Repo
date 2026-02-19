package com.project.demo.Entity;


import com.project.demo.Entity.enums.ChatSessionId;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "chat_sessions")
public class ChatSession {

    @EmbeddedId
    private ChatSessionId id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @MapsId("projectId")
    @JoinColumn(name = "project_id",nullable = false,updatable = false)
    Project project;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id",nullable = false,updatable = false)
    User user;


    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    Instant createdAt;          // Instant is same as LocalDateTime with some extra functionality

    @UpdateTimestamp
    Instant updatedAt;

    Instant deletedAt;//soft delete

}
