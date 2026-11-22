<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 8/24/2026
  Time: 12:00 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en" data-theme="dracula">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GlobalTrade Logistics - Customs Document Details</title>

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
                <a href="${pageContext.request.contextPath}/customs" class="text-white/30 hover:text-white/60 transition-all">
                    <i class="fas fa-arrow-left"></i>
                </a>
                <div>
                    <h1 class="text-xl sm:text-2xl font-bold text-white">Customs Document Details</h1>
                    <p class="text-white/30 text-sm font-light">View document information</p>
                </div>
                <div class="ml-auto flex items-center gap-2">
                    <c:if test="${document.status == 'PENDING' or document.status == 'SUBMITTED'}">
                        <button onclick="updateDocumentStatus(${document.docId}, '${document.status}')" class="px-4 py-2 bg-dracula-red/20 border border-dracula-red/30 rounded-lg text-dracula-red text-sm font-medium hover:bg-dracula-red/30 transition-all flex items-center gap-2">
                            <i class="fas fa-pen"></i>
                            <span>Update Status</span>
                        </button>
                    </c:if>
                </div>
            </div>

            <!-- ===== DOCUMENT INFO ===== -->
            <div class="dashboard-card">
                <div class="flex items-start justify-between mb-4">
                    <h2 class="text-white/60 text-sm font-semibold">Document Information</h2>
                    <c:choose>
                        <c:when test="${document.status == 'APPROVED'}">
                            <span class="transaction-status completed">Approved</span>
                        </c:when>
                        <c:when test="${document.status == 'SUBMITTED'}">
                            <span class="transaction-status pending">Submitted</span>
                        </c:when>
                        <c:when test="${document.status == 'REJECTED'}">
                            <span class="transaction-status failed">Rejected</span>
                        </c:when>
                        <c:otherwise>
                            <span class="transaction-status pending">${document.status}</span>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                        <label class="text-white/30 text-xs block">Document Type</label>
                        <div class="text-white/80 text-base font-medium">${document.documentType}</div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Shipment</label>
                        <div class="text-white/80 text-base font-medium">
                            <a href="${pageContext.request.contextPath}/shipments/details?shipmentId=${document.shipment.shipmentId}" class="hover:text-dracula-red transition-all">
                                ${document.shipment.trackingNumber}
                            </a>
                        </div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Status</label>
                        <div class="text-white/80 text-base font-medium">
                            <c:choose>
                                <c:when test="${document.status == 'APPROVED'}">
                                    <span class="text-green-400">Approved</span>
                                </c:when>
                                <c:when test="${document.status == 'SUBMITTED'}">
                                    <span class="text-yellow-400">Submitted</span>
                                </c:when>
                                <c:when test="${document.status == 'REJECTED'}">
                                    <span class="text-red-400">Rejected</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-white/60">${document.status}</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Customs Agent</label>
                        <div class="text-white/80 text-base font-medium">${document.customsAgent != null ? document.customsAgent.fullName : 'N/A'}</div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Submitted Date</label>
                        <div class="text-white/80 text-base font-medium">
                            ${document.submittedDate != null ? document.submittedDate.toString().replace('T', ' ').substring(0, 16) : 'N/A'}
                        </div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Approved Date</label>
                        <div class="text-white/80 text-base font-medium">
                            ${document.approvedDate != null ? document.approvedDate.toString().replace('T', ' ').substring(0, 16) : 'N/A'}
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
<script src="${pageContext.request.contextPath}/js/customs.js"></script>
</body>
</html>