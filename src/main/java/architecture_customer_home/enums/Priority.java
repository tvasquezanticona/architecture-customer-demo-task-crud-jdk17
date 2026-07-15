package architecture_customer_home.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Priority {
    LOW(1, "Baja"),
    MEDIUM(2, "Media"),
    HIGH(3, "Alta");

    private final int level;
    private final String displayName;

    Priority(int level, String displayName) {
        this.level = level;
        this.displayName = displayName;
    }

    @JsonCreator
    public static Priority fromString(String value) {
        return Priority.valueOf(value);
    }

    public int getLevel() {
        return level;
    }

    public String getDisplayName() {
        return displayName;
    }
}
