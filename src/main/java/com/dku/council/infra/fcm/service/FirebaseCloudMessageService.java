package com.dku.council.infra.fcm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;

// TODO 향후 사용시 활성화
//@Service
@RequiredArgsConstructor
public class FirebaseCloudMessageService {
    private final FirebaseMessaging firebaseMessaging;

    public void send(String token, String title, String body) throws FirebaseMessagingException {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .build();

        firebaseMessaging.send(message);
    }
}
