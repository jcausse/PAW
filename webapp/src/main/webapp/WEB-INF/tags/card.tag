<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="subtitle" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="max-w-sm rounded-lg border-t border-b border-t-white/60 border-b-black/20 [background-position:-1px_-1px] [background-size:calc(100%+2px)_calc(100%+2px)] bg-gradient-to-b from-neutral-50 to-white to-40% shadow-md shadow-black/5 p-4 flex flex-col gap-1">
    <h3 class="text-base font-semibold"><c:out value="${title}"/></h3>
    <c:if test="${not empty subtitle}">
        <p class="text-sm text-black/50"><c:out value="${subtitle}"/></p>
    </c:if>
    <div class="mt-2">
        <jsp:doBody/>
    </div>
</div>
