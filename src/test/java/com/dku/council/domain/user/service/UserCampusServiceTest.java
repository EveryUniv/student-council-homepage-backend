package com.dku.council.domain.user.service;

import com.dku.council.domain.user.model.Campus;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.CheonanMajorFilterRepository;
import com.dku.council.mock.UserMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCampusServiceTest {

    @Mock
    private CheonanMajorFilterRepository repository;

    @InjectMocks
    private UserCampusService service;

    @Test
    @DisplayName("정확하게 캠퍼스를 구분하는가?")
    void getUserCampus() {
        // given
        User user1 = UserMock.createMajor("천안학과", "천안대학");
        User user2 = UserMock.createMajor("컴퓨터공학과", "공과대학");

        when(repository.countByFilter(any())).thenAnswer(inv -> {
            String arg = (String) inv.getArguments()[0];
            if (arg.equals("천안대학 천안학과")) {
                return 1L;
            }
            return 0L;
        });

        // when
        Campus campus1 = service.getUserCampus(user1);
        Campus campus2 = service.getUserCampus(user2);

        // then
        assertThat(campus1).isEqualTo(Campus.CHEONAN);
        assertThat(campus2).isEqualTo(Campus.JUKJEON);
    }
}