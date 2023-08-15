package me.commonerd.springbootdeveloper.dto;

import lombok.Data;
import lombok.Getter;
import me.commonerd.springbootdeveloper.domain.Article;
import me.commonerd.springbootdeveloper.domain.Like;

@Data
public class LikeResponse {
    private int likeCount;
    private String Error;

}
