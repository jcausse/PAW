<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<html>
<head>
    <title>PAW TPE 0</title>
    <link rel="stylesheet" href="<c:url value="/css/tailwind.css"/>"/>
</head>
<body class="p-8">
    <h1 class="text-2xl font-bold mb-4">PAW TPE 0</h1>

    <h2 class="text-lg font-semibold mb-2">Button component</h2>
    <paw:button text="Buy" variant="primary"/>
    <paw:button text="Cancel" variant="secondary"/>
    <paw:button text="Delete" variant="danger"/>

    <h2 class="text-lg font-semibold mb-2 mt-6">Card component</h2>
    <paw:card title="Asus Tuf Gaming RTX 3070 Ti" subtitle="Tarjetas Gráficas para PC">
        <div class="bg-black/5 rounded h-40 flex items-center justify-center text-black/30 text-sm mb-3">imagen</div>
        <p class="text-xl font-bold">$650.000</p>
    </paw:card>
</body>
</html>
