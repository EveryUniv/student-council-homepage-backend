package com.dku.council.infra.bus.provider;

import com.dku.council.domain.bus.model.BusStation;
import com.dku.council.infra.bus.model.BusArrival;

import java.util.List;

public interface BusArrivalProvider {

    /**
     * 버스 정류소에 도착할 예정인 버스들을 가져옵니다.
     * 도착 정보가 없는 버스는 자동으로 예측됩니다.
     *
     * @param station 정류소
     * @return 도착 예정 버스 목록.
     */
    List<BusArrival> retrieveBusArrival(BusStation station);

    /**
     * Provider에서 사용하는 prefix를 지정합니다. 이 prefix는 provider에서 제공해준 버스 도착정보를
     * 필터링할 때 사용됩니다. Bus enum 목록들중 이름 앞에 이 prefix가 붙은 버스만 필터링해서 사용합니다.
     *
     * @return provider prefix
     */
    String getProviderPrefix();
}
