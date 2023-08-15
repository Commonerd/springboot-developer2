package me.commonerd.springbootdeveloper.service;

import jakarta.transaction.Transactional;
import me.commonerd.springbootdeveloper.domain.Article;
import me.commonerd.springbootdeveloper.domain.Like;
import me.commonerd.springbootdeveloper.repository.BlogRepository;
import me.commonerd.springbootdeveloper.repository.FollowRepository;
import me.commonerd.springbootdeveloper.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private BlogRepository blogRepository;

    @Transactional
    // LikeService.java 파일의 likeArticle 메서드 내부
    public boolean likeArticle(Long articleId, String userId, String following) {
        // 글 작성자를 팔로잉하지 않는 경우 false 반환
        if (!isFollowingAuthor(articleId, userId, following)) {
            return false;
        }

        // 이미 좋아요한 경우도 false 반환
        if (likeRepository.existsByarticleIdAndUserId(articleId, userId)) {
            return false;
        }

        // 해당 글에 대한 좋아요를 저장한다
        Like like = new Like(articleId, userId);
        like.setArticleId(articleId);
        like.setUserId(userId);
        likeRepository.save(like);
        return true;
    }


    public int getLikeCount(Long articleId) {
        return likeRepository.countByarticleId(articleId);
    }


    //기록자를 팔로우하고 있는지 확인하는 메서드
    public boolean isFollowingAuthor(Long articleId, String userId, String following) {

        // 기록아이디를 통해 기록자를 찾아 세팅
        String authorId = getAuthorByarticleId(articleId);

        // 기록자의 아이디가 같다면
        if (userId.equals(authorId)) {

            // 팔로잉유저와 기록자의 아이디를 받아,
            String followed = authorId;
            return followRepository.existsByFollowingAndFollowed(following, followed);
        }
        return false;
    }

    public String getAuthorByarticleId(Long articleId) {
        // Use the method automatically implemented by Spring Data JPA to find the subscriber of the article by the article ID
        Optional<Article> articleOptional = blogRepository.findById(articleId);

        if (articleOptional.isPresent()) {
            Article article = articleOptional.get();
            return article.getAuthor();
        } else {
            // 처리할 값이 존재하지 않을 경우에 대한 처리
            return "Article not found";
        }
    }

    @Transactional
    public boolean cancelLikeArticle(Long articleId, String userId, String following) {
        if (likeRepository.existsByarticleIdAndUserId(articleId, userId) && isFollowingAuthor(articleId, userId, following)) {
            likeRepository.deleteByarticleIdAndUserId(articleId, userId);
            return true;
        }
        return false;
    }

    //게시물 ID와 사용자 ID에 대해 좋아요가 이미 등록되어 있는지 확인
    public boolean isLiked(Long articleId, String userId, String following) {
        if (isFollowingAuthor(articleId, userId, following)) {
            return likeRepository.existsByarticleIdAndUserId(articleId, userId);
        }
        return false;
    }
}
