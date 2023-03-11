package com.dku.council.infra.bus.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Getter
@NoArgsConstructor
@XmlRootElement(name = "response")
public class ResponseGGBusArrival {

    @XmlElement(name = "msgHeader")
    private Header msgHeader;

    @XmlElement(name = "msgBody")
    private Body msgBody;

    @Getter
    @NoArgsConstructor
    public static class Header {
        @XmlElement(name = "queryTime")
        private String queryTime;

        @XmlElement(name = "resultCode")
        private Integer resultCode;

        @XmlElement(name = "resultMessage")
        private String resultMessage;
    }

    @Getter
    @NoArgsConstructor
    public static class Body {
        @XmlElement(name = "busArrivalList")
        private List<BusArrival> busArrivalList;

        @Getter
        @NoArgsConstructor
        public static class BusArrival {
            @XmlElement(name = "flag")
            private String flag;

            @XmlElement(name = "locationNo1")
            private Integer locationNo1;

            @XmlElement(name = "predictTime1")
            private Integer predictTime1;

            @XmlElement(name = "plateNo1")
            private String plateNo1;

            @XmlElement(name = "locationNo2")
            private Integer locationNo2;

            @XmlElement(name = "predictTime2")
            private Integer predictTime2;

            @XmlElement(name = "plateNo2")
            private String plateNo2;

            @XmlElement(name = "routeId")
            private String routeId;

            @XmlElement(name = "staOrder")
            private Integer staOrder;
        }
    }
}
