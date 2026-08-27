<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Tonkatsu - Inicio</title>
    <link rel="stylesheet" href="<c:url value="/css/tailwind.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/input.css"/>"/>
</head>

<body class=" pb-24 bg-neutral-50">
  <header class="flex items-center p-4 bg-sky-600">
      <div class="border border-white text-white bg-sky-400 rounded-lg px-3 py-1 font-semibold">Logo</div>
  </header>

  <main class="flex flex-col items-center gap-6 py-16 px-4">
      <h1 class="text-2xl font-bold text-sky-600">Tonkatsu</h1>
      <p class="text-black/70">Comprá, intercambia o retirá artículos electrónicos</p>

      <div class="flex items-center gap-12 mt-8">
          <div class="flex flex-col items-center gap-3">
              <span class="text-sm font-medium">Adquirí lo que buscás</span>
              <paw:button text="Buscar" size="lg"/>
          </div>

          <div class="w-px h-16 bg-black/15"></div>

          <div class="flex flex-col items-center gap-3">
              <span class="text-sm font-medium">Vendé lo que ya no usás</span>
              <paw:button text="Publicar" size="lg"/>
          </div>
      </div>
  </main>

  </body>
</html>