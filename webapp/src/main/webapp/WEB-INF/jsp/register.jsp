<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>Create your account</title>
    <link rel="stylesheet" href="<c:url value="/css/tailwind.css"/>"/>
</head>
<body class="min-h-screen flex flex-col items-center justify-center bg-neutral-50 p-4">
    <h2 class="text-3xl font-bold mb-4">Create your account</h2>

    <div class="w-96 bg-white border border-black/10 rounded-2xl p-6">
        <c:url value="/register" var="registerUrl"/>
        <form:form modelAttribute="userForm" action="${registerUrl}" method="post" cssClass="flex flex-col gap-4">
            <div class="flex flex-col gap-1">
                <label for="username" class="text-xs text-black/70 font-medium">Username</label>
                <form:input path="username" id="username"
                            cssClass="px-2 py-1 rounded-lg text-sm outline-0 transition duration-150 outline-sky-600/30 placeholder:text-black/40 border border-black/20 focus-visible:border-sky-600 focus-visible:outline-2"/>
                <form:errors path="username" element="div" cssClass="text-xs text-red-600"/>
            </div>
            <div class="flex flex-col gap-1">
                <label for="firstName" class="text-xs text-black/70 font-medium">First name</label>
                <form:input path="firstName" id="firstName"
                            cssClass="px-2 py-1 rounded-lg text-sm outline-0 transition duration-150 outline-sky-600/30 placeholder:text-black/40 border border-black/20 focus-visible:border-sky-600 focus-visible:outline-2"/>
                <form:errors path="firstName" element="div" cssClass="text-xs text-red-600"/>
            </div>
            <div class="flex flex-col gap-1">
                <label for="lastName" class="text-xs text-black/70 font-medium">Last name</label>
                <form:input path="lastName" id="lastName"
                            cssClass="px-2 py-1 rounded-lg text-sm outline-0 transition duration-150 outline-sky-600/30 placeholder:text-black/40 border border-black/20 focus-visible:border-sky-600 focus-visible:outline-2"/>
                <form:errors path="lastName" element="div" cssClass="text-xs text-red-600"/>
            </div>
            <div class="flex flex-col gap-1">
                <label for="email" class="text-xs text-black/70 font-medium">Email</label>
                <form:input path="email" id="email" type="email"
                            cssClass="px-2 py-1 rounded-lg text-sm outline-0 transition duration-150 outline-sky-600/30 placeholder:text-black/40 border border-black/20 focus-visible:border-sky-600 focus-visible:outline-2"/>
                <form:errors path="email" element="div" cssClass="text-xs text-red-600"/>
            </div>
            <input type="submit" value="Sign up"
                   class="mt-2 py-2 px-4 text-sm font-semibold rounded-lg cursor-pointer transition duration-150 text-sky-600 border-t border-b border-t-white/30 border-b-black/20 bg-gradient-to-b from-current/3 to-current/7 hover:from-current/5 hover:to-current/10"/>
        </form:form>
    </div>

    <a href="<c:url value="/login"/>" class="text-sm underline mt-4">Already have an account? Log in</a>
</body>
</html>
