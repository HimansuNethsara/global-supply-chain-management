<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 8/22/2026
  Time: 12:00 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%--@elvariable id="warehouses" type="java.util.List"--%>
<!DOCTYPE html>
<html lang="en" data-theme="dracula">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GlobalTrade Logistics - Create Item</title>

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
        <div class="p-4 sm:p-6 lg:p-8 max-w-3xl mx-auto">

            <!-- ===== PAGE HEADER ===== -->
            <div class="flex items-center gap-3 mb-6">
                <a href="${pageContext.request.contextPath}/inventory"  class="text-white/30 hover:text-white/60 transition-all">
                    <i class="fas fa-arrow-left"></i>
                </a>
                <div>
                    <h1 class="text-xl sm:text-2xl font-bold text-white">Create Inventory Item</h1>
                    <p class="text-white/30 text-sm font-light">Add a new item to inventory</p>
                </div>
            </div>

            <!-- ===== CREATE FORM ===== -->
            <div class="dashboard-card">
                <form id="inventoryCreateForm" action="${pageContext.request.contextPath}/inventory/create" method="POST">
                    <input type="hidden" name="csrfToken" value="placeholder_token">

                    <!-- Row 1: Name + SKU -->
                    <div class="form-row">
                        <div class="form-group">
                            <label for="name" class="text-white/40 text-xs font-medium block mb-1.5">Item Name <span class="text-dracula-red">*</span></label>
                            <input type="text" id="name" name="name" placeholder="Enter item name"
                                   class="form-control w-full px-3 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:outline-none transition-all" required>
                        </div>
                        <div class="form-group">
                            <label for="sku" class="text-white/40 text-xs font-medium block mb-1.5">SKU <span class="text-dracula-red">*</span></label>
                            <input type="text" id="sku" name="sku" placeholder="Enter SKU"
                                   class="form-control w-full px-3 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:outline-none transition-all" required>
                        </div>
                    </div>

                    <!-- Row 2: Warehouse + Quantity -->
                    <div class="form-row">
                        <div class="form-group">
                            <label for="warehouseId" class="text-white/40 text-xs font-medium block mb-1.5">Warehouse <span class="text-dracula-red">*</span></label>
                            <div class="select-wrapper">
                                <select id="warehouseId" name="warehouseId" class="form-select w-full px-3 py-2.5 pr-10 bg-white/5 border border-white/10 rounded-lg text-white text-sm focus:border-dracula-red focus:outline-none transition-all appearance-none cursor-pointer" required>
                                    <option value="" class="bg-black text-white/40" disabled selected>Select Warehouse</option>
                                    <c:forEach var="w" items="${warehouses}">
                                        <option value="${w.warehouseId}" class="bg-black">${w.name} - ${w.location}</option>
                                    </c:forEach>
                                </select>
                                <span class="absolute right-3 top-1/2 -translate-y-1/2 text-white/20 pointer-events-none">
                                    <i class="fas fa-chevron-down text-xs"></i>
                                </span>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="quantity" class="text-white/40 text-xs font-medium block mb-1.5">Quantity <span class="text-dracula-red">*</span></label>
                            <input type="number" id="quantity" name="quantity" placeholder="0"
                                   class="form-control w-full px-3 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:outline-none transition-all" required min="0">
                        </div>
                    </div>

                    <!-- Row 3: Reorder Level + Unit Price -->
                    <div class="form-row">
                        <div class="form-group">
                            <label for="reorderLevel" class="text-white/40 text-xs font-medium block mb-1.5">Reorder Level</label>
                            <input type="number" id="reorderLevel" name="reorderLevel" placeholder="10"
                                   class="form-control w-full px-3 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:outline-none transition-all" value="10">
                        </div>
                        <div class="form-group">
                            <label for="unitPrice" class="text-white/40 text-xs font-medium block mb-1.5">Unit Price <span class="text-dracula-red">*</span></label>
                            <div class="relative">
                                <span class="absolute left-3 top-1/2 -translate-y-1/2 text-white/30 text-xs z-10 pointer-events-none select-none">LKR</span>
                                <input type="text" id="unitPrice" name="unitPrice" placeholder="0.00"
                                       style="padding-left: 48px !important; padding-right: 12px;"
                                       class="form-control w-full bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:outline-none transition-all"
                                       required>
                            </div>
                        </div>
                    </div>

                    <!-- Buttons -->
                    <div class="flex items-center gap-3 pt-3 border-t border-white/5 mt-4">
                        <button type="submit" id="submitBtn" class="px-6 py-2.5 bg-gradient-to-r from-dracula-red to-red-900 rounded-lg text-white text-sm font-semibold hover:shadow-lg hover:shadow-red-500/25 transition-all">
                            <i class="fas fa-plus mr-1.5"></i>Create Item
                        </button>
                        <button type="reset" id="resetBtn" class="px-6 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white/40 text-sm hover:bg-white/10 hover:text-white/60 transition-all">
                            <i class="fas fa-undo mr-1.5"></i>Reset
                        </button>
                        <a href="${pageContext.request.contextPath}/inventory"  class="px-6 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white/30 text-sm hover:bg-white/10 hover:text-white/50 transition-all">
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
<script src="${pageContext.request.contextPath}/js/inventory.js"></script>
</body>
</html>