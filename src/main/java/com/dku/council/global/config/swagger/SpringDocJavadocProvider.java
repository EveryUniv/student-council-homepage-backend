
package com.dku.council.global.config.swagger;

import com.github.therapi.runtimejavadoc.*;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.providers.JavadocProvider;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static java.lang.Math.min;
import static java.util.stream.Collectors.toMap;

@Component
public class SpringDocJavadocProvider implements JavadocProvider {

    private final CommentFormatter formatter = new CommentFormatter();

    @Override
    public String getClassJavadoc(Class<?> cl) {
        ClassJavadoc classJavadoc = RuntimeJavadoc.getJavadoc(cl);
        return formatter.format(classJavadoc.getComment());
    }

    @Override
    public String getMethodJavadocDescription(Method method) {
        MethodJavadoc methodJavadoc = RuntimeJavadoc.getJavadoc(method);
        return formatter.format(methodJavadoc.getComment());
    }

    @Override
    public String getMethodJavadocReturn(Method method) {
        MethodJavadoc methodJavadoc = RuntimeJavadoc.getJavadoc(method);
        return formatter.format(methodJavadoc.getReturns());
    }

    public Map<String, String> getMethodJavadocThrows(Method method) {
        return RuntimeJavadoc.getJavadoc(method)
                .getThrows()
                .stream()
                .collect(toMap(ThrowsJavadoc::getName, javadoc -> formatter.format(javadoc.getComment())));
    }

    @Override
    public String getParamJavadoc(Method method, String name) {
        MethodJavadoc methodJavadoc = RuntimeJavadoc.getJavadoc(method);
        List<ParamJavadoc> paramsDoc = methodJavadoc.getParams();
        return paramsDoc.stream().filter(paramJavadoc1 -> name.equals(paramJavadoc1.getName())).findAny()
                .map(paramJavadoc1 -> formatter.format(paramJavadoc1.getComment())).orElse(null);
    }

    @Override
    public String getFieldJavadoc(Field field) {
        FieldJavadoc fieldJavadoc = RuntimeJavadoc.getJavadoc(field);
        return formatter.format(fieldJavadoc.getComment());
    }

    @Override
    public String getFirstSentence(String text) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }
        int pOpenIndex = text.indexOf("<p>");
        int pCloseIndex = text.indexOf("</p>");
        int dotIndex = text.indexOf(".");
        if (pOpenIndex != -1) {
            if (pOpenIndex == 0 && pCloseIndex != -1) {
                if (dotIndex != -1) {
                    return text.substring(3, min(pCloseIndex, dotIndex));
                }
                return text.substring(3, pCloseIndex);
            }
            if (dotIndex != -1) {
                return text.substring(0, min(pOpenIndex, dotIndex));
            }
            return text.substring(0, pOpenIndex);
        }
        if (dotIndex != -1
                && text.length() != dotIndex + 1
                && Character.isWhitespace(text.charAt(dotIndex + 1))) {
            return text.substring(0, dotIndex + 1);
        }
        return text;
    }
}
