<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="text" required="true" %>
<%@ attribute name="variant" required="false" %>
<%@ attribute name="disabled" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="btnVariant" value="${not empty variant ? variant : 'primary'}"/>
<c:set var="btnDisabled" value="${disabled ne null ? disabled : false}"/>

<c:set var="cls" value="px-4 py-2 rounded text-sm font-medium cursor-pointer bg-green-600 hover:bg-green-700 text-white"/>
<c:if test="${btnVariant == 'secondary'}">
    <c:set var="cls" value="px-4 py-2 rounded text-sm font-medium cursor-pointer bg-black/5 hover:bg-black/10 text-black/70 border border-black/15"/>
</c:if>
<c:if test="${btnVariant == 'danger'}">
    <c:set var="cls" value="px-4 py-2 rounded text-sm font-medium cursor-pointer bg-red-600 hover:bg-red-700 text-white"/>
</c:if>

<button type="button" class="${cls}" <c:if test="${btnDisabled}">disabled</c:if>>
    <c:out value="${text}"/>
</button>
