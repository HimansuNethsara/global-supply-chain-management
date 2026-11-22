<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 8/28/2026
  Time: 12:00 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%--@elvariable id="exceptionLogs" type=""--%>
<!DOCTYPE html>
<html lang="en" data-theme="dracula">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GlobalTrade Logistics - Exception Logs</title>

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
                    <h1 class="text-xl sm:text-2xl font-bold text-white">Exception Logs</h1>
                    <p class="text-white/30 text-sm font-light">System exception tracking and monitoring</p>
                </div>
                <div class="flex items-center gap-2">
                    <button onclick="refreshLogs()" class="px-4 py-2 bg-dracula-red/20 border border-dracula-red/30 rounded-lg text-dracula-red text-sm font-medium hover:bg-dracula-red/30 transition-all flex items-center gap-2">
                        <i class="fas fa-sync-alt"></i>
                        <span>Refresh</span>
                    </button>
                    <button onclick="window.print()" class="px-4 py-2 bg-white/5 border border-white/5 rounded-lg text-white/50 hover:text-white hover:bg-white/10 transition-all text-xs flex items-center gap-2">
                        <i class="fas fa-file-export"></i>
                        <span>Export</span>
                    </button>
                </div>
            </div>

            <!-- ===== STATS ===== -->
            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
                <div class="dashboard-card bg-white/3">
                    <div class="text-center">
                        <label class="text-white/30 text-xs block uppercase tracking-wider">Total Exceptions</label>
                        <div class="text-3xl font-bold text-white mt-1">${fn:length(exceptionLogs)}</div>
                    </div>
                </div>
                <div class="dashboard-card bg-red-500/5 border-red-500/20">
                    <div class="text-center">
                        <label class="text-red-400/60 text-xs block uppercase tracking-wider">Unresolved</label>
                        <div class="text-3xl font-bold text-red-400 mt-1">${unresolvedCount}</div>
                    </div>
                </div>
                <div class="dashboard-card bg-green-500/5 border-green-500/20">
                    <div class="text-center">
                        <label class="text-green-400/60 text-xs block uppercase tracking-wider">Resolved</label>
                        <div class="text-3xl font-bold text-green-400 mt-1">${resolvedCount}</div>
                    </div>
                </div>
                <div class="dashboard-card bg-yellow-500/5 border-yellow-500/20">
                    <div class="text-center">
                        <label class="text-yellow-400/60 text-xs block uppercase tracking-wider">Application Exceptions</label>
                        <div class="text-3xl font-bold text-yellow-400 mt-1">${appExceptionCount}</div>
                    </div>
                </div>
            </div>

            <!-- ===== FILTER SECTION ===== -->
            <div class="dashboard-card mb-6">
                <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                    <div class="form-group">
                        <label for="searchInput" class="text-white/40 text-xs font-medium block mb-1.5">Search</label>
                        <input type="text" id="searchInput" placeholder="Search exceptions..."
                               class="form-control w-full px-3 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:outline-none transition-all">
                    </div>
                    <div class="form-group">
                        <label for="exceptionTypeFilter" class="text-white/40 text-xs font-medium block mb-1.5">Exception Type</label>
                        <div class="select-wrapper">
                            <select id="exceptionTypeFilter" class="form-select w-full px-3 py-2.5 pr-10 bg-white/5 border border-white/10 rounded-lg text-white text-sm focus:border-dracula-red focus:outline-none transition-all appearance-none cursor-pointer">
                                <option value="" class="bg-black text-white/40">All Types</option>
                                <option value="ApplicationException" class="bg-black">Application Exception</option>
                                <option value="SystemException" class="bg-black">System Exception</option>
                                <option value="NullPointerException" class="bg-black">NullPointerException</option>
                                <option value="SQLException" class="bg-black">SQLException</option>
                                <option value="IOException" class="bg-black">IOException</option>
                                <option value="TimerException" class="bg-black">Timer Exception</option>
                                <option value="SecurityException" class="bg-black">Security Exception</option>
                            </select>
                            <span class="absolute right-3 top-1/2 -translate-y-1/2 text-white/20 pointer-events-none">
                                <i class="fas fa-chevron-down text-xs"></i>
                            </span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="statusFilter" class="text-white/40 text-xs font-medium block mb-1.5">Status</label>
                        <div class="select-wrapper">
                            <select id="statusFilter" class="form-select w-full px-3 py-2.5 pr-10 bg-white/5 border border-white/10 rounded-lg text-white text-sm focus:border-dracula-red focus:outline-none transition-all appearance-none cursor-pointer">
                                <option value="" class="bg-black text-white/40">All Status</option>
                                <option value="false" class="bg-black">Unresolved</option>
                                <option value="true" class="bg-black">Resolved</option>
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

            <!-- ===== EXCEPTION LOGS TABLE ===== -->
            <div class="dashboard-card overflow-hidden">
                <div class="overflow-x-auto">
                    <table class="transaction-table">
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>Exception Type</th>
                            <th>Message</th>
                            <th>Entity</th>
                            <th>Occurred At</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${not empty exceptionLogs}">
                                <c:forEach var="e" items="${exceptionLogs}" varStatus="status">
                                    <tr>
                                        <td class="text-white/40 text-xs">${status.index + 1}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${e.exceptionType == 'ApplicationException'}">
                                                        <span class="px-2 py-0.5 bg-yellow-500/20 text-yellow-400 rounded-full text-[10px] font-medium">
                                                            <i class="fas fa-exclamation-circle mr-1"></i>Application
                                                        </span>
                                                </c:when>
                                                <c:when test="${e.exceptionType == 'SystemException'}">
                                                        <span class="px-2 py-0.5 bg-red-500/20 text-red-400 rounded-full text-[10px] font-bold">
                                                            <i class="fas fa-bug mr-1"></i>System
                                                        </span>
                                                </c:when>
                                                <c:when test="${e.exceptionType == 'NullPointerException'}">
                                                        <span class="px-2 py-0.5 bg-purple-500/20 text-purple-400 rounded-full text-[10px] font-medium">
                                                            <i class="fas fa-database mr-1"></i>Null Pointer
                                                        </span>
                                                </c:when>
                                                <c:when test="${e.exceptionType == 'SQLException'}">
                                                        <span class="px-2 py-0.5 bg-blue-500/20 text-blue-400 rounded-full text-[10px] font-medium">
                                                            <i class="fas fa-database mr-1"></i>SQL
                                                        </span>
                                                </c:when>
                                                <c:when test="${e.exceptionType == 'IOException'}">
                                                        <span class="px-2 py-0.5 bg-cyan-500/20 text-cyan-400 rounded-full text-[10px] font-medium">
                                                            <i class="fas fa-file mr-1"></i>IO
                                                        </span>
                                                </c:when>
                                                <c:when test="${e.exceptionType == 'TimerException'}">
                                                        <span class="px-2 py-0.5 bg-orange-500/20 text-orange-400 rounded-full text-[10px] font-medium">
                                                            <i class="fas fa-clock mr-1"></i>Timer
                                                        </span>
                                                </c:when>
                                                <c:when test="${e.exceptionType == 'SecurityException'}">
                                                        <span class="px-2 py-0.5 bg-red-500/20 text-red-400 rounded-full text-[10px] font-medium">
                                                            <i class="fas fa-shield mr-1"></i>Security
                                                        </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="px-2 py-0.5 bg-white/10 text-white/60 rounded-full text-[10px]">${e.exceptionType}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-white/50 text-xs max-w-[200px] truncate" title="${e.message}">
                                                ${e.message != null ? e.message : 'N/A'}
                                        </td>
                                        <td>
                                            <c:if test="${e.entityType != null && e.entityId != null}">
                                                <span class="text-white/40 text-xs">${e.entityType} #${e.entityId}</span>
                                            </c:if>
                                            <c:if test="${e.entityType == null || e.entityId == null}">
                                                <span class="text-white/20 text-xs">—</span>
                                            </c:if>
                                        </td>
                                        <td class="text-white/30 text-xs">
                                                ${e.occurredAt != null ? e.occurredAt.toString().replace('T', ' ').substring(0, 16) : 'N/A'}
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${e.resolved}">
                                                    <span class="transaction-status completed">Resolved</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="transaction-status pending">Unresolved</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <div class="flex items-center gap-2">
                                                <button class="text-blue-400/60 hover:text-blue-400 text-xs transition-all" title="View Details" onclick="viewExceptionDetails(${e.exceptionId})">
                                                    <i class="fas fa-eye"></i>
                                                </button>
                                                <c:if test="${!e.resolved}">
                                                    <button class="text-green-400/60 hover:text-green-400 text-xs transition-all" title="Mark Resolved" onclick="resolveException(${e.exceptionId})">
                                                        <i class="fas fa-check"></i>
                                                    </button>
                                                </c:if>
                                                <button class="text-white/20 hover:text-red-400 text-xs transition-all" title="Delete" onclick="deleteException(${e.exceptionId})">
                                                    <i class="fas fa-trash"></i>
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
                                            <span class="text-sm">No exceptions found</span>
                                            <span class="text-xs text-white/10">All systems are running normally</span>
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
                    <div class="text-white/20 text-xs">Showing ${fn:length(exceptionLogs)} exceptions</div>
                    <div class="flex items-center gap-1">
                        <button class="px-3 py-1 bg-white/5 border border-white/10 rounded-lg text-white/30 text-xs hover:bg-white/10 transition-all" disabled>Previous</button>
                        <button class="px-3 py-1 bg-dracula-red/20 border border-dracula-red/30 rounded-lg text-dracula-red text-xs hover:bg-dracula-red/30 transition-all">1</button>
                        <button class="px-3 py-1 bg-white/5 border border-white/10 rounded-lg text-white/30 text-xs hover:bg-white/10 transition-all" disabled>Next</button>
                    </div>
                </div>
            </div>

        </div>
    </main>

    <!-- ===== EXCEPTION DETAIL MODAL ===== -->
    <div id="exceptionModal" class="fixed inset-0 z-50 hidden">
        <div class="absolute inset-0 bg-black/70 backdrop-blur-sm" onclick="closeExceptionModal()"></div>
        <div class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-full max-w-3xl p-4">
            <div class="bg-[#0d0d0d] border border-white/10 rounded-2xl p-6 shadow-2xl shadow-red-900/20 max-h-[80vh] overflow-y-auto">
                <div class="flex items-center justify-between mb-4">
                    <h3 class="text-white font-bold text-xl">Exception Details</h3>
                    <button onclick="closeExceptionModal()" class="text-white/40 hover:text-white transition-all">
                        <i class="fas fa-times text-xl"></i>
                    </button>
                </div>
                <div id="exceptionDetailsContent">
                    <div class="space-y-4">
                        <div>
                            <label class="text-white/30 text-xs block">Exception ID</label>
                            <div class="text-white/80 text-sm" id="exceptionId">-</div>
                        </div>
                        <div>
                            <label class="text-white/30 text-xs block">Type</label>
                            <div class="text-white/80 text-sm" id="exceptionType">-</div>
                        </div>
                        <div>
                            <label class="text-white/30 text-xs block">Message</label>
                            <div class="text-white/80 text-sm" id="exceptionMessage">-</div>
                        </div>
                        <div>
                            <label class="text-white/30 text-xs block">Stack Trace</label>
                            <div class="bg-black/50 border border-white/10 rounded-lg p-4 font-mono text-[10px] text-white/60 overflow-x-auto max-h-48" id="exceptionStackTrace">
                                -
                            </div>
                        </div>
                        <div class="grid grid-cols-2 gap-4">
                            <div>
                                <label class="text-white/30 text-xs block">Entity</label>
                                <div class="text-white/60 text-sm" id="exceptionEntity">-</div>
                            </div>
                            <div>
                                <label class="text-white/30 text-xs block">Occurred At</label>
                                <div class="text-white/60 text-sm" id="exceptionOccurredAt">-</div>
                            </div>
                        </div>
                        <div>
                            <label class="text-white/30 text-xs block">Status</label>
                            <div class="text-white/60 text-sm" id="exceptionStatus">-</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- ===== FOOTER ===== -->
    <jsp:include page="/WEB-INF/views/footer.jsp" />
</div>

<!-- Custom JavaScript -->
<script src="${pageContext.request.contextPath}/js/monitoring.js"></script>
</body>
</html>