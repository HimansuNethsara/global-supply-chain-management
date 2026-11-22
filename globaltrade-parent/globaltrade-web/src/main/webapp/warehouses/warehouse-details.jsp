<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 8/22/2026
  Time: 12:00 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<%--@elvariable id="warehouse" type="lk.jiat.global.entity.Warehouse"--%>
<%--@elvariable id="inventoryItems" type="java.util.List"--%>
<!DOCTYPE html>
<html lang="en" data-theme="dracula">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GlobalTrade Logistics - Warehouse Details</title>

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
                <a href="${pageContext.request.contextPath}/warehouses" class="text-white/30 hover:text-white/60 transition-all">
                    <i class="fas fa-arrow-left"></i>
                </a>
                <div>
                    <h1 class="text-xl sm:text-2xl font-bold text-white">Warehouse Details</h1>
                    <p class="text-white/30 text-sm font-light">View warehouse information</p>
                </div>
            </div>

            <!-- ===== WAREHOUSE INFO ===== -->
            <div class="dashboard-card mb-6">
                <h2 class="text-white/60 text-sm font-semibold mb-4">Warehouse Information</h2>
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                        <label class="text-white/30 text-xs block">Name</label>
                        <div class="text-white/80 text-base font-medium">${warehouse.name}</div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Location</label>
                        <div class="text-white/80 text-base font-medium">${warehouse.location}</div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Country</label>
                        <div class="text-white/80 text-base font-medium">
                            <span class="px-3 py-1 bg-white/5 rounded-full text-sm">${warehouse.country}</span>
                        </div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Capacity</label>
                        <div class="text-white/80 text-base font-medium">${warehouse.capacity} sq ft</div>
                    </div>
                </div>
            </div>

            <!-- ===== INVENTORY ITEMS ===== -->
            <div class="dashboard-card">
                <div class="flex items-center justify-between mb-4">
                    <h3 class="text-white/60 text-sm font-semibold flex items-center gap-2">
                        <i class="fas fa-boxes text-dracula-red"></i>
                        Inventory Items
                    </h3>
                    <a href="${pageContext.request.contextPath}/inventory" class="text-dracula-red text-xs hover:text-red-400 transition-all">View All →</a>
                </div>
                <div class="overflow-x-auto">
                    <table class="transaction-table">
                        <thead>
                        <tr>
                            <th>SKU</th>
                            <th>Name</th>
                            <th>Quantity</th>
                            <th>Unit Price</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${not empty inventoryItems}">
                                <c:forEach var="item" items="${inventoryItems}">
                                    <tr>
                                        <td class="text-white/60 text-xs font-medium">${item.sku}</td>
                                        <td class="text-white/50 text-xs">${item.name}</td>
                                        <td class="text-white/60 text-xs">
                                            <span class="${item.quantity <= item.reorderLevel ? 'text-red-400' : 'text-white/60'}">
                                                    ${item.quantity}
                                            </span>
                                            <c:if test="${item.quantity <= item.reorderLevel}">
                                                <span class="text-red-400/60 text-[10px] ml-1">(Low)</span>
                                            </c:if>
                                        </td>
                                        <td class="text-white/60 text-xs">LKR ${item.unitPrice}</td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="4" class="text-center text-white/20 py-4">
                                        <div class="flex flex-col items-center gap-1">
                                            <i class="fas fa-box-open text-2xl text-white/10"></i>
                                            <span class="text-xs">No inventory items in this warehouse</span>
                                        </div>
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
<script src="${pageContext.request.contextPath}/js/warehouses.js"></script>
</body>
</html>