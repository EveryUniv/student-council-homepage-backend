package com.dku.council.infra.dku.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MajorSubject extends Subject {

    /**
     * 수강조직
     */
    private final String major;

    /**
     * 학년
     */
    private final int grade;


    public MajorSubject(String major, int grade, Subject subject) {
        super(subject);
        this.major = major;
        this.grade = grade;
    }
}
