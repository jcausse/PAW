<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<html>
<head>
    <title>Tech Marketplace</title>
    <link rel="stylesheet" href="<c:url value="/css/button.css"/>"/>
</head>
<body>
    <h1>PAW TPE 0</h1>
    <h2>Button Component</h2>

    <h3>Variants</h3>
    <paw:button text="Buy" variant="primary"/>
    <paw:button text="Cancel" variant="secondary"/>
    <paw:button text="Delete" variant="danger"/>

    <h3>Sizes</h3>
    <paw:button text="Small" variant="primary" size="sm"/>
    <paw:button text="Medium" variant="primary" size="md"/>
    <paw:button text="Large" variant="primary" size="lg"/>

    <h3>Disabled</h3>
    <paw:button text="Buy" variant="primary" disabled="${true}"/>
    <paw:button text="Cancel" variant="secondary" disabled="${true}"/>
    <paw:button text="Delete" variant="danger" disabled="${true}"/>
</body>
</html>
