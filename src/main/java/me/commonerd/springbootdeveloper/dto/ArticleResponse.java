package me.commonerd.springbootdeveloper.dto;

import lombok.Getter;
import me.commonerd.springbootdeveloper.domain.Article;
import me.commonerd.springbootdeveloper.domain.ImageFile;
import me.commonerd.springbootdeveloper.domain.VideoFile;

import java.util.List;

@Getter
public class ArticleResponse {

    private final String title;
    private final String content;


    public ArticleResponse(Article article) {
        this.title = article.getTitle();
        this.content = article.getContent();

    }
}
