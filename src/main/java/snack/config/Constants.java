package snack.config;

public class Constants {
    // Regex for acceptable usernames
    public static final int USERNAME_MIN_LENGTH = 4;
    public static final int USERNAME_MAX_LENGTH = 20;
    public static final String USERNAME_REGEX = "^[A-Za-z_-]{4,20}$";
    public static final int EMAIL_MAX_LENGTH = 50;
    public static final int PASSWORD_HASH_LENGTH = 32; // BCrypt
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 30;
    public static final int CHANNEL_NAME_MIN_LENGTH = 4;
    public static final int CHANNEL_NAME_MAX_LENGTH = 20;
    public static final int NAME_MAX_LENGTH = 20;
    public static final int TEXT_MAX_LENGTH = 255; // TINYTEXT
    public static final int STATUS_MAX_LENGTH = 20; // TEXT
    public static final int AUTHORITY_NAME_MAX_LENGTH = 10;
    public static final String SYSTEM = "system";
    public static final String DEFAULT_LANGUAGE = "en";

    public static final String ACCESS_TOKEN = "access_token";

    private Constants() {
    }
}
