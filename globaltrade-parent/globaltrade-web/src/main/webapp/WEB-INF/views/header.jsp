<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 7/31/2026
  Time: 10:24 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%
    // Check if user is logged in
    Object userObj = session.getAttribute("user");
    if (userObj == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    String fullName = (String) session.getAttribute("fullName");
    String role = (String) session.getAttribute("role");

    // Get initials for avatar
    String initials = "";
    if (fullName != null && !fullName.isEmpty()) {
        String[] parts = fullName.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                sb.append(part.charAt(0));
            }
        }
        initials = sb.toString().toUpperCase();
        if (initials.length() > 2) initials = initials.substring(0, 2);
    }
%>

<header class="header-glass fixed top-0 left-0 right-0 z-50 h-[60px] px-4 flex items-center justify-between">
    <!-- Left: Logo + Sidebar Toggle -->
    <div class="flex items-center gap-3">
        <button id="sidebarToggle" class="lg:hidden text-white/40 hover:text-white transition-all p-1.5 -ml-1">
            <i class="fas fa-bars text-lg"></i>
        </button>
        <a href="${pageContext.request.contextPath}/dashboard" class="flex items-center gap-2.5 group">
            <div class="w-8 h-8 flex items-center justify-center bg-gradient-to-br from-dracula-red to-red-900 rounded-lg text-white shadow-lg shadow-red-500/20 group-hover:shadow-red-500/40 transition-all">
                <i class="fas fa-ship text-sm"></i>
            </div>
            <div class="flex items-baseline">
                <span class="text-sm font-extrabold tracking-widest text-white uppercase">
                    GLOBAL<span class="text-dracula-red text-[8px]">®</span>
                </span>
                <span class="text-[10px] font-light text-white/30 ml-1 hidden sm:inline">TRADE LOGISTICS</span>
            </div>
        </a>
    </div>

    <!-- Center: Time -->
    <div class="hidden lg:flex items-center gap-2 text-white/30 text-xs absolute left-1/2 -translate-x-1/2">
        <i class="fas fa-clock text-dracula-red"></i>
        <span id="currentTime">Loading...</span>
    </div>

    <!-- Right: Navigation Buttons -->
    <div class="flex items-center gap-1">
        <button class="header-btn active" onclick="window.location.href='${pageContext.request.contextPath}/dashboard'">
            <i class="fas fa-th-large"></i>
            <span class="hidden sm:inline">Dashboard</span>
        </button>
        <button class="header-btn" onclick="window.location.href='${pageContext.request.contextPath}/help.jsp'">
            <i class="fas fa-circle-question"></i>
            <span class="hidden sm:inline">Help</span>
        </button>
        <button class="header-btn" onclick="window.location.href='${pageContext.request.contextPath}/user/settings.jsp'">
            <i class="fas fa-gear"></i>
            <span class="hidden sm:inline">Settings</span>
        </button>
        <div class="w-px h-6 bg-white/5 mx-1"></div>
        <button class="header-btn header-btn-notif relative">
            <i class="fas fa-bell"></i>
            <span class="absolute -top-0.5 -right-0.5 w-2 h-2 bg-dracula-red rounded-full border-2 border-black"></span>
        </button>

        <!-- User Avatar -->
        <a href="${pageContext.request.contextPath}/user/user-profile.jsp"
           class="flex items-center gap-2 ml-1 cursor-pointer group hover:bg-white/5 rounded-lg px-2 py-1 transition-all">
            <div class="header-user-avatar">
                <span><%= initials %></span>
            </div>
            <div class="hidden md:block">
                <div class="text-xs font-medium text-white/80 group-hover:text-white transition-all"><%= fullName != null ? fullName : "User" %></div>
                <div class="text-[9px] text-white/25"><%= role != null ? role.replace("_", " ") : "Guest" %></div>
            </div>
            <i class="fas fa-chevron-down text-white/15 text-[10px] group-hover:text-white/40 transition-all"></i>
        </a>

        <button class="header-btn header-btn-logout ml-0.5" onclick="openLogoutModal()">
            <i class="fas fa-right-from-bracket"></i>
            <span class="hidden sm:inline">Logout</span>
        </button>
    </div>
</header>