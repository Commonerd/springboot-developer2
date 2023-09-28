package me.commonerd.springbootdeveloper.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.commonerd.springbootdeveloper.domain.Article;
import me.commonerd.springbootdeveloper.domain.ImageFile;
import me.commonerd.springbootdeveloper.domain.VideoFile;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
public class ArticleViewResponse {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String author;
    private List<ImageFile> imageFiles; // 이미지 파일 리스트 추가

    public ArticleViewResponse(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.createdAt = article.getCreatedAt();
        this.author = article.getAuthor();
        this.imageFiles = article.getImageFiles(); // 이미지 파일 리스트 가져오기
    }
}