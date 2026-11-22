<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 8/25/2026
  Time: 12:00 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en" data-theme="dracula">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GlobalTrade Logistics - My Orders</title>

    <!-- Favicon -->
    <link rel="icon" type="image/png" sizes="32x32" href="${pageContext.request.contextPath}/images/favicon.png">

    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&display=swap" rel="stylesheet">

    <!-- Font Awesome -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/all.min.css">

    <!-- Tailwind CSS Output -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/output.css">
</head>

<!-- ===== SESSION NOTIFICATIONS ===== -->
<c:if test="${not empty sessionScope.errorNotification}">
    <input type="hidden" id="sessionErrorMsg" value="${sessionScope.errorNotification}">
    <c:remove var="errorNotification" scope="session" />
</c:if>

<c:if test="${not empty sessionScope.successNotification}">
    <input type="hidden" id="sessionSuccessMsg" value="${sessionScope.successNotification}">
    <c:remove var="successNotification" scope="session" />
</c:if>

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
                    <h1 class="text-xl sm:text-2xl font-bold text-white">My Orders</h1>
                    <p class="text-white/30 text-sm font-light">View and manage your orders</p>
                </div>
                <div class="flex items-center gap-2">
                    <a href="${pageContext.request.contextPath}/customers/orders/create" class="px-4 py-2 bg-gradient-to-r from-dracula-red to-red-900 rounded-lg text-white text-xs font-semibold hover:shadow-lg hover:shadow-red-500/25 transition-all flex items-center gap-2">
                        <i class="fas fa-plus"></i>
                        <span>Create Order</span>
                    </a>
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
                        <input type="text" id="searchInput" placeholder="Search orders..."
                               class="form-control w-full px-3 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:outline-none transition-all">
                    </div>
                    <div class="form-group">
                        <label for="statusFilter" class="text-white/40 text-xs font-medium block mb-1.5">Status</label>
                        <div class="select-wrapper">
                            <select id="statusFilter" class="form-select w-full px-3 py-2.5 pr-10 bg-white/5 border border-white/10 rounded-lg text-white text-sm focus:border-dracula-red focus:outline-none transition-all appearance-none cursor-pointer">
                                <option value="" class="bg-black text-white/40">All Status</option>
                                <option value="PENDING" class="bg-black">Pending</option>
                                <option value="ASSIGNED" class="bg-black">Assigned</option>
                                <option value="FULFILLED" class="bg-black">Fulfilled</option>
                                <option value="CANCELLED" class="bg-black">Cancelled</option>
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

            <!-- ===== ORDERS TABLE ===== -->
            <div class="dashboard-card overflow-hidden">
                <div class="overflow-x-auto">
                    <table class="transaction-table">
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>Order ID</th>
                            <th>Destination</th>
                            <th>Status</th>
                            <th>Items</th>
                            <th>Requested Date</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${not empty orders}">
                                <c:forEach var="o" items="${orders}" varStatus="status">
                                    <tr>
                                        <td class="text-white/40 text-xs">${status.index + 1}</td>
                                        <td class="text-white/60 text-xs font-medium">#${o.orderId}</td>
                                        <td class="text-white/50 text-xs">${o.destination}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${o.status == 'FULFILLED'}">
                                                    <span class="transaction-status completed">Fulfilled</span>
                                                </c:when>
                                                <c:when test="${o.status == 'ASSIGNED'}">
                                                    <span class="transaction-status pending">Assigned</span>
                                                </c:when>
                                                <c:when test="${o.status == 'CANCELLED'}">
                                                    <span class="transaction-status failed">Cancelled</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="transaction-status pending">${o.status}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-white/40 text-xs">${o.itemCount}</td>
                                        <td class="text-white/30 text-xs">
                                                ${o.requestedDate != null ? o.requestedDate.toString().replace('T', ' ').substring(0, 16) : 'N/A'}
                                        </td>
                                        <td>
                                            <div class="flex items-center gap-2">
                                                <a href="${pageContext.request.contextPath}/customers/orders/details?orderId=${o.orderId}"
                                                   class="text-blue-400/60 hover:text-blue-400 text-xs transition-all" title="View Details">
                                                    <i class="fas fa-eye"></i>
                                                </a>
                                                <c:if test="${o.status == 'PENDING'}">
                                                    <button class="text-yellow-400/60 hover:text-yellow-400 text-xs transition-all" title="Cancel Order"
                                                            onclick="confirmCancelOrder(${o.orderId}, '${o.destination}')">
                                                        <i class="fas fa-times"></i>
                                                    </button>
                                                </c:if>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="7" class="text-center text-white/20 py-8">
                                        <div class="flex flex-col items-center gap-2">
                                            <i class="fas fa-shopping-cart text-3xl text-white/10"></i>
                                            <span class="text-sm">No orders found</span>
                                            <a href="${pageContext.request.contextPath}/customers/orders/create" class="text-dracula-red text-xs hover:text-red-400 transition-all mt-2">
                                                <i class="fas fa-plus mr-1"></i>Create your first order
                                            </a>
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
                    <div class="text-white/20 text-xs">Showing ${fn:length(orders)} orders</div>
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
<script src="${pageContext.request.contextPath}/js/orders.js"></script>
</body>
</html>