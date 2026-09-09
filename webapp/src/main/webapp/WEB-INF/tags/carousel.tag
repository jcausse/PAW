<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="id" required="true" %>

<div class="relative group">
    <button
        type="button"
        onclick="document.getElementById('${id}').scrollBy({left: -260, behavior: 'smooth'})"
        class="absolute left-0 top-1/2 -translate-y-1/2 -translate-x-1/2 z-10
               w-9 h-9 rounded-full bg-white shadow-md border border-black/10
               flex items-center justify-center cursor-pointer
               opacity-0 group-hover:opacity-100 transition"
               aria-label="Anterior"
    >‹</button>

    <div
        id="${id}"
        class="flex gap-4 overflow-x-auto scroll-smooth snap-x snap-mandatory [scrollbar-width:none]"
    >
        <jsp:doBody/>
    </div>

    <button
        type="button"
        onclick="document.getElementById('${id}').scrollBy({left: 260, behavior: 'smooth'})"
        class="absolute right-0 top-1/2 -translate-y-1/2 translate-x-1/2 z-10
               w-9 h-9 rounded-full bg-white shadow-md border border-black/10
               flex items-center justify-center cursor-pointer
               opacity-0 group-hover:opacity-100 transition"
               aria-label="Siguiente"
    >›</button>
</div>