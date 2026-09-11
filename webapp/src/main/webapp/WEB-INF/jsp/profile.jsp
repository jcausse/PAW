<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html lang="${pageContext.response.locale.language}">
<paw:head title="${user.displayName}"/>
<body class="min-h-screen bg-neutral-50 flex flex-col">
    <paw:navbar/>
    <main class="max-w-6xl w-full mx-auto px-6 pt-8 pb-16">
        <paw:card classname="w-full flex flex-col gap-8">
            <div class="flex items-center gap-6">
                <paw:userAvatar user="${user}" size="xl" />
                <div class="flex flex-col">
                    <h1 class="text-3xl font-bold tracking-tight text-neutral-900"><c:out value="${user.displayName}"/></h1>
                    <span class="text-lg text-neutral-500 font-medium">@<c:out value="${user.username}"/></span>
                    <c:if test="${allowEdit}">
                        <div class="mt-6">
                            <c:url value="/profile/edit" var="editUrl"/>
                            <spring:message code="profile.edit" var="editLabel"/>
                            <paw:linkButton href="${editUrl}" text="${editLabel}" size="sm" variant="outline"/>
                        </div>
                    </c:if>
                </div>
            </div>

            <div class="flex flex-col gap-4">
                <div class="flex items-center justify-between bg-neutral-50 p-4 rounded-xl border border-black/5">
                    <div class="flex flex-col">
                        <span class="text-xs font-semibold text-neutral-500 uppercase tracking-wider mb-1"><spring:message code="profile.email"/></span>
                        <span class="text-neutral-800 font-medium"><c:out value="${user.email}"/></span>
                    </div>
                    <spring:message code="profile.sendEmail" var="sendEmailLabel"/>
                    <paw:linkButton href="mailto:${user.email}" text="${sendEmailLabel}" size="sm" variant="outline"/>
                </div>
            </div>
        </paw:card>
    </main>
</body>
</html>
