<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<%-- The logged-in username is read from the session (set on login/register). --%>
<c:set var="currentUser" value="${sessionScope.username}"/>

<div class="sticky top-0 pt-3 px-6 z-50">
  <nav class="mx-auto max-w-6xl rounded-xl overflow-hidden relative z-10">
    <div class="rounded-xl w-full p-2 flex flex-row items-center gap-6 bg-white/80 backdrop-blur-sm border border-black/10">

      <a href="<c:url value='/'/>" class="px-3 py-2 rounded-lg font-bold hover:bg-black/5 transition">
        PAW
      </a>

      <div class="ml-auto flex flex-row items-center gap-3">
        <c:choose>
          <c:when test="${not empty currentUser}">
            <span class="text-sm text-black/70">Hey, <c:out value="${currentUser}"/></span>
            <c:url value="/logout" var="logoutUrl"/>
            <form action="${logoutUrl}" method="post">
              <paw:button text="Log out" type="submit" variant="outline" size="sm" role="secondary"/>
            </form>
          </c:when>
          <c:otherwise>
            <a href="<c:url value='/login'/>">
              <paw:button text="Sign in" variant="outline" size="sm"/>
            </a>
          </c:otherwise>
        </c:choose>
      </div>

    </div>
  </nav>
</div>
