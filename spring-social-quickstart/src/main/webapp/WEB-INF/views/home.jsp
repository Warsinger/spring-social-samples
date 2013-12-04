<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
    <title>Home</title>
</head>
<body>
<ul>
    <li><a href="<c:url value="/signout" />">Sign Out</a></li>
</ul>
<h3>Common Likes of your Friends</h3>
Most Liked Page: <c:out value="${mostLiked}"/> (<c:out value="${mostLikedCount}"/> likes)<br/>
Friend who likes the most pages: <img src="http://graph.facebook.com/<c:out value="${likesMost}"/>/picture" align="middle"/> (<c:out value="${likesMostCount}"/> likes)
<ul>
    <c:forEach items="${commonLikes}" var="likedPage">
        <li>
            <c:forEach items="${likedPage.value}" var="userId">
                <img src="http://graph.facebook.com/<c:out value="${userId}"/>/picture" align="middle"/>
            </c:forEach>
            <c:out value="${likedPage.key}"/> (<c:out value="${fn:length(likedPage.value)}"/> likes)
        </li>
    </c:forEach>
</ul>
</body>
</html>