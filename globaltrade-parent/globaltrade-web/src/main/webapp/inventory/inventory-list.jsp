<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 8/22/2026
  Time: 12:00 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%--@elvariable id="warehouses" type="java.util.List"--%>
<%--@elvariable id="inventoryItems" type=""--%>
<!DOCTYPE html>
<html lang="en" data-theme="dracula">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GlobalTrade Logistics - Inventory</title>

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
                    <h1 class="text-xl sm:text-2xl font-bold text-white">Inventory</h1>
                    <p class="text-white/30 text-sm font-light">Manage your inventory items</p>
                </div>
                <div class="flex items-center gap-2">
                    <button onclick="window.location.href='${pageContext.request.contextPath}/inventory/create'" class="px-4 py-2 bg-gradient-to-r from-dracula-red to-red-900 rounded-lg text-white text-xs font-semibold hover:shadow-lg hover:shadow-red-500/25 transition-all flex items-center gap-2">
                        <i class="fas fa-plus"></i>
                        <span>Add Item</span>
                    </button>
                    <button onclick="window.print()" class="px-4 py-2 bg-white/5 border border-white/5 rounded-lg text-white/50 hover:text-white hover:bg-white/10 transition-all text-xs flex items-center gap-2">
                        <i class="fas fa-file-export"></i>
                        <span>Export</span>
                    </button>
                </div>
            </div>

            <!-- ===== FILTER SECTION ===== -->
            <div class="dashboard-card mb-6">
                <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                    <div class="form-group">
                        <label for="searchInput" class="text-white/40 text-xs font-medium block mb-1.5">Search</label>
                        <input type="text" id="searchInput" placeholder="Search items..."
                               class="form-control w-full px-3 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:outline-none transition-all">
                    </div>
                    <div class="form-group">
                        <label for="warehouseFilter" class="text-white/40 text-xs font-medium block mb-1.5">Warehouse</label>
                        <div class="select-wrapper">
                            <select id="warehouseFilter" class="form-select w-full px-3 py-2.5 pr-10 bg-white/5 border border-white/10 rounded-lg text-white text-sm focus:border-dracula-red focus:outline-none transition-all appearance-none cursor-pointer">
                                <option value="" class="bg-black text-white/40">All Warehouses</option>
                                <c:forEach var="w" items="${warehouses}">
                                    <option value="${w.warehouseId}" class="bg-black">${w.name}</option>
                                </c:forEach>
                            </select>
                            <span class="absolute right-3 top-1/2 -translate-y-1/2 text-white/20 pointer-events-none">
                                <i class="fas fa-chevron-down text-xs"></i>
                            </span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="statusFilter" class="text-white/40 text-xs font-medium block mb-1.5">Stock Status</label>
                        <div class="select-wrapper">
                            <select id="statusFilter" class="form-select w-full px-3 py-2.5 pr-10 bg-white/5 border border-white/10 rounded-lg text-white text-sm focus:border-dracula-red focus:outline-none transition-all appearance-none cursor-pointer">
                                <option value="" class="bg-black text-white/40">All</option>
                                <option value="low" class="bg-black">Low Stock</option>
                                <option value="normal" class="bg-black">Normal</option>
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

            <!-- ===== INVENTORY TABLE ===== -->
            <div class="dashboard-card overflow-hidden">
                <div class="overflow-x-auto">
                    <table class="transaction-table">
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>SKU</th>
                            <th>Name</th>
                            <th>Warehouse</th>
                            <th>Quantity</th>
                            <th>Reorder Level</th>
                            <th>Unit Price</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${not empty inventoryItems}">
                                <c:forEach var="item" items="${inventoryItems}" varStatus="status">
                                    <tr>
                                        <td class="text-white/40 text-xs">${status.index + 1}</td>
                                        <td class="text-white/60 text-xs font-medium">${item.sku}</td>
                                        <td>
                                            <div class="text-white/70 text-sm font-medium">${item.name}</div>
                                        </td>
                                        <td class="text-white/50 text-xs" data-warehouse-id="${item.warehouse.warehouseId}">${item.warehouse.name}</td>
                                        <td class="text-white/60 text-xs">
                                                <span class="${item.quantity <= item.reorderLevel ? 'text-red-400 font-medium' : 'text-white/60'}">
                                                        ${item.quantity}
                                                </span>
                                            <c:if test="${item.quantity <= item.reorderLevel}">
                                                <span class="text-red-400/60 text-[10px] ml-1">(Low)</span>
                                            </c:if>
                                        </td>
                                        <td class="text-white/40 text-xs">${item.reorderLevel}</td>
                                        <td class="text-white/60 text-xs">LKR ${item.unitPrice}</td>
                                        <td>
                                            <div class="flex items-center gap-2">
                                                <!-- View Details -->
                                                <a href="${pageContext.request.contextPath}/inventory/details?itemId=${item.itemId}"
                                                   class="text-blue-400/60 hover:text-blue-400 text-xs transition-all" title="View Details">
                                                    <i class="fas fa-eye"></i>
                                                </a>
                                                <!-- Update Quantity -->
                                                <button class="text-yellow-400/60 hover:text-yellow-400 text-xs transition-all"
                                                        title="Update Quantity"
                                                        onclick="showUpdateQuantityModal(
                                                            ${item.itemId},
                                                                '${item.name}',
                                                                '${item.sku}',
                                                                '${item.warehouse.name}',
                                                            ${item.quantity},
                                                            ${item.reorderLevel},
                                                            ${item.unitPrice}
                                                                )">
                                                    <i class="fas fa-pen"></i>
                                                </button>
                                                <!-- Delete -->
                                                <button class="text-white/20 hover:text-red-400 text-xs transition-all"
                                                        title="Delete"
                                                        onclick="showDeleteModal(${item.itemId}, '${item.name}', '${pageContext.request.contextPath}', 'inventory')">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="8" class="text-center text-white/20 py-8">
                                        <div class="flex flex-col items-center gap-2">
                                            <i class="fas fa-boxes text-3xl text-white/10"></i>
                                            <span class="text-sm">No inventory items found</span>
                                        </div>
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                        </tbody>
                    </table>
                </div>
                <div class="flex flex-wrap items-center justify-between gap-3 mt-4 pt-4 border-t border-white/5">
                    <div class="text-white/20 text-xs">Showing ${fn:length(inventoryItems)} items</div>
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

    <!-- ===== UPDATE QUANTITY MODAL ===== -->
    <jsp:include page="/WEB-INF/views/components/update-quantity-modal.jsp" />

    <!-- ===== FOOTER ===== -->
    <jsp:include page="/WEB-INF/views/footer.jsp" />
</div>

<!-- Custom JavaScript -->
<script src="${pageContext.request.contextPath}/js/inventory.js"></script>
<script src="${pageContext.request.contextPath}/js/delete-modal.js"></script>
</body>
</html>