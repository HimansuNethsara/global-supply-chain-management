<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 8/27/2026
  Time: 12:00 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%--@elvariable id="vendor" type=""--%>
<%--@elvariable id="avgRating" type=""--%>
<%--@elvariable id="evaluations" type=""--%>
<!DOCTYPE html>
<html lang="en" data-theme="dracula">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GlobalTrade Logistics - Vendor Performance Evaluation</title>

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
                    <h1 class="text-xl sm:text-2xl font-bold text-white">Vendor Performance Evaluation</h1>
                    <p class="text-white/30 text-sm font-light">Evaluate and monitor vendor performance</p>
                </div>
                <div class="flex items-center gap-2">
                    <button onclick="window.print()" class="px-4 py-2 bg-white/5 border border-white/5 rounded-lg text-white/50 hover:text-white hover:bg-white/10 transition-all text-xs flex items-center gap-2">
                        <i class="fas fa-file-export"></i>
                        <span>Export</span>
                    </button>
                </div>
            </div>

            <!-- ===== VENDOR SELECTION ===== -->
            <div class="dashboard-card mb-6">
                <form id="evaluationForm" method="GET" action="${pageContext.request.contextPath}/vendors/performance">
                    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                        <div class="form-group">
                            <label for="vendorId" class="text-white/40 text-xs font-medium block mb-1.5">Select Vendor <span class="text-dracula-red">*</span></label>
                            <div class="select-wrapper">
                                <select id="vendorId" name="vendorId" class="form-select w-full px-3 py-2.5 pr-10 bg-white/5 border border-white/10 rounded-lg text-white text-sm focus:border-dracula-red focus:outline-none transition-all appearance-none cursor-pointer" required>
                                    <option value="" class="bg-black text-white/40" disabled ${empty param.vendorId ? 'selected' : ''}>Select Vendor</option>
                                    <c:forEach var="v" items="${vendors}">
                                        <option value="${v.vendorId}" class="bg-black" ${param.vendorId == v.vendorId ? 'selected' : ''}>
                                                ${v.vendorName} ${v.performanceRating != null ? '(' += v.performanceRating += '★)' : ''}
                                        </option>
                                    </c:forEach>
                                </select>
                                <span class="absolute right-3 top-1/2 -translate-y-1/2 text-white/20 pointer-events-none">
                                    <i class="fas fa-chevron-down text-xs"></i>
                                </span>
                            </div>
                        </div>
                        <div class="form-group flex items-end">
                            <button type="submit" id="viewBtn" class="w-full px-4 py-2.5 bg-dracula-red/20 border border-dracula-red/30 rounded-lg text-dracula-red text-sm font-medium hover:bg-dracula-red/30 transition-all">
                                <i class="fas fa-eye mr-1.5"></i>View Performance
                            </button>
                        </div>
                        <div class="form-group flex items-end">
                            <button type="button" id="addEvaluationBtn" onclick="showAddEvaluationModal()" class="w-full px-4 py-2.5 bg-green-500/20 border border-green-500/30 rounded-lg text-green-400 text-sm font-medium hover:bg-green-500/30 transition-all">
                                <i class="fas fa-plus mr-1.5"></i>Add Evaluation
                            </button>
                        </div>
                    </div>
                </form>
            </div>

            <!-- ===== VENDOR INFO & PERFORMANCE ===== -->
            <c:if test="${not empty vendor}">
                <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-6">
                    <!-- Vendor Info -->
                    <div class="dashboard-card">
                        <h3 class="text-white/60 text-sm font-semibold mb-3">Vendor Information</h3>
                        <div class="space-y-2">
                            <div>
                                <label class="text-white/30 text-xs block">Name</label>
                                <div class="text-white/80 text-sm font-medium">${vendor.vendorName}</div>
                            </div>
                            <div>
                                <label class="text-white/30 text-xs block">Email</label>
                                <div class="text-white/80 text-sm">${vendor.contactEmail}</div>
                            </div>
                            <div>
                                <label class="text-white/30 text-xs block">Country</label>
                                <div class="text-white/80 text-sm">
                                    <span class="px-2 py-0.5 bg-white/5 rounded-full text-[10px]">${vendor.country}</span>
                                </div>
                            </div>
                            <div>
                                <label class="text-white/30 text-xs block">Status</label>
                                <c:choose>
                                    <c:when test="${vendor.status == 'ACTIVE'}">
                                        <span class="transaction-status completed">Active</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="transaction-status failed">${vendor.status}</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>

                    <!-- Performance Summary -->
                    <div class="dashboard-card">
                        <h3 class="text-white/60 text-sm font-semibold mb-3">Performance Summary</h3>
                        <div class="space-y-2">
                            <div>
                                <label class="text-white/30 text-xs block">Average Rating</label>
                                <div class="text-3xl font-bold ${avgRating != null && avgRating >= 4.0 ? 'text-green-400' : avgRating != null && avgRating >= 3.0 ? 'text-yellow-400' : 'text-red-400'}">
                                        ${avgRating != null ? avgRating : 'N/A'}
                                    <span class="text-lg">★</span>
                                </div>
                            </div>
                            <div>
                                <label class="text-white/30 text-xs block">Total Evaluations</label>
                                <div class="text-white/80 text-sm font-medium">${fn:length(evaluations)}</div>
                            </div>
                            <div>
                                <label class="text-white/30 text-xs block">Last Evaluated</label>
                                <div class="text-white/50 text-sm">
                                    <c:if test="${not empty evaluations}">
                                        ${evaluations[0].evaluationDate != null ? evaluations[0].evaluationDate.toString() : 'N/A'}
                                    </c:if>
                                    <c:if test="${empty evaluations}">
                                        <span class="text-white/30">Never</span>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Quick Actions -->
                    <div class="dashboard-card">
                        <h3 class="text-white/60 text-sm font-semibold mb-3">Quick Actions</h3>
                        <div class="space-y-2">
                            <button onclick="showAddEvaluationModal()" class="w-full px-4 py-2 bg-green-500/20 border border-green-500/30 rounded-lg text-green-400 text-sm hover:bg-green-500/30 transition-all flex items-center justify-center gap-2">
                                <i class="fas fa-plus"></i> Add Evaluation
                            </button>
                            <button onclick="recalculateRating()" class="w-full px-4 py-2 bg-blue-500/20 border border-blue-500/30 rounded-lg text-blue-400 text-sm hover:bg-blue-500/30 transition-all flex items-center justify-center gap-2">
                                <i class="fas fa-sync-alt"></i> Recalculate Rating
                            </button>
                        </div>
                    </div>
                </div>

                <!-- Evaluations Table -->
                <div class="dashboard-card overflow-hidden">
                    <div class="flex items-center justify-between mb-4">
                        <h3 class="text-white/60 text-sm font-semibold flex items-center gap-2">
                            <i class="fas fa-list text-dracula-red"></i>
                            Evaluation History
                        </h3>
                    </div>
                    <div class="overflow-x-auto">
                        <table class="transaction-table">
                            <thead>
                            <tr>
                                <th>#</th>
                                <th>Date</th>
                                <th>Evaluator</th>
                                <th>Score</th>
                                <th>Comments</th>
                                <th>Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${not empty evaluations}">
                                    <c:forEach var="e" items="${evaluations}" varStatus="status">
                                        <tr>
                                            <td class="text-white/40 text-xs">${status.index + 1}</td>
                                            <td class="text-white/50 text-xs">
                                                    ${e.evaluationDate != null ? e.evaluationDate.toString() : 'N/A'}
                                            </td>
                                            <td class="text-white/50 text-xs">${e.evaluator.fullName}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${e.score >= 4.0}">
                                                        <span class="text-green-400 font-medium">${e.score} ★</span>
                                                    </c:when>
                                                    <c:when test="${e.score >= 3.0}">
                                                        <span class="text-yellow-400 font-medium">${e.score} ★</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-red-400 font-medium">${e.score} ★</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="text-white/40 text-xs max-w-[200px] truncate" title="${e.comments}">
                                                    ${e.comments != null ? e.comments : '—'}
                                            </td>
                                            <td>
                                                <button class="text-white/20 hover:text-red-400 text-xs transition-all" title="Delete" onclick="deleteEvaluation(${e.evalId})">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="6" class="text-center text-white/20 py-8">
                                            <div class="flex flex-col items-center gap-2">
                                                <i class="fas fa-clipboard text-3xl text-white/10"></i>
                                                <span class="text-sm">No evaluations found</span>
                                                <span class="text-xs text-white/10">Add an evaluation to track vendor performance</span>
                                            </div>
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>
            </c:if>

            <c:if test="${empty vendor and not empty param.vendorId}">
                <div class="dashboard-card">
                    <div class="text-center text-white/20 py-8">
                        <div class="flex flex-col items-center gap-2">
                            <i class="fas fa-building text-3xl text-white/10"></i>
                            <span class="text-sm">Vendor not found</span>
                        </div>
                    </div>
                </div>
            </c:if>

        </div>
    </main>

    <!-- ===== ADD EVALUATION MODAL ===== -->
    <div id="addEvaluationModal" class="status-modal-overlay">
        <div class="status-modal-content">
            <div class="status-modal-header">
                <h3>Add Vendor Evaluation</h3>
                <button class="close-btn" onclick="closeAddEvaluationModal()">
                    <i class="fas fa-times"></i>
                </button>
            </div>
            <p class="status-modal-subtitle">Rate the vendor's performance</p>

            <form id="evaluationCreateForm" action="${pageContext.request.contextPath}/vendors/performance/create" method="POST">
                <input type="hidden" name="vendorId" id="evalVendorId" value="${param.vendorId}">

                <div class="status-modal-body">
                    <!-- Score -->
                    <div class="form-group">
                        <label for="evalScore" class="text-white/40 text-xs font-medium block mb-1.5">Score (0.0 - 5.0) <span class="text-dracula-red">*</span></label>
                        <input type="number" id="evalScore" name="score" min="0" max="5" step="0.01"
                               placeholder="e.g., 4.5"
                               class="form-control w-full px-3 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:outline-none transition-all" required>
                    </div>

                    <!-- Evaluation Date -->
                    <div class="form-group">
                        <label for="evalDate" class="text-white/40 text-xs font-medium block mb-1.5">Evaluation Date</label>
                        <input type="date" id="evalDate" name="evaluationDate"
                               class="form-control w-full px-3 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white text-sm focus:border-dracula-red focus:outline-none transition-all">
                    </div>

                    <!-- Comments -->
                    <div class="form-group">
                        <label for="evalComments" class="text-white/40 text-xs font-medium block mb-1.5">Comments</label>
                        <textarea id="evalComments" name="comments" rows="3"
                                  placeholder="Add comments about the vendor's performance..."
                                  class="form-control w-full px-3 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:outline-none transition-all"></textarea>
                    </div>
                </div>

                <div class="status-modal-actions">
                    <button type="button" class="btn-cancel" onclick="closeAddEvaluationModal()">Cancel</button>
                    <button type="submit" class="btn-update">
                        <i class="fas fa-save mr-1.5"></i> Save Evaluation
                    </button>
                </div>
            </form>
        </div>
    </div>

    <!-- ===== FOOTER ===== -->
    <jsp:include page="/WEB-INF/views/footer.jsp" />
</div>

<!-- Custom JavaScript -->
<script src="${pageContext.request.contextPath}/js/vendor-performance.js"></script>
</body>
</html>