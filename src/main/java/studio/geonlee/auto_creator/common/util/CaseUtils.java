package studio.geonlee.auto_creator.common.util;

/**
 * @author GEON
 * @since 2025-04-28
 **/
public class CaseUtils {
    public static String toUppercaseFirstLetter(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String toCamelCase(String name) {
        if (name == null || name.isEmpty()) return name;
        String[] parts = name.toLowerCase().split("_");
        StringBuilder sb = new StringBuilder(parts[0]);

        for (int i = 1; i < parts.length; i++) {
            sb.append(Character.toUpperCase(parts[i].charAt(0)));
            sb.append(parts[i].substring(1));
        }

        return sb.toString();
    }

    public static String toPascalCase(String name) {
        if (name == null || name.isEmpty()) return "";
        String[] parts = name.toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(Character.toUpperCase(part.charAt(0)))
                    .append(part.substring(1));
        }
        return sb.toString();
    }
}
