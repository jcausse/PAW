<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="classname" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>


<c:set var="collapsibleClass" value="${not empty classname ? classname : ''}"/>

<details class="group ${collapsibleClass} open:bg-lime-600/10 rounded-md">
    <summary class="
        flex items-center gap-2 px-2 py-1
        text-sm text-lime-600 font-medium rounded-md
        cursor-pointer list-none transition duration-150
        focus-visible:outline outline-offset-0 outline-lime-600 focus-visible:shadow-[0_0_0_3px] shadow-lime-600/30
        hover:bg-current/10 disabled:bg-current/10 active:bg-current/15 disabled:active:bg-current/10
    ">
        <c:out value="${title}"/>
        <paw:icon name="chevron-down" classname="ml-auto group-open:rotate-180 transition duration-300" />
    </summary>
    <div class="flex flex-col gap-2 p-2 text-black">
        <jsp:doBody />
    </div>
</details>
