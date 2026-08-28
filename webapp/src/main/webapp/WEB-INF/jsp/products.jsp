<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Tonkatsu - Productos</title>
    <link rel="stylesheet" href="<c:url value="/css/tailwind.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/input.css"/>"/>
</head>

<body class="bg-neutral-50 pb-24">

<paw:navbar/>

<main class="flex flex-col gap-10 px-8 py-8">

    <section class="flex flex-col gap-4">
        <h2 class="text-lg font-semibold text-sky-600">Grandes ofertas</h2>
        <paw:carousel id="ofertas-carousel">
            <c:forEach var="i" begin="1" end="12">
                <div class="snap-start shrink-0 w-48">
                    <paw:card title="Producto de ejemplo" subtitle="$0000"/>
                </div>
            </c:forEach>
        </paw:carousel>
    </section>

    <section class="flex flex-col gap-4">
        <h2 class="text-lg font-semibold text-sky-600">Lo más reciente</h2>
        <paw:carousel id="recientes-carousel">
            <c:forEach var="i" begin="1" end="12">
                <div class="snap-start shrink-0 w-48">
                    <paw:card title="Producto de ejemplo" subtitle="$0000"/>
                </div>
            </c:forEach>
        </paw:carousel>
    </section>

</main>

</body>
</html>