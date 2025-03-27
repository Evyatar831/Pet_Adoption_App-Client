package util;


import java.util.regex.Pattern;


public class ValidationUtils {

    // Regular expression patterns
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[0-9 ()-]{7,15}$");


    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }


    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }


    public static boolean isValidPhone(String phone) {
        if (phone == null) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }


    public static boolean isValidInteger(String text) {
        if (text == null) {
            return false;
        }
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    public static boolean isValidIntegerInRange(String text, int min, int max) {
        if (!isValidInteger(text)) {
            return false;
        }

        int value = Integer.parseInt(text);
        return value >= min && value <= max;
    }


    public static boolean isValidDouble(String text) {
        if (text == null) {
            return false;
        }
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    public static boolean isValidDoubleInRange(String text, double min, double max) {
        if (!isValidDouble(text)) {
            return false;
        }

        double value = Double.parseDouble(text);
        return value >= min && value <= max;
    }
}
