package Services;

public class CurrentSession {
    public enum Role { PODATNIK, PRACOWNIK }
    private static String userId;
    private static Role role;

    public static void setSession(String uid, Role r) {
        userId = uid;
        role = r;
    }
    public static String getUserId() { return userId; }
    public static Role getRole() { return role; }
    public static boolean isPodatnik() { return role == Role.PODATNIK; }
    public static boolean isPracownik() { return role == Role.PRACOWNIK; }
    public static void clear() {
        userId = null;
        role = null;
    }
}
