package architecture_customer_home.enums;

public enum TaskStatus {
    PENDING("Pendiente"),
    IN_PROGRESS("En Progreso"),
    COMPLETED("Completada"),
    CANCELLED("Cancelada");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
