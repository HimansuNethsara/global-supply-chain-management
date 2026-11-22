<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 7/26/2026
  Time: 7:11 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en" data-theme="dracula">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GlobalTrade Logistics - Secure Login</title>

    <!-- Favicon -->
    <link rel="icon" type="image/png" sizes="32x32" href="${pageContext.request.contextPath}/images/favicon.png">

    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&display=swap" rel="stylesheet">

    <!-- Font Awesome 7 - Local -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/all.min.css">

    <!-- Notyf CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/notyf.min.css">

    <!-- Tailwind CSS Output -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/output.css">
</head>
<body class="min-h-screen flex items-center justify-center font-inter bg-black overflow-hidden p-4">

<!-- ========== BACKGROUND ========== -->
<div class="fixed inset-0 z-0 overflow-hidden">
    <div class="absolute inset-0 bg-cover bg-center bg-no-repeat animate-imageZoom"
         style="background-image: url('${pageContext.request.contextPath}/images/login-background.jpg');"></div>
    <div class="absolute inset-0 z-[1] bg-gradient-to-b from-black/85 via-black/70 via-red-900/10 to-black/85 animate-bgPulse"></div>
    <div class="glow-orb w-[600px] h-[600px] -top-[200px] -left-[200px] bg-red-500/30 animate-orbFloat"></div>
    <div class="glow-orb w-[400px] h-[400px] -bottom-[100px] -right-[100px] bg-red-900/20 animate-orbFloat" style="animation-delay: -5s;"></div>
    <div class="glow-orb w-[300px] h-[300px] top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-red-500/10 animate-orbFloat" style="animation-delay: -10s;"></div>
</div>

<!-- ========== MAIN CONTAINER ========== -->
<div class="container relative z-10 grid grid-cols-1 lg:grid-cols-[1fr_1.1fr] max-w-6xl w-full min-h-[600px] lg:min-h-[650px] glass rounded-2xl border border-white/5 shadow-2xl shadow-red-900/20 overflow-hidden">

    <!-- ========== LEFT PANEL - BRANDING ========== -->
    <div class="bg-gradient-to-br from-white/5 to-white/0 p-6 sm:p-8 lg:p-10 flex items-center border-r border-white/5 relative overflow-hidden">
        <div class="absolute top-0 left-0 right-0 h-1 bg-gradient-to-r from-dracula-red to-red-900 opacity-30"></div>

        <div class="relative z-10 w-full">
            <!-- Logo -->
            <div class="flex items-center gap-3 mb-6">
                <div class="logo-icon-hover w-10 h-10 sm:w-12 sm:h-12 flex items-center justify-center bg-gradient-to-br from-dracula-red to-red-900 rounded-xl text-lg sm:text-xl text-white shadow-lg shadow-red-500/30 cursor-pointer">
                    <i class="fas fa-ship"></i>
                </div>
                <div>
                    <div class="text-lg sm:text-xl font-extrabold tracking-widest text-white uppercase">
                        GLOBAL<span class="text-[8px] sm:text-[10px] text-dracula-red ml-0.5">®</span>
                    </div>
                    <div class="text-[8px] sm:text-[10px] font-semibold tracking-[4px] sm:tracking-[5px] text-dracula-red uppercase">TRADE LOGISTICS</div>
                </div>
            </div>

            <div class="w-10 sm:w-14 h-1 bg-gradient-to-r from-dracula-red to-red-900 rounded mb-5"></div>

            <h1 class="text-xl sm:text-2xl lg:text-3xl font-extrabold text-white mb-1 animate-fadeIn">Welcome Back</h1>
            <p class="text-white/50 text-xs sm:text-sm font-light leading-relaxed mb-5 animate-fadeIn" style="animation-delay: 0.1s;">
                Secure your supply chain operations with GlobalTrade's advanced logistics platform
            </p>

            <!-- Trust Badges -->
            <div class="flex flex-wrap gap-1.5 sm:gap-2 mb-5">
                <div class="flex items-center gap-1.5 px-2.5 sm:px-3 py-1 sm:py-1.5 bg-white/5 border border-white/5 rounded-full text-white/80 text-[8px] sm:text-[10px] font-medium hover:bg-red-500/10 hover:border-red-500/20 transition-all animate-fadeIn" style="animation-delay: 0.2s;">
                    <i class="fas fa-shield-halved text-dracula-red text-[10px] sm:text-xs"></i> ISO 27001
                </div>
                <div class="flex items-center gap-1.5 px-2.5 sm:px-3 py-1 sm:py-1.5 bg-white/5 border border-white/5 rounded-full text-white/80 text-[8px] sm:text-[10px] font-medium hover:bg-red-500/10 hover:border-red-500/20 transition-all animate-fadeIn" style="animation-delay: 0.3s;">
                    <i class="fas fa-lock text-dracula-red text-[10px] sm:text-xs"></i> GDPR Compliant
                </div>
                <div class="flex items-center gap-1.5 px-2.5 sm:px-3 py-1 sm:py-1.5 bg-white/5 border border-white/5 rounded-full text-white/80 text-[8px] sm:text-[10px] font-medium hover:bg-red-500/10 hover:border-red-500/20 transition-all animate-fadeIn" style="animation-delay: 0.4s;">
                    <i class="fas fa-certificate text-dracula-red text-[10px] sm:text-xs"></i> Customs Compliant
                </div>
            </div>

            <!-- Features -->
            <div class="space-y-2 mb-5 hidden sm:block">
                <div class="feature-item-hover flex gap-3 p-2.5 bg-white/5 rounded-lg border border-white/5 cursor-default">
                    <div class="w-7 h-7 min-w-[28px] flex items-center justify-center bg-red-500/10 rounded-lg text-dracula-red text-xs">
                        <i class="fas fa-truck-fast"></i>
                    </div>
                    <div>
                        <div class="text-xs font-semibold text-white/90">Real-time Shipment Tracking</div>
                        <div class="text-[10px] text-white/50 font-light">End-to-end visibility across global routes</div>
                    </div>
                </div>
                <div class="feature-item-hover flex gap-3 p-2.5 bg-white/5 rounded-lg border border-white/5 cursor-default">
                    <div class="w-7 h-7 min-w-[28px] flex items-center justify-center bg-red-500/10 rounded-lg text-dracula-red text-xs">
                        <i class="fas fa-chart-line"></i>
                    </div>
                    <div>
                        <div class="text-xs font-semibold text-white/90">Vendor Performance Monitoring</div>
                        <div class="text-[10px] text-white/50 font-light">Real-time supplier performance analytics</div>
                    </div>
                </div>
                <div class="feature-item-hover flex gap-3 p-2.5 bg-white/5 rounded-lg border border-white/5 cursor-default">
                    <div class="w-7 h-7 min-w-[28px] flex items-center justify-center bg-red-500/10 rounded-lg text-dracula-red text-xs">
                        <i class="fas fa-globe"></i>
                    </div>
                    <div>
                        <div class="text-xs font-semibold text-white/90">Global Logistics Network</div>
                        <div class="text-[10px] text-white/50 font-light">50+ countries with carrier integration</div>
                    </div>
                </div>
            </div>

            <!-- Security Notice -->
            <div class="flex gap-2.5 p-2.5 bg-red-500/5 border-l-3 border-dracula-red rounded-lg animate-fadeIn" style="animation-delay: 0.5s;">
                <i class="fas fa-triangle-exclamation text-dracula-red text-sm"></i>
                <div>
                    <div class="text-[10px] sm:text-xs font-semibold text-white/90">Security Alert:</div>
                    <p class="text-[8px] sm:text-[10px] text-white/50 font-light leading-relaxed">Never share your credentials. GlobalTrade will never ask for your password.</p>
                </div>
            </div>
        </div>
    </div>

    <!-- ========== RIGHT PANEL - LOGIN FORM ========== -->
    <div class="p-5 sm:p-8 lg:p-10 flex items-center justify-center bg-gradient-to-br from-white/5 to-white/0">
        <div class="w-full max-w-sm animate-fadeIn" style="animation-delay: 0.2s;">

            <!-- Header -->
            <div class="mb-5">
                <span class="text-[8px] sm:text-[10px] font-semibold tracking-[2px] uppercase text-dracula-red">Welcome back</span>
                <h2 class="text-lg sm:text-xl lg:text-2xl font-bold text-white mt-0.5">Sign in to your account</h2>
                <p class="text-white/40 text-[10px] sm:text-xs font-light">Enter your credentials to access your dashboard</p>
            </div>

            <!-- Login Form -->
            <form id="loginForm" class="space-y-3" action="${pageContext.request.contextPath}/login" method="POST">

                <!-- Username Field -->
                <div>
                    <label for="username" class="text-[10px] sm:text-xs font-medium text-white/70 flex items-center gap-1.5 pb-1">
                        <i class="fas fa-user text-dracula-red text-[10px]"></i> Username
                    </label>
                    <div class="relative">
                        <input type="text" id="username" name="username"
                               placeholder="Enter your username"
                               class="input-focus-glow w-full px-3 py-2.5 pr-9 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:bg-white/5 focus:outline-none transition-all">
                        <span class="absolute right-3 top-1/2 -translate-y-1/2 text-white/15 text-xs"><i class="fas fa-user"></i></span>
                    </div>
                </div>

                <!-- Password Field -->
                <div>
                    <label for="password" class="text-[10px] sm:text-xs font-medium text-white/70 flex items-center gap-1.5 pb-1">
                        <i class="fas fa-lock text-dracula-red text-[10px]"></i> Password
                    </label>
                    <div class="relative password-wrapper">
                        <input type="password" id="password" name="password"
                               placeholder="Enter your password"
                               class="input-focus-glow w-full px-3 py-2.5 pr-11 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:bg-white/5 focus:outline-none transition-all">
                        <button type="button" class="toggle-password absolute right-2 top-1/2 -translate-y-1/2 text-white/25 hover:text-white hover:bg-white/5 p-1.5 rounded-full transition-all">
                            <i class="fas fa-eye text-xs"></i>
                        </button>
                    </div>
                    <div class="text-[8px] sm:text-[9px] text-white/20 font-light mt-0.5">Min 8 chars with uppercase, lowercase, number & special</div>
                </div>

                <!-- Form Options -->
                <div class="flex flex-wrap justify-between items-center gap-2 py-0.5">
                    <label class="remember-me flex items-center gap-2 text-white/50 text-[10px] sm:text-xs cursor-pointer relative pl-5">
                        <input type="checkbox" name="remember" id="remember" class="absolute opacity-0 cursor-pointer">
                        <span class="checkmark absolute left-0 top-1/2 -translate-y-1/2 w-3.5 h-3.5 bg-white/5 border border-white/10 rounded transition-all hover:border-dracula-red"></span>
                        Remember me
                    </label>
                    <a href="#" class="text-white/40 text-[10px] sm:text-xs hover:text-dracula-red transition-all flex items-center gap-1">
                        <i class="fas fa-key text-[8px]"></i> Forgot Password?
                    </a>
                </div>

                <!-- Login Button -->
                <button type="submit" class="btn-shimmer w-full bg-gradient-to-r from-dracula-red to-red-900 text-white text-sm font-semibold py-2.5 rounded-lg hover:shadow-lg hover:shadow-red-500/30 hover:-translate-y-0.5 transition-all">
                    Sign In
                </button>

                <!-- Divider -->
                <div class="flex items-center gap-3 py-0.5">
                    <div class="flex-1 h-px bg-white/5"></div>
                    <span class="text-white/20 text-[9px] sm:text-[10px] font-light whitespace-nowrap">New to GlobalTrade?</span>
                    <div class="flex-1 h-px bg-white/5"></div>
                </div>

                <!-- Register Link -->
                <a href="${pageContext.request.contextPath}/register" class="group flex items-center justify-center gap-2 p-2.5 bg-white/5 border border-white/5 rounded-lg text-white/60 hover:bg-white/10 hover:border-dracula-red hover:text-white hover:-translate-y-0.5 transition-all text-xs sm:text-sm">
                    <i class="fas fa-user-plus text-dracula-red text-xs"></i>
                    Create an Account
                    <i class="register-arrow fas fa-arrow-right text-[8px] sm:text-[10px]"></i>
                </a>
            </form>

            <!-- Footer -->
            <div class="mt-4 text-center">
                <p class="text-white/10 text-[8px] sm:text-[9px] flex items-center justify-center gap-1.5">
                    <i class="fas fa-shield-halved text-dracula-red text-[10px]"></i>
                    Protected under Sri Lanka PDPA No. 9 of 2022
                </p>
            </div>
        </div>
    </div>
</div>

<!-- ===== SESSION NOTIFICATION HIDDEN INPUTS ===== -->
<c:if test="${not empty sessionScope.errorNotification}">
    <input type="hidden" id="sessionErrorMsg" value="${sessionScope.errorNotification}">
    <c:remove var="errorNotification" scope="session" />
</c:if>

<c:if test="${not empty sessionScope.successNotification}">
    <input type="hidden" id="sessionSuccessMsg" value="${sessionScope.successNotification}"
           data-redirect="true" data-redirect-url="${pageContext.request.contextPath}/dashboard">
    <c:remove var="successNotification" scope="session" />
</c:if>

<script src="${pageContext.request.contextPath}/js/notyf.min.js"></script>
<script src="${pageContext.request.contextPath}/js/utils.js"></script>
<script src="${pageContext.request.contextPath}/js/auth.js"></script>
<script src="${pageContext.request.contextPath}/js/notifications.js"></script>

</body>
</html>