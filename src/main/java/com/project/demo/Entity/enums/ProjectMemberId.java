package com.project.demo.Entity.enums;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable // so that now this field can be embedded anywhere
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberId {
    Long projectId;
    Long userId;
}
