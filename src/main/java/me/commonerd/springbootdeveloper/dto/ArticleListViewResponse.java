package me.commonerd.springbootdeveloper.dto;

import lombok.Getter;
import me.commonerd.springbootdeveloper.domain.Article;

import java.time.LocalDateTime;


@Getter
public class ArticleListViewResponse {

    private final Long id;

    private final String author; //2023.06.06.by commonerd : 게시물 리스트에 작성자 추가

    private final String title;
    private final String content;

    private LocalDateTime createdAt; //2023.06.06.by commonerd : 게시물 리스트에 작성시간 추가

    private int likeCount;
    private boolean liked;


    public ArticleListViewResponse(Article article, int likeCount, boolean liked) {
        this.id = article.getId();
        this.author = article.getAuthor(); //2023.06.06.by commonerd : 게시물 리스트에 작성자 추가
        this.title = article.getTitle();
        this.content = article.getContent();
        this.createdAt = article.getCreatedAt(); //2023.06.06.by commonerd : 게시물 리스트에 작성시간 추가
        this.likeCount = likeCount; // 2023.08.10.by commonerd : 게시물 리스트에 좋아요 수 추가
        this.liked = liked;// 2023.08.10.by commonerd : 게시물 리스트에 좋아요  여부 추가

    }
}