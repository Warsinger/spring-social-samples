package com.blogspot.kateel.analytics;

import org.springframework.social.facebook.api.Reference;

import java.io.Serializable;

/**
 * Data object for a pair of friends that know each other.
 * <p/>
 * {@code equals} and {@code hashCode} are implemented to use the friends IDs for comparisons
 *
 * @author Matt Lee
 */
public class FriendPair implements Serializable {
    private final Reference friend1;
    private final Reference friend2;

    public FriendPair(Reference friend1, Reference friend2) {
        this.friend1 = friend1;
        this.friend2 = friend2;
    }

    public Reference getFriend1() {
        return friend1;
    }

    public Reference getFriend2() {
        return friend2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FriendPair that = (FriendPair) o;

        if (!friend1.getId().equals(that.friend1.getId())) return false;
        if (!friend2.getId().equals(that.friend2.getId())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        // hashCode needs to be independent of the order
        return (friend1.getId().hashCode() + friend2.getId().hashCode()) * 31;
    }
}
