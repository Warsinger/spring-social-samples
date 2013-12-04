package com.blogspot.kateel.analytics;

import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.Reference;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Analytic that finds friends of our friends that have each other in common.
 *
 * @author Matt Lee
 */
public class FriendPairAnalytic {
    /**
     * Retrieves the list of my friends that know each other
     *
     * @param facebook the facebook api interface
     * @return my friends that know each other
     */
    public Collection<FriendPair> getFriendPairs(Facebook facebook) {

        Collection<FriendPair> friendPairs = new HashSet<>();
        Collection<Reference> friends = facebook.friendOperations().getFriends();
        // loop over all the friends and collect their ids in a set
        Map<String, Reference> myFriends = new HashMap<>();
        for (Reference friend : friends) {
            myFriends.put(friend.getId(), friend);
        }

        // get all the friends for each friend and check for their existence in the initial list
        for (String friendId : myFriends.keySet()) {
            Collection<Reference> fofs = getFriends(facebook, friendId);
            for (Reference fof : fofs) {
                if (myFriends.containsKey(fof.getId())) {
                    // the friend of friend is also in my friends list so add it to the list of friend pairs
                    // since this is a set and the equals/hashCode methods are based on the IDs and not order dependent duplicate friend pairs will be eliminated
                    friendPairs.add(new FriendPair(fof, myFriends.get(friendId)));
                }
            }
        }
        return friendPairs;
    }

    private Collection<Reference> getFriends(Facebook facebook, String friendId) {
        // this doesn't work, apparently don't have permission to see friends of friends via API, need to figure out permissions but then this code should work
        final Collection<Reference> friends = facebook.friendOperations().getFriends(friendId);
        return friends;
    }
}
