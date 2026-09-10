<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="classname" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="iconName" value="${name}" />
<c:set var="iconClass" value="${not empty classname ? classname : ''}" />

<span class="${iconClass}">
    <i class="icon-${iconName}"></i>
</span>
