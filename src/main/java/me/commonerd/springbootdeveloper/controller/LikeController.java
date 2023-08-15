package me.commonerd.springbootdeveloper.controller;
import me.commonerd.springbootdeveloper.service.*;
import me.commonerd.springbootdeveloper.dto.LikeRequest;
import me.commonerd.springbootdeveloper.dto.LikeResponse;
import me.commonerd.springbootdeveloper.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
public class LikeController {

    @Autowired
    private LikeService likeService;


    @PostMapping("/likeArticle")
    public ResponseEntity<LikeResponse> likeArticle(@RequestBody LikeRequest request, Principal principal) {
        LikeResponse response = new LikeResponse();
        String current_userId = principal.getName(); // 현재 사용자의 ID를 가져옴

        // 글 작성자를 팔로잉하고 있는지 확인
        if (likeService.isFollowingAuthor(request.getArticleId(), request.getUserId(), current_userId)) {
            if (likeService.likeArticle(request.getArticleId(), request.getUserId(), current_userId)) {
                response.setLikeCount(likeService.getLikeCount(request.getArticleId()));
            }
        } else {
            // 글 작성자를 팔로잉하지 않는 경우에 대한 처리
            response.setError("ユーザーをフォローしていません。");
        }

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/cancelLikeArticle")
    public LikeResponse cancelLikeArticle(@RequestBody LikeRequest request, Principal principal) {
        LikeResponse response = new LikeResponse();
        String current_userId = principal.getName(); // 현재 사용자의 ID를 가져옴
        if (likeService.cancelLikeArticle(request.getArticleId(), request.getUserId(), current_userId)) {
            response.setLikeCount(likeService.getLikeCount(request.getArticleId()));
        }
        return response;
    }

}
