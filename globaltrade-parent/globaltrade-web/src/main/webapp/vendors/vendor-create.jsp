<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 8/19/2026
  Time: 12:00 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page import="lk.jiat.global.entity.Vendor" %>

<%--@elvariable id="vendor" type="lk.jiat.global.entity.Vendor"--%>
<%--@elvariable id="error" type="java.lang.String"--%>
<%
    boolean editMode = request.getAttribute("editMode") != null && (Boolean) request.getAttribute("editMode");
    Vendor vendor = (Vendor) request.getAttribute("vendor");
    String pageTitle = editMode ? "Edit Vendor" : "Create Vendor";
    String pageSubtitle = editMode ? "Update vendor information" : "Add a new vendor to your supply chain";
    String buttonText = editMode ? "Update Vendor" : "Create Vendor";
    String buttonIcon = editMode ? "fa-save" : "fa-plus";
%>

<!DOCTYPE html>
<html lang="en" data-theme="dracula">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GlobalTrade Logistics - <%= pageTitle %></title>

    <!-- Favicon -->
    <link rel="icon" type="image/png" sizes="32x32" href="${pageContext.request.contextPath}/images/favicon.png">

    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&display=swap" rel="stylesheet">

    <!-- Font Awesome -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/all.min.css">

    <!-- Tailwind CSS Output -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/output.css">
</head>
<body class="dashboard-container font-inter">

<!-- Background Glow Orbs -->
<div class="fixed inset-0 z-0 pointer-events-none overflow-hidden">
    <div class="glow-orb w-[600px] h-[600px] -top-[200px] -right-[200px] bg-red-500/5"></div>
    <div class="glow-orb w-[400px] h-[400px] -bottom-[100px] -left-[100px] bg-red-900/5"></div>
    <div class="glow-orb w-[300px] h-[300px] top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-red-500/3"></div>
</div>

<!-- ===== HEADER ===== -->
<jsp:include page="/WEB-INF/views/header.jsp" />

<!-- ===== SIDEBAR ===== -->
<jsp:include page="/WEB-INF/views/sidebar.jsp" />

<!-- ===== MAIN CONTENT WRAPPER ===== -->
<div class="main-content-wrapper">
    <main class="main-content relative z-10">
        <div class="p-4 sm:p-6 lg:p-8 max-w-4xl mx-auto">

            <!-- ===== PAGE HEADER ===== -->
            <div class="flex items-center gap-3 mb-6">
                <a href="${pageContext.request.contextPath}/vendors" class="text-white/30 hover:text-white/60 transition-all">
                    <i class="fas fa-arrow-left"></i>
                </a>
                <div>
                    <h1 class="text-xl sm:text-2xl font-bold text-white"><%= pageTitle %></h1>
                    <p class="text-white/30 text-sm font-light"><%= pageSubtitle %></p>
                </div>
            </div>

            <!-- ===== SESSION NOTIFICATIONS ===== -->
            <c:if test="${not empty sessionScope.errorNotification}">
                <input type="hidden" id="sessionErrorMsg" value="${sessionScope.errorNotification}">
                <c:remove var="errorNotification" scope="session" />
            </c:if>

            <c:if test="${not empty sessionScope.successNotification}">
                <input type="hidden" id="sessionSuccessMsg" value="${sessionScope.successNotification}">
                <c:remove var="successNotification" scope="session" />
            </c:if>

            <!-- ===== ERROR DISPLAY (from request attribute) ===== -->
            <c:if test="${not empty error}">
                <div class="mb-4 p-3 bg-red-500/10 border border-red-500/20 rounded-lg text-red-400 text-sm flex items-center gap-2">
                    <i class="fas fa-exclamation-circle"></i>
                        ${error}
                </div>
            </c:if>

            <!-- ===== CREATE/EDIT FORM ===== -->
            <div class="dashboard-card">
                <form id="vendorForm" action="${pageContext.request.contextPath}/vendors/create" method="POST">
                    <input type="hidden" name="csrfToken" value="placeholder_token">

                    <!-- Hidden field for edit mode -->
                    <c:if test="<%= editMode %>">
                        <input type="hidden" name="vendorId" value="${vendor.vendorId}">
                        <input type="hidden" name="editMode" value="true">
                    </c:if>

                    <!-- Row 1: Vendor Name + Contact Email (2 columns) -->
                    <div class="form-row">
                        <div class="form-group">
                            <label for="vendorName" class="text-white/40 text-xs font-medium block mb-1.5">Vendor Name <span class="text-dracula-red">*</span></label>
                            <input type="text" id="vendorName" name="vendorName" placeholder="Enter vendor name"
                                   value="<%= editMode && vendor != null ? vendor.getVendorName() : "" %>"
                                   class="form-control w-full px-3 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:outline-none transition-all" required>
                        </div>
                        <div class="form-group">
                            <label for="contactEmail" class="text-white/40 text-xs font-medium block mb-1.5">Contact Email <span class="text-dracula-red">*</span></label>
                            <input type="email" id="contactEmail" name="contactEmail" placeholder="Enter contact email"
                                   value="<%= editMode && vendor != null ? vendor.getContactEmail() : "" %>"
                                   class="form-control w-full px-3 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:outline-none transition-all" required>
                        </div>
                    </div>

                    <!-- Row 2: Contact Phone + Country (2 columns) -->
                    <div class="form-row">
                        <div class="form-group">
                            <label for="contactPhone" class="text-white/40 text-xs font-medium block mb-1.5">Contact Phone</label>
                            <input type="text" id="contactPhone" name="contactPhone" placeholder="Enter contact phone"
                                   value="<%= editMode && vendor != null && vendor.getContactPhone() != null ? vendor.getContactPhone() : "" %>"
                                   class="form-control w-full px-3 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:outline-none transition-all">
                        </div>
                        <div class="form-group">
                            <label for="country" class="text-white/40 text-xs font-medium block mb-1.5">Country <span class="text-dracula-red">*</span></label>
                            <div class="select-wrapper">
                                <select id="country" name="country" class="form-select ..." required>
                                    <option value="" class="bg-black text-white/40">Select Country</option>
                                    <c:forEach var="c" items="${countryList}">
                                        <option value="${c}" class="bg-black" ${c eq vendor.country ? 'selected' : ''}>
                                                ${c}
                                        </option>
                                    </c:forEach>
                                </select>
                                <span class="absolute right-3 top-1/2 -translate-y-1/2 text-white/20 pointer-events-none">
                                    <i class="fas fa-chevron-down text-xs"></i>
                                </span>
                            </div>
                        </div>
                    </div>

                    <!-- Row 3: Status (1 column - full width) -->
                    <div class="form-row">
                        <div class="form-group">
                            <label for="status" class="text-white/40 text-xs font-medium block mb-1.5">Status</label>
                            <div class="select-wrapper">
                                <select id="status" name="status" class="form-select ...">
                                    <option value="ACTIVE" ${vendor.status eq 'ACTIVE' ? 'selected' : ''}>Active</option>
                                    <option value="SUSPENDED" ${vendor.status eq 'SUSPENDED' ? 'selected' : ''}>Suspended</option>
                                </select>
                                <span class="absolute right-3 top-1/2 -translate-y-1/2 text-white/20 pointer-events-none">
                                    <i class="fas fa-chevron-down text-xs"></i>
                                </span>
                            </div>
                        </div>
                        <div class="form-group"></div>
                    </div>

                    <!-- Buttons -->
                    <div class="flex items-center gap-3 pt-3 border-t border-white/5 mt-4">
                        <button type="submit" id="submitBtn" class="px-6 py-2.5 bg-gradient-to-r from-dracula-red to-red-900 rounded-lg text-white text-sm font-semibold hover:shadow-lg hover:shadow-red-500/25 transition-all">
                            <i class="fas <%= buttonIcon %> mr-1.5"></i><%= buttonText %>
                        </button>
                        <button type="reset" id="resetBtn" class="px-6 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white/40 text-sm hover:bg-white/10 hover:text-white/60 transition-all">
                            <i class="fas fa-undo mr-1.5"></i>Reset
                        </button>
                        <a href="${pageContext.request.contextPath}/vendors" class="px-6 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white/30 text-sm hover:bg-white/10 hover:text-white/50 transition-all">
                            Cancel
                        </a>
                    </div>

                </form>
            </div>

        </div>
    </main>

    <!-- ===== FOOTER ===== -->
    <jsp:include page="/WEB-INF/views/footer.jsp" />
</div>

<!-- Custom JavaScript -->
<script src="${pageContext.request.contextPath}/js/vendors.js"></script>
</body>
</html>