package com.blogspot.kateel.analytics;

import org.apache.log4j.Logger;
import org.springframework.social.facebook.api.*;

import java.util.*;

/**
 * Finds common likes between me and friends, stores a map of likes to lists of friends who like that also. Additionally find things my friends like in common even if I don't like them
 *
 * @author Matt Lee
 */
public class CommonLikesAnalytic {
    final Logger logger = Logger.getLogger(getClass());
    private String mostLiked;
    private int mostLikedCount;
    private String likesMost;
    private int likesMostCount;

    /**
     * The page that is liked by the most friends
     *
     * @return The page that is liked by the most friends
     */
    public String getMostLiked() {
        return mostLiked;
    }

    /**
     * The friend that likes the most things
     *
     * @return The friend that likes the most things
     */
    public String getLikesMost() {
        return likesMost;
    }

    /**
     * The number of friends that like the most liked page
     *
     * @return The number of friends that like the most liked page
     */
    public int getMostLikedCount() {
        return mostLikedCount;
    }

    /**
     * The number of pages the friend who likes the most things likes
     *
     * @return The number of pages the friend who likes the most things likes
     */
    public int getLikesMostCount() {
        return likesMostCount;
    }

    /**
     * Returns a map of likes to a collection of users that like that thing. These are all my friends, and I may or may not be in the set. The like is the name of the like not the ID.
     *
     * @param facebook the facebook api interface
     * @return a map of likes to a collection of users that like that thing
     */
    public Map<String, Collection<String>> getCommonLikes(Facebook facebook) {
        Map<String, Collection<String>> likes = new HashMap<>();
        // add my likes and my id to the list

        // get my user profile
        final FacebookProfile profile = facebook.userOperations().getUserProfile();

        final PagedList<Page> pagesLiked = facebook.likeOperations().getPagesLiked();
        for (Page page : pagesLiked) {
            addLike(likes, page.getName(), profile.getId());
            if (logger.isDebugEnabled()) logger.debug("You like " + page.getName());
        }

        // i currently like the most things
        this.likesMost = profile.getId();
        this.likesMostCount = likes.size();
        // i like things only once
        this.mostLikedCount = 1;

        // for all my friends get their likes
        Collection<Reference> friends = facebook.friendOperations().getFriends();
        for (Reference friend : friends) {
            try {
                final PagedList<Page> friendPagesLiked = facebook.likeOperations().getPagesLiked(friend.getId());
                // test the max number of likes for users
                final int pagesLikedCount = friendPagesLiked.size();
                if (pagesLikedCount > this.likesMostCount) {
                    this.likesMost = friend.getId();
                    this.likesMostCount = pagesLikedCount;
                }
                // loop over each page the friend likes and add it to the list
                for (Page page : friendPagesLiked) {
                    addLike(likes, page.getName(), friend.getId());
                    // test the number of likes for this page
                    final int likedCount = likes.get(page.getName()).size();
                    if (likedCount > this.mostLikedCount) {
                        this.mostLiked = page.getName();
                        this.mostLikedCount = likedCount;
                    }
                    if (logger.isDebugEnabled()) logger.debug(friend.getName() + " likes " + page.getName());
                }
            } catch (Exception e) {
                // this may be due to some sort of timeout or too many requests issue rather than bad user data
                logger.error("Error getting page likes for friend id:" + friend.getId() + "; name: " + friend.getName());
            }
        }

        // remove pages that only a single person likes
        cullSingles(likes);
        // sort by number of people liking something
        likes = sortLikes(likes);
        return likes;
    }

    /**
     * Sort the likes map by putting the entries in a list and sorting that using a custom comparator, then put them back into a linked hash map in sorted order. Could possibly also use a tree map and
     * just give it the comparator or even use a tree map from the start.
     */
    private Map<String, Collection<String>> sortLikes(Map<String, Collection<String>> likes) {
        // get sort the set of entries then put them back in a LinkedHasMap to preserve iteration order
        final List<Map.Entry<String, Collection<String>>> entries = new ArrayList<>(likes.entrySet());
        Collections.sort(entries, new LikeComparator(likes));
        Map<String, Collection<String>> sortedLikes = new LinkedHashMap<>();
        for (Map.Entry<String, Collection<String>> entry : entries) {
            sortedLikes.put(entry.getKey(), entry.getValue());
        }
        return sortedLikes;
    }

    /*
     * Remove single user entries from the likes.
     */
    private void cullSingles(Map<String, Collection<String>> likes) {
        // use an iterator to traverse the map so we can call remove to cull the list
        Iterator<Map.Entry<String, Collection<String>>> iter = likes.entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry<String, Collection<String>> entry = iter.next();
            // if only one person liked the page then remove that entry from the map
            if (entry.getValue().size() == 1) {
                iter.remove();
            }
        }
    }

    /*
     * track that the user id likes a page.
     */
    private void addLike(Map<String, Collection<String>> likes, String pageName, String userId) {
        Collection<String> userIds = likes.get(pageName);
        // no entry for page yet so start a new set and put it in the map
        if (userIds == null) {
            userIds = new HashSet<>();
            likes.put(pageName, userIds);
        }
        userIds.add(userId);
    }

    /**
     * Comparator that sorts the list of entries by the size of the collection in the value in descending order.
     */
    private class LikeComparator implements Comparator<Map.Entry<String, Collection<String>>> {
        private final Map<String, Collection<String>> likes;

        public LikeComparator(Map<String, Collection<String>> likes) {
            this.likes = likes;
        }

        @Override
        public int compare(Map.Entry<String, Collection<String>> o1, Map.Entry<String, Collection<String>> o2) {
            // get the size of each collection and sort in descending order
            return o2.getValue().size() - o1.getValue().size();
        }
    }
}
