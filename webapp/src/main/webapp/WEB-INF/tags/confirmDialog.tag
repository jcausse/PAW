<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="id" required="true" %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="confirmText" required="true" %>
<%@ attribute name="cancelText" required="true" %>
<%@ attribute name="formAction" required="true" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<dialog id="${id}" class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 rounded-xl border border-black/10 p-6 max-w-sm w-[90vw] backdrop:bg-black/40">    <h3 class="text-lg font-semibold mb-2">${title}</h3>
    <div class="text-sm text-black/70 mb-6">
        <jsp:doBody/>
    </div>
    <div class="flex justify-end gap-2">
        <button type="button"
                onclick="document.getElementById('${id}').close()"
                class="font-semibold rounded-lg px-4 py-2 text-sm text-black/60 hover:bg-black/5 transition">
            ${cancelText}
        </button>
        <form action="${formAction}" method="POST">
            <button type="submit"
                    class="font-semibold rounded-lg px-4 py-2 text-sm text-red-600 border border-current/15 hover:bg-current/10 transition">
                ${confirmText}
            </button>
        </form>
    </div>
</dialog>