package com.dku.council.domain.user.model.entity;

import com.dku.council.domain.user.model.Major;
import com.dku.council.domain.user.model.UserRole;
import com.dku.council.domain.user.model.UserStatus;
import com.dku.council.global.base.BaseEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

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

    @NotNull
    private String studentId;

    @NotNull
    private String password;

    @NotNull
    @Column(length = 20)
    private String name;

    /**
     * 알 수 없는 학과 정보라면 NO_DATA가 저장됩니다.
     */
    @NotNull
    @Enumerated(STRING)
    private Major major;

    /**
     * 알 수 없는 학과 이름일 때, 그 이름이 여기에 저장됩니다. (평소에는 null)
     * 결국 사용자에게는 NO_DATA가 아니라 이 학과이름으로 보여지므로 큰 문제는 없습니다.
     * 하지만 시간날 때 틈틈히 이 이름을 Major에 추가해두는 것이 좋습니다.
     */
    private String unexpectedMajorName;

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
                 String unexpectedMajorName,
                 @NonNull String phone,
                 UserStatus status,
                 UserRole role) {
        this.studentId = studentId;
        this.password = password;
        this.name = name;
        this.major = major;
        this.unexpectedMajorName = unexpectedMajorName;
        this.phone = phone;
        this.status = status;
        this.userRole = role;
    }
}
