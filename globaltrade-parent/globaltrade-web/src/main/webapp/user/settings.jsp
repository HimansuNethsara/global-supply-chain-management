<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 8/29/2026
  Time: 12:00 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en" data-theme="dracula">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GlobalTrade Logistics - Settings</title>

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
                <a href="${pageContext.request.contextPath}/dashboard" class="text-white/30 hover:text-white/60 transition-all">
                    <i class="fas fa-arrow-left"></i>
                </a>
                <div>
                    <h1 class="text-xl sm:text-2xl font-bold text-white">Settings</h1>
                    <p class="text-white/30 text-sm font-light">Manage your account preferences</p>
                </div>
            </div>

            <!-- ===== SETTINGS SECTIONS ===== -->
            <div class="space-y-4">

                <!-- Account Settings -->
                <div class="dashboard-card">
                    <h2 class="text-white/60 text-sm font-semibold mb-4 flex items-center gap-2">
                        <i class="fas fa-user-cog text-dracula-red"></i>
                        Account Settings
                    </h2>
                    <div class="space-y-3">
                        <div class="flex items-center justify-between py-2 border-b border-white/5">
                            <div>
                                <div class="text-white/70 text-sm font-medium">Email Address</div>
                                <div class="text-white/30 text-xs">Update your email address</div>
                            </div>
                            <button class="px-4 py-1.5 bg-dracula-red/20 border border-dracula-red/30 rounded-lg text-dracula-red text-xs hover:bg-dracula-red/30 transition-all">
                                Change
                            </button>
                        </div>
                        <div class="flex items-center justify-between py-2 border-b border-white/5">
                            <div>
                                <div class="text-white/70 text-sm font-medium">Password</div>
                                <div class="text-white/30 text-xs">Update your password</div>
                            </div>
                            <button class="px-4 py-1.5 bg-dracula-red/20 border border-dracula-red/30 rounded-lg text-dracula-red text-xs hover:bg-dracula-red/30 transition-all">
                                Change
                            </button>
                        </div>
                        <div class="flex items-center justify-between py-2">
                            <div>
                                <div class="text-white/70 text-sm font-medium">Two-Factor Authentication</div>
                                <div class="text-white/30 text-xs">Add an extra layer of security</div>
                            </div>
                            <button class="px-4 py-1.5 bg-white/5 border border-white/10 rounded-lg text-white/40 text-xs hover:bg-white/10 transition-all">
                                Enable
                            </button>
                        </div>
                    </div>
                </div>

                <!-- Notification Preferences -->
                <div class="dashboard-card">
                    <h2 class="text-white/60 text-sm font-semibold mb-4 flex items-center gap-2">
                        <i class="fas fa-bell text-dracula-red"></i>
                        Notification Preferences
                    </h2>
                    <div class="space-y-3">
                        <div class="flex items-center justify-between py-2 border-b border-white/5">
                            <div>
                                <div class="text-white/70 text-sm font-medium">Email Notifications</div>
                                <div class="text-white/30 text-xs">Receive updates via email</div>
                            </div>
                            <button class="px-4 py-1.5 bg-green-500/20 border border-green-500/30 rounded-lg text-green-400 text-xs hover:bg-green-500/30 transition-all flex items-center gap-1">
                                <i class="fas fa-check"></i> Enabled
                            </button>
                        </div>
                        <div class="flex items-center justify-between py-2 border-b border-white/5">
                            <div>
                                <div class="text-white/70 text-sm font-medium">SMS Alerts</div>
                                <div class="text-white/30 text-xs">Get alerts on your phone</div>
                            </div>
                            <button class="px-4 py-1.5 bg-white/5 border border-white/10 rounded-lg text-white/40 text-xs hover:bg-white/10 transition-all">
                                Disabled
                            </button>
                        </div>
                        <div class="flex items-center justify-between py-2">
                            <div>
                                <div class="text-white/70 text-sm font-medium">Shipment Status Updates</div>
                                <div class="text-white/30 text-xs">Real-time shipment notifications</div>
                            </div>
                            <button class="px-4 py-1.5 bg-green-500/20 border border-green-500/30 rounded-lg text-green-400 text-xs hover:bg-green-500/30 transition-all flex items-center gap-1">
                                <i class="fas fa-check"></i> Enabled
                            </button>
                        </div>
                    </div>
                </div>

                <!-- Theme Preferences -->
                <div class="dashboard-card">
                    <h2 class="text-white/60 text-sm font-semibold mb-4 flex items-center gap-2">
                        <i class="fas fa-palette text-dracula-red"></i>
                        Theme Preferences
                    </h2>
                    <div class="grid grid-cols-2 sm:grid-cols-3 gap-3">
                        <button class="p-3 bg-dracula-red/10 border-2 border-dracula-red rounded-lg text-center transition-all">
                            <div class="w-8 h-8 mx-auto rounded-full bg-gradient-to-br from-black to-red-900 border-2 border-white/10 mb-2"></div>
                            <span class="text-white/70 text-xs font-medium">Dracula</span>
                            <span class="block text-white/20 text-[10px]">(Current)</span>
                        </button>
                        <button class="p-3 bg-white/5 border border-white/10 rounded-lg text-center hover:border-white/20 transition-all cursor-pointer">
                            <div class="w-8 h-8 mx-auto rounded-full bg-gradient-to-br from-gray-100 to-gray-300 border-2 border-white/10 mb-2"></div>
                            <span class="text-white/40 text-xs font-medium">Light</span>
                        </button>
                        <button class="p-3 bg-white/5 border border-white/10 rounded-lg text-center hover:border-white/20 transition-all cursor-pointer">
                            <div class="w-8 h-8 mx-auto rounded-full bg-gradient-to-br from-blue-900 to-blue-700 border-2 border-white/10 mb-2"></div>
                            <span class="text-white/40 text-xs font-medium">Blue</span>
                        </button>
                    </div>
                </div>

                <!-- Danger Zone -->
                <div class="dashboard-card border-red-500/20 bg-red-500/5">
                    <h2 class="text-red-400/80 text-sm font-semibold mb-4 flex items-center gap-2">
                        <i class="fas fa-exclamation-triangle"></i>
                        Danger Zone
                    </h2>
                    <div class="flex items-center justify-between py-2">
                        <div>
                            <div class="text-white/70 text-sm font-medium">Delete Account</div>
                            <div class="text-white/30 text-xs">Permanently delete your account and all data</div>
                        </div>
                        <button class="px-4 py-1.5 bg-red-500/20 border border-red-500/30 rounded-lg text-red-400 text-xs hover:bg-red-500/30 transition-all">
                            Delete Account
                        </button>
                    </div>
                </div>

            </div>

        </div>
    </main>

    <!-- ===== FOOTER ===== -->
    <jsp:include page="/WEB-INF/views/footer.jsp" />
</div>

<!-- Custom JavaScript -->
<script src="${pageContext.request.contextPath}/js/miscellaneous.js"></script>
</body>
</html>