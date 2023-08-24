package me.commonerd.springbootdeveloper.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Data
public class UpdateArticleRequest {
    private String title;
    private String content;

    private List<MultipartFile> imageFiles; // 이미지 파일 리스트



}