<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="text" required="true" %>
<%@ attribute name="variant" required="false" %>
<%@ attribute name="size" required="false" %>
<%@ attribute name="disabled" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="btnVariant" value="${not empty variant ? variant : 'primary'}"/>
<c:set var="btnSize" value="${not empty size ? size : 'md'}"/>
<c:set var="btnDisabled" value="${disabled ne null ? disabled : false}"/>

<button type="button"
        class="btn btn-${btnVariant} btn-${btnSize}"
        <c:if test="${btnDisabled}">disabled</c:if>
>
    <c:out value="${text}"/>
</button>
