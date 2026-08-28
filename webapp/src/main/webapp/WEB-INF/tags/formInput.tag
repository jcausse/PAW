<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="type" required="false" %>
<%@ attribute name="label" required="false" %>
<%@ attribute name="placeholder" required="false" %>
<%@ attribute name="variant" required="false" %>
<%@ attribute name="disabled" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:set var="inputVariant" value="${not empty variant ? variant : 'default'}"/>
<c:set var="inputType" value="${not empty type ? type : 'text'}"/>

<c:set var="variantClassnames" value="${
  inputVariant eq 'outline'
    ? 'border border-black/20 focus-visible:border-sky-600 focus-visible:outline-2
       invalid:border-red-600 data-error:border-red-600 invalid:outline-red-600/30 data-error:outline-red-600/30'
    : 'border-t border-b border-black/15 border-b-white/20 bg-gradient-to-b from-black/5 to-black/2 [background-position:-1px_-1px] [background-size:calc(100%+2px)_calc(100%+2px)]
       focus-visible:outline outline-sky-600 focus-visible:shadow-[0_0_0_3px] shadow-sky-600/30
       invalid:outline data-error:outline invalid:outline-red-600 data-error:outline-red-600 invalid:shadow-red-600/30 data-error:shadow-red-600/30
       invalid:from-red-600/5 invalid:to-red-600/2 data-error:from-red-600/5 data-error:to-red-600/2'
}"/>

<c:set var="isDisabled" value="${disabled ne null ? disabled : false}"/>

<div class="flex flex-col gap-1">
  <c:if test="${not empty label}">
    <label for="${path}" class="text-xs text-black/70 font-medium">
      ${label}
    </label>
  </c:if>

  <form:input
    path="${path}"
    id="${path}"
    type="${inputType}"
    placeholder="${placeholder}"
    disabled="${isDisabled}"
    cssClass="px-2 py-1 rounded-lg text-sm outline-0 transition duration-150 outline-sky-600/30 placeholder:text-black/40 ${variantClassnames}"
  />

  <form:errors path="${path}" element="div" cssClass="text-xs text-red-600"/>
</div>
