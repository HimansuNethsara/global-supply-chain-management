<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 8/19/2026
  Time: 12:00 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="en" data-theme="dracula">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GlobalTrade Logistics - Customer Details</title>

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
                <a href="${pageContext.request.contextPath}/customers" class="text-white/30 hover:text-white/60 transition-all">
                    <i class="fas fa-arrow-left"></i>
                </a>
                <div>
                    <h1 class="text-xl sm:text-2xl font-bold text-white">Customer Details</h1>
                    <p class="text-white/30 text-sm font-light">View customer information</p>
                </div>
            </div>

            <!-- ===== CUSTOMER INFO ===== -->
            <div class="dashboard-card mb-6">
                <h2 class="text-white/60 text-sm font-semibold mb-4">Customer Information</h2>

                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                        <label class="text-white/30 text-xs block">Full Name</label>
                        <div class="text-white/80 text-base font-medium">${customer.user.fullName}</div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Username</label>
                        <div class="text-white/80 text-base font-medium">${customer.user.username}</div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Email</label>
                        <div class="text-white/80 text-base font-medium">${customer.user.email}</div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Company Name</label>
                        <div class="text-white/80 text-base font-medium">${customer.companyName != null ? customer.companyName : 'N/A'}</div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Contact Phone</label>
                        <div class="text-white/80 text-base font-medium">${customer.contactPhone != null ? customer.contactPhone : 'N/A'}</div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Country</label>
                        <div class="text-white/80 text-base font-medium">
                            <span class="px-3 py-1 bg-white/5 rounded-full text-sm">${customer.country}</span>
                        </div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Created</label>
                        <div class="text-white/80 text-base font-medium">
                            ${customer.createdAt != null ? customer.createdAt.toString().replace('T', ' ').substring(0, 16) : 'N/A'}
                        </div>
                    </div>
                </div>
            </div>

            <!-- ===== SHIPMENTS ===== -->
            <div class="dashboard-card">
                <div class="flex items-center justify-between mb-4">
                    <h3 class="text-white/60 text-sm font-semibold flex items-center gap-2">
                        <i class="fas fa-truck text-dracula-red"></i>
                        Shipments
                    </h3>
                    <a href="#" class="text-dracula-red text-xs hover:text-red-400 transition-all">View All →</a>
                </div>

                <div class="overflow-x-auto">
                    <table class="transaction-table">
                        <thead>
                        <tr>
                            <th>Tracking #</th>
                            <th>Origin</th>
                            <th>Destination</th>
                            <th>Status</th>
                            <th>Date</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${not empty shipments}">
                                <c:forEach var="s" items="${shipments}">
                                    <tr>
                                        <td class="text-white/60 text-xs font-medium">${s.trackingNumber}</td>
                                        <td class="text-white/40 text-xs">${s.origin}</td>
                                        <td class="text-white/40 text-xs">${s.destination}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${s.status == 'DELIVERED'}">
                                                    <span class="transaction-status completed">Delivered</span>
                                                </c:when>
                                                <c:when test="${s.status == 'IN_TRANSIT'}">
                                                    <span class="transaction-status pending">In Transit</span>
                                                </c:when>
                                                <c:when test="${s.status == 'DELAYED'}">
                                                    <span class="transaction-status failed">Delayed</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="transaction-status pending">${s.status}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-white/30 text-xs">
                                            <fmt:formatDate value="${s.createdAt}" pattern="yyyy-MM-dd" />
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="5" class="text-center text-white/20 py-4">
                                        <span class="text-xs">No shipments found for this customer</span>
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>

        </div>
    </main>

    <!-- ===== FOOTER ===== -->
    <jsp:include page="/WEB-INF/views/footer.jsp" />
</div>

<!-- Custom JavaScript -->
<script src="${pageContext.request.contextPath}/js/customers.js"></script>
</body>
</html>