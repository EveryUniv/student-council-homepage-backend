package com.dku.council.domain.user.model.entity;

import com.dku.council.domain.user.model.UserStatus;
import com.dku.council.global.auth.role.UserRole;
import com.dku.council.global.base.BaseEntity;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "DKU_USER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @NotNull
    private String studentId;

    @NotNull
    private String password;

    @NotNull
    @Column(length = 20)
    private String name;

    @NotNull
    @Column(length = 20)
    private String nickname;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "major_id")
    private Major major;

    private Integer yearOfAdmission;

    @NotNull
    private String phone;

    @Enumerated(STRING)
    private UserStatus status;

    @Enumerated(STRING)
    private UserRole userRole;


    @Builder
    private User(@NonNull String studentId,
                 @NonNull String password,
                 @NonNull String name,
                 @NonNull Major major,
                 @NonNull String phone,
                 @NonNull String nickname,
                 Integer yearOfAdmission,
                 UserStatus status,
                 UserRole role) {
        this.studentId = studentId;
        this.password = password;
        this.name = name;
        this.major = major;
        this.phone = phone;
        this.nickname = nickname;
        this.yearOfAdmission = yearOfAdmission;
        this.status = status;
        this.userRole = role;
    }

    /**
     * 비밀번호를 변경합니다. {@link PasswordEncoder}로 인코딩된 비밀번호를 넣어야 합니다.
     *
     * @param encodedPassword 인코딩된 비밀번호
     */
    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void changeNickName(String nickname){ this.nickname = nickname; }
}
