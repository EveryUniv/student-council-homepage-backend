package com.dku.council.mock;


import com.dku.council.domain.comment.model.entity.Comment;
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

    public static List<PetitionStatisticDto> createList(){
        List<Map.Entry<String, Integer>> dto = new ArrayList<>();
        List<PetitionStatisticDto> dtoList = new ArrayList<>();
        for(int i=0; i<4; i++){
            dto.add(Map.entry("major"+i, i));
            dtoList.add(new PetitionStatisticDto("major"+i, i));
        }
        return dtoList;
    }

    public static List<PetitionStatistic> list(Petition petition){
        List<PetitionStatistic> ret = new ArrayList<>();
        for(int i=0; i<10; i++){
            ret.add(create(petition, "department10"));
        }

        for(int i=0; i<100; i++){
            ret.add(create(petition, "department100"));
        }

        for(int i=0; i<40; i++){
            ret.add(create(petition, "department40"));
        }
        for(int i=0; i<39; i++){
            ret.add(create(petition, "department39"));
        }
        for(int i=0; i<41; i++){
            ret.add(create(petition, "department41"));
        }
        return ret;

    }

    public static List<PetitionStatistic> createList(Petition petition, List<User> users, int size) {
        List<PetitionStatistic> ret = new ArrayList<>();
        final int userSize = users.size();
        for (int i = 0; i < size; i++) {
            ret.add(create(users.get(i % userSize), petition));
        }
        return ret;
    }
}
