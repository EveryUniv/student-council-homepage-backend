package com.dku.council.domain.user.service;

import com.dku.council.domain.user.model.Campus;
import com.dku.council.domain.user.model.entity.Major;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.CheonanMajorFilterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCampusService {

    private final CheonanMajorFilterRepository repository;

    public Campus getUserCampus(User user) {
        Major major = user.getMajor();
        String fullName = major.getDepartment() + " " + major.getName();
        if (repository.countByFilter(fullName) > 0) {
            return Campus.CHEONAN;
        } else {
            return Campus.JUKJEON;
        }
    }
}
