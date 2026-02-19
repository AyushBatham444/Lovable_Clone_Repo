package com.project.demo.repository;

import com.project.demo.Entity.ChatEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatEventRepository extends JpaRepository<ChatEvent,Long> {
}
