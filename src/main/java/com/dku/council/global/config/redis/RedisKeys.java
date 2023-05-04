package com.dku.council.global.config.redis;

public class RedisKeys {
    public static final String KEY_DELIMITER = ":";

    public static final String POST_VIEW_COUNT_SET_KEY = "postViewSet";
    public static final String POST_WRITE_KEY = "postWrite";

    public static final String LIKE_KEY = "like";
    public static final String LIKE_POSTS_KEY = "likePosts";
    public static final String LIKE_USERS_KEY = "likeUsers";
    public static final String LIKE_COUNT_KEY = "likeCount";

    public static final String BUS_ARRIVAL_KEY = "busArrival";

    public static final String TICKET_EVENTS_KEY = "ticketEvents";
    public static final String TICKET_RESERVATION_SET_KEY = "ticketReservations";
    public static final String TICKET_NEXT_KEY = "ticketNextId";
    public static final String TICKET_KEY = "ticket";

    public static final String SIGNUP_AUTH_KEY = "signupAuth";
    public static final String USER_FIND_AUTH_KEY = "userFindAuth";
    public static final String USER_INFO_CACHE_KEY = "userInfo";


    public static String combine(Object key1, Object key2) {
        return key1 + KEY_DELIMITER + key2;
    }

    public static String combine(Object key1, Object key2, Object key3) {
        return key1 + KEY_DELIMITER + key2 + KEY_DELIMITER + key3;
    }

    public static String combine(Object... keys) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.length; i++) {
            sb.append(keys[i]);
            if (i < keys.length - 1) {
                sb.append(KEY_DELIMITER);
            }
        }
        return sb.toString();
    }
}
