package com.dku.council.domain.user;

import com.dku.council.domain.UserRole;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "DKU_USER")
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
