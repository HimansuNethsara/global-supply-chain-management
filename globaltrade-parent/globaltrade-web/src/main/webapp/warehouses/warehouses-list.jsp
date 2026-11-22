<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 8/22/2026
  Time: 12:00 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%--@elvariable id="warehouses" type=""--%>
<%--@elvariable id="countryList" type="java.util.List"--%>
<!DOCTYPE html>
<html lang="en" data-theme="dracula">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GlobalTrade Logistics - Warehouses</title>

    <!-- Favicon -->
    <link rel="icon" type="image/png" sizes="32x32" href="${pageContext.request.contextPath}/images/favicon.png">

    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&display=swap" rel="stylesheet">

    <!-- Font Awesome -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/all.min.css">

    <!-- Tailwind CSS Output -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/output.css">

    <style>
        @media print {
            body * {
                visibility: hidden !important;
            }
            .main-content, .main-content * {
                visibility: visible !important;
            }
            .main-content {
                position: absolute !important;
                left: 0 !important;
                top: 0 !important;
                width: 100% !important;
                background: white !important;
                padding: 20px !important;
            }
            /* Darker text colors for better readability */
            .main-content .text-white { color: #000000 !important; }
            .main-content .text-white\/70 { color: #222222 !important; }
            .main-content .text-white\/50 { color: #444444 !important; }
            .main-content .text-white\/30 { color: #555555 !important; }
            .main-content .text-white\/20 { color: #666666 !important; }
            .main-content .text-white\/40 { color: #333333 !important; }
            .main-content .text-white\/60 { color: #333333 !important; }
            /* Background colors */
            .main-content .bg-white\/5 { background: #f0f0f0 !important; }
            .main-content .border-white\/5 { border-color: #cccccc !important; }
            .dashboard-card {
                background: white !important;
                border: 1px solid #999999 !important;
                box-shadow: none !important;
            }
            .dashboard-header, .dashboard-footer, #sidebar, .sidebar-overlay,
            .fixed, .glow-orb, .dashboard-footer {
                display: none !important;
            }
            .main-content-wrapper { margin-left: 0 !important; }
            /* Hide action column in print */
            td:last-child, th:last-child { display: none !important; }
            /* Table styling */
            .transaction-table th, .transaction-table td {
                border: 1px solid #999999 !important;
                padding: 8px !important;
                color: #000000 !important;
            }
            .transaction-table {
                border-collapse: collapse !important;
                width: 100% !important;
            }
            .transaction-table th {
                background: #e0e0e0 !important;
                color: #000000 !important;
                font-weight: bold !important;
            }
            .transaction-table td {
                background: white !important;
                color: #000000 !important;
            }
            .transaction-table .text-white\/30 { color: #555555 !important; }
            .transaction-table .text-white\/20 { color: #666666 !important; }
            .transaction-table .text-white\/40 { color: #444444 !important; }
            .transaction-table .text-white\/50 { color: #333333 !important; }
            .transaction-table .text-white\/60 { color: #222222 !important; }
            .transaction-table .text-white\/70 { color: #111111 !important; }
            /* Links */
            .main-content a { color: #0066cc !important; }
            /* Font weight for labels */
            .main-content .font-medium { font-weight: 600 !important; }
            /* Capacity text */
            .main-content .text-white\/60 { color: #333333 !important; }
        }
    </style>
</head>
<body class="dashboard-container font-inter">

<!-- Background Glow Orbs -->
<div class="fixed inset-0 z-0 pointer-events-none overflow-hidden">
    <div class="glow-orb w-[600px] h-[600px] -top-[200px] -right-[200px] bg-red-500/5"></div>
    <div class="glow-orb w-[400px] h-[400px] -bottom-[100px] -left-[100px] bg-red-900/5"></div>
    <div class="glow-orb w-[300px] h-[300px] top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-red-500/3"></div>
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
                    <h1 class="text-xl sm:text-2xl font-bold text-white">Warehouses</h1>
                    <p class="text-white/30 text-sm font-light">Manage your warehouse facilities</p>
                </div>
                <div class="flex items-center gap-2">
                    <c:if test="${sessionScope.role == 'LOGISTICS_COORDINATOR' || sessionScope.role == 'WAREHOUSE_MANAGER'}">
                        <button onclick="window.location.href='${pageContext.request.contextPath}/warehouses/create'" class="px-4 py-2 bg-gradient-to-r from-dracula-red to-red-900 rounded-lg text-white text-xs font-semibold hover:shadow-lg hover:shadow-red-500/25 transition-all flex items-center gap-2">
                            <i class="fas fa-plus"></i>
                            <span>Add Warehouse</span>
                        </button>
                    </c:if>
                    <button onclick="window.print()" class="px-4 py-2 bg-white/5 border border-white/5 rounded-lg text-white/50 hover:text-white hover:bg-white/10 transition-all text-xs flex items-center gap-2">
                        <i class="fas fa-file-export"></i>
                        <span>Export</span>
                    </button>
                </div>
            </div>

            <!-- ===== FILTER SECTION ===== -->
            <div class="dashboard-card mb-6">
                <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                    <div class="form-group">
                        <label for="searchInput" class="text-white/40 text-xs font-medium block mb-1.5">Search</label>
                        <input type="text" id="searchInput" placeholder="Search warehouses..."
                               class="form-control w-full px-3 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:outline-none transition-all">
                    </div>
                    <div class="form-group">
                        <label for="countryFilter" class="text-white/40 text-xs font-medium block mb-1.5">Country</label>
                        <div class="select-wrapper">
                            <select id="countryFilter" class="form-select w-full px-3 py-2.5 pr-10 bg-white/5 border border-white/10 rounded-lg text-white text-sm focus:border-dracula-red focus:outline-none transition-all appearance-none cursor-pointer">
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
                    <div class="form-group flex items-end">
                        <button id="applyFiltersBtn" class="w-full px-4 py-2.5 bg-dracula-red/20 border border-dracula-red/30 rounded-lg text-dracula-red text-sm font-medium hover:bg-dracula-red/30 transition-all">
                            <i class="fas fa-filter mr-1.5"></i>Apply Filters
                        </button>
                    </div>
                </div>
            </div>

            <!-- ===== WAREHOUSES TABLE ===== -->
            <div class="dashboard-card overflow-hidden">
                <div class="overflow-x-auto">
                    <table class="transaction-table">
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>Name</th>
                            <th>Location</th>
                            <th>Country</th>
                            <th>Capacity</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${not empty warehouses}">
                                <c:forEach var="w" items="${warehouses}" varStatus="status">
                                    <tr>
                                        <td class="text-white/40 text-xs">${status.index + 1}</td>
                                        <td>
                                            <div class="text-white/70 text-sm font-medium">${w.name}</div>
                                        </td>
                                        <td class="text-white/50 text-xs">${w.location}</td>
                                        <td class="text-white/50 text-xs">
                                            <span class="px-2 py-0.5 bg-white/5 rounded-full text-[10px]">${w.country}</span>
                                        </td>
                                        <td class="text-white/60 text-xs">
                                            <span class="font-medium">${w.capacity}</span> <span class="text-white/30">sq ft</span>
                                        </td>
                                        <td>
                                            <div class="flex items-center gap-2">
                                                <a href="${pageContext.request.contextPath}/warehouses/details?warehouseId=${w.warehouseId}"
                                                   class="text-blue-400/60 hover:text-blue-400 text-xs transition-all" title="View Details">
                                                    <i class="fas fa-eye"></i>
                                                </a>
                                                <c:if test="${sessionScope.role == 'LOGISTICS_COORDINATOR' || sessionScope.role == 'WAREHOUSE_MANAGER'}">
                                                    <button class="text-white/20 hover:text-red-400 text-xs transition-all" title="Delete" onclick="showDeleteModal(${w.warehouseId}, '${w.name}', '${pageContext.request.contextPath}', 'warehouse')">
                                                        <i class="fas fa-trash"></i>
                                                    </button>
                                                </c:if>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="6" class="text-center text-white/20 py-8">
                                        <div class="flex flex-col items-center gap-2">
                                            <i class="fas fa-warehouse text-3xl text-white/10"></i>
                                            <span class="text-sm">No warehouses found</span>
                                        </div>
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                        </tbody>
                    </table>
                </div>
                <div class="flex flex-wrap items-center justify-between gap-3 mt-4 pt-4 border-t border-white/5">
                    <div class="text-white/20 text-xs">Showing ${fn:length(warehouses)} warehouses</div>
                    <div class="flex items-center gap-1">
                        <button class="px-3 py-1 bg-white/5 border border-white/10 rounded-lg text-white/30 text-xs hover:bg-white/10 transition-all" disabled>Previous</button>
                        <button class="px-3 py-1 bg-dracula-red/20 border border-dracula-red/30 rounded-lg text-dracula-red text-xs hover:bg-dracula-red/30 transition-all">1</button>
                        <button class="px-3 py-1 bg-white/5 border border-white/10 rounded-lg text-white/30 text-xs hover:bg-white/10 transition-all" disabled>Next</button>
                    </div>
                </div>
            </div>

        </div>
    </main>

    <!-- ===== DELETE CONFIRMATION MODAL ===== -->
    <jsp:include page="/WEB-INF/views/components/delete-modal.jsp" />

    <!-- ===== FOOTER ===== -->
    <jsp:include page="/WEB-INF/views/footer.jsp" />
</div>

<!-- Custom JavaScript -->
<script src="${pageContext.request.contextPath}/js/delete-modal.js"></script>
<script src="${pageContext.request.contextPath}/js/warehouses.js"></script>
</body>
</html>