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
    <title>GlobalTrade Logistics - Create Customs Document</title>

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
        <div class="p-4 sm:p-6 lg:p-8 max-w-3xl mx-auto">

            <!-- ===== PAGE HEADER ===== -->
            <div class="flex items-center gap-3 mb-6">
                <a href="${pageContext.request.contextPath}/customs" class="text-white/30 hover:text-white/60 transition-all">
                    <i class="fas fa-arrow-left"></i>
                </a>
                <div>
                    <h1 class="text-xl sm:text-2xl font-bold text-white">Create Customs Document</h1>
                    <p class="text-white/30 text-sm font-light">Create a new customs document</p>
                </div>
            </div>

            <!-- ===== CREATE FORM ===== -->
            <div class="dashboard-card">
                <form id="customsDocumentCreateForm" action="${pageContext.request.contextPath}/customs/create" method="POST">
                    <input type="hidden" name="csrfToken" value="placeholder_token">

                    <!-- Row 1: Document Type + Shipment -->
                    <div class="form-row">
                        <div class="form-group">
                            <label for="documentType" class="text-white/40 text-xs font-medium block mb-1.5">Document Type <span class="text-dracula-red">*</span></label>
                            <div class="select-wrapper">
                                <select id="documentType" name="documentType" class="form-select w-full px-3 py-2.5 pr-10 bg-white/5 border border-white/10 rounded-lg text-white text-sm focus:border-dracula-red focus:outline-none transition-all appearance-none cursor-pointer" required>
                                    <option value="" class="bg-black text-white/40" disabled selected>Select Document Type</option>
                                    <option value="Import Declaration" class="bg-black">Import Declaration</option>
                                    <option value="Export Declaration" class="bg-black">Export Declaration</option>
                                    <option value="Commercial Invoice" class="bg-black">Commercial Invoice</option>
                                    <option value="Packing List" class="bg-black">Packing List</option>
                                    <option value="Certificate of Origin" class="bg-black">Certificate of Origin</option>
                                    <option value="Bill of Lading" class="bg-black">Bill of Lading</option>
                                    <option value="Air Waybill" class="bg-black">Air Waybill</option>
                                    <option value="Customs Clearance" class="bg-black">Customs Clearance</option>
                                    <option value="Proforma Invoice" class="bg-black">Proforma Invoice</option>
                                    <option value="Shipping Bill" class="bg-black">Shipping Bill</option>
                                    <option value="Delivery Order" class="bg-black">Delivery Order</option>
                                    <option value="Insurance Certificate" class="bg-black">Insurance Certificate</option>
                                    <option value="Phytosanitary Certificate" class="bg-black">Phytosanitary Certificate</option>
                                    <option value="Fumigation Certificate" class="bg-black">Fumigation Certificate</option>
                                    <option value="Dangerous Goods Declaration" class="bg-black">Dangerous Goods Declaration</option>
                                    <option value="ATA Carnet" class="bg-black">ATA Carnet (Temporary Import)</option>
                                    <option value="Import License" class="bg-black">Import License</option>
                                    <option value="Export License" class="bg-black">Export License</option>
                                </select>
                                <span class="absolute right-3 top-1/2 -translate-y-1/2 text-white/20 pointer-events-none">
                                    <i class="fas fa-chevron-down text-xs"></i>
                                </span>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="shipmentId" class="text-white/40 text-xs font-medium block mb-1.5">Shipment <span class="text-dracula-red">*</span></label>
                            <div class="select-wrapper">
                                <select id="shipmentId" name="shipmentId" class="form-select w-full px-3 py-2.5 pr-10 bg-white/5 border border-white/10 rounded-lg text-white text-sm focus:border-dracula-red focus:outline-none transition-all appearance-none cursor-pointer" required>
                                    <option value="" class="bg-black text-white/40" disabled selected>Select Shipment</option>
                                    <c:forEach var="s" items="${shipments}">
                                        <option value="${s.shipmentId}" class="bg-black">${s.trackingNumber} - ${s.destination}</option>
                                    </c:forEach>
                                </select>
                                <span class="absolute right-3 top-1/2 -translate-y-1/2 text-white/20 pointer-events-none">
                                    <i class="fas fa-chevron-down text-xs"></i>
                                </span>
                            </div>
                        </div>
                    </div>

                    <!-- Row 2: Status + Customs Agent -->
                    <div class="form-row">
                        <div class="form-group">
                            <label for="status" class="text-white/40 text-xs font-medium block mb-1.5">Status</label>
                            <div class="select-wrapper">
                                <select id="status" name="status" class="form-select w-full px-3 py-2.5 pr-10 bg-white/5 border border-white/10 rounded-lg text-white text-sm focus:border-dracula-red focus:outline-none transition-all appearance-none cursor-pointer">
                                    <option value="PENDING" class="bg-black" selected>Pending</option>
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
                            <label for="customsAgentId" class="text-white/40 text-xs font-medium block mb-1.5">Customs Agent</label>
                            <div class="select-wrapper">
                                <select id="customsAgentId" name="customsAgentId" class="form-select w-full px-3 py-2.5 pr-10 bg-white/5 border border-white/10 rounded-lg text-white text-sm focus:border-dracula-red focus:outline-none transition-all appearance-none cursor-pointer">
                                    <option value="" class="bg-black text-white/40" selected>Select Customs Agent</option>
                                    <c:forEach var="agent" items="${customsAgents}">
                                        <option value="${agent.userId}" class="bg-black">${agent.fullName}</option>
                                    </c:forEach>
                                </select>
                                <span class="absolute right-3 top-1/2 -translate-y-1/2 text-white/20 pointer-events-none">
                                    <i class="fas fa-chevron-down text-xs"></i>
                                </span>
                            </div>
                        </div>
                    </div>

                    <!-- Buttons -->
                    <div class="flex items-center gap-3 pt-3 border-t border-white/5 mt-4">
                        <button type="submit" id="submitBtn" class="px-6 py-2.5 bg-gradient-to-r from-dracula-red to-red-900 rounded-lg text-white text-sm font-semibold hover:shadow-lg hover:shadow-red-500/25 transition-all">
                            <i class="fas fa-plus mr-1.5"></i>Create Document
                        </button>
                        <button type="reset" id="resetBtn" class="px-6 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white/40 text-sm hover:bg-white/10 hover:text-white/60 transition-all">
                            <i class="fas fa-undo mr-1.5"></i>Reset
                        </button>
                        <a href="${pageContext.request.contextPath}/customs" class="px-6 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white/30 text-sm hover:bg-white/10 hover:text-white/50 transition-all">
                            Cancel
                        </a>
                    </div>

                </form>
            </div>

        </div>
    </main>

    <!-- ===== STATUS UPDATE MODAL ===== -->
    <jsp:include page="/customs/customs-status-update-modal.jsp" />

    <!-- ===== FOOTER ===== -->
    <jsp:include page="/WEB-INF/views/footer.jsp" />
</div>

<!-- Custom JavaScript -->
<script src="${pageContext.request.contextPath}/js/customs.js"></script>
</body>
</html>