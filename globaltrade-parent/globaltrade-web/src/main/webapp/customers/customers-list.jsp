<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 8/19/2026
  Time: 12:00 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%--@elvariable id="customers" type=""--%>
<!DOCTYPE html>
<html lang="en" data-theme="dracula">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GlobalTrade Logistics - Customers List</title>

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
        <div class="p-4 sm:p-6 lg:p-8 max-w-7xl mx-auto">

            <!-- ===== PAGE HEADER ===== -->
            <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 mb-6">
                <div>
                    <h1 class="text-xl sm:text-2xl font-bold text-white">Customers</h1>
                    <p class="text-white/30 text-sm font-light">Manage your customers</p>
                </div>
                <div class="flex items-center gap-2">
                    <c:if test="${sessionScope.role == 'LOGISTICS_COORDINATOR'}">
                        <button onclick="window.print()" class="px-4 py-2 bg-white/5 border border-white/5 rounded-lg text-white/50 hover:text-white hover:bg-white/10 transition-all text-xs flex items-center gap-2">
                            <i class="fas fa-file-export"></i>
                            <span>Export</span>
                        </button>
                    </c:if>
                </div>
            </div>

            <!-- ===== FILTER SECTION ===== -->
            <div class="dashboard-card mb-6">
                <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                    <!-- Search -->
                    <div class="form-group">
                        <label for="searchInput" class="text-white/40 text-xs font-medium block mb-1.5">Search</label>
                        <input type="text" id="searchInput" name="searchInput" placeholder="Search customers..."
                               class="form-control w-full px-3 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:outline-none transition-all">
                    </div>

                    <!-- Country Filter -->

                    <div class="form-group">
                        <label for="countryFilter" class="text-white/40 text-xs font-medium block mb-1.5">Country</label>
                        <div class="select-wrapper">
                            <select id="countryFilter" name="countryFilter" class="form-select w-full px-3 py-2.5 pr-10 bg-white/5 border border-white/10 rounded-lg text-white text-sm focus:border-dracula-red focus:outline-none transition-all appearance-none cursor-pointer">
                                <option value="" class="bg-black text-white/40">All Countries</option>
                                <c:forEach var="country" items="${countryList}">
                                    <option value="${country}" class="bg-black">${country}</option>
                                </c:forEach>
                            </select>
                            <span class="absolute right-3 top-1/2 -translate-y-1/2 text-white/20 pointer-events-none">
                                <i class="fas fa-chevron-down text-xs"></i>
                            </span>
                        </div>
                    </div>

                    <!-- Apply Button -->
                    <div class="form-group flex items-end">
                        <button id="applyFiltersBtn" class="w-full px-4 py-2.5 bg-dracula-red/20 border border-dracula-red/30 rounded-lg text-dracula-red text-sm font-medium hover:bg-dracula-red/30 transition-all">
                            <i class="fas fa-filter mr-1.5"></i>Apply Filters
                        </button>
                    </div>
                </div>
            </div>

            <!-- ===== CUSTOMERS TABLE ===== -->
            <div class="dashboard-card overflow-hidden">
                <div class="overflow-x-auto">
                    <table class="transaction-table">
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>Customer Name</th>
                            <th>Company</th>
                            <th>Contact Phone</th>
                            <th>Country</th>
                            <th>Created</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${not empty customers}">
                                <c:forEach var="c" items="${customers}" varStatus="status">
                                    <tr>
                                        <td class="text-white/40 text-xs">${status.index + 1}</td>
                                        <td>
                                            <div class="text-white/70 text-sm font-medium">${c.user.fullName}</div>
                                            <div class="text-white/30 text-[10px]">${c.user.username}</div>
                                        </td>
                                        <td class="text-white/50 text-xs">${c.companyName != null ? c.companyName : 'N/A'}</td>
                                        <td class="text-white/40 text-xs">${c.contactPhone != null ? c.contactPhone : 'N/A'}</td>
                                        <td class="text-white/50 text-xs">
                                            <span class="px-2 py-0.5 bg-white/5 rounded-full text-[10px]">${c.country}</span>
                                        </td>
                                        <td class="text-white/30 text-xs">${c.createdAt != null ? c.createdAt.toString().substring(0, 10) : 'N/A'}</td>
                                        <td>
                                            <div class="flex items-center gap-2">
                                                <a href="${pageContext.request.contextPath}/customers/details?customerId=${c.customerId}"
                                                   class="text-blue-400/60 hover:text-blue-400 text-xs transition-all" title="View Details">
                                                    <i class="fas fa-eye"></i>
                                                </a>
                                                <button class="text-white/20 hover:text-red-400 text-xs transition-all" title="Delete"  onclick="showDeleteModal(${c.customerId}, '${c.user.fullName}', '${pageContext.request.contextPath}')">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="7" class="text-center text-white/20 py-8">
                                        <div class="flex flex-col items-center gap-2">
                                            <i class="fas fa-users text-3xl text-white/10"></i>
                                            <span class="text-sm">No customers found</span>
                                        </div>
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                        </tbody>
                    </table>
                </div>

                <!-- ===== TABLE FOOTER ===== -->
                <div class="flex flex-wrap items-center justify-between gap-3 mt-4 pt-4 border-t border-white/5">
                    <div class="text-white/20 text-xs">
                        Showing ${fn:length(customers)} customers
                    </div>
                    <div class="flex items-center gap-1">
                        <button class="px-3 py-1 bg-white/5 border border-white/10 rounded-lg text-white/30 text-xs hover:bg-white/10 transition-all" disabled>
                            Previous
                        </button>
                        <button class="px-3 py-1 bg-dracula-red/20 border border-dracula-red/30 rounded-lg text-dracula-red text-xs hover:bg-dracula-red/30 transition-all">
                            1
                        </button>
                        <button class="px-3 py-1 bg-white/5 border border-white/10 rounded-lg text-white/30 text-xs hover:bg-white/10 transition-all" disabled>
                            Next
                        </button>
                    </div>
                </div>
            </div>

        </div>
    </main>

    <!-- ===== DELETE CONFIRMATION MODAL ===== -->
    <div id="deleteModal" class="delete-modal-overlay">
        <div class="delete-modal-content">
            <div class="delete-modal-header">
                <h3>Confirm Delete</h3>
                <button class="close-btn" onclick="closeDeleteModal()">
                    <i class="fas fa-times"></i>
                </button>
            </div>
            <p class="delete-modal-subtitle">This action cannot be undone</p>

            <!-- Delete Icon -->
            <div class="delete-icon-wrapper">
                <i class="fas fa-trash-alt"></i>
            </div>

            <!-- Message -->
            <p class="delete-modal-message" id="deleteMessage">
                Are you sure you want to delete this customer?
            </p>

            <!-- Warning -->
            <div class="delete-modal-warning">
                <i class="fas fa-exclamation-triangle"></i>
                <span>All customer data will be permanently removed.</span>
            </div>

            <!-- Actions -->
            <div class="delete-modal-actions">
                <button class="btn-cancel" onclick="closeDeleteModal()">Cancel</button>
                <button class="btn-delete" onclick="confirmDeleteAction()">
                    <i class="fas fa-trash"></i> Delete
                </button>
            </div>
        </div>
    </div>

    <!-- ===== FOOTER ===== -->
    <jsp:include page="/WEB-INF/views/footer.jsp" />
</div>

<!-- Custom JavaScript -->
<script src="${pageContext.request.contextPath}/js/customers.js"></script>
</body>
</html>