<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 8/19/2026
  Time: 12:00 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%--@elvariable id="vendors" type=""--%>
<!DOCTYPE html>
<html lang="en" data-theme="dracula">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GlobalTrade Logistics - Vendors List</title>

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

<c:if test="${not empty sessionScope.errorNotification}">
    <input type="hidden" id="sessionErrorMsg" value="${sessionScope.errorNotification}">
    <c:remove var="errorNotification" scope="session" />
</c:if>

<c:if test="${not empty sessionScope.successNotification}">
    <input type="hidden" id="sessionSuccessMsg" value="${sessionScope.successNotification}">
    <c:remove var="successNotification" scope="session" />
</c:if>

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
                    <h1 class="text-xl sm:text-2xl font-bold text-white">Vendors</h1>
                    <p class="text-white/30 text-sm font-light">Manage your supply chain vendors</p>
                </div>
                <div class="flex items-center gap-2">
                    <a href="${pageContext.request.contextPath}/vendors/create"
                       class="px-4 py-2 bg-gradient-to-r from-dracula-red to-red-900 rounded-lg text-white text-xs font-semibold hover:shadow-lg hover:shadow-red-500/25 transition-all flex items-center gap-2">
                        <i class="fas fa-plus"></i>
                        <span>Add Vendor</span>
                    </a>
                    <button onclick="window.print()"
                            class="px-4 py-2 bg-white/5 border border-white/5 rounded-lg text-white/50 hover:text-white hover:bg-white/10 transition-all text-xs flex items-center gap-2">
                        <i class="fas fa-file-export"></i>
                        <span>Export</span>
                    </button>
                </div>
            </div>

            <!-- ===== FILTER SECTION ===== -->
            <div class="dashboard-card mb-6">
                <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4">
                    <!-- Search -->
                    <div class="form-group">
                        <label for="searchInput" class="text-white/40 text-xs font-medium block mb-1.5">Search</label>
                        <input type="text" id="searchInput" name="searchInput" placeholder="Search vendors..."
                               class="form-control w-full px-3 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:outline-none transition-all">
                    </div>

                    <!-- Country Filter -->
                    <div class="form-group">
                        <label for="countryFilter" class="text-white/40 text-xs font-medium block mb-1.5">Country</label>
                        <div class="select-wrapper">
                            <select id="countryFilter" name="countryFilter" class="form-select w-full px-3 py-2.5 pr-10 bg-white/5 border border-white/10 rounded-lg text-white text-sm focus:border-dracula-red focus:outline-none transition-all appearance-none cursor-pointer">
                                <option value="" class="bg-black text-white/40">All Countries</option>
                                <c:forEach var="c" items="${countryList}">
                                    <option value="${c}" class="bg-black">${c}</option>
                                </c:forEach>
                            </select>
                            <span class="absolute right-3 top-1/2 -translate-y-1/2 text-white/20 pointer-events-none">
                                <i class="fas fa-chevron-down text-xs"></i>
                            </span>
                        </div>
                    </div>

                    <!-- Status Filter -->
                    <div class="form-group">
                        <label for="statusFilter" class="text-white/40 text-xs font-medium block mb-1.5">Status</label>
                        <div class="select-wrapper">
                            <select id="statusFilter" name="statusFilter" class="form-select w-full px-3 py-2.5 pr-10 bg-white/5 border border-white/10 rounded-lg text-white text-sm focus:border-dracula-red focus:outline-none transition-all appearance-none cursor-pointer">
                                <option value="" class="bg-black text-white/40">All Status</option>
                                <option value="ACTIVE" class="bg-black">Active</option>
                                <option value="SUSPENDED" class="bg-black">Suspended</option>
                            </select>
                            <span class="absolute right-3 top-1/2 -translate-y-1/2 text-white/20 pointer-events-none">
                                <i class="fas fa-chevron-down text-xs"></i>
                            </span>
                        </div>
                    </div>

                    <!-- Rating Filter -->
                    <div class="form-group">
                        <label for="ratingFilter" class="text-white/40 text-xs font-medium block mb-1.5">Min Rating</label>
                        <div class="select-wrapper">
                            <select id="ratingFilter" name="ratingFilter" class="form-select w-full px-3 py-2.5 pr-10 bg-white/5 border border-white/10 rounded-lg text-white text-sm focus:border-dracula-red focus:outline-none transition-all appearance-none cursor-pointer">
                                <option value="" class="bg-black text-white/40">Any Rating</option>
                                <option value="4.5" class="bg-black">4.5 ★ & above</option>
                                <option value="4.0" class="bg-black">4.0 ★ & above</option>
                                <option value="3.5" class="bg-black">3.5 ★ & above</option>
                                <option value="3.0" class="bg-black">3.0 ★ & above</option>
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

            <!-- ===== VENDORS TABLE ===== -->
            <div class="dashboard-card overflow-hidden">
                <div class="overflow-x-auto">
                    <table class="transaction-table">
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>Vendor Name</th>
                            <th>Contact Email</th>
                            <th>Phone</th>
                            <th>Country</th>
                            <th>Rating</th>
                            <th>Status</th>
                            <th>Created</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>

                            <c:when test="${not empty vendors}">
                                <c:forEach var="v" items="${vendors}" varStatus="status">
                                    <tr>
                                        <td class="text-white/40 text-xs">${status.index + 1}</td>
                                        <td>
                                            <div class="text-white/70 text-sm font-medium">${v.vendorName}</div>
                                        </td>
                                        <td class="text-white/50 text-xs">${v.contactEmail}</td>
                                        <td class="text-white/40 text-xs">${v.contactPhone != null ? v.contactPhone : 'N/A'}</td>
                                        <td class="text-white/50 text-xs">
                                            <span class="px-2 py-0.5 bg-white/5 rounded-full text-[10px]">${v.country}</span>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${v.performanceRating != null && v.performanceRating >= 4.0}">
                                                    <span class="text-green-400 font-medium text-xs">${v.performanceRating} ★</span>
                                                </c:when>
                                                <c:when test="${v.performanceRating != null && v.performanceRating >= 3.0}">
                                                    <span class="text-yellow-400 font-medium text-xs">${v.performanceRating} ★</span>
                                                </c:when>
                                                <c:when test="${v.performanceRating != null}">
                                                    <span class="text-red-400 font-medium text-xs">${v.performanceRating} ★</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-white/20 text-xs">N/A</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${v.status == 'ACTIVE'}">
                                                    <span class="transaction-status completed">Active</span>
                                                </c:when>
                                                <c:when test="${v.status == 'SUSPENDED'}">
                                                    <span class="transaction-status failed">Suspended</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="transaction-status pending">${v.status}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-white/30 text-xs">
                                                ${v.createdAt.toString().substring(0, 10)}
                                        </td>
                                        <td>
                                            <div class="flex items-center gap-2">
                                                <a href="${pageContext.request.contextPath}/vendors/details?vendorId=${v.vendorId}"
                                                   class="text-blue-400/60 hover:text-blue-400 text-xs transition-all" title="View Details">
                                                    <i class="fas fa-eye"></i>
                                                </a>
                                                <a href="${pageContext.request.contextPath}/vendors/edit?vendorId=${v.vendorId}"
                                                   class="text-white/30 hover:text-white text-xs transition-all" title="Edit Vendor">
                                                    <i class="fas fa-edit"></i>
                                                </a>
                                                <button onclick="showDeleteModal(${v.vendorId}, '${v.vendorName}', '${pageContext.request.contextPath}')"
                                                        class="text-white/20 hover:text-red-400 text-xs transition-all" title="Delete">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="9" class="text-center text-white/20 py-8">
                                        <div class="flex flex-col items-center gap-2">
                                            <i class="fas fa-building text-3xl text-white/10"></i>
                                            <span class="text-sm">No vendors found</span>
                                            <c:if test="${sessionScope.role == 'LOGISTICS_COORDINATOR'}">
                                                <a href="${pageContext.request.contextPath}/vendors/create" class="text-dracula-red text-xs hover:text-red-400 transition-all mt-2">
                                                    <i class="fas fa-plus mr-1"></i>Add your first vendor
                                                </a>
                                            </c:if>
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
                    <div class="text-white/20 text-xs table-footer-count">
                        Showing ${fn:length(vendors)} vendors
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
    <div id="deleteModal" class="delete-modal-overlay hidden">
        <div class="delete-modal-content">
            <div class="delete-modal-header">
                <h3>Confirm Delete</h3>
                <button class="close-btn" onclick="closeDeleteModal()">
                    <i class="fas fa-times"></i>
                </button>
            </div>
            <p class="delete-modal-subtitle">This action cannot be undone</p>

            <div class="delete-icon-wrapper">
                <i class="fas fa-trash-alt"></i>
            </div>

            <p class="delete-modal-message" id="deleteMessage">
                Are you sure you want to delete this vendor?
            </p>

            <div class="delete-modal-warning">
                <i class="fas fa-exclamation-triangle"></i>
                <span>All vendor data will be permanently removed.</span>
            </div>

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
<script src="${pageContext.request.contextPath}/js/vendors.js"></script>

</body>
</html>