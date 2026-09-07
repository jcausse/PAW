<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<html lang="${pageContext.response.locale.language}">
<head>
    <title>PAW TPE 0</title>
    <link rel="stylesheet" href="<c:url value="/css/tailwind.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/input.css"/>"/>
</head>
<body class="pb-24 bg-neutral-50">
    <paw:navbar/>
    <div class="grid grid-cols-2 gap-4 max-w-2xl mx-auto">
      <h1 class="text-4xl font-bold mb-4 col-span-2">PAW TPE 0: Librería de componentes</h1>

      <div class="flex flex-col gap-2 p-4 rounded-2xl border border-black/15 col-span-2 bg-neutral-50">
        <h2 class="text-xl font-bold">Button component</h2>

        <h3 class="font-semibold mt-2 mb-1">Primary</h3>
        <div class="grid grid-cols-4 gap-1">
          <paw:button text="Small/Primary" size="sm" />
          <paw:button text="Small/Secondary" size="sm" role="secondary" />
          <paw:button text="Small/Danger" size="sm" role="danger" />
          <paw:button text="Small/Success" size="sm" role="success" />

          <paw:button text="Medium/Primary" />
          <paw:button text="Medium/Secondary" role="secondary" />
          <paw:button text="Medium/Danger" role="danger" />
          <paw:button text="Medium/Success" role="success" />

          <paw:button text="Large/Primary" size="lg" />
          <paw:button text="Large/Secondary" size="lg" role="secondary" />
          <paw:button text="Large/Danger" size="lg" role="danger" />
          <paw:button text="Large/Success" size="lg" role="success" />
        </div>

        <h3 class="font-semibold mt-2 mb-1">Outlined</h3>
        <div class="grid grid-cols-4 gap-1">
          <paw:button text="Small/Primary" variant="outline" size="sm" />
          <paw:button text="Small/Secondary" variant="outline" size="sm" role="secondary" />
          <paw:button text="Small/Danger" variant="outline" size="sm" role="danger" />
          <paw:button text="Small/Success" variant="outline" size="sm" role="success" />

          <paw:button text="Medium/Primary" variant="outline" />
          <paw:button text="Medium/Secondary" variant="outline" role="secondary" />
          <paw:button text="Medium/Danger" variant="outline" role="danger" />
          <paw:button text="Medium/Success" variant="outline" role="success" />

          <paw:button text="Large/Primary" variant="outline" size="lg" />
          <paw:button text="Large/Secondary" variant="outline" size="lg" role="secondary" />
          <paw:button text="Large/Danger" variant="outline" size="lg" role="danger" />
          <paw:button text="Large/Success" variant="outline" size="lg" role="success" />
        </div>

        <h3 class="font-semibold mt-2 mb-1">Ghost</h3>
        <div class="grid grid-cols-4 gap-1">
          <paw:button text="Small/Primary" variant="ghost" size="sm" />
          <paw:button text="Small/Secondary" variant="ghost" size="sm" role="secondary" />
          <paw:button text="Small/Danger" variant="ghost" size="sm" role="danger" />
          <paw:button text="Small/Success" variant="ghost" size="sm" role="success" />

          <paw:button text="Medium/Primary" variant="ghost" />
          <paw:button text="Medium/Secondary" variant="ghost" role="secondary" />
          <paw:button text="Medium/Danger" variant="ghost" role="danger" />
          <paw:button text="Medium/Success" variant="ghost" role="success" />

          <paw:button text="Large/Primary" variant="ghost" size="lg" />
          <paw:button text="Large/Secondary" variant="ghost" size="lg" role="secondary" />
          <paw:button text="Large/Danger" variant="ghost" size="lg" role="danger" />
          <paw:button text="Large/Success" variant="ghost" size="lg" role="success" />
        </div>
      </div>

      <div class="flex flex-col gap-2 p-4 rounded-2xl border border-black/15 bg-neutral-50">
        <h2 class="text-xl font-bold mb-2">Card component</h2>
        <paw:card title="Asus Tuf Gaming RTX 3070 Ti" subtitle="Tarjetas Gráficas para PC">
            <div class="bg-black/5 rounded h-40 flex items-center justify-center text-black/30 text-sm mb-3">imagen</div>
            <p class="text-xl font-bold">$650.000</p>
        </paw:card>
      </div>

      <div class="flex flex-col gap-2 p-4 rounded-2xl border border-black/15 bg-neutral-50">
        <h2 class="text-xl font-bold mb-2">Input component</h2>
        <paw:input id="myInput" label="Input" placeholder="Enter some text..." />
        <paw:input id="myInput2" label="Input" placeholder="Enter some text..." error="This input has an error" />

        <paw:input id="myInput3" variant="outline" placeholder="Enter some text..." label="Outlined Input" />
        <paw:input id="myInput4" variant="outline" placeholder="Enter some text..." label="Outlined Input" error="This input has an error" />

        <paw:input id="myInput5" type="password" variant="outline" label="Password Input" />
      </div>
    </div>
</body>
</html>
