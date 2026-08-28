<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Listing</title>
    <link rel="stylesheet" href="<c:url value="/css/tailwind.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/input.css"/>"/>
</head>
<body class="p-8 pb-24 bg-neutral-50">
    <div class="max-w-5xl mx-auto">
      <div class="flex flex-row gap-4">
        <div class="flex-2 bg-white">
          <form action="/listing" method="post">
            <%-- TODO add error handling --%>
            <paw:input id="titleInput" name="title" label="Listing title" />
            <paw:input type="number" id="creatorIdInput" name="creatorId" label="Creator ID" />
            <paw:input type="number" id="productIdInput" name="productId" label="Product ID" />
            <paw:input type="number" id="priceInput" name="price" label="Price" />

            <paw:button text="Submit" />
          </form>
        </div>
        <div class="flex-1 bg-white min-w-sm">
          <h1>Product name</h1>
        </div>
      </div>
    </div>
</body>
</html>
