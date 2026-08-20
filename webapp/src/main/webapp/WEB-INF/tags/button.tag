<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="text" required="true" %>
<%@ attribute name="variant" required="false" %>
<%@ attribute name="size" required="false" %>
<%@ attribute name="role" required="false" %>
<%@ attribute name="disabled" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="btnVariant" value="${not empty variant ? variant : 'default'}"/>
<c:set var="btnSize" value="${not empty size ? size : 'md'}"/>
<c:set var="btnRole" value="${not empty role ? role : 'default'}"/>

<c:set var="isDisabled" value="${disabled ne null ? disabled : false}"/>

<c:set var="variantClassnames" value="${
  btnVariant eq 'outline'
    ? 'border border-current/15 hover:bg-current/10 disabled:bg-current/10 active:bg-current/15 disabled:active:bg-current/10'
    : btnVariant eq 'ghost'
    ? 'hover:bg-current/10 disabled:bg-current/10 active:bg-current/15 disabled:active:bg-current/10'
    : '[background-position:-1px_-1px] [background-size:calc(100%+2px)_calc(100%+2px)] bg-gradient-to-b from-current/3 to-current/7 border-t border-b border-t-white/30 border-b-black/20
       hover:from-current/5 hover:to-current/10 active:border-t-black/15 active:border-b-white/30 active:translate-y-px'
}"/>

<c:set var="sizeClassnames" value="${
  btnSize eq 'sm'
    ? 'p-1 px-2 text-xs/3 min-w-16'
    : btnSize eq 'lg'
    ? 'p-4 px-6 text-base/5 min-w-20'
    : 'p-2 px-4 text-sm/4 min-w-20'
}"/>

<c:set var="roleClassnames" value="${
  btnRole eq 'danger'
    ? 'text-red-600'
    : btnRole eq 'success'
    ? 'text-green-600'
    : btnRole eq 'secondary'
    ? 'text-black/60'
    : 'text-sky-600'
}"/>

<button
  type="button"
  class="
    font-semibold rounded-lg
    flex flex-row items-center justify-center gap-2
    cursor-pointer transition duration-150 data-[state=on]:text-sky-500
    focus-visible:outline outline-offset-0 outline-sky-600
    focus-visible:shadow-[0_0_0_3px] shadow-sky-600/30
    disabled:text-black/40 disabled:cursor-default
    ${variantClassnames}
    ${sizeClassnames}
    ${roleClassnames}
  "
  <c:if test="${isDisabled}">disabled</c:if>
>
    <c:out value="${text}"/>
</button>
