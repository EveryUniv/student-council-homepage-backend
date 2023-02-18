package com.dku.council.domain.user;

import com.dku.council.global.base.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.EnumType.STRING;

@Entity
@Table(name = "DKU_USER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    private String classId;

    private String password;

    @Column(length = 20)
    private String name;

    @Enumerated(STRING)
    private Major major;

    private String phone;

    @Enumerated(STRING)
    private UserStatus status;

    @Enumerated(STRING)
    private UserRole userRole;


    @Builder
    public User(String classId, String password, String name, Major major, String phone, UserStatus status, UserRole role) {
        this.classId = classId;
        this.password = password;
        this.name = name;
        this.major = major;
        this.phone = phone;
        this.status = status;
        this.userRole = role;
    }
}
