<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 7/31/2026
  Time: 10:24 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!-- Mobile Overlay -->
<div id="sidebarOverlay" class="sidebar-overlay" onclick="toggleSidebar()"></div>

<!-- Sidebar -->
<aside id="sidebar" class="sidebar-glass fixed top-0 left-0 z-40 w-[260px] h-screen sidebar-desktop lg:translate-x-0 flex flex-col">

    <!-- Sidebar Header with Logo (Mobile only) -->
    <div class="h-[60px] flex items-center px-4 border-b border-white/5 lg:hidden">
        <div class="flex items-center gap-2.5">
            <div class="w-8 h-8 flex items-center justify-center bg-gradient-to-br from-dracula-red to-red-900 rounded-lg text-white shadow-lg shadow-red-500/20">
                <i class="fas fa-ship text-sm"></i>
            </div>
            <div class="flex items-baseline">
                <span class="text-sm font-extrabold tracking-widest text-white uppercase">
                    GLOBAL<span class="text-dracula-red text-[8px]">®</span>
                </span>
                <span class="text-[10px] font-light text-white/30 ml-1">TRADE</span>
            </div>
        </div>
        <button id="sidebarClose" class="ml-auto text-white/40 hover:text-white transition-all p-1">
            <i class="fas fa-times"></i>
        </button>
    </div>

    <!-- Sidebar Body -->
    <div class="flex-1 px-3 py-4 overflow-y-auto sidebar-scroll">

        <!-- User Info (Mobile only) -->
        <div class="lg:hidden flex items-center gap-3 px-2 pb-4 mb-3 border-b border-white/5">
            <div class="header-user-avatar w-10 h-10">
                <span>JD</span>
            </div>
            <div>
                <div class="text-sm font-medium text-white">John Doe</div>
                <div class="text-[10px] text-white/30">Logistics Coordinator</div>
            </div>
        </div>

        <!-- ===== NAVIGATION ===== -->
        <nav class="space-y-0.5">

            <!-- Dashboard -->
            <a href="${pageContext.request.contextPath}/dashboard" class="sidebar-item" id="sidebarDashboard">
                <i class="fas fa-th-large"></i>
                <span>DASHBOARD</span>
            </a>

            <!-- ===== SHIPMENTS ===== -->
            <div>
                <div class="sidebar-item" onclick="toggleSubmenu('shipmentsSubmenu')">
                    <i class="fas fa-truck"></i>
                    <span class="flex-1">SHIPMENTS</span>
                    <i class="fas fa-chevron-right text-[10px] text-white/15 transition-transform" id="shipmentsArrow"></i>
                </div>
                <div id="shipmentsSubmenu" class="sidebar-submenu hidden space-y-0.5 mt-0.5">
                    <a href="${pageContext.request.contextPath}/shipments" class="sidebar-item" id="sidebarShipmentsList">
                        <i class="fas fa-circle"></i>SHIPMENTS LIST
                    </a>
                    <a href="${pageContext.request.contextPath}/shipments/create" class="sidebar-item" id="sidebarShipmentCreate">
                        <i class="fas fa-circle"></i>CREATE SHIPMENT
                    </a>
                </div>
            </div>

            <!-- ===== INVENTORY ===== -->
            <div>
                <div class="sidebar-item" onclick="toggleSubmenu('inventorySubmenu')">
                    <i class="fas fa-warehouse"></i>
                    <span class="flex-1">INVENTORY</span>
                    <i class="fas fa-chevron-right text-[10px] text-white/15 transition-transform" id="inventoryArrow"></i>
                </div>
                <div id="inventorySubmenu" class="sidebar-submenu hidden space-y-0.5 mt-0.5">
                    <a href="${pageContext.request.contextPath}/inventory" class="sidebar-item" id="sidebarInventoryList">
                        <i class="fas fa-circle"></i>INVENTORY LIST
                    </a>
                    <a href="${pageContext.request.contextPath}/inventory/create" class="sidebar-item" id="sidebarInventoryItemCreate">
                        <i class="fas fa-circle"></i>CREATE ITEM
                    </a>
                </div>
            </div>

            <!-- ===== VENDORS ===== -->
            <div>
                <div class="sidebar-item" onclick="toggleSubmenu('vendorsSubmenu')">
                    <i class="fas fa-building"></i>
                    <span class="flex-1">VENDORS</span>
                    <i class="fas fa-chevron-right text-[10px] text-white/15 transition-transform" id="vendorsArrow"></i>
                </div>
                <div id="vendorsSubmenu" class="sidebar-submenu hidden space-y-0.5 mt-0.5">
                    <a href="${pageContext.request.contextPath}/vendors" class="sidebar-item" id="sidebarVendorsList">
                        <i class="fas fa-circle"></i>VENDORS LIST
                    </a>
                    <a href="${pageContext.request.contextPath}/vendors/create" class="sidebar-item" id="sidebarVendorCreate">
                        <i class="fas fa-circle"></i>CREATE VENDOR
                    </a>
                    <a href="${pageContext.request.contextPath}/vendors/performance" class="sidebar-item" id="sidebarVendorPerformance">
                        <i class="fas fa-circle"></i>PERFORMANCE EVALUATION
                    </a>
                </div>
            </div>

            <!-- ===== CUSTOMERS ===== -->
            <div>
                <div class="sidebar-item" onclick="toggleSubmenu('customersSubmenu')">
                    <i class="fas fa-users"></i>
                    <span class="flex-1">CUSTOMERS</span>
                    <i class="fas fa-chevron-right text-[10px] text-white/15 transition-transform" id="customersArrow"></i>
                </div>
                <div id="customersSubmenu" class="sidebar-submenu hidden space-y-0.5 mt-0.5">
                    <a href="${pageContext.request.contextPath}/customers" class="sidebar-item" id="sidebarCustomersList">
                        <i class="fas fa-circle"></i>CUSTOMERS LIST
                    </a>
                    <c:if test="${sessionScope.role == 'CUSTOMER'}">
                        <a href="${pageContext.request.contextPath}/customers/orders" class="sidebar-item" id="sidebarCustomerOrders">
                            <i class="fas fa-circle"></i>MY ORDERS
                        </a>
                        <a href="${pageContext.request.contextPath}/customers/orders/create" class="sidebar-item" id="sidebarCustomerOrderCreate">
                            <i class="fas fa-circle"></i>CREATE ORDER
                        </a>
                    </c:if>
                    <c:if test="${sessionScope.role == 'LOGISTICS_COORDINATOR'}">
                        <a href="${pageContext.request.contextPath}/orders/pending" class="sidebar-item" id="sidebarPendingOrders">
                            <i class="fas fa-circle"></i>PENDING ORDERS
                        </a>
                    </c:if>
                </div>
            </div>

            <!-- ===== WAREHOUSES ===== -->
            <div>
                <div class="sidebar-item" onclick="toggleSubmenu('warehousesSubmenu')">
                    <i class="fas fa-store"></i>
                    <span class="flex-1">WAREHOUSES</span>
                    <i class="fas fa-chevron-right text-[10px] text-white/15 transition-transform" id="warehousesArrow"></i>
                </div>
                <div id="warehousesSubmenu" class="sidebar-submenu hidden space-y-0.5 mt-0.5">
                    <a href="${pageContext.request.contextPath}/warehouses" class="sidebar-item" id="sidebarWarehousesList">
                        <i class="fas fa-circle"></i>WAREHOUSES LIST
                    </a>
                    <a href="${pageContext.request.contextPath}/warehouses/create" class="sidebar-item" id="sidebarWarehousesList">
                        <i class="fas fa-circle"></i>WAREHOUSES CREATE
                    </a>
                </div>
            </div>

            <!-- ===== CARRIERS ===== -->
            <div>
                <div class="sidebar-item" onclick="toggleSubmenu('carriersSubmenu')">
                    <i class="fas fa-ship"></i>
                    <span class="flex-1">CARRIERS</span>
                    <i class="fas fa-chevron-right text-[10px] text-white/15 transition-transform" id="carriersArrow"></i>
                </div>
                <div id="carriersSubmenu" class="sidebar-submenu hidden space-y-0.5 mt-0.5">
                    <a href="${pageContext.request.contextPath}/carriers" class="sidebar-item" id="sidebarCarriersList">
                        <i class="fas fa-circle"></i>CARRIERS LIST
                    </a>
                    <a href="${pageContext.request.contextPath}/carriers/create" class="sidebar-item" id="sidebarCarriersList">
                        <i class="fas fa-circle"></i>CARRIERS CREATE
                    </a>
                </div>
            </div>

            <!-- ===== CUSTOMS ===== -->
            <div>
                <div class="sidebar-item" onclick="toggleSubmenu('customsSubmenu')">
                    <i class="fas fa-passport"></i>
                    <span class="flex-1">CUSTOMS</span>
                    <i class="fas fa-chevron-right text-[10px] text-white/15 transition-transform" id="customsArrow"></i>
                </div>
                <div id="customsSubmenu" class="sidebar-submenu hidden space-y-0.5 mt-0.5">
                    <a href="${pageContext.request.contextPath}/customs" class="sidebar-item" id="sidebarCustomsDocumentsList">
                        <i class="fas fa-circle"></i>CUSTOMS DOCUMENTS
                    </a>
                    <a href="${pageContext.request.contextPath}/customs/create" class="sidebar-item" id="sidebarCustomsDocumentCreate">
                        <i class="fas fa-circle"></i>DOCUMENT CREATE
                    </a>
                </div>
            </div>

            <!-- ===== ROUTE PLANNING ===== -->
            <a href="${pageContext.request.contextPath}/route-plan" class="sidebar-item" id="sidebarRoutePlan">
                <i class="fas fa-route"></i>
                <span>ROUTE PLANNING</span>
            </a>

            <!-- ===== ===== DIVIDER ===== ===== -->
            <div class="sidebar-divider my-3"></div>

            <!-- ===== MONITORING ===== -->
            <a href="${pageContext.request.contextPath}/monitoring/alerts" class="sidebar-item" id="sidebarAlerts">
                <i class="fas fa-bell"></i>
                <span>ALERTS</span>
                <span class="badge" id="alertBadge" style="display: none;">0</span>
            </a>

            <a href="${pageContext.request.contextPath}/monitoring/audit-logs" class="sidebar-item" id="sidebarAuditLogs">
                <i class="fas fa-clipboard-list"></i>
                <span>AUDIT LOGS</span>
            </a>

            <a href="${pageContext.request.contextPath}/monitoring/exception-logs" class="sidebar-item" id="sidebarExceptionLogs">
                <i class="fas fa-bug"></i>
                <span>EXCEPTION LOGS</span>
            </a>

            <a href="${pageContext.request.contextPath}/monitoring/timers" class="sidebar-item" id="sidebarSystemTimers">
                <i class="fas fa-clock"></i>
                <span>SYSTEM TIMERS</span>
            </a>

            <!-- ===== ===== DIVIDER ===== ===== -->
            <div class="sidebar-divider my-3"></div>

            <!-- ===== ADMIN (Role-based visibility) ===== -->
            <div>
                <div class="sidebar-item" onclick="toggleSubmenu('adminSubmenu')">
                    <i class="fas fa-user-shield"></i>
                    <span class="flex-1">ADMIN</span>
                    <i class="fas fa-chevron-right text-[10px] text-white/15 transition-transform" id="adminArrow"></i>
                </div>
                <div id="adminSubmenu" class="sidebar-submenu hidden space-y-0.5 mt-0.5">
                    <a href="${pageContext.request.contextPath}/admin/users-list" class="sidebar-item" id="sidebarAdminUsers">
                        <i class="fas fa-circle"></i>USER MANAGEMENT
                    </a>
                </div>
            </div>

            <!-- ===== DIVIDER ===== -->
            <div class="sidebar-divider my-3"></div>

            <!-- User Profile -->
            <a href="${pageContext.request.contextPath}/user/user-profile" class="sidebar-item" id="sidebarProfile">
                <i class="fas fa-user"></i>
                <span>PROFILE</span>
            </a>

            <!-- Settings -->
            <a href="${pageContext.request.contextPath}/user/settings.jsp" class="sidebar-item" id="sidebarSettings">
                <i class="fas fa-gear"></i>
                <span>SETTINGS</span>
            </a>

        </nav>

        <!-- ===== BOTTOM SECTION ===== -->
        <div class="mt-4 pt-4 border-t border-white/5">
            <div class="flex items-center gap-2 px-2 text-white/15 text-[10px]">
                <i class="fas fa-shield-alt text-dracula-red"></i>
                <span>Secure Connection v2.0</span>
            </div>
            <button onclick="openLogoutModal()"
                    class="w-full mt-2 text-white/20 hover:text-white/60 text-[10px] flex items-center justify-center gap-1.5 transition-all px-2 py-1.5 rounded hover:bg-white/5">
                <i class="fas fa-right-from-bracket"></i>
                <span>Sign Out</span>
            </button>
        </div>

    </div>
</aside>