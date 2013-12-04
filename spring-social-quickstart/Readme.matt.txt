This file describes the changes made from the original spring-social-quickstart sample application located at https://github.com/spring-projects/spring-social-samples.
I started with the existing project that accesses Facebook and added some of my own code to it to find the pages that are liked in common by my friends.
Most of the code in this project is the same as the original with the exception of the .jsp pages and the Maven POM which were modified slightly.
The new code is in the package com.blogspot.kateel.analytics.CommonLikesAnalytic.
I originally wanted find my friends that knew each other but the API would not let me access my friends' friend lists for some reason. But I left this code in there in the file com.blogspot.kateel.analytics.FriendPairAnalytic

Matt Lee