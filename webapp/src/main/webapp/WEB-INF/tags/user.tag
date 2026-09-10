<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="avatarSize" required="false" %>
<%@ attribute name="user" required="true" type="ar.edu.itba.paw.model.User" %>
<%@ attribute name="classname" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<c:set var="userClass" value="${not empty classname ? classname : ''}"/>


<c:url value="/profile/${user.id}" var="profileUrl"/>
<paw:linkButton href="${profileUrl}" variant="ghost" classname="w-full justify-start px-0 gap-3 ${userClass}">
    <div class="flex flex-row gap-2 items-center text-sm">
        <paw:userAvatar user="${user}" size="${avatarSize}" />
        <p>
            <span class="text-black font-normal"><c:out value="${user.displayName}"/></span>
            <span class="text-black/60 font-normal">(<c:out value="${user.username}"/>)</span>
        </p>
    </div>
</paw:linkButton>
