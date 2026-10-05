<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="user" required="true" type="ar.edu.itba.paw.model.User" %>
<%@ attribute name="role" required="false" %>
<%@ attribute name="classname" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>


<spring:message code="profile.ratings.asSeller" var="ratingsAsSeller"/>
<spring:message code="profile.ratings.asBuyer" var="ratingsAsBuyer"/>
<spring:message code="profile.ratings.positive" var="ratingPositive"/>
<spring:message code="profile.ratings.neutral" var="ratingNeutral"/>
<spring:message code="profile.ratings.negative" var="ratingNegative"/>
<spring:message code="profile.ratings.total" var="ratingTotal"/>

<c:set var="repClass" value="${not empty classname ? classname : ''}"/>
<c:set var="repRole" value="${role == 'buyer' ? 'buyer' : 'seller'}"/>
<c:set var="ratingsTitle" value="${repRole == 'seller' ? ratingsAsSeller : ratingsAsBuyer}"/>

<c:set var="positiveRatings" value="${repRole == 'seller' ? user.sellerPositiveRatings : user.buyerPositiveRatings}"/>
<c:set var="neutralRatings" value="${repRole == 'seller' ? user.sellerNeutralRatings : user.buyerNeutralRatings}"/>
<c:set var="negativeRatings" value="${repRole == 'seller' ? user.sellerNegativeRatings : user.buyerNegativeRatings}"/>

<c:set var="totalRatings" value="${repRole == 'seller' ? user.sellerTotalRatings : user.buyerTotalRatings}"/>
<c:set var="balance" value="${repRole == 'seller' ? user.sellerRatingBalance : user.buyerRatingBalance}"/>
<c:set var="balanceFraction" value="${totalRatings == 0 ? 0 : balance / totalRatings}"/>

<div class="flex flex-col gap-2 ${repClass}">
    <div class="flex flex-row items-center gap-6">
        <h3 class="text-sm font-semibold text-black/60 uppercase tracking-wide"><c:out value="${ratingsTitle}"/></h3>
        <div class="h-3 w-full bg-gradient-to-r from-red-50 via-neutral-50 to-green-50 relative rounded-full bg-border border border-black/10">
            <div
                class="absolute top-1/2 -translate-x-1/2 -translate-y-1/2 min-w-6 h-6 bg-white border border-black/10 rounded-full text-sm z-5 grid place-items-center px-1"
                style="left: calc(5px + (100% - 10px) * ${0.5 + balanceFraction * 0.5})"
            >
                <c:choose>
                    <c:when test="${balance > 0}">
                        <span class="text-green-600 font-bold">+<c:out value="${balance}"/></span>
                        <c:set var="balanceStyle" value="clip-path: xywh(50% 0 ${balanceFraction * 50}% 100%);"/>
                    </c:when>
                    <c:when test="${balance < 0}">
                        <span class="text-red-600 font-bold"><c:out value="${balance}"/></span>
                        <c:set var="balanceStyle" value="clip-path: xywh(${50 + balanceFraction * 50}% 0 ${-balanceFraction * 50}% 100%);"/>
                    </c:when>
                    <c:otherwise>
                        <span class="text-black/60 font-bold">0</span>
                        <c:set var="balanceStyle" value="clip-path: xywh(0 0 0 0)"/>
                    </c:otherwise>
                </c:choose>
            </div>
            <div
                class="absolute inset-0 bg-gradient-to-r from-red-400 via-neutral-50 to-green-400 rounded-full"
                style="${balanceStyle}"
            ></div>
        </div>
    </div>

    <div class="flex gap-4 text-sm text-black/60">
        <span><c:out value="${ratingPositive}"/>: <span class="text-green-600"><c:out value="${positiveRatings}"/></span></span>
        <span><c:out value="${ratingNeutral}"/>: <c:out value="${neutralRatings}"/></span>
        <span><c:out value="${ratingNegative}"/>: <span class="text-red-600"><c:out value="${negativeRatings}"/></span></span>
    </div>
</div>
