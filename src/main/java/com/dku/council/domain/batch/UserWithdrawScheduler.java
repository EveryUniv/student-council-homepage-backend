package com.dku.council.domain.batch;

import com.dku.council.domain.comment.model.CommentStatus;
import com.dku.council.domain.comment.model.entity.Comment;
import com.dku.council.domain.comment.repository.CommentRepository;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.repository.post.PostRepository;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.error.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserWithdrawScheduler {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Value("${app.user.default-user-id}")
    private final Long defaultUserId;

    @Value("${app.user.delete-period}")
    private final Period deletePeriod;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void updateInactiveUsersToDefault() {
        LocalDateTime inactiveDate = LocalDateTime.now().minus(deletePeriod);
        List<User> inactiveUsers = userRepository.findAllWithDeleted(inactiveDate, defaultUserId);

        User defaultUser = userRepository.findByIdWithNotActive(defaultUserId).orElseThrow(UserNotFoundException::new);

        for (User user : inactiveUsers) {
            user.emptyOutUserInfo();

            Page<Post> posts = postRepository.findAllByUserIdWithNotActive(user.getId(), Pageable.unpaged());
            for (Post post : posts) {
                post.changeUser(defaultUser);
                post.markAsDeleted(false);
            }

            List<Comment> comments = commentRepository.findAllByUserId(user.getId());
            for (Comment comment : comments) {
                comment.changeUser(defaultUser);
                comment.updateStatus(CommentStatus.DELETED);
            }
        }
    }
}
