<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Profile</title>
    <link rel="stylesheet" href="<c:url value="/css/tailwind.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/input.css"/>"/>
</head>
<body class="p-8 pb-24 bg-neutral-50">
    <div class="max-w-2xl mx-auto">
      <h1 class="text-4xl font-bold mb-4">Hello, <c:out value="${user.id}"/></h1>
      <p>Username: --------- <c:out value="${user.username}"/></p
      <p>Email: ------------ <c:out value="${user.email}"/></p
      <p>First Name: ------- <c:out value="${user.firstName}"/></p
      <p>Last Name: -------- <c:out value="${user.lastName}"/></p
    </div>
</body>
</html>
