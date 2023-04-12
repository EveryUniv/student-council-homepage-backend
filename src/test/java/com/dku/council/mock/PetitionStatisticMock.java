package com.dku.council.mock;


import com.dku.council.domain.post.model.entity.posttype.Petition;
import com.dku.council.domain.statistic.model.dto.PetitionStatisticDto;
import com.dku.council.domain.statistic.model.entity.PetitionStatistic;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.util.EntityUtil;

import java.util.ArrayList;
import java.util.List;

public class PetitionStatisticMock {

    public static PetitionStatistic create(User user, Petition petition) {
        PetitionStatistic build = PetitionStatistic.builder()
                .user(user)
                .petition(petition)
                .build();
        EntityUtil.injectId(PetitionStatistic.class, build, RandomGen.nextLong());
        return build;
    }

    public static PetitionStatistic create(Petition petition) {
        return create(UserMock.createDummyMajor(), petition);
    }

    public static PetitionStatistic createDummy(Petition petition, String department) {
        return create(UserMock.createMajor("major", department), petition);
    }

    public static List<PetitionStatisticDto> createList(int size) {
        List<PetitionStatisticDto> dtoList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            dtoList.add(new PetitionStatisticDto("major" + i, (long) i));
        }
        return dtoList;
    }

    public static List<PetitionStatistic> list(Petition petition) {
        return list(null, petition);
    }

    public static List<PetitionStatistic> list(List<User> users, Petition petition) {
        List<PetitionStatistic> ret = new ArrayList<>();
        int[] counts = {10, 100, 40, 39, 41};
        for (int i = 0; i < counts.length; i++) {
            for (int j = 0; j < counts[i]; j++) {
                PetitionStatistic ent;
                if (users != null) {
                    ent = create(users.get(i), petition);
                } else {
                    ent = createDummy(petition, "department" + counts[i]);
                }
                ret.add(ent);
            }
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
