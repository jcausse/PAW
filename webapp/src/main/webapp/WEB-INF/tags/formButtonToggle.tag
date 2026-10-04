<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="items" required="true" type="java.lang.Object" %>
<%@ attribute name="selectedOption" required="true" %>
<%@ attribute name="variant" required="false" %>
<%@ attribute name="disabled" required="false" type="java.lang.Boolean" %>
<%@ attribute name="classname" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<c:set var="inputVariant" value="${not empty variant ? variant : 'default'}"/>
<c:set var="inputClass" value="${not empty classname ? classname : ''}"/>

<c:set var="isDisabled" value="${disabled ne null ? disabled : false}"/>

<paw:card classname="p-2! ${inputClass}">
    <div class="flex flex-row w-full gap-2 relative" id="${path}_base">
        <c:forEach var="option" items="${items}">
            <form:radiobutton path="${path}" id="${path}_${option.getValue()}" value="${option.getValue()}" cssClass="hidden" />
            <label for="${path}_${option.getValue()}" class="grow flex flex-col" >
                <paw:fakeButton text="${option.getLabel()}" variant="ghost" on="${option.getValue() == selectedOption ? 'true' : 'false'}" />
            </label>
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
</paw:card>
