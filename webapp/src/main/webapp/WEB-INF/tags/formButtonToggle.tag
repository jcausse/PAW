<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="items" required="true" type="java.lang.Object" %>
<%@ attribute name="selectedOption" required="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<div class="flex flex-row w-full gap-2 relative" id="${path}_base">
    <c:forEach var="option" items="${items}">
        <form:radiobutton path="${path}" id="${path}_${option.getValue()}" value="${option.getValue()}" cssClass="hidden" />
        <paw:fakeButton htmlFor="${path}_${option.getValue()}" text="${option.getLabel()}" variant="ghost" classname="grow" on="${option.getValue() == selectedOption ? 'true' : 'false'}" />
    </c:forEach>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        const root = document.getElementById('${path}' + '_base');
        if (root) {
            const radios = root.querySelectorAll("input");
            radios.forEach((radio) =>
                radio.addEventListener('change', function() {
                    document.getElementById('filterForm').submit();
                })
            );
        }
    });
</script>
