<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title><spring:message code="listing.new.title"/></title>
    <link rel="stylesheet" href="<c:url value="/css/tailwind.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/input.css"/>"/>
</head>
<body class="p-8 pb-24 bg-neutral-50">
    <div class="max-w-5xl mx-auto">
      <div class="flex flex-row gap-4">
        <div class="flex-2 bg-white">
          <form action="/listing" method="post">
            <%-- TODO add error handling --%>
            <spring:message code="listing.new.titleLabel" var="titleLabel"/>
            <paw:input id="titleInput" name="title" label="${titleLabel}" />
            <spring:message code="listing.new.creatorId" var="creatorIdLabel"/>
            <paw:input type="number" id="creatorIdInput" name="creatorId" label="${creatorIdLabel}" />
            <spring:message code="listing.new.productId" var="productIdLabel"/>
            <paw:input type="number" id="productIdInput" name="productId" label="${productIdLabel}" />
            <spring:message code="listing.new.price" var="priceLabel"/>
            <paw:input type="number" id="priceInput" name="price" label="${priceLabel}" />

            <spring:message code="listing.new.submit" var="submitLabel"/>
            <paw:button text="${submitLabel}" />
          </form>
        </div>
        <div class="flex-1 bg-white min-w-sm">
          <h1><spring:message code="listing.new.productName"/></h1>
        </div>
      </div>
    </div>
</body>
</html>
