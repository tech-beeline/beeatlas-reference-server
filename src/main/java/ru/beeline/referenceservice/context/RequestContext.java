package ru.beeline.referenceservice.context;

import ru.beeline.referenceservice.domain.UserEntity;

public class RequestContext {

    private static final ThreadLocal<UserEntity> currentUser = new ThreadLocal<>();

    public static void setCurrentUser(UserEntity user) {
        currentUser.set(user);
    }

    public static UserEntity getCurrentUser() {
        return currentUser.get();
    }

    public static void clear() {
        currentUser.remove();
    }
}
