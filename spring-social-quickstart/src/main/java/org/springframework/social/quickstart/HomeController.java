/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.quickstart;

import com.blogspot.kateel.analytics.CommonLikesAnalytic;
import com.blogspot.kateel.analytics.FriendPair;
import com.blogspot.kateel.analytics.FriendPairAnalytic;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;

/**
 * Simple little @Controller that invokes Facebook and renders the result. The injected {@link org.springframework.social.facebook.api.Facebook} reference is configured with the required authorization credentials for the current user behind
 * the scenes.
 *
 * @author original Keith Donald, modified by Matt Lee
 */
@Controller
public class HomeController {

    private final Facebook facebook;
    private final FriendPairAnalytic friendPairAnalytic = new FriendPairAnalytic();
    private final CommonLikesAnalytic commonLikesAnalytic = new CommonLikesAnalytic();

    @Inject
    public HomeController(Facebook facebook) {
        this.facebook = facebook;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Model model) {
        model.addAttribute("commonLikes", getCommonLikes());
        model.addAttribute("mostLiked", this.commonLikesAnalytic.getMostLiked());
        model.addAttribute("likesMost", this.commonLikesAnalytic.getLikesMost());
        model.addAttribute("likesMostCount", this.commonLikesAnalytic.getLikesMostCount());
        model.addAttribute("mostLikedCount", this.commonLikesAnalytic.getMostLikedCount());
        return "home";
    }

    private Collection<FriendPair> getFriendPairs() {
        return this.friendPairAnalytic.getFriendPairs(this.facebook);
    }

    private Map<String, Collection<String>> getCommonLikes() {
        return this.commonLikesAnalytic.getCommonLikes(this.facebook);
    }
}
