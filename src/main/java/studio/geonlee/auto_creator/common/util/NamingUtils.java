package studio.geonlee.auto_creator.common.util;

import studio.geonlee.auto_creator.config.setting.GlobalConfig;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NamingUtils {

    public static String convertFullNaming(String originalName) {
        List<String> tokens = splitCamelCase(originalName);
        StringBuilder result = new StringBuilder();

        for (String token : tokens) {
            String lowerToken = token.toLowerCase();
            String replacement = GlobalConfig.dictionary.getOrDefault(lowerToken, token);
            result.append(capitalize(replacement));
        }

        // 첫 글자는 원래 입력처럼 유지 (필요 시 소문자화)
        if (!result.isEmpty()) {
            result.setCharAt(0, originalName.charAt(0));
        }

        return result.toString();
    }

    // 카멜케이스 분리 함수
    private static List<String> splitCamelCase(String str) {
        return Arrays.stream(str.split("(?=[A-Z])"))
                .collect(Collectors.toList());
    }

    // 첫 글자 대문자
    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}
