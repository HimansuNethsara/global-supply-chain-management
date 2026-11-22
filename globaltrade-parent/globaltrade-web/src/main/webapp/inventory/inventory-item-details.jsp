<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 8/22/2026
  Time: 12:00 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<%--@elvariable id="item" type=""--%>
<!DOCTYPE html>
<html lang="en" data-theme="dracula">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GlobalTrade Logistics - Item Details</title>

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
                <a href="${pageContext.request.contextPath}/inventory"  class="text-white/30 hover:text-white/60 transition-all">
                    <i class="fas fa-arrow-left"></i>
                </a>
                <div>
                    <h1 class="text-xl sm:text-2xl font-bold text-white">Item Details</h1>
                    <p class="text-white/30 text-sm font-light">View inventory item information</p>
                </div>
            </div>

            <!-- ===== ITEM INFO ===== -->
            <div class="dashboard-card">
                <h2 class="text-white/60 text-sm font-semibold mb-4">Item Information</h2>
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                        <label class="text-white/30 text-xs block">SKU</label>
                        <div class="text-white/80 text-base font-medium">${item.sku}</div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Name</label>
                        <div class="text-white/80 text-base font-medium">${item.name}</div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Warehouse</label>
                        <div class="text-white/80 text-base font-medium">${item.warehouse.name}</div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Quantity</label>
                        <div class="text-white/80 text-base font-medium">
                            <span class="${item.quantity <= item.reorderLevel ? 'text-red-400' : 'text-white/80'}">
                                ${item.quantity}
                            </span>
                            <c:if test="${item.quantity <= item.reorderLevel}">
                                <span class="text-red-400/60 text-xs ml-2">(Low Stock)</span>
                            </c:if>
                        </div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Reorder Level</label>
                        <div class="text-white/80 text-base font-medium">${item.reorderLevel}</div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Unit Price</label>
                        <div class="text-white/80 text-base font-medium">LKR ${item.unitPrice}</div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Last Updated</label>
                        <div class="text-white/80 text-base font-medium">
                            ${item.lastUpdated != null ? item.lastUpdated.toString().replace('T', ' ').substring(0, 16) : 'N/A'}
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </main>

    <!-- ===== FOOTER ===== -->
    <jsp:include page="/WEB-INF/views/footer.jsp" />
</div>

<!-- Custom JavaScript -->
<script src="${pageContext.request.contextPath}/js/inventory.js"></script>
</body>
</html>