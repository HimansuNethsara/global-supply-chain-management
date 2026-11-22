<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 7/29/2026
  Time: 9:11 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en" data-theme="dracula">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GlobalTrade Logistics - Register</title>

    <!-- Favicon -->
    <link rel="icon" type="image/png" sizes="32x32" href="${pageContext.request.contextPath}/images/favicon.png">

    <!-- NOTYF CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/notyf.min.css">

    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&display=swap" rel="stylesheet">

    <!-- Font Awesome 7 - Local -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/all.min.css">

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
<div class="container relative z-10 grid grid-cols-1 lg:grid-cols-[1fr_1.1fr] max-w-6xl w-full min-h-[600px] lg:min-h-[700px] glass rounded-2xl border border-white/5 shadow-2xl shadow-red-900/20 overflow-hidden">

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

            <h1 class="text-xl sm:text-2xl lg:text-3xl font-extrabold text-white mb-1 animate-fadeIn">Create Account</h1>
            <p class="text-white/50 text-xs sm:text-sm font-light leading-relaxed mb-5 animate-fadeIn" style="animation-delay: 0.1s;">
                Register to access the GlobalTrade<br>Supply Chain Management Platform
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

    <!-- ========== RIGHT PANEL - REGISTER FORM ========== -->
    <div class="p-5 sm:p-8 lg:p-10 flex items-center justify-center bg-gradient-to-br from-white/5 to-white/0">
        <div class="w-full max-w-sm animate-fadeIn" style="animation-delay: 0.2s;">

            <!-- Header -->
            <div class="mb-5">
                <span class="text-[8px] sm:text-[10px] font-semibold tracking-[2px] uppercase text-dracula-red">Register Now</span>
                <h2 class="text-lg sm:text-xl lg:text-2xl font-bold text-white mt-0.5">Create Your Account</h2>
                <p class="text-white/40 text-[10px] sm:text-xs font-light">Select your role and enter your details</p>
            </div>

            <!-- Register Form -->
            <form id="registerForm" class="space-y-3" action="${pageContext.request.contextPath}/register" method="POST">
                <input type="hidden" name="csrfToken" value="placeholder_token">

                <!-- ===== ROLE SELECTION - Click to Select ===== -->
                <div>
                    <label class="text-[10px] sm:text-xs font-medium text-white/70 flex items-center gap-1.5 pb-1.5">
                        <i class="fas fa-user-tag text-dracula-red text-[10px]"></i> Select Your Role
                    </label>
                    <button type="button" id="selectRoleBtn" class="w-full px-4 py-3 bg-white/5 border border-white/10 rounded-lg text-white/60 text-sm hover:bg-white/10 hover:border-dracula-red/30 transition-all flex items-center justify-between">
                        <span id="selectedRoleDisplay">Choose a role</span>
                        <i class="fas fa-chevron-down text-white/20 text-xs"></i>
                    </button>
                    <input type="hidden" id="selectedRole" name="role" value="">
                </div>

                <!-- ===== VENDOR SELECTION ===== -->
                <div id="vendorSelection" style="display: none;">
                    <label for="vendorId" class="text-[10px] sm:text-xs font-medium text-white/70 flex items-center gap-1.5 pb-1">
                        <i class="fas fa-building text-dracula-red text-[10px]"></i> Select Vendor
                    </label>
                    <div class="select-wrapper">
                        <select id="vendorId" name="vendorId">
                            <option value="" class="placeholder" disabled selected>Select a Vendor</option>
                            <c:forEach var="v" items="${vendors}">
                                <option value="${v.vendorId}">${v.vendorName} - ${v.country}</option>
                            </c:forEach>
                        </select>
                        <span class="select-arrow">
                            <i class="fas fa-chevron-down"></i>
                        </span>
                    </div>
                </div>

                <!-- ===== STAFF VERIFICATION ===== -->
                <div id="staffVerification" style="display: none;">
                    <label for="nicNumber" class="text-[10px] sm:text-xs font-medium text-white/70 flex items-center gap-1.5 pb-1">
                        <i class="fas fa-id-card text-dracula-red text-[10px]"></i> NIC Number (Verification)
                    </label>
                    <div class="relative">
                        <input type="text" id="nicNumber" name="nicNumber"
                               placeholder="Enter your NIC number for verification"
                               class="input-focus-glow w-full px-3 py-2.5 pr-9 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:bg-white/5 focus:outline-none transition-all">
                        <span class="absolute right-3 top-1/2 -translate-y-1/2 text-white/15 text-xs"><i class="fas fa-id-card"></i></span>
                    </div>
                    <div class="text-[8px] sm:text-[9px] text-white/20 font-light mt-0.5">NIC will be verified against employee records.</div>
                </div>

                <!-- ===== FULL NAME ===== -->
                <div>
                    <label for="fullName" class="text-[10px] sm:text-xs font-medium text-white/70 flex items-center gap-1.5 pb-1">
                        <i class="fas fa-user text-dracula-red text-[10px]"></i> Full Name
                    </label>
                    <div class="relative">
                        <input type="text" id="fullName" name="fullName"
                               placeholder="Enter your full name"
                               class="input-focus-glow w-full px-3 py-2.5 pr-9 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:bg-white/5 focus:outline-none transition-all">
                        <span class="absolute right-3 top-1/2 -translate-y-1/2 text-white/15 text-xs"><i class="fas fa-user"></i></span>
                    </div>
                </div>

                <!-- ===== USERNAME ===== -->
                <div>
                    <label for="username" class="text-[10px] sm:text-xs font-medium text-white/70 flex items-center gap-1.5 pb-1">
                        <i class="fas fa-id-card text-dracula-red text-[10px]"></i> Username
                    </label>
                    <div class="relative">
                        <input type="text" id="username" name="username"
                               placeholder="Choose a username"
                               class="input-focus-glow w-full px-3 py-2.5 pr-9 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:bg-white/5 focus:outline-none transition-all">
                        <span class="absolute right-3 top-1/2 -translate-y-1/2 text-white/15 text-xs"><i class="fas fa-id-card"></i></span>
                    </div>
                </div>

                <!-- ===== EMAIL ===== -->
                <div>
                    <label for="email" class="text-[10px] sm:text-xs font-medium text-white/70 flex items-center gap-1.5 pb-1">
                        <i class="fas fa-envelope text-dracula-red text-[10px]"></i> Email Address
                    </label>
                    <div class="relative">
                        <input type="email" id="email" name="email"
                               placeholder="Enter your email address"
                               class="input-focus-glow w-full px-3 py-2.5 pr-9 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:bg-white/5 focus:outline-none transition-all">
                        <span class="absolute right-3 top-1/2 -translate-y-1/2 text-white/15 text-xs"><i class="fas fa-envelope"></i></span>
                    </div>
                </div>

                <!-- ===== PASSWORD ===== -->
                <div>
                    <label for="password" class="text-[10px] sm:text-xs font-medium text-white/70 flex items-center gap-1.5 pb-1">
                        <i class="fas fa-lock text-dracula-red text-[10px]"></i> Password
                    </label>
                    <div class="relative password-wrapper">
                        <input type="password" id="password" name="password"
                               placeholder="Create a password"
                               class="input-focus-glow w-full px-3 py-2.5 pr-11 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:bg-white/5 focus:outline-none transition-all">
                        <button type="button" class="toggle-password absolute right-2 top-1/2 -translate-y-1/2 text-white/25 hover:text-white hover:bg-white/5 p-1.5 rounded-full transition-all">
                            <i class="fas fa-eye text-xs"></i>
                        </button>
                    </div>
                    <div class="text-[8px] sm:text-[9px] text-white/20 font-light mt-0.5">Min 8 chars with uppercase, lowercase, number & special</div>
                </div>

                <!-- ===== CONFIRM PASSWORD ===== -->
                <div>
                    <label for="confirmPassword" class="text-[10px] sm:text-xs font-medium text-white/70 flex items-center gap-1.5 pb-1">
                        <i class="fas fa-lock text-dracula-red text-[10px]"></i> Confirm Password
                    </label>
                    <div class="relative password-wrapper">
                        <input type="password" id="confirmPassword" name="confirmPassword"
                               placeholder="Confirm your password"
                               class="input-focus-glow w-full px-3 py-2.5 pr-11 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:bg-white/5 focus:outline-none transition-all">
                        <button type="button" class="toggle-password absolute right-2 top-1/2 -translate-y-1/2 text-white/25 hover:text-white hover:bg-white/5 p-1.5 rounded-full transition-all">
                            <i class="fas fa-eye text-xs"></i>
                        </button>
                    </div>
                </div>

                <!-- ===== ROLE INFO MESSAGE ===== -->
                <div id="roleInfo" class="text-[9px] text-white/30 font-light mt-1">
                    <i class="fas fa-info-circle text-dracula-red"></i>
                    <span id="roleInfoText">Please select a role to continue</span>
                </div>

                <!-- ===== REGISTER BUTTON ===== -->
                <button type="submit" id="submitBtn" class="w-full btn-shimmer bg-gradient-to-r from-dracula-red to-red-900 text-white text-sm font-semibold py-2.5 rounded-lg hover:shadow-lg hover:shadow-red-500/30 hover:-translate-y-0.5 transition-all opacity-50 cursor-not-allowed" disabled>
                    <i class="fas fa-user-plus text-xs mr-1.5"></i>
                    Select a Role First
                </button>

                <!-- Divider -->
                <div class="flex items-center gap-3 py-0.5">
                    <div class="flex-1 h-px bg-white/5"></div>
                    <span class="text-white/20 text-[9px] sm:text-[10px] font-light whitespace-nowrap">Already have an account?</span>
                    <div class="flex-1 h-px bg-white/5"></div>
                </div>

                <!-- Login Link -->
                <a href="${pageContext.request.contextPath}/login" class="group flex items-center justify-center gap-2 p-2.5 bg-white/5 border border-white/5 rounded-lg text-white/60 hover:bg-white/10 hover:border-dracula-red hover:text-white hover:-translate-y-0.5 transition-all text-xs sm:text-sm">
                    <i class="fas fa-sign-in-alt text-dracula-red text-xs"></i>
                    Sign in to your account
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

<!-- ===== ROLE SELECTION MODAL ===== -->
<div id="roleModal" class="role-modal-overlay">
    <div class="role-modal-content">
        <div class="role-modal-header">
            <h3>Select Your Role</h3>
            <button class="close-btn" id="modalCloseBtn">
                <i class="fas fa-times"></i>
            </button>
        </div>
        <p class="role-modal-subtitle">Choose the role that best describes your function</p>

        <!-- Customer -->
        <button type="button" class="role-option-btn" data-role="CUSTOMER">
            <div class="icon-wrapper"><i class="fas fa-user"></i></div>
            <div class="role-info">
                <div class="name">Customer</div>
                <div class="desc">Track shipments and manage orders</div>
            </div>
            <i class="fas fa-chevron-right arrow-icon"></i>
        </button>

        <!-- Vendor Representative -->
        <button type="button" class="role-option-btn" data-role="VENDOR_REP">
            <div class="icon-wrapper"><i class="fas fa-building"></i></div>
            <div class="role-info">
                <div class="name">Vendor Representative</div>
                <div class="desc">Manage vendor profile and shipments</div>
            </div>
            <i class="fas fa-chevron-right arrow-icon"></i>
        </button>

        <!-- Customs Agent -->
        <button type="button" class="role-option-btn" data-role="CUSTOMS_AGENT">
            <div class="icon-wrapper"><i class="fas fa-passport"></i></div>
            <div class="role-info">
                <div class="name">Customs Agent</div>
                <div class="desc">Handle customs documentation (Approval required)</div>
            </div>
            <i class="fas fa-chevron-right arrow-icon"></i>
        </button>

        <!-- Logistics Coordinator -->
        <button type="button" class="role-option-btn" data-role="LOGISTICS_COORDINATOR">
            <div class="icon-wrapper"><i class="fas fa-truck"></i></div>
            <div class="role-info">
                <div class="name">Logistics Coordinator</div>
                <div class="desc">Manage shipments and logistics (NIC required)</div>
            </div>
            <i class="fas fa-chevron-right arrow-icon"></i>
        </button>

        <!-- Warehouse Manager -->
        <button type="button" class="role-option-btn" data-role="WAREHOUSE_MANAGER">
            <div class="icon-wrapper"><i class="fas fa-warehouse"></i></div>
            <div class="role-info">
                <div class="name">Warehouse Manager</div>
                <div class="desc">Manage inventory and warehouses (NIC required)</div>
            </div>
            <i class="fas fa-chevron-right arrow-icon"></i>
        </button>
    </div>
</div>

<!-- ===== SESSION NOTIFICATION HIDDEN INPUTS ===== -->
<c:if test="${not empty sessionScope.errorNotification}">
    <input type="hidden" id="sessionErrorMsg" value="${sessionScope.errorNotification}">
    <c:remove var="errorNotification" scope="session" />
</c:if>

<c:if test="${not empty sessionScope.successNotification}">
    <input type="hidden" id="sessionSuccessMsg" value="${sessionScope.successNotification}"
           data-redirect="true" data-redirect-url="${pageContext.request.contextPath}${sessionScope.redirectAfterSuccess}">
    <c:remove var="successNotification" scope="session" />
    <c:remove var="redirectAfterSuccess" scope="session" />
</c:if>

<!-- Custom JavaScript -->
<script src="${pageContext.request.contextPath}/js/notyf.min.js"></script>
<script src="${pageContext.request.contextPath}/js/utils.js"></script>
<script src="${pageContext.request.contextPath}/js/auth.js"></script>
<script src="${pageContext.request.contextPath}/js/notifications.js"></script>

</body>
</html>