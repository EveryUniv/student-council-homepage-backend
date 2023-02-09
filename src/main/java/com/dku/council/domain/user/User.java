package com.dku.council.domain.user;

import com.dku.council.domain.UserRole;
import lombok.*;

import javax.persistence.*;
import javax.websocket.server.ServerEndpoint;

@Entity
@Table(name = "DKUSER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

}
