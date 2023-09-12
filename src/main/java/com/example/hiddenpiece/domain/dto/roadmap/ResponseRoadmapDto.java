package com.example.hiddenpiece.domain.dto.roadmap;

import com.example.hiddenpiece.domain.entity.roadmap.Roadmap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseRoadmapDto {
    private Long id;
    private String type;
    private String title;
    private String username;
    private String description;
    private LocalDateTime createdAt;

    public static ResponseRoadmapDto fromEntity(Roadmap entity) {
        return ResponseRoadmapDto.builder()
                .id(entity.getId())
                .type(entity.getType())
                .title(entity.getTitle())
                .username(entity.getUser().getUsername())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
