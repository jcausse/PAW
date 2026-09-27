<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="buttonVariant" required="false" rtexprvalue="true" type="java.lang.String" description="Button variant (ghost, outline, etc.)" />
<%@ attribute name="buttonClassname" required="false" rtexprvalue="true" type="java.lang.String" description="Additional CSS classes for the button" />
<%@ attribute name="buttonIcon" required="false" rtexprvalue="true" type="java.lang.String" description="Icon name for the button" />
<%@ attribute name="buttonId" required="true" rtexprvalue="true" type="java.lang.String" description="Unique ID for the button and panel" />
<%@ attribute name="panelWidth" required="false" rtexprvalue="true" type="java.lang.String" description="Width of the panel (e.g., w-48)" />
<%@ attribute name="panelAlign" required="false" rtexprvalue="true" type="java.lang.String" description="Panel alignment (left, right)" />
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<c:set var="panelAlignClass" value="${panelAlign == 'left' ? 'left-0' : 'right-0'}"/>

<div class="relative" id="${buttonId}Menu">
    <paw:button id="${buttonId}Button" variant="${buttonVariant}" classname="${buttonClassname}" icon="${buttonIcon}" type="button" />

    <div id="${buttonId}Panel" class="hidden absolute ${panelAlignClass} top-full ${panelWidth} bg-white rounded-lg border border-black/10 z-50 flex flex-col p-2 shadow-lg">
        <jsp:doBody />
    </div>
</div>

<script>
    (function() {
        const button = document.getElementById('${buttonId}Button');
        const panel = document.getElementById('${buttonId}Panel');
        const menu = document.getElementById('${buttonId}Menu');

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