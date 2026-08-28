<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="title" required="false" %>
<%@ attribute name="subtitle" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="max-w-sm rounded-lg border border-black/10 bg-border bg-gradient-to-b from-neutral-50 to-white to-40% p-4 flex flex-col gap-1">
  <c:if test="${not empty title}">
    <h3 class="text-base font-semibold"><c:out value="${title}"/></h3>
  </c:if>
  <c:if test="${not empty subtitle}">
    <p class="text-sm text-black/60 mb-2"><c:out value="${subtitle}"/></p>
  </c:if>
  <jsp:doBody/>
</div>
