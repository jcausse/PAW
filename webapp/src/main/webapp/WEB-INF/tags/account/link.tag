<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="href" required="true" %>
<%@ attribute name="text" required="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<paw:linkButton variant="ghost" href="${href}" classname="justify-start text-start">
    <c:out value="${text}"/>
</paw:linkButton>
