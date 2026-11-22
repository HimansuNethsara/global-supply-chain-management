package lk.jiat.global.util;

public class UserContext {
    private static final ThreadLocal<Integer> currentUserId = new ThreadLocal<>();
    private static final ThreadLocal<String> currentUsername = new ThreadLocal<>();

    public static void setCurrentUser(Integer userId, String username) {
        currentUserId.set(userId);
        currentUsername.set(username);
    }

    public static Integer getCurrentUserId() {
        return currentUserId.get();
    }

    public static String getCurrentUsername() {
        return currentUsername.get();
    }

    public static void clear() {
        currentUserId.remove();
        currentUsername.remove();
    }
}