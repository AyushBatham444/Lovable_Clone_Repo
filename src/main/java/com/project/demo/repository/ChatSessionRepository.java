package com.project.demo.repository;

import com.project.demo.Entity.ChatSession;
import com.project.demo.Entity.enums.ChatSessionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepository extends JpaRepository<ChatSession, ChatSessionId> {
}
