package com.dku.council.domain.timetable.model.entity;

import com.dku.council.domain.user.model.entity.User;
import com.dku.council.global.base.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class TimeTable extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "timetable_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String name;

    @OneToMany(mappedBy = "timetable", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<TimeSchedule> schedules = new ArrayList<>();


    public TimeTable(User user, String name) {
        this.user = user;
        this.name = name;
    }

    public void changeName(String name) {
        this.name = name;
    }
}
