<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<html>
<head>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/input.css" />
</head>

<body>
  <h2 class="text-red">
    <c:out value="Hello ${username}!"/>
  </h2>
</body>
</html>
