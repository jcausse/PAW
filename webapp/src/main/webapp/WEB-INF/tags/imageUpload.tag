<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="disabled" required="false" type="java.lang.Boolean" %>
<%@ attribute name="label" required="false" %>
<%@ attribute name="multiple" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<spring:message code="imageUpload.clearLabel" var="clearLabel"/>
<spring:message code="imageUpload.removeLabel" var="removeLabel"/>

<c:set var="inputDisabled" value="${disabled ne null ? disabled : false}"/>
<c:set var="inputMultiple" value="${multiple ne null ? multiple : false}"/>

<div class="flex flex-col gap-2">
    <div class="flex flex-row gap-2 items-center min-h-5.5 justify-between">
        <c:if test="${not empty label}">
            <div class="text-xs text-black/70 font-medium">
                <c:out value="${label}"/>
            </div>
        </c:if>
        <c:if test="${inputMultiple}">
            <paw:button id="${path}-clear" variant="outline" role="danger" size="sm" classname="hidden" onclick="clearAllImages('${path}')">
                <paw:icon name="trash-2" />
                <span class="text-center px-2"><c:out value="${clearLabel}"/></span>
            </paw:button>
        </c:if>
    </div>

    <div id="${path}-previews" class="grid grid-cols-5 gap-2">
        <div id="${path}-preview-template" class="relative group aspect-square rounded-lg border border-black/10 overflow-hidden hidden">
            <img class="w-full h-full object-cover" />
            <div class="absolute top-1 right-1 bg-white rounded-lg opacity-0 group-hover:opacity-100 transition-opacity">
                <paw:button size="sm" variant="outline" icon="x" type="button" role="danger" />
            </div>
        </div>

        <label for="${path}-file" class="aspect-square rounded-lg border-2 border-dashed border-black/20 bg-neutral-50 flex flex-col items-center justify-center gap-2 cursor-pointer hover:border-lime-600 hover:bg-lime-50 transition-all order-999">
            <c:choose>
                <c:when test="${inputMultiple}">
                    <spring:message code="imageUpload.uploadLabelMultiple" var="uploadLabel"/>
                    <form:input path="${path}" id="${path}-file" type="file" accept="image/*" disabled="${inputDisabled}" class="hidden" onchange="handleImageUpload(this, '${path}')" multiple="true" />
                </c:when>
                <c:otherwise>
                    <spring:message code="imageUpload.uploadLabel" var="uploadLabel"/>
                    <form:input path="${path}" id="${path}-file" type="file" accept="image/*" disabled="${inputDisabled}" class="hidden" onchange="handleImageUpload(this, '${path}')"/>
                </c:otherwise>
            </c:choose>
            <paw:icon name="plus" classname="text-black/40 text-3xl" />
            <span class="text-sm text-black/60 font-medium text-center px-2"><c:out value="${uploadLabel}"/></span>
        </label>
    </div>

    <div class="flex flex-wrap gap-2">
    </div>

    <form:errors path="${path}" element="div" cssClass="text-xs text-red-600" />
</div>

<script>
    // Store original file input reference and object URLs
    let imageUploadState = new Map();

    function handleImageUpload(fileInput, fieldId) {
        const previewsContainer = document.getElementById(fieldId + '-previews');
        const clearBtn = document.getElementById(fieldId + '-clear');
        const isMultiple = fileInput.multiple == "true";

        if (!imageUploadState.has(fieldId)) {
            imageUploadState.set(fieldId, {
                objectUrls: new Map(),
                originalFiles: []
            });
        }

        const state = imageUploadState.get(fieldId);
        const files = Array.from(fileInput.files);

        console.log(state, files, isMultiple);
        if (!isMultiple) {
            // Single file mode: clear existing previews
            const previews = previewsContainer.querySelectorAll('[data-index]');
            previews.forEach((preview) => preview.remove());
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
        const template = document.getElementById(fieldId + '-preview-template');
        const preview = template.cloneNode(true);
        preview.id = fieldId + '-preview-' + index;
        preview.dataset.index = index;
        preview.classList.remove('hidden');

        const img = preview.querySelector('img');
        img.src = objectUrl;
        img.alt = fileName;

        const removeBtn = preview.querySelector('button');
        removeBtn.addEventListener('click', function(e) {
            e.preventDefault();
            removePreview(fieldId, index, state);
        });

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

        const previews = previewsContainer.querySelectorAll('[data-index]');
        previews.forEach((preview) => preview.remove());

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
