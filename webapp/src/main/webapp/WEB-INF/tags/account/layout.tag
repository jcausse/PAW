<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="subtitle" required="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="account" tagdir="/WEB-INF/tags/account" %>

<body class="min-h-screen bg-neutral-50">
    <paw:navbar/>

    <main class="max-w-6xl mx-auto px-6 py-8">
        <div class="mb-8">
            <h1 class="text-3xl font-bold"><c:out value="${title}"/></h1>
            <p class="text-black/60 mt-2"><c:out value="${subtitle}"/></p>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div class="md:col-span-1">
                <account:sidebar user="${user}" />
            </div>

            <div class="md:col-span-2">
                <jsp:doBody />
            </div>
        </div>
    </main>
</body>
</html>
