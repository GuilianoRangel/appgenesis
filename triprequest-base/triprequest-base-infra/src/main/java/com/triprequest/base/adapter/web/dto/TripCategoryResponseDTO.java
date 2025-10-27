package com.triprequest.base.adapter.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripCategoryResponseDTO {
    public Long id;
    public String code;
    public String name;
    public String description;
    public Boolean active;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
}