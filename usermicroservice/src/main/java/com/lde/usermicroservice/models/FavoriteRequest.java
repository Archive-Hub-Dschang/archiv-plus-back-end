package com.lde.usermicroservice.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavoriteRequest {
    private Long learnerId;
    private String subjectId;
}
