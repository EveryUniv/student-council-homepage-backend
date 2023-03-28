package com.dku.council.mock;


import com.dku.council.domain.post.model.entity.posttype.Petition;
import com.dku.council.domain.statistic.PetitionStatistic;
import com.dku.council.domain.statistic.model.dto.PetitionStatisticDto;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.util.EntityUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PetitionStatisticMock {

    public static PetitionStatistic create(User user, Petition petition){
        PetitionStatistic build = PetitionStatistic.builder()
                .user(user)
                .petition(petition)
                .build();
        EntityUtil.injectId(PetitionStatistic.class, build, RandomGen.nextLong());
        return build;
    }

    public static PetitionStatistic create(User user){
        return create(user, PetitionMock.createWithDummy());
    }

    public static PetitionStatistic create(Petition petition){
        return create(UserMock.createDummyMajor(), petition);
    }

    public static PetitionStatistic create(Petition petition, String department){
        return create(UserMock.createMajor("major", department), petition);
    }

    public static PetitionStatistic create(String major, String department){
        return create(UserMock.createMajor(major, department));
    }
    public static PetitionStatistic create(String department){
        return create(UserMock.createMajor("major", department));
    }
    public static PetitionStatisticDto createDto(){
        List<Map.Entry<String, Integer>> dto = new ArrayList<>();
        for(int i=0; i<10; i++){
            dto.add(Map.entry("major"+i, i));
        }
        return new PetitionStatisticDto(dto);
    }

    public static List<PetitionStatistic> list(Petition petition){
        List<PetitionStatistic> ret = new ArrayList<>();
        for(int i=0; i<10; i++){
//            ret.add(create(petition));
            ret.add(create(petition, "department" + i));
        }
        return ret;

    }

}
