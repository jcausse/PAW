<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="variant" required="false" %>
<%@ attribute name="href" required="false" %>
<%@ attribute name="user" required="true" type="ar.edu.itba.paw.model.User" %>
<%@ attribute name="classname" required="false" %>
<%@ attribute name="showSellerRating" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>


<c:set var="userClass" value="${not empty classname ? classname : ''}"/>
<c:set var="userVariant" value="${not empty variant ? variant : 'basic'}"/>
<c:set var="avatarSize" value="${variant eq 'detailed' ? 'lg' : variant eq 'compact' ? 'sm' : 'md'}" />
<c:set var="buttonSize" value="${variant eq 'compact' ? 'sm' : 'md'}" />

<c:set var="profileUrl" value="/profile/${user.id}" />
<c:set var="hrefOrDefault" value="${not empty href ? href : profileUrl}"/>
<c:url value="${hrefOrDefault}" var="hrefUrl"/>

<paw:linkButton href="${hrefUrl}" variant="ghost" size="${buttonSize}" classname="w-full justify-start ${userClass}">
    <div class="flex flex-row gap-2 items-center text-sm">
        <paw:userAvatar user="${user}" size="${avatarSize}" />
        <c:if test="${userVariant eq 'compact'}">
            <div class="text-xs">
                <p class="text-black font-normal"><c:out value="${user.displayName}"/></p>
                <p class="text-black/60 font-normal"><c:out value="${user.username}"/></p>
            </div>
        </c:if>
        <c:if test="${userVariant eq 'basic'}">
            <div class="flex flex-col">
                <p>
                    <span class="text-black font-normal"><c:out value="${user.displayName}"/></span>
                    <span class="text-black/60 font-normal">(<c:out value="${user.username}"/>)</span>
                </p>
                <c:if test="${showSellerRating}">
                    <c:set var="sellerBalance" value="${user.sellerRatingBalance}"/>
                    <spring:message code="listing.detail.sellerReviewsCount" arguments="${user.sellerTotalRatings}" var="sellerReviewsMsg"/>
                    <p class="text-xs text-black/60">
                        <spring:message code="listing.detail.sellerRating"/>
                        <c:choose>
                            <c:when test="${sellerBalance > 0}">
                                <span class="text-green-600">+<c:out value="${sellerBalance}"/></span>
                            </c:when>
                            <c:when test="${sellerBalance < 0}">
                                <span class="text-red-600"><c:out value="${sellerBalance}"/></span>
                            </c:when>
                            <c:otherwise>
                                <span class="text-neutral-500">0</span>
                            </c:otherwise>
                        </c:choose>
                        (<c:out value="${sellerReviewsMsg}"/>)
                    </p>
                </c:if>
            </div>
        </c:if>
        <c:if test="${userVariant eq 'detailed'}">
            <div class="flex flex-col">
                <p>
                    <span class="text-black font-medium"><c:out value="${user.displayName}"/></span>
                    <span class="text-black/60 font-normal">(<c:out value="${user.username}"/>)</span>
                </p>
                <p class="text-black/60 font-normal"><c:out value="${user.email}"/></p>
            </div>
        </c:if>
    </div>
</paw:linkButton>
