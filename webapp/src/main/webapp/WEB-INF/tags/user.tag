<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="variant" required="false" %>
<%@ attribute name="href" required="false" %>
<%@ attribute name="user" required="true" type="ar.edu.itba.paw.model.User" %>
<%@ attribute name="classname" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>


<c:set var="userClass" value="${not empty classname ? classname : ''}"/>
<c:set var="userVariant" value="${not empty variant ? variant : 'basic'}"/>
<c:set var="avatarSize" value="${variant eq 'detailed' ? 'lg' : 'md'}"/>

<c:set var="profileUrl" value="/profile/${user.id}" />
<c:set var="hrefOrDefault" value="${not empty href ? href : profileUrl}"/>
<c:url value="${hrefOrDefault}" var="hrefUrl"/>

<paw:linkButton href="${hrefUrl}" variant="ghost" classname="w-full justify-start px-0 gap-3 ${userClass}">
    <div class="flex flex-row gap-2 items-center text-sm">
        <paw:userAvatar user="${user}" size="${avatarSize}" />
        <c:if test="${userVariant eq 'basic'}">
            <p>
                <span class="text-black font-normal"><c:out value="${user.displayName}"/></span>
                <span class="text-black/60 font-normal">(<c:out value="${user.username}"/>)</span>
            </p>
        </c:if>
        <c:if test="${userVariant eq 'detailed'}">
            <div class="flex flex-col">
                <p>
                    <span class="text-black font-medium text-base"><c:out value="${user.displayName}"/></span>
                    <span class="text-black/60 font-normal">(<c:out value="${user.username}"/>)</span>
                </p>
                <p class="text-black/60 font-normal"><c:out value="${user.email}"/></p>
            </div>
        </c:if>
    </div>
</paw:linkButton>
