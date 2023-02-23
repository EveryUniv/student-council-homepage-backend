package com.dku.council.domain.user.model.entity;

import com.dku.council.domain.user.model.Major;
import com.dku.council.domain.user.model.UserRole;
import com.dku.council.domain.user.model.UserStatus;
import com.dku.council.global.base.BaseEntity;
import lombok.*;
import org.springframework.context.MessageSource;

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
    @Getter(AccessLevel.NONE)
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

    /**
     * 소속학과 이름을 가져옵니다. 인식되지 않은 학과가 저장된 경우에도
     * 이름은 DB에 저장되어있으므로, 그 이름을 가져옵니다.
     *
     * @return 소속학과 이름
     */
    public String getMajorName(MessageSource messageSource) {
        if (major == Major.NO_DATA) {
            return unexpectedMajorName;
        }
        return major.getName(messageSource);
    }

    /**
     * 학과 정보를 인식하지 못한 경우에 NO_DATA로 저장됩니다. 이때는 학과 이름을
     * getMajorName으로 얻어올 수 있습니다. 애초에 학과 이름을 가져오는게 목적이라면
     * 이 함수말고 getMajorName을 사용하세요.
     */
    public Major getMajor() {
        return major;
    }
}
