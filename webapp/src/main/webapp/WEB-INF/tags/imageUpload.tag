<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="disabled" required="false" type="java.lang.Boolean" %>
<%@ attribute name="label" required="false" %>
<%@ attribute name="multiple" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<spring:message code="imageUpload.uploadLabel" var="uploadLabel"/>
<spring:message code="imageUpload.clearLabel" var="clearLabel"/>
<spring:message code="imageUpload.removeLabel" var="removeLabel"/>

<c:set var="inputDisabled" value="${disabled ne null ? disabled : false}"/>
<c:set var="inputMultiple" value="${multiple ne null ? multiple : false}"/>
<c:set var="inputId" value="${path}"/>

<div class="flex flex-col gap-2">
    <c:if test="${not empty label}">
        <label for="${inputId}" class="text-xs text-black/70 font-medium">
            <c:out value="${label}"/>
        </label>
    </c:if>

    <div id="${inputId}-previews" class="flex flex-wrap gap-2"></div>

    <div class="flex flex-wrap gap-2">
        <label for="${inputId}-file" class="flex-shrink-0 w-32 h-32 rounded-lg border-2 border-dashed border-black/20 bg-neutral-50 flex flex-col items-center justify-center gap-2 cursor-pointer hover:border-lime-600 hover:bg-lime-50 transition-all">
            <paw:icon name="plus" classname="text-black/40 text-3xl" />
            <span class="text-sm text-black/60 font-medium text-center px-2"><c:out value="${uploadLabel}"/></span>
            <form:input
                path="${path}"
                id="${inputId}-file"
                type="file"
                accept="image/*"
                multiple="${inputMultiple}"
                disabled="${inputDisabled}"
                class="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
                onchange="handleImageUpload(this, '${inputId}')"
            />
        </label>

        <c:if test="${inputMultiple}">
            <button
                type="button"
                id="${inputId}-clear"
                class="flex-shrink-0 w-32 h-32 rounded-lg border border-black/10 bg-neutral-50 flex flex-col items-center justify-center gap-2 text-red-600 font-medium text-sm cursor-pointer hover:bg-red-50 transition-colors hidden"
                onclick="clearAllImages('${inputId}')"
            >
                <paw:icon name="trash-2" classname="text-2xl" />
                <span class="text-center px-2"><c:out value="${clearLabel}"/></span>
            </button>
        </c:if>
    </div>

    <form:errors path="${path}" element="div" cssClass="text-xs text-red-600" />
</div>

<script>
    // Store original file input reference and object URLs
    const imageUploadState = imageUploadState || new Map();

    function handleImageUpload(fileInput, fieldId) {
        const previewsContainer = document.getElementById(fieldId + '-previews');
        const clearBtn = document.getElementById(fieldId + '-clear');
        const isMultiple = fileInput.multiple;

        if (!imageUploadState.has(fieldId)) {
            imageUploadState.set(fieldId, {
                objectUrls: new Map(),
                originalFiles: []
            });
        }

        const state = imageUploadState.get(fieldId);
        const files = Array.from(fileInput.files);

        if (!isMultiple) {
            // Single file mode: clear existing previews
            previewsContainer.innerHTML = '';
            state.objectUrls.forEach(url => URL.revokeObjectURL(url));
            state.objectUrls.clear();
            state.originalFiles = [];
        }

        files.forEach((file, index) => {
            if (!file.type.startsWith('image/')) return;

            const objectUrl = URL.createObjectURL(file);
            const fileIndex = state.originalFiles.length;
            state.objectUrls.set(fileIndex, objectUrl);
            state.originalFiles.push(file);

            createPreview(previewsContainer, fieldId, objectUrl, file.name, fileIndex, state);
        });

        if (isMultiple && state.originalFiles.length > 0) {
            clearBtn.classList.remove('hidden');
        }

        updateFileInput(fileInput, fieldId, state);
    }

    function createPreview(container, fieldId, objectUrl, fileName, index, state) {
        const preview = document.createElement('div');
        preview.className = 'relative group w-32 h-32 flex-shrink-0';
        preview.dataset.index = index;

        const img = document.createElement('img');
        img.src = objectUrl;
        img.alt = fileName;
        img.className = 'w-full h-full object-cover rounded-lg border border-black/10';

        const removeBtn = document.createElement('button');
        removeBtn.type = 'button';
        removeBtn.className = 'absolute top-1 right-1 w-5 h-5 rounded-full bg-red-600 text-white text-xs opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center';
        removeBtn.innerHTML = '&times;';
        removeBtn.setAttribute('aria-label', '<spring:message code="imageUpload.removeLabel"/>');
        removeBtn.addEventListener('click', function(e) {
            e.preventDefault();
            removePreview(fieldId, index, state);
        });

        preview.appendChild(img);
        preview.appendChild(removeBtn);
        container.appendChild(preview);
    }

    function removePreview(fieldId, index, state) {
        const previewsContainer = document.getElementById(fieldId + '-previews');
        const preview = previewsContainer.querySelector('[data-index="' + index + '"]');
        if (preview) {
            preview.remove();
        }

        const objectUrl = state.objectUrls.get(index);
        if (objectUrl) {
            URL.revokeObjectURL(objectUrl);
            state.objectUrls.delete(index);
        }

        state.originalFiles[index] = null;

        const fileInput = document.getElementById(fieldId + '-file');
        updateFileInput(fileInput, fieldId, state);

        const clearBtn = document.getElementById(fieldId + '-clear');
        if (clearBtn && state.originalFiles.filter(f => f !== null).length === 0) {
            clearBtn.classList.add('hidden');
        }
    }

    function clearAllImages(fieldId) {
        const previewsContainer = document.getElementById(fieldId + '-previews');
        const state = imageUploadState.get(fieldId);

        if (state) {
            state.objectUrls.forEach(url => URL.revokeObjectURL(url));
            state.objectUrls.clear();
            state.originalFiles = [];
        }

        previewsContainer.innerHTML = '';

        const fileInput = document.getElementById(fieldId + '-file');
        fileInput.value = '';

        const clearBtn = document.getElementById(fieldId + '-clear');
        if (clearBtn) {
            clearBtn.classList.add('hidden');
        }
    }

    function updateFileInput(fileInput, fieldId, state) {
        const dataTransfer = new DataTransfer();
        state.originalFiles.forEach(file => {
            if (file) {
                dataTransfer.items.add(file);
            }
        });
        fileInput.files = dataTransfer.files;
    }

    // Cleanup on form submit
    document.addEventListener('DOMContentLoaded', function() {
        const forms = document.querySelectorAll('form');
        forms.forEach(form => {
            form.addEventListener('submit', function() {
                imageUploadState.forEach(state => {
                    state.objectUrls.forEach(url => URL.revokeObjectURL(url));
                });
            });
        });
    });
</script>