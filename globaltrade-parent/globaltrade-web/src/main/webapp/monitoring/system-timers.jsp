<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 8/26/2026
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
    <title>GlobalTrade Logistics - System Timers</title>

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
                    <h1 class="text-xl sm:text-2xl font-bold text-white">System Timers</h1>
                    <p class="text-white/30 text-sm font-light">Monitor and manage scheduled system tasks</p>
                </div>
                <div class="flex items-center gap-2">
                    <button onclick="refreshTimers()" class="px-4 py-2 bg-dracula-red/20 border border-dracula-red/30 rounded-lg text-dracula-red text-sm font-medium hover:bg-dracula-red/30 transition-all flex items-center gap-2">
                        <i class="fas fa-sync-alt"></i>
                        <span>Refresh</span>
                    </button>
                    <button onclick="window.print()" class="px-4 py-2 bg-white/5 border border-white/5 rounded-lg text-white/50 hover:text-white hover:bg-white/10 transition-all text-xs flex items-center gap-2">
                        <i class="fas fa-file-export"></i>
                        <span>Export</span>
                    </button>
                </div>
            </div>

            <!-- ===== TIMER STATS ===== -->
            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
                <div class="dashboard-card bg-white/3">
                    <div class="text-center">
                        <label class="text-white/30 text-xs block uppercase tracking-wider">Total Timers</label>
                        <div class="text-3xl font-bold text-white mt-1">${fn:length(timers)}</div>
                    </div>
                </div>
                <div class="dashboard-card bg-green-500/5 border-green-500/20">
                    <div class="text-center">
                        <label class="text-green-400/60 text-xs block uppercase tracking-wider">Active</label>
                        <div class="text-3xl font-bold text-green-400 mt-1">${activeCount}</div>
                    </div>
                </div>
                <div class="dashboard-card bg-yellow-500/5 border-yellow-500/20">
                    <div class="text-center">
                        <label class="text-yellow-400/60 text-xs block uppercase tracking-wider">Paused</label>
                        <div class="text-3xl font-bold text-yellow-400 mt-1">${pausedCount}</div>
                    </div>
                </div>
                <div class="dashboard-card bg-red-500/5 border-red-500/20">
                    <div class="text-center">
                        <label class="text-red-400/60 text-xs block uppercase tracking-wider">Failed</label>
                        <div class="text-3xl font-bold text-red-400 mt-1">${failedCount}</div>
                    </div>
                </div>
            </div>

            <!-- ===== TIMERS TABLE ===== -->
            <div class="dashboard-card overflow-hidden">
                <div class="overflow-x-auto">
                    <table class="transaction-table">
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>Timer Name</th>
                            <th>Type</th>
                            <th>Schedule Expression</th>
                            <th>Last Run</th>
                            <th>Next Run</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${not empty timers}">
                                <c:forEach var="t" items="${timers}" varStatus="status">
                                    <tr>
                                        <td class="text-white/40 text-xs">${status.index + 1}</td>
                                        <td class="text-white/70 text-sm font-medium">${t.timerName}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${t.timerType == 'PROGRAMMATIC'}">
                                                    <span class="px-2 py-0.5 bg-blue-500/20 text-blue-400 rounded-full text-[10px] font-medium">Programmatic</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="px-2 py-0.5 bg-purple-500/20 text-purple-400 rounded-full text-[10px] font-medium">Declarative</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-white/50 text-xs font-mono">${t.scheduleExpression != null ? t.scheduleExpression : 'N/A'}</td>
                                        <td class="text-white/30 text-xs">
                                                ${t.lastRun != null ? t.lastRun.toString().replace('T', ' ').substring(0, 16) : 'Never'}
                                        </td>
                                        <td class="text-white/30 text-xs">
                                                ${t.nextRun != null ? t.nextRun.toString().replace('T', ' ').substring(0, 16) : 'N/A'}
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${t.status == 'ACTIVE'}">
                                                    <span class="transaction-status completed">Active</span>
                                                </c:when>
                                                <c:when test="${t.status == 'PAUSED'}">
                                                    <span class="transaction-status pending">Paused</span>
                                                </c:when>
                                                <c:when test="${t.status == 'FAILED'}">
                                                    <span class="transaction-status failed">Failed</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="transaction-status pending">${t.status}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <div class="flex items-center gap-2">
                                                <c:if test="${t.status == 'ACTIVE'}">
                                                    <button class="text-yellow-400/60 hover:text-yellow-400 text-xs transition-all" title="Pause" onclick="updateTimerStatus(${t.timerId}, 'PAUSED')">
                                                        <i class="fas fa-pause"></i>
                                                    </button>
                                                </c:if>
                                                <c:if test="${t.status == 'PAUSED'}">
                                                    <button class="text-green-400/60 hover:text-green-400 text-xs transition-all" title="Resume" onclick="updateTimerStatus(${t.timerId}, 'ACTIVE')">
                                                        <i class="fas fa-play"></i>
                                                    </button>
                                                </c:if>
                                                <c:if test="${t.status != 'FAILED'}">
                                                    <button class="text-blue-400/60 hover:text-blue-400 text-xs transition-all" title="Run Now" onclick="runTimerNow(${t.timerId})">
                                                        <i class="fas fa-play-circle"></i>
                                                    </button>
                                                </c:if>
                                                <button class="text-white/20 hover:text-red-400 text-xs transition-all" title="Delete" onclick="deleteTimer(${t.timerId}, '${t.timerName}')">
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
                                            <i class="fas fa-clock text-3xl text-white/10"></i>
                                            <span class="text-sm">No system timers found</span>
                                            <span class="text-xs text-white/10">Timers will appear when scheduled tasks are created</span>
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
                    <div class="text-white/20 text-xs">Showing ${fn:length(timers)} timers</div>
                    <div class="flex items-center gap-1">
                        <button class="px-3 py-1 bg-white/5 border border-white/10 rounded-lg text-white/30 text-xs hover:bg-white/10 transition-all" disabled>Previous</button>
                        <button class="px-3 py-1 bg-dracula-red/20 border border-dracula-red/30 rounded-lg text-dracula-red text-xs hover:bg-dracula-red/30 transition-all">1</button>
                        <button class="px-3 py-1 bg-white/5 border border-white/10 rounded-lg text-white/30 text-xs hover:bg-white/10 transition-all" disabled>Next</button>
                    </div>
                </div>
            </div>

        </div>
    </main>

    <!-- ===== FOOTER ===== -->
    <jsp:include page="/WEB-INF/views/footer.jsp" />
</div>

<!-- Custom JavaScript -->
<script src="${pageContext.request.contextPath}/js/monitoring.js"></script>
</body>
</html>