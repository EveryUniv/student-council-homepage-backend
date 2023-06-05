package com.dku.council.infra.fcm.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.io.InputStream;

// TODO 향후 사용시 활성화
//@Configuration
public class FCMConfig {

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        try (InputStream keyStream = FCMConfig.class.getResourceAsStream("/serviceAccountKey.json")) {
            if (keyStream == null) {
                throw new IOException("Not found serviceAccountKey.json file");
            }
            if (!FirebaseApp.getApps().isEmpty()) {
                return FirebaseMessaging.getInstance();
            }
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(keyStream))
                    .build();
            FirebaseApp app = FirebaseApp.initializeApp(options);
            return FirebaseMessaging.getInstance(app);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
