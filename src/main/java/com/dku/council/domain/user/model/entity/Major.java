package com.dku.council.domain.user.model.entity;

import com.dku.council.domain.user.model.MajorData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.MessageSource;

import javax.persistence.Embeddable;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import static javax.persistence.EnumType.STRING;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Major {

    /**
     * 알 수 없는 학과 정보라면 NO_DATA가 저장됩니다. 그 외에는 정상 값입니다.
     */
    @NotNull
    @Enumerated(STRING)
    private MajorData major;

    /**
     * 알 수 없는 학과 이름일 때, 그 이름이 여기에 저장됩니다. (평소에는 null)
     * 결국 사용자에게는 NO_DATA가 아니라 이 학과이름으로 보여지므로 큰 문제는 없습니다.
     * 하지만 시간날 때 틈틈히 이 이름을 Major에 추가해두는 것이 좋습니다.
     */
    @Getter(AccessLevel.NONE)
    private String unexpectedMajorName;

    /**
     * 알 수 없는 학과인 경우 여기에 소속 대학이 저장됩니다. (평소에는 null)
     */
    @Getter(AccessLevel.NONE)
    private String unexpectedDepartmentName;

    public Major(MajorData major) {
        this.major = major;
        this.unexpectedMajorName = null;
        this.unexpectedDepartmentName = null;
    }

    public Major(String unexpectedMajorName, String unexpectedDepartmentName) {
        this.major = MajorData.NO_DATA;
        this.unexpectedMajorName = unexpectedMajorName;
        this.unexpectedDepartmentName = unexpectedDepartmentName;
    }

    public String getMajorName(MessageSource messageSource) {
        if (major.isEmpty()) {
            return unexpectedMajorName;
        }
        return major.getName(messageSource);
    }

    public String getDepartmentName(MessageSource messageSource) {
        if (major.isEmpty()) {
            return unexpectedDepartmentName;
        }
        return major.getDepartment().getName(messageSource);
    }
}
