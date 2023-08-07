package me.commonerd.springbootdeveloper.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.commonerd.springbootdeveloper.domain.Follow;
import me.commonerd.springbootdeveloper.domain.User;
import me.commonerd.springbootdeveloper.repository.FollowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FollowService {

    private final FollowRepository followRepository;


    // フォロー要求を処理するサービスロジック
    @Transactional
    public String addFollow(String followingUser, String followedUser, Long id) {

        Follow follow = followRepository.findByFollowingAndFollowed(followingUser, followedUser);

        // 登録されたフォローがまだない場合
        if (follow == null) {

            follow = new Follow();
            follow.setFollowing(followingUser);
            follow.setFollowed(followedUser);
            followRepository.save(follow);

        } else {
            // 登録済みフォローの場合、失敗メッセージ
            return "もう登録されたフォローです。";
        }
        // フォロー登録時の成功メッセージ
        return "フォローが登録されたました。";
    }


    // アンフォロー要求を処理するサービスロジック
    @Transactional
    public String deleteFollow(String followingUser, String followedUser, Long id) {

        Follow follow = followRepository.findByFollowingAndFollowed(followingUser, followedUser);

        // 該当するフォローがあれば
        if (follow != null) {

            // アンフォローする
            followRepository.delete(follow);

        } else {

            // フォローがない場合はまだ登録されていないというメッセージを表示する
            return "まだ、フォローされていないです。";
        }
        // アンフォロー成功メッセージ
        return "フォローを削除しました。";
    }

    // ログインユーザーがフォローするユーザー数を計算する
    public Long getFollowingCount(String followingUser) {

        return followRepository.countByFollowing(followingUser);
    }

    // ログインユーザーをフォローするユーザー数を計算する
    public Long getFollowerCount(String followedUser) {

        return followRepository.countByFollowed(followedUser);
    }

    // ログインユーザーがフォローするユーザーリストを抽出する
    public List<String> getFollowing(String userId) {

        return followRepository.findFollowingByUserId(userId);
    }

    // ログインユーザーをフォローするユーザーリストを抽出する
    public List<String> getFollowers(String userId) {

        return followRepository.findFollowersByUserId(userId);
    }

    // ログインユーザーがフォローするユーザーリストでアンフォロー要求を処理する。
    @Transactional
    public String deleteFollowList(String followingUser, String followedUser) {

        Follow follow = followRepository.findByFollowingAndFollowed(followingUser, followedUser);

        // 該当するフォローがあれば
        if (follow != null) {

            // アンフォローする
            followRepository.delete(follow);

        } else {

            // フォローがない場合はまだ登録されていないというメッセージを表示する
            return "まだ、フォローされていないです。";
        }
        // アンフォロー成功メッセージ
        return "フォローを削除しました。";
    }
}



