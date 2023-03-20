package com.dku.council.global.util;

import javax.servlet.http.HttpServletRequest;

public class RemoteAddressUtil {
    public static String getProxyableAddr(HttpServletRequest request) {
        String originalAddress = request.getHeader("X-Forwarded-For");
        if (originalAddress != null && !originalAddress.isBlank() && !originalAddress.equalsIgnoreCase("unknown")) {
            return originalAddress.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}
