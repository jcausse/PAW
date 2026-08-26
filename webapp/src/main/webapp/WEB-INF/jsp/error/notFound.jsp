<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>
        <c:choose>
            <c:when test="${not empty title}">
                <c:out value="${title}"/>
            </c:when>
            <c:otherwise>
                Not Found
            </c:otherwise>
        </c:choose>
    </title>
    <link rel="stylesheet" href="<c:url value="/css/tailwind.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/input.css"/>"/>
</head>
<body class="min-h-screen bg-neutral-50 flex items-center justify-center p-6">
    <div class="max-w-md w-full text-center flex flex-col items-center gap-6 p-8 rounded-2xl border border-black/15 bg-white shadow-sm">
        <div class="flex flex-col items-center gap-2">
            <span class="px-3 py-1 text-xs font-bold tracking-wider uppercase rounded-full bg-red-100 text-red-700">
                404 Not Found
            </span>
            <h1 class="text-3xl font-extrabold text-neutral-900 mt-2">
                <c:choose>
                    <c:when test="${not empty title}">
                        <c:out value="${title}"/>
                    </c:when>
                    <c:otherwise>
                        Page Not Found
                    </c:otherwise>
                </c:choose>
            </h1>
        </div>

        <p class="text-neutral-600 text-base">
            <c:choose>
                <c:when test="${not empty message}">
                    <c:out value="${message}"/>
                </c:when>
                <c:otherwise>
                    The resource you are looking for could not be found or does not exist.
                </c:otherwise>
            </c:choose>
        </p>

        <a href="<c:url value="/"/>"
           class="inline-flex items-center justify-center font-semibold rounded-lg p-2.5 px-5 text-sm text-white bg-sky-600 hover:bg-sky-700 transition duration-150 shadow-sm focus-visible:outline outline-offset-2 outline-sky-600">
            Return to Home
        </a>
    </div>
</body>
</html>
