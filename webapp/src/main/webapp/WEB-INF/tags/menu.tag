<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="id" required="true" rtexprvalue="true" type="java.lang.String" description="Unique ID for the button and panel" %>
<%@ attribute name="variant" required="false" rtexprvalue="true" type="java.lang.String" description="Button variant (ghost, outline, etc.)" %>
<%@ attribute name="classname" required="false" rtexprvalue="true" type="java.lang.String" description="Additional CSS classes for the button" %>
<%@ attribute name="icon" required="false" rtexprvalue="true" type="java.lang.String" description="Icon name for the button" %>
<%@ attribute name="size" required="false" rtexprvalue="true" type="java.lang.String" description="Button size (sm, md, lg)" %>
<%@ attribute name="panelWidth" required="false" rtexprvalue="true" type="java.lang.String" description="Width of the panel (e.g., w-48)" %>
<%@ attribute name="panelAlign" required="false" rtexprvalue="true" type="java.lang.String" description="Panel alignment (left, right)" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<c:set var="panelAlignClass" value="${panelAlign == 'left' ? 'left-0' : 'right-0'}"/>
<c:set var="panelWidthClass" value="${empty panelWidth ? 'w-48' : panelWidth}"/>

<div class="relative" id="${id}Menu">
    <paw:button id="${id}Button" variant="${variant}" classname="${classname}" icon="${icon}" size="${size}" type="button" />

    <div id="${id}Panel" class="hidden absolute ${panelAlignClass} top-full ${panelWidthClass} bg-white rounded-2xl border border-black/10 z-50 flex flex-col p-2 shadow-lg">
        <jsp:doBody />
    </div>
</div>

<script>
    (function() {
        const button = document.getElementById('${id}Button');
        const panel = document.getElementById('${id}Panel');
        const menu = document.getElementById('${id}Menu');

        if (button && panel) {
            button.addEventListener('click', function(e) {
                e.stopPropagation();
                panel.classList.toggle('hidden');
            });

            document.addEventListener('click', function(e) {
                if (menu && !menu.contains(e.target)) {
                    panel.classList.add('hidden');
                }
            });
        }
    })();
</script>
