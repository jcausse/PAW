<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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
          <c:url value="/listing/new" var="newListingUrl"/>
          <form:form modelAttribute="listingForm" action="${newListingUrl}" method="post">
            <paw:formInput path="title" label="Listing title" />
            <paw:formInput path="creatorId" type="number" label="Creator ID" />
            <paw:formInput path="productId" type="number" label="Product ID" />
            <paw:formInput path="price" type="number" label="Price" />

            <paw:button text="Submit" type="submit" />
          </form:form>
        </div>
        <div class="flex-1 bg-white min-w-sm">
          <h1>Product name</h1>
        </div>
      </div>
    </div>
</body>
</html>
