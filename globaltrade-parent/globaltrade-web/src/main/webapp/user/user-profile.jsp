<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 8/4/2026
  Time: 12:00 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%--@elvariable id="profile" type="lk.jiat.global.web.user.UserProfileServlet.ProfileData"--%>
<!DOCTYPE html>
<html lang="en" data-theme="dracula">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GlobalTrade Logistics - User Profile</title>

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
    <main class="main-content min-h-[calc(100vh-120px)] relative z-10">
        <div class="p-4 sm:p-6 lg:p-8 max-w-5xl mx-auto">

            <!-- ===== PAGE HEADER ===== -->
            <div class="mb-6">
                <h1 class="text-xl sm:text-2xl font-bold text-white">User Profile</h1>
                <p class="text-white/30 text-sm font-light">Personal Details of the Account Holder</p>
            </div>

            <!-- ===== PROFILE TABLE ===== -->
            <div class="dashboard-card overflow-hidden">
                <table class="profile-table w-full">
                    <thead>
                    <tr>
                        <th class="text-left text-white/40 text-xs font-semibold uppercase tracking-wider py-3 px-4 border-b border-white/5">Personal Details</th>
                        <th class="text-left text-white/40 text-xs font-semibold uppercase tracking-wider py-3 px-4 border-b border-white/5">Contact Details</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td class="py-2.5 px-4 border-b border-white/5">
                            <label class="text-white/30 text-[10px] block">Full Name</label>
                            <div class="text-white/80 text-sm">${profile.fullName}</div>
                        </td>
                        <td class="py-2.5 px-4 border-b border-white/5">
                            <label class="text-white/30 text-[10px] block">Email</label>
                            <div class="text-white/80 text-sm">${profile.email}</div>
                        </td>
                    </tr>
                    <tr>
                        <td class="py-2.5 px-4 border-b border-white/5">
                            <label class="text-white/30 text-[10px] block">Username</label>
                            <div class="text-white/80 text-sm">${profile.username}</div>
                        </td>
                        <td class="py-2.5 px-4 border-b border-white/5">
                            <label class="text-white/30 text-[10px] block">Role</label>
                            <div class="text-white/80 text-sm">
                                <span class="px-2 py-0.5 bg-dracula-red/10 text-dracula-red rounded-full text-[10px]">${profile.roleName}</span>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td class="py-2.5 px-4 border-b border-white/5">
                            <label class="text-white/30 text-[10px] block">Status</label>
                            <div class="text-white/80 text-sm">
                                <c:choose>
                                    <c:when test="${profile.status == 'ACTIVE'}">
                                        <span class="transaction-status completed">Active</span>
                                    </c:when>
                                    <c:when test="${profile.status == 'PENDING_APPROVAL'}">
                                        <span class="transaction-status pending">Pending Approval</span>
                                    </c:when>
                                    <c:when test="${profile.status == 'SUSPENDED'}">
                                        <span class="transaction-status failed">Suspended</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="transaction-status pending">${profile.status}</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </td>
                        <td class="py-2.5 px-4 border-b border-white/5">
                            <label class="text-white/30 text-[10px] block">Created At</label>
                            <div class="text-white/80 text-sm">
                                ${profile.createdAt.toString().replace('T', ' ').substring(0, 16)}
                            </div>
                        </td>
                    </tr>
                    <c:if test="${profile.vendorName != null}">
                        <tr>
                            <td class="py-2.5 px-4 border-b border-white/5" colspan="2">
                                <label class="text-white/30 text-[10px] block">Vendor</label>
                                <div class="text-white/80 text-sm">${profile.vendorName}</div>
                            </td>
                        </tr>
                    </c:if>
                    <c:if test="${profile.companyName != null}">
                        <tr>
                            <td class="py-2.5 px-4 border-b border-white/5" colspan="2">
                                <label class="text-white/30 text-[10px] block">Company</label>
                                <div class="text-white/80 text-sm">${profile.companyName}</div>
                            </td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>

            <!-- ===== ADDITIONAL DETAILS ===== -->
            <div class="dashboard-card mt-6 overflow-hidden">
                <table class="profile-table w-full">
                    <thead>
                    <tr>
                        <th class="text-left text-white/40 text-xs font-semibold uppercase tracking-wider py-3 px-4 border-b border-white/5" colspan="2">Additional Details</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td class="py-2.5 px-4 border-b border-white/5 w-1/2">
                            <label class="text-white/30 text-[10px] block">Contact Phone</label>
                            <div class="text-white/80 text-sm">${profile.contactPhone != null ? profile.contactPhone : 'N/A'}</div>
                        </td>
                        <td class="py-2.5 px-4 border-b border-white/5 w-1/2">
                            <label class="text-white/30 text-[10px] block">Country</label>
                            <div class="text-white/80 text-sm">${profile.country != null ? profile.country : 'N/A'}</div>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>

            <!-- ===== ACTION BUTTONS ===== -->
            <div class="flex flex-wrap items-center gap-3 mt-6">
                <button class="px-6 py-2 bg-gradient-to-r from-dracula-red to-red-900 rounded-lg text-white text-sm font-semibold hover:shadow-lg hover:shadow-red-500/25 transition-all" onclick="editProfile()">
                    <i class="fas fa-edit mr-1.5"></i>Edit Profile
                </button>
                <button class="px-6 py-2 bg-white/5 border border-white/10 rounded-lg text-white/50 text-sm hover:bg-white/10 hover:text-white/60 transition-all" onclick="changePassword()">
                    <i class="fas fa-key mr-1.5"></i>Change Password
                </button>
                <button class="px-6 py-2 bg-white/5 border border-white/10 rounded-lg text-white/30 text-sm hover:bg-white/10 hover:text-white/50 transition-all" onclick="window.print()">
                    <i class="fas fa-print mr-1.5"></i>Print
                </button>
            </div>

        </div>
    </main>

    <!-- ===== FOOTER ===== -->
    <jsp:include page="/WEB-INF/views/footer.jsp" />
</div>

<!-- Custom JavaScript -->
<script src="${pageContext.request.contextPath}/js/user-profile.js"></script>
</body>
</html>