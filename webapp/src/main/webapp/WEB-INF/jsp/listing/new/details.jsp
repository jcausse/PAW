<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
    <title><spring:message code="listing.new.title"/></title>
    <%-- FOR DEVELOPMENT ONLY!! --%>
    <%-- <script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script> --%>
    <link rel="stylesheet" href="<c:url value="/css/tailwind.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/input.css"/>"/>
</head>
<body class="px-8 pb-24 bg-neutral-50">
    <paw:navbar />

    <div class="max-w-3xl mx-auto mt-8">
        <c:url value="/listing/new/details" var="detailsUrl"/>

        <paw:card>
            <jsp:body>
                <div class="mb-2 flex items-center gap-2">
                    <div class="flex-1 text-sky-600 font-medium text-sm flex items-center gap-2 p-3 rounded-lg">
                        <div class="w-6 h-6 rounded-full border-2 border-sky-600 flex items-center justify-center text-xs text-sky-600">✓</div>
                        <spring:message code="listing.new.step1" var="step1Label"/>
                        <span><c:out value="${step1Label}"/></span>
                    </div>
                    <div class="flex-1 text-sky-600 font-medium text-sm flex items-center gap-2 bg-sky-100 p-3 rounded-lg">
                        <div class="w-6 h-6 rounded-full border-2 border-sky-600 bg-sky-600 flex items-center justify-center text-xs text-sky-100">2</div>
                        <spring:message code="listing.new.step2" var="step2Label"/>
                        <span><c:out value="${step2Label}"/></span>
                    </div>
                </div>

                <div class="text-sm text-black/60 mb-2">
                    <spring:message code="listing.new.details.desc" var="detailsDesc"/>
                    <c:out value="${detailsDesc}"/>
                </div>

                <%-- TODO move this to a custom tag --%>
                <hr class="border-t-0 border-b border-black/10">

                <c:url value="/listing/new/details" var="detailsUrl"/>

                <form:form id="detailsForm" modelAttribute="detailsForm" action="${detailsUrl}" method="post" class="flex flex-col gap-5" enctype="multipart/form-data">
                    <form:hidden path="productId"/>

                    <div class="grid grid-cols-1 md:grid-cols-2 gap-6 md:gap-4 items-start">
                        <spring:message code="listing.new.titleLabel" var="titleLabel"/>
                        <paw:formInput path="title" label="${titleLabel}" />

                        <spring:message code="listing.new.price" var="priceLabel"/>
                        <paw:formInput path="price" label="${priceLabel}" type="number" step="0.01" min="0" />

                        <spring:message code="listing.new.conditionLabel" var="conditionLabel"/>
                        <spring:message code="listing.new.condition.select" var="conditionPlaceholder"/>
                        <paw:formSelect path="condition" label="${conditionLabel}" placeholder="${conditionPlaceholder}"
                                        items="${conditionOptions}" stringOptions="true" />

                        <div class="flex items-center h-full pt-5">
                            <spring:message code="listing.new.acceptsTradeLabel" var="acceptsTradeLabel"/>
                            <paw:formCheckbox path="acceptsTrade" label="${acceptsTradeLabel}" />
                        </div>
                    </div>

                    <spring:message code="listing.new.descriptionLabel" var="descriptionLabel"/>
                    <paw:formTextarea path="description" label="${descriptionLabel}" rows="4" />

                    <spring:message code="listing.new.imagesLabel" var="imagesLabel"/>
                    <paw:formInput path="images" label="${imagesLabel}" type="file" multiple="true" accept="image/*" />

                    <div id="imagePreviews" class="flex flex-wrap gap-2 empty:hidden"></div>

                    <div class="mt-2 flex justify-center gap-4">
                        <c:url value="/listing/new/choose-product" var="backUrl">
                            <c:param name="productId" value="${detailsForm.productId}"/>
                        </c:url>
                        <spring:message code="listing.new.back" var="backLabel"/>
                        <paw:linkButton href="${backUrl}" text="${backLabel}" size="lg" variant="outline" classname="w-40" />
                        <spring:message code="listing.new.submitListing" var="submitLabel"/>
                        <paw:button text="${submitLabel}" size="lg" classname="w-60" type="submit" variant="primary"/>
                    </div>
                </form:form>
            </jsp:body>
        </paw:card>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const fileInput = document.getElementById('images');
            const previewsContainer = document.getElementById('imagePreviews');
            const objectUrls = new Map();

            function createPreview(file, index) {
                const objectUrl = URL.createObjectURL(file);
                objectUrls.set(index, objectUrl);

                const preview = document.createElement('div');
                preview.className = 'relative group w-32 h-32 flex-shrink-0';
                preview.dataset.index = index;

                const img = document.createElement('img');
                img.src = objectUrl;
                img.alt = file.name;
                img.className = 'w-full h-full object-cover rounded-lg border border-black/10';

                const removeBtn = document.createElement('button');
                removeBtn.type = 'button';
                removeBtn.className = 'absolute top-1 right-1 w-5 h-5 rounded-full bg-red-600 text-white text-xs opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center';
                removeBtn.innerHTML = '&times;';
                removeBtn.setAttribute('aria-label', 'Remove image');
                removeBtn.addEventListener('click', function(e) {
                    e.preventDefault();
                    removePreview(index);
                });

                preview.appendChild(img);
                preview.appendChild(removeBtn);
                previewsContainer.appendChild(preview);
            }

            function removePreview(index) {
                const preview = previewsContainer.querySelector('[data-index="' + index + '"]');
                if (preview) {
                    preview.remove();
                }
                const objectUrl = objectUrls.get(index);
                if (objectUrl) {
                    URL.revokeObjectURL(objectUrl);
                    objectUrls.delete(index);
                }
                updateFileInput();
            }

            function updateFileInput() {
                const dataTransfer = new DataTransfer();
                const remainingPreviews = Array.from(previewsContainer.querySelectorAll('[data-index]')).sort((a, b) => 
                    parseInt(a.dataset.index) - parseInt(b.dataset.index)
                );
                const originalFiles = fileInput.files;

                remainingPreviews.forEach((preview, newIndex) => {
                    const originalIndex = parseInt(preview.dataset.index);
                    if (originalFiles[originalIndex]) {
                        dataTransfer.items.add(originalFiles[originalIndex]);
                    }
                    preview.dataset.index = newIndex;
                });

                fileInput.files = dataTransfer.files;
            }

            fileInput.addEventListener('change', function() {
                previewsContainer.innerHTML = '';
                objectUrls.forEach(url => URL.revokeObjectURL(url));
                objectUrls.clear();

                const files = Array.from(this.files);
                files.forEach((file, index) => {
                    if (file.type.startsWith('image/')) {
                        createPreview(file, index);
                    }
                });
            });

            detailsForm.addEventListener('submit', function() {
                objectUrls.forEach(url => URL.revokeObjectURL(url));
            });
        });
    </script>
</body>
</html>