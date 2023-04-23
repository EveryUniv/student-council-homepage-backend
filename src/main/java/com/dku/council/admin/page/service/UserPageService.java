package com.dku.council.admin.page.service;

import com.dku.council.admin.page.dto.CommentPageDto;
import com.dku.council.admin.page.dto.PostPageDto;
import com.dku.council.admin.page.dto.UserPageDto;
import com.dku.council.domain.comment.repository.CommentRepository;
import com.dku.council.domain.post.repository.post.PostRepository;
import com.dku.council.domain.user.model.UserStatus;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.domain.user.repository.spec.UserSpec;
import com.dku.council.global.error.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserPageService {
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public Page<UserPageDto> list(String name, Pageable pageable){
        Specification<User> spec = UserSpec.withUsernameOrNickname(name);
        return userRepository.findAll(spec, pageable).map(UserPageDto::new);
    }
    public User findUser(Long id){
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public void activeUser(Long id){
        User user = findUser(id);
        user.changeStatus(UserStatus.ACTIVE);
    }
    public void deActiveUser(Long id){
        User user = findUser(id);
        user.changeStatus(UserStatus.INACTIVE);
    }
    public Page<CommentPageDto> commentList(Long id, Pageable pageable){
        return commentRepository.findAllByUserIdWithAdmin(id, pageable).map(CommentPageDto::new);
    }

    public Page<PostPageDto> postList(Long id, Pageable pageable){
        return postRepository.findAllByUserIdWithAdmin(id, pageable).map(PostPageDto::new);
    }

}
