package com.triprequest.base.adapter.web.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripCategoryRequestDTO {
    public String code;
    public String name;
    public String description;
    public Boolean active;
}