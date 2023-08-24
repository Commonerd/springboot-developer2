package me.commonerd.springbootdeveloper.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.commonerd.springbootdeveloper.domain.Article;
import me.commonerd.springbootdeveloper.domain.ImageFile;
import me.commonerd.springbootdeveloper.domain.VideoFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Data
public class AddArticleRequest {

    private String title;
    private String content;
//    private List<MultipartFile> videoFiles; // 비디오 파일 리스트
    private List<MultipartFile> imageFiles; // 이미지 파일 리스트

    public Article toEntity(String author) {
        return Article.builder()
                .title(title)
                .content(content)
                .author(author)
                /*.videoFiles(videoFiles)*/
                /*.imageFiles(imageFiles)*/
                .build();
    }
}