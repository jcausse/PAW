<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="label" required="false" %>
<%@ attribute name="disabled" required="false" type="java.lang.Boolean" %>
<%@ attribute name="classname" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:set var="inputClass" value="${not empty classname ? classname : ''}"/>
<c:set var="isDisabled" value="${disabled ne null ? disabled : false}"/>

<div class="flex flex-col gap-1 ${inputClass}">
  <label for="${path}" class="flex items-center gap-2 text-sm text-black/80 cursor-pointer">
    <form:checkbox
      path="${path}"
      id="${path}"
      disabled="${isDisabled}"
      cssClass="w-4 h-4 rounded border-black/20 accent-sky-600 outline-sky-600/30"
    />
    <c:if test="${not empty label}">
      <span>${label}</span>
    </c:if>
  </label>
  <form:errors path="${path}" element="div" cssClass="text-xs text-red-600"/>
</div>
