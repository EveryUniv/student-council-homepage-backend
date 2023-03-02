package com.dku.council.global.util;

import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class TextTemplateEngine {

    private static final String ALLOWED_KEY_CHARACTERS = "a-zA-Z_";
    private static final Pattern TEMPLATE_VARIABLE = Pattern.compile("@\\{([" + ALLOWED_KEY_CHARACTERS + "]+)}");
    private final HashMap<String, String> arguments;

    private TextTemplateEngine(HashMap<String, String> arguments) {
        this.arguments = arguments;
    }

    private String replaceToArgumentValue(String input) {
        return TEMPLATE_VARIABLE.matcher(input).replaceAll(mr -> arguments.get(mr.group(1)));
    }

    public String readHtmlFromResource(String htmlResourcePath) {
        ClassPathResource resource = new ClassPathResource(htmlResourcePath);

        List<String> lines = new ArrayList<>();
        try {
            InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            BufferedReader bReader = new BufferedReader(reader);
            String buffer;
            while ((buffer = bReader.readLine()) != null) {
                lines.add(buffer);
            }
        } catch (IOException e) {
            throw new RuntimeException("리소스 파일을 읽을 수 없습니다: " + htmlResourcePath);
        }

        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(replaceToArgumentValue(line));
            sb.append('\n');
        }

        return sb.toString();
    }

    // builder
    public static class Builder {

        // replaceAll과정은 regex를 이용하므로 아래의 키만 허용하도록 한다.
        private static final Pattern NOT_ALLOWED_KEY_CHARACTERS = Pattern.compile("[^" + ALLOWED_KEY_CHARACTERS + "]");
        private final HashMap<String, String> arguments = new HashMap<>();

        public Builder argument(String key, String value) {
            if (NOT_ALLOWED_KEY_CHARACTERS.matcher(key).matches()) {
                throw new RuntimeException("Key에 사용할 수 없는 문자가 포함되어있습니다.");
            }
            arguments.put(key, value);
            return this;
        }

        public TextTemplateEngine build() {
            return new TextTemplateEngine(arguments);
        }
    }
}