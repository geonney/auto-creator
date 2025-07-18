package studio.geonlee.auto_creator.config.message;

import studio.geonlee.auto_creator.config.setting.GlobalConfig;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author GEON
 * @since 2025-04-27
 **/
public class MessageUtil {
    private static ResourceBundle bundle;

    static {
        String language = GlobalConfig.defaultConfig.getLanguage();
        loadBundle(language);
    }

    public static void loadBundle(String language) {
        Locale locale = switch (language.toLowerCase()) {
            case "korean", "ko" -> Locale.KOREAN;
            default -> Locale.ENGLISH;
        };
        // ✅ basename 수정: message/messages
        bundle = ResourceBundle.getBundle("message.messages", locale);
    }

    public static String get(String key) {
        if (bundle == null) {
            loadBundle("en");
        }
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return key; // 키 못찾으면 키 자체를 반환
        }
    }
}
