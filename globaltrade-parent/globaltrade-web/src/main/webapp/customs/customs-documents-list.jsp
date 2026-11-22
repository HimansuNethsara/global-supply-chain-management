<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 8/24/2026
  Time: 12:00 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%--@elvariable id="customsDocuments" type=""--%>
<!DOCTYPE html>
<html lang="en" data-theme="dracula">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GlobalTrade Logistics - Customs Documents</title>

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

<!-- ===== HIDDEN USER ROLE FOR JS ===== -->
<input type="hidden" id="userRole" value="${sessionScope.role}">

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
                    <h1 class="text-xl sm:text-2xl font-bold text-white">Customs Documents</h1>
                    <p class="text-white/30 text-sm font-light">Manage customs documentation</p>
                </div>
                <div class="flex items-center gap-2">
                    <a href="${pageContext.request.contextPath}/customs/create" class="px-4 py-2 bg-gradient-to-r from-dracula-red to-red-900 rounded-lg text-white text-xs font-semibold hover:shadow-lg hover:shadow-red-500/25 transition-all flex items-center gap-2">
                        <i class="fas fa-plus"></i>
                        <span>Create Document</span>
                    </a>
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
                        <input type="text" id="searchInput" placeholder="Search by document type..."
                               class="form-control w-full px-3 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:outline-none transition-all">
                    </div>
                    <div class="form-group">
                        <label for="statusFilter" class="text-white/40 text-xs font-medium block mb-1.5">Status</label>
                        <div class="select-wrapper">
                            <select id="statusFilter" class="form-select w-full px-3 py-2.5 pr-10 bg-white/5 border border-white/10 rounded-lg text-white text-sm focus:border-dracula-red focus:outline-none transition-all appearance-none cursor-pointer">
                                <option value="" class="bg-black text-white/40">All Status</option>
                                <option value="PENDING" class="bg-black">Pending</option>
                                <option value="SUBMITTED" class="bg-black">Submitted</option>
                                <option value="APPROVED" class="bg-black">Approved</option>
                                <option value="REJECTED" class="bg-black">Rejected</option>
                            </select>
                            <span class="absolute right-3 top-1/2 -translate-y-1/2 text-white/20 pointer-events-none">
                                <i class="fas fa-chevron-down text-xs"></i>
                            </span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="shipmentFilter" class="text-white/40 text-xs font-medium block mb-1.5">Shipment</label>
                        <div class="select-wrapper">
                            <select id="shipmentFilter" class="form-select w-full px-3 py-2.5 pr-10 bg-white/5 border border-white/10 rounded-lg text-white text-sm focus:border-dracula-red focus:outline-none transition-all appearance-none cursor-pointer">
                                <option value="" class="bg-black text-white/40">All Shipments</option>
                                <c:forEach var="s" items="${shipments}">
                                    <option value="${s.shipmentId}" class="bg-black">${s.trackingNumber}</option>
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

            <!-- ===== CUSTOMS DOCUMENTS TABLE ===== -->
            <div class="dashboard-card overflow-hidden">
                <div class="overflow-x-auto">
                    <table class="transaction-table">
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>Document Type</th>
                            <th>Shipment</th>
                            <th>Status</th>
                            <th>Submitted Date</th>
                            <th>Approved Date</th>
                            <th>Customs Agent</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${not empty customsDocuments}">
                                <c:forEach var="d" items="${customsDocuments}" varStatus="status">
                                    <tr>
                                        <td class="text-white/40 text-xs">${status.index + 1}</td>
                                        <td class="text-white/70 text-sm font-medium">${d.documentType}</td>
                                        <td class="text-white/50 text-xs">
                                            <a href="${pageContext.request.contextPath}/shipments/details?shipmentId=${d.shipment.shipmentId}" class="hover:text-dracula-red transition-all">
                                                    ${d.shipment.trackingNumber}
                                            </a>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${d.status == 'APPROVED'}">
                                                    <span class="transaction-status completed">Approved</span>
                                                </c:when>
                                                <c:when test="${d.status == 'SUBMITTED'}">
                                                    <span class="transaction-status pending">Submitted</span>
                                                </c:when>
                                                <c:when test="${d.status == 'REJECTED'}">
                                                    <span class="transaction-status failed">Rejected</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="transaction-status pending">${d.status}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-white/30 text-xs">
                                                ${d.submittedDate != null ? d.submittedDate.toString().replace('T', ' ').substring(0, 16) : 'N/A'}
                                        </td>
                                        <td class="text-white/30 text-xs">
                                                ${d.approvedDate != null ? d.approvedDate.toString().replace('T', ' ').substring(0, 16) : 'N/A'}
                                        </td>
                                        <td class="text-white/40 text-xs">${d.customsAgent != null ? d.customsAgent.fullName : 'N/A'}</td>
                                        <td>
                                            <div class="flex items-center gap-2">
                                                <a href="${pageContext.request.contextPath}/customs/details?docId=${d.docId}"
                                                   class="text-blue-400/60 hover:text-blue-400 text-xs transition-all" title="View Details">
                                                    <i class="fas fa-eye"></i>
                                                </a>
                                                <c:if test="${d.status == 'PENDING' or d.status == 'SUBMITTED'}">
                                                    <button class="text-yellow-400/60 hover:text-yellow-400 text-xs transition-all" title="Update Status" onclick="updateDocumentStatus(${d.docId}, '${d.status}')">
                                                        <i class="fas fa-pen"></i>
                                                    </button>
                                                </c:if>
                                                <button class="text-white/20 hover:text-red-400 text-xs transition-all" title="Delete" onclick="confirmDelete(${d.docId}, '${d.documentType}', '${pageContext.request.contextPath}')">
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
                                            <i class="fas fa-file-alt text-3xl text-white/10"></i>
                                            <span class="text-sm">No customs documents found</span>
                                            <a href="${pageContext.request.contextPath}/customs/create" class="text-dracula-red text-xs hover:text-red-400 transition-all mt-2">
                                                <i class="fas fa-plus mr-1"></i>Create your first document
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
                    <div class="text-white/20 text-xs">Showing ${fn:length(customsDocuments)} documents</div>
                    <div class="flex items-center gap-1">
                        <button class="px-3 py-1 bg-white/5 border border-white/10 rounded-lg text-white/30 text-xs hover:bg-white/10 transition-all" disabled>Previous</button>
                        <button class="px-3 py-1 bg-dracula-red/20 border border-dracula-red/30 rounded-lg text-dracula-red text-xs hover:bg-dracula-red/30 transition-all">1</button>
                        <button class="px-3 py-1 bg-white/5 border border-white/10 rounded-lg text-white/30 text-xs hover:bg-white/10 transition-all" disabled>Next</button>
                    </div>
                </div>
            </div>

        </div>
    </main>

    <!-- ===== STATUS UPDATE MODAL ===== -->
    <jsp:include page="/customs/customs-status-update-modal.jsp" />

    <!-- ===== DELETE CONFIRMATION MODAL ===== -->
    <jsp:include page="/WEB-INF/views/components/delete-modal.jsp" />

    <!-- ===== FOOTER ===== -->
    <jsp:include page="/WEB-INF/views/footer.jsp" />
</div>

<!-- Custom JavaScript -->
<script src="${pageContext.request.contextPath}/js/delete-modal.js"></script>
<script src="${pageContext.request.contextPath}/js/customs.js"></script>
</body>
</html>