// FollowController.java

package me.commonerd.springbootdeveloper.controller;
import me.commonerd.springbootdeveloper.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
public class FollowController {

    private final FollowService followService;
    private final BlogService blogService;


    @Autowired
    public FollowController(FollowService followService, BlogService blogService) {
        this.followService = followService;
        this.blogService = blogService;
    }

    // フォロー要求処理
    @PostMapping("/follow/{id}")
    public String follow(@PathVariable Long id, Principal principal) {

        // スプリングセキュリティPrincipalからログインユーザー情報を抽出
        String followingUser = principal.getName();

        // 該当掲示物の作成者を探す
        String followedUser = blogService.findById(id).getAuthor();

        //フォローアップユーザーとフォロワーユーザーが両方いて、この2人が同じでなければ
         if (followingUser != null && followedUser != null && !followingUser.equals(followedUser)) {

             //フォローする
            return followService.addFollow(followingUser, followedUser, id);
        } else {
            return "フォローを失敗しました。";
        }
    }

    // アンフォロー要求処理
    @DeleteMapping("/unfollow/{id}")
    public String unfollow(@PathVariable Long id, Principal principal) {

        // スプリングセキュリティPrincipalからログインユーザー情報を抽出
        String followingUser = principal.getName();

        // 該当掲示物の作成者を探す
        String followedUser = blogService.findById(id).getAuthor();

        // フォローアップユーザーとフォロワーユーザーが両方いると
        if (followingUser != null && followedUser != null) {

            //アンフォローする
            return followService.deleteFollow(followingUser, followedUser, id);
        } else {
            return "アンフォローを失敗しました。";
        }
    }

    // ログインユーザーがフォローしているユーザー数の照会
    @GetMapping("/following-count")
    public Long getFollowingCount(Principal principal) {
        String followingUser = principal.getName();
        return followService.getFollowingCount(followingUser);
    }

    // ログインユーザーをフォローしているユーザー数の照会
    @GetMapping("/follower-count")
    public Long getFollowerCount(Principal principal) {
        String followedUser = principal.getName();
        return followService.getFollowerCount(followedUser);
    }

    // ログインしたユーザーのフォローアップリスト
    @GetMapping("/get-following/{userId}")
    public List<String> getFollowing(@PathVariable String userId) {
        return followService.getFollowing(userId);
    }

    // ログインしたユーザーのフォロワー一覧
    @GetMapping("/get-followers/{userId}")
    public List<String> getFollowers(@PathVariable String userId) {
        return followService.getFollowers(userId);
    }


    // フォローリスト中のアンフォロー要求処理
    @DeleteMapping("/list-unfollow/")
    public String unfollowList(@RequestBody Map<String, String> userId, Principal principal) {

        // スプリングセキュリティPrincipalからログインユーザー情報を抽出
        String followingUser = principal.getName();

        // リクエストから削除するフォローアップユーザーを抽出
        String followedUser = userId.get("userId");

        // フォローアップユーザーとフォロワーユーザーが両方いると
        if (followingUser != null && followedUser != null) {

            //アンフォローする
            return followService.deleteFollowList(followingUser, followedUser);
        } else {
            return "アンフォローを失敗しました。";
        }
    }
}




