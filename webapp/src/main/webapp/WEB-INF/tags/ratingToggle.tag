<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="items" required="true" type="java.lang.Object" %>
<%@ attribute name="selectedOption" required="true" %>
<%@ attribute name="classname" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<c:set var="inputClass" value="${not empty classname ? classname : ''}"/>

<div class="flex flex-row w-full gap-2 ${inputClass}" id="${path}_base">
    <c:forEach var="option" items="${items}">
        <form:radiobutton path="${path}" id="${path}_${option.getKey()}" value="${option.getKey()}" cssClass="hidden" />
        <label for="${path}_${option.getKey()}" class="grow flex flex-col items-center gap-2 cursor-pointer p-4 border-2 rounded-lg transition hover:bg-black/5 ${option.getKey() == selectedOption ? 'border-lime-500 bg-lime-50' : 'border-black/10 hover:border-black/10'}" >
            <c:choose>
                <c:when test="${option.getKey() == 'POSITIVE'}">
                    <paw:icon name="arrow-up" classname="text-3xl text-lime-600" />
                </c:when>
                <c:when test="${option.getKey() == 'NEUTRAL'}">
                    <paw:icon name="minus" classname="text-3xl text-stone-500" />
                </c:when>
                <c:otherwise>
                    <paw:icon name="arrow-down" classname="text-3xl text-red-600" />
                </c:otherwise>
            </c:choose>
            <span class="text-sm font-medium ${option.getKey() == 'POSITIVE' ? 'text-lime-600' : (option.getKey() == 'NEUTRAL' ? 'text-black/60' : 'text-red-600')}"><c:out value="${option.getValue()}"/></span>
        </label>
    </c:forEach>
</div>