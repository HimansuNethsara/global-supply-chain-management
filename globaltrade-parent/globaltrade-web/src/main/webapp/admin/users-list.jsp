<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 8/28/2026
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
    <title>GlobalTrade Logistics - Users List</title>

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
                    <h1 class="text-xl sm:text-2xl font-bold text-white">Users</h1>
                    <p class="text-white/30 text-sm font-light">Manage system users</p>
                </div>
                <div class="flex items-center gap-2">
                    <button onclick="window.print()" class="px-4 py-2 bg-white/5 border border-white/5 rounded-lg text-white/50 hover:text-white hover:bg-white/10 transition-all text-xs flex items-center gap-2">
                        <i class="fas fa-file-export"></i>
                        <span>Export</span>
                    </button>
                </div>
            </div>

            <!-- ===== TABS ===== -->
            <div class="flex border-b border-white/5 mb-6">
                <button onclick="switchTab('pending')" id="tabPending" class="tab-btn px-6 py-3 text-sm font-medium text-white/60 hover:text-white border-b-2 border-dracula-red transition-all">
                    <i class="fas fa-clock mr-2"></i>Pending Approval
                    <span class="ml-2 px-2 py-0.5 bg-yellow-500/20 text-yellow-400 rounded-full text-[10px]">${pendingCount}</span>
                </button>
                <button onclick="switchTab('all')" id="tabAll" class="tab-btn px-6 py-3 text-sm font-medium text-white/40 hover:text-white border-b-2 border-transparent hover:border-white/20 transition-all">
                    <i class="fas fa-users mr-2"></i>All Users
                    <span class="ml-2 px-2 py-0.5 bg-white/10 text-white/40 rounded-full text-[10px]">${totalUsers}</span>
                </button>
            </div>

            <!-- ===== PENDING APPROVAL TABLE ===== -->
            <div id="pendingTab" class="dashboard-card overflow-hidden">
                <div class="flex items-center justify-between mb-4">
                    <h2 class="text-white/60 text-sm font-semibold flex items-center gap-2">
                        <i class="fas fa-clock text-yellow-400"></i>
                        Pending Approval (<span id="pendingCountDisplay">${pendingCount}</span>)
                    </h2>
                    <span class="text-white/30 text-xs">Customs Agents awaiting approval</span>
                </div>

                <div class="overflow-x-auto">
                    <table class="transaction-table">
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>Full Name</th>
                            <th>Username</th>
                            <th>Email</th>
                            <th>Role</th>
                            <th>Requested</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${not empty pendingUsers}">
                                <c:forEach var="u" items="${pendingUsers}" varStatus="status">
                                    <tr>
                                        <td class="text-white/40 text-xs">${status.index + 1}</td>
                                        <td>
                                            <div class="text-white/70 text-sm font-medium">${u.fullName}</div>
                                        </td>
                                        <td class="text-white/40 text-xs">${u.username}</td>
                                        <td class="text-white/50 text-xs">${u.email}</td>
                                        <td>
                                                <span class="px-2 py-0.5 bg-purple-500/20 text-purple-400 rounded-full text-[10px] font-medium">
                                                    <i class="fas fa-passport mr-1"></i>${u.role.roleName}
                                                </span>
                                        </td>
                                        <td class="text-white/30 text-xs">
                                                ${u.createdAt != null ? u.createdAt.toString().replace('T', ' ').substring(0, 16) : 'N/A'}
                                        </td>
                                        <td>
                                            <div class="flex items-center gap-2">
                                                <button class="px-3 py-1 bg-green-500/20 border border-green-500/30 rounded-lg text-green-400 text-xs hover:bg-green-500/30 transition-all flex items-center gap-1" onclick="openApproveModal(${u.userId}, '${u.fullName}')">
                                                    <i class="fas fa-check"></i>
                                                    <span>Approve</span>
                                                </button>
                                                <button class="px-3 py-1 bg-red-500/20 border border-red-500/30 rounded-lg text-red-400 text-xs hover:bg-red-500/30 transition-all flex items-center gap-1" onclick="openRejectModal(${u.userId}, '${u.fullName}')">
                                                    <i class="fas fa-times"></i>
                                                    <span>Reject</span>
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
                                            <i class="fas fa-check-circle text-3xl text-white/10"></i>
                                            <span class="text-sm">No pending approvals</span>
                                            <span class="text-xs text-white/10">All customs agents have been reviewed</span>
                                        </div>
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- ===== ALL USERS TABLE ===== -->
            <div id="allTab" class="dashboard-card overflow-hidden hidden">
                <div class="flex items-center justify-between mb-4">
                    <h2 class="text-white/60 text-sm font-semibold flex items-center gap-2">
                        <i class="fas fa-users text-dracula-red"></i>
                        All Users
                    </h2>
                </div>

                <!-- ===== FILTER ===== -->
                <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 mb-4">
                    <div class="form-group">
                        <label for="userSearchInput" class="text-white/40 text-xs font-medium block mb-1.5">Search</label>
                        <input type="text" id="userSearchInput" placeholder="Search users..."
                               class="form-control w-full px-3 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:outline-none transition-all">
                    </div>
                    <div class="form-group">
                        <label for="roleFilter" class="text-white/40 text-xs font-medium block mb-1.5">Role</label>
                        <div class="select-wrapper">
                            <select id="roleFilter" class="form-select w-full px-3 py-2.5 pr-10 bg-white/5 border border-white/10 rounded-lg text-white text-sm focus:border-dracula-red focus:outline-none transition-all appearance-none cursor-pointer">
                                <option value="" class="bg-black text-white/40">All Roles</option>
                                <option value="LOGISTICS_COORDINATOR" class="bg-black">Logistics Coordinator</option>
                                <option value="CUSTOMS_AGENT" class="bg-black">Customs Agent</option>
                                <option value="WAREHOUSE_MANAGER" class="bg-black">Warehouse Manager</option>
                                <option value="VENDOR_REP" class="bg-black">Vendor Rep</option>
                                <option value="CUSTOMER" class="bg-black">Customer</option>
                            </select>
                            <span class="absolute right-3 top-1/2 -translate-y-1/2 text-white/20 pointer-events-none">
                                <i class="fas fa-chevron-down text-xs"></i>
                            </span>
                        </div>
                    </div>
                    <div class="form-group flex items-end">
                        <button id="applyUserFilters" class="w-full px-4 py-2.5 bg-dracula-red/20 border border-dracula-red/30 rounded-lg text-dracula-red text-sm font-medium hover:bg-dracula-red/30 transition-all">
                            <i class="fas fa-filter mr-1.5"></i>Apply Filters
                        </button>
                    </div>
                </div>

                <div class="overflow-x-auto">
                    <table class="transaction-table">
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>Full Name</th>
                            <th>Username</th>
                            <th>Email</th>
                            <th>Role</th>
                            <th>Status</th>
                            <th>Created</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${not empty allUsers}">
                                <c:forEach var="u" items="${allUsers}" varStatus="status">
                                    <tr>
                                        <td class="text-white/40 text-xs">${status.index + 1}</td>
                                        <td>
                                            <div class="text-white/70 text-sm font-medium">${u.fullName}</div>
                                        </td>
                                        <td class="text-white/40 text-xs">${u.username}</td>
                                        <td class="text-white/50 text-xs">${u.email}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${u.role.roleName == 'LOGISTICS_COORDINATOR'}">
                                                    <span class="px-2 py-0.5 bg-blue-500/20 text-blue-400 rounded-full text-[10px]">Logistics Coordinator</span>
                                                </c:when>
                                                <c:when test="${u.role.roleName == 'CUSTOMS_AGENT'}">
                                                    <span class="px-2 py-0.5 bg-purple-500/20 text-purple-400 rounded-full text-[10px]">Customs Agent</span>
                                                </c:when>
                                                <c:when test="${u.role.roleName == 'WAREHOUSE_MANAGER'}">
                                                    <span class="px-2 py-0.5 bg-orange-500/20 text-orange-400 rounded-full text-[10px]">Warehouse Manager</span>
                                                </c:when>
                                                <c:when test="${u.role.roleName == 'VENDOR_REP'}">
                                                    <span class="px-2 py-0.5 bg-green-500/20 text-green-400 rounded-full text-[10px]">Vendor Rep</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="px-2 py-0.5 bg-white/10 text-white/60 rounded-full text-[10px]">${u.role.roleName}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${u.status == 'ACTIVE'}">
                                                    <span class="transaction-status completed">Active</span>
                                                </c:when>
                                                <c:when test="${u.status == 'PENDING_APPROVAL'}">
                                                    <span class="transaction-status pending">Pending</span>
                                                </c:when>
                                                <c:when test="${u.status == 'SUSPENDED'}">
                                                    <span class="transaction-status failed">Suspended</span>
                                                </c:when>
                                                <c:when test="${u.status == 'DISABLED'}">
                                                    <span class="transaction-status failed">Disabled</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="transaction-status pending">${u.status}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-white/30 text-xs">
                                                ${u.createdAt != null ? u.createdAt.toString().replace('T', ' ').substring(0, 16) : 'N/A'}
                                        </td>
                                        <td>
                                            <div class="flex items-center gap-2">
                                                <button class="text-blue-400/60 hover:text-blue-400 text-xs transition-all" title="View Details" onclick="viewUserDetails(${u.userId})">
                                                    <i class="fas fa-eye"></i>
                                                </button>
                                                <c:if test="${u.status != 'DISABLED'}">
                                                    <button class="text-yellow-400/60 hover:text-yellow-400 text-xs transition-all" title="Suspend" onclick="toggleUserStatus(${u.userId}, '${u.status}')">
                                                        <i class="fas fa-pause"></i>
                                                    </button>
                                                </c:if>
                                                <c:if test="${u.status == 'DISABLED'}">
                                                    <button class="text-green-400/60 hover:text-green-400 text-xs transition-all" title="Enable" onclick="toggleUserStatus(${u.userId}, '${u.status}')">
                                                        <i class="fas fa-play"></i>
                                                    </button>
                                                </c:if>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="8" class="text-center text-white/20 py-8">
                                        <div class="flex flex-col items-center gap-2">
                                            <i class="fas fa-users text-3xl text-white/10"></i>
                                            <span class="text-sm">No users found</span>
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
                    <div class="text-white/20 text-xs">Showing ${fn:length(allUsers)} users</div>
                    <div class="flex items-center gap-1">
                        <button class="px-3 py-1 bg-white/5 border border-white/10 rounded-lg text-white/30 text-xs hover:bg-white/10 transition-all" disabled>Previous</button>
                        <button class="px-3 py-1 bg-dracula-red/20 border border-dracula-red/30 rounded-lg text-dracula-red text-xs hover:bg-dracula-red/30 transition-all">1</button>
                        <button class="px-3 py-1 bg-white/5 border border-white/10 rounded-lg text-white/30 text-xs hover:bg-white/10 transition-all" disabled>Next</button>
                    </div>
                </div>
            </div>

        </div>
    </main>

    <!-- ===== APPROVE CONFIRMATION MODAL ===== -->
    <div id="approveModal" class="delete-modal-overlay">
        <div class="delete-modal-content">
            <div class="delete-modal-header">
                <h3>Confirm Approval</h3>
                <button class="close-btn" onclick="closeApproveModal()">
                    <i class="fas fa-times"></i>
                </button>
            </div>
            <p class="delete-modal-subtitle">This action will activate the user account</p>

            <div class="delete-icon-wrapper" style="background: rgba(34, 197, 94, 0.1); border-color: rgba(34, 197, 94, 0.2);">
                <i class="fas fa-check-circle" style="color: #22c55e;"></i>
            </div>

            <p class="delete-modal-message" id="approveMessage">
                Are you sure you want to approve this user?
            </p>

            <div class="delete-modal-warning" style="background: rgba(34, 197, 94, 0.06); border-color: rgba(34, 197, 94, 0.12);">
                <i class="fas fa-info-circle" style="color: #22c55e;"></i>
                <span style="color: rgba(34, 197, 94, 0.6);">The user will gain full access as a Customs Agent.</span>
            </div>

            <div class="delete-modal-actions">
                <button class="btn-cancel" onclick="closeApproveModal()">Cancel</button>
                <button class="btn-delete" onclick="confirmApproveAction()" style="background: linear-gradient(135deg, #22c55e, #16a34a);">
                    <i class="fas fa-check"></i> Approve
                </button>
            </div>
        </div>
    </div>

    <!-- ===== REJECT CONFIRMATION MODAL ===== -->
    <div id="rejectModal" class="delete-modal-overlay">
        <div class="delete-modal-content">
            <div class="delete-modal-header">
                <h3>Confirm Rejection</h3>
                <button class="close-btn" onclick="closeRejectModal()">
                    <i class="fas fa-times"></i>
                </button>
            </div>
            <p class="delete-modal-subtitle">This action cannot be undone</p>

            <div class="delete-icon-wrapper">
                <i class="fas fa-times-circle"></i>
            </div>

            <p class="delete-modal-message" id="rejectMessage">
                Are you sure you want to reject this user?
            </p>

            <div class="delete-modal-warning">
                <i class="fas fa-exclamation-triangle"></i>
                <span>The user will be notified of the rejection.</span>
            </div>

            <div class="delete-modal-actions">
                <button class="btn-cancel" onclick="closeRejectModal()">Cancel</button>
                <button class="btn-delete" onclick="confirmRejectAction()">
                    <i class="fas fa-times"></i> Reject
                </button>
            </div>
        </div>
    </div>

    <!-- ===== FOOTER ===== -->
    <jsp:include page="/WEB-INF/views/footer.jsp" />
</div>

<!-- Custom JavaScript -->
<script src="${pageContext.request.contextPath}/js/user-profile.js"></script>
</body>
</html>