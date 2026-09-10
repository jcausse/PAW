<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="href" required="true" %>
<%@ attribute name="text" required="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<c:set var="currentUrl" value="${requestScope['javax.servlet.forward.request_uri']}" />
<c:set var="buttonClass" value="${
  currentUrl eq href ? 'bg-current/10' : ''
}" />

<paw:linkButton variant="ghost" href="${href}" classname="justify-start text-start ${buttonClass}">
    <c:out value="${text}"/>
</paw:linkButton>
