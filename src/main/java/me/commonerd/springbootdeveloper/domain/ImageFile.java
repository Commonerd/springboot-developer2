package me.commonerd.springbootdeveloper.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ImageFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    private String filename;
    private String contentType;

    // 나머지 코드는 그대로 둡니다
}
