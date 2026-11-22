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
    <title>GlobalTrade Logistics - Help</title>

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
                    <h1 class="text-xl sm:text-2xl font-bold text-white">Help Center</h1>
                    <p class="text-white/30 text-sm font-light">Get the most out of GlobalTrade Logistics</p>
                </div>
            </div>

            <!-- ===== HELP SECTIONS ===== -->
            <div class="space-y-4">

                <!-- Quick Start Guide -->
                <div class="dashboard-card">
                    <h2 class="text-white/60 text-sm font-semibold mb-4 flex items-center gap-2">
                        <i class="fas fa-rocket text-dracula-red"></i>
                        Quick Start Guide
                    </h2>
                    <div class="space-y-3">
                        <div class="flex items-start gap-3 p-3 bg-white/5 rounded-lg border border-white/5">
                            <div class="w-6 h-6 min-w-[24px] rounded-full bg-dracula-red/20 text-dracula-red text-xs font-bold flex items-center justify-center">1</div>
                            <div>
                                <div class="text-white/70 text-sm font-medium">Create an Account</div>
                                <div class="text-white/30 text-xs">Register with your role and complete your profile</div>
                            </div>
                        </div>
                        <div class="flex items-start gap-3 p-3 bg-white/5 rounded-lg border border-white/5">
                            <div class="w-6 h-6 min-w-[24px] rounded-full bg-dracula-red/20 text-dracula-red text-xs font-bold flex items-center justify-center">2</div>
                            <div>
                                <div class="text-white/70 text-sm font-medium">Create a Shipment</div>
                                <div class="text-white/30 text-xs">Enter shipment details, origin, destination, and items</div>
                            </div>
                        </div>
                        <div class="flex items-start gap-3 p-3 bg-white/5 rounded-lg border border-white/5">
                            <div class="w-6 h-6 min-w-[24px] rounded-full bg-dracula-red/20 text-dracula-red text-xs font-bold flex items-center justify-center">3</div>
                            <div>
                                <div class="text-white/70 text-sm font-medium">Track Your Shipment</div>
                                <div class="text-white/30 text-xs">Use the tracking number to monitor your shipment status</div>
                            </div>
                        </div>
                        <div class="flex items-start gap-3 p-3 bg-white/5 rounded-lg border border-white/5">
                            <div class="w-6 h-6 min-w-[24px] rounded-full bg-dracula-red/20 text-dracula-red text-xs font-bold flex items-center justify-center">4</div>
                            <div>
                                <div class="text-white/70 text-sm font-medium">Manage Inventory</div>
                                <div class="text-white/30 text-xs">Add, update, and track inventory items across warehouses</div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- FAQ -->
                <div class="dashboard-card">
                    <h2 class="text-white/60 text-sm font-semibold mb-4 flex items-center gap-2">
                        <i class="fas fa-question-circle text-dracula-red"></i>
                        Frequently Asked Questions
                    </h2>
                    <div class="space-y-3">
                        <div class="border-b border-white/5 pb-3">
                            <button class="w-full text-left flex items-center justify-between text-white/70 text-sm font-medium hover:text-white transition-all" onclick="toggleFaq(this)">
                                <span>How do I create a new shipment?</span>
                                <i class="fas fa-chevron-down text-white/20 text-xs"></i>
                            </button>
                            <div class="faq-answer mt-2 text-white/40 text-xs leading-relaxed hidden">
                                Navigate to Shipments → Create Shipment. Fill in the required details including tracking number, origin warehouse, destination, vendor, carrier, and scheduled date. Then click "Create Shipment".
                            </div>
                        </div>
                        <div class="border-b border-white/5 pb-3">
                            <button class="w-full text-left flex items-center justify-between text-white/70 text-sm font-medium hover:text-white transition-all" onclick="toggleFaq(this)">
                                <span>How do I update shipment status?</span>
                                <i class="fas fa-chevron-down text-white/20 text-xs"></i>
                            </button>
                            <div class="faq-answer mt-2 text-white/40 text-xs leading-relaxed hidden">
                                Go to Shipments → Shipments List, find your shipment, and click the "Update Status" button. Select the new status and add any remarks before submitting.
                            </div>
                        </div>
                        <div class="border-b border-white/5 pb-3">
                            <button class="w-full text-left flex items-center justify-between text-white/70 text-sm font-medium hover:text-white transition-all" onclick="toggleFaq(this)">
                                <span>What is the difference between customs agent and logistics coordinator?</span>
                                <i class="fas fa-chevron-down text-white/20 text-xs"></i>
                            </button>
                            <div class="faq-answer mt-2 text-white/40 text-xs leading-relaxed hidden">
                                Customs agents handle customs documentation and approvals for shipments. Logistics coordinators manage the entire shipment lifecycle including creation, tracking, and vendor coordination.
                            </div>
                        </div>
                        <div class="border-b border-white/5 pb-3">
                            <button class="w-full text-left flex items-center justify-between text-white/70 text-sm font-medium hover:text-white transition-all" onclick="toggleFaq(this)">
                                <span>How do I add inventory items?</span>
                                <i class="fas fa-chevron-down text-white/20 text-xs"></i>
                            </button>
                            <div class="faq-answer mt-2 text-white/40 text-xs leading-relaxed hidden">
                                Go to Inventory → Create Item. Fill in the SKU, item name, select warehouse, quantity, reorder level, and unit price. Click "Create Item" to add it to inventory.
                            </div>
                        </div>
                        <div>
                            <button class="w-full text-left flex items-center justify-between text-white/70 text-sm font-medium hover:text-white transition-all" onclick="toggleFaq(this)">
                                <span>What should I do if I encounter an error?</span>
                                <i class="fas fa-chevron-down text-white/20 text-xs"></i>
                            </button>
                            <div class="faq-answer mt-2 text-white/40 text-xs leading-relaxed hidden">
                                If you encounter an error, please contact our support team at support@globaltrade.com or call +94 11 2 00 88 88. Include a description of the issue and any error messages you received.
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Video Tutorials -->
                <div class="dashboard-card">
                    <h2 class="text-white/60 text-sm font-semibold mb-4 flex items-center gap-2">
                        <i class="fas fa-video text-dracula-red"></i>
                        Video Tutorials
                    </h2>
                    <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
                        <div class="p-4 bg-white/5 border border-white/5 rounded-lg text-center hover:border-white/10 transition-all cursor-pointer">
                            <div class="w-12 h-12 mx-auto rounded-full bg-dracula-red/10 flex items-center justify-center text-dracula-red mb-2">
                                <i class="fas fa-play text-lg"></i>
                            </div>
                            <div class="text-white/60 text-sm font-medium">Getting Started</div>
                            <div class="text-white/20 text-xs">5 min tutorial</div>
                        </div>
                        <div class="p-4 bg-white/5 border border-white/5 rounded-lg text-center hover:border-white/10 transition-all cursor-pointer">
                            <div class="w-12 h-12 mx-auto rounded-full bg-dracula-red/10 flex items-center justify-center text-dracula-red mb-2">
                                <i class="fas fa-play text-lg"></i>
                            </div>
                            <div class="text-white/60 text-sm font-medium">Creating a Shipment</div>
                            <div class="text-white/20 text-xs">8 min tutorial</div>
                        </div>
                        <div class="p-4 bg-white/5 border border-white/5 rounded-lg text-center hover:border-white/10 transition-all cursor-pointer">
                            <div class="w-12 h-12 mx-auto rounded-full bg-dracula-red/10 flex items-center justify-center text-dracula-red mb-2">
                                <i class="fas fa-play text-lg"></i>
                            </div>
                            <div class="text-white/60 text-sm font-medium">Managing Inventory</div>
                            <div class="text-white/20 text-xs">6 min tutorial</div>
                        </div>
                        <div class="p-4 bg-white/5 border border-white/5 rounded-lg text-center hover:border-white/10 transition-all cursor-pointer">
                            <div class="w-12 h-12 mx-auto rounded-full bg-dracula-red/10 flex items-center justify-center text-dracula-red mb-2">
                                <i class="fas fa-play text-lg"></i>
                            </div>
                            <div class="text-white/60 text-sm font-medium">Customs Documentation</div>
                            <div class="text-white/20 text-xs">7 min tutorial</div>
                        </div>
                    </div>
                </div>

                <!-- Contact Support -->
                <div class="dashboard-card bg-white/3 border-white/5">
                    <h2 class="text-white/60 text-sm font-semibold mb-4 flex items-center gap-2">
                        <i class="fas fa-headset text-dracula-red"></i>
                        Need More Help?
                    </h2>
                    <div class="flex flex-wrap gap-4">
                        <a href="#" onclick="openContactModal(); return false;" class="px-4 py-2 bg-gradient-to-r from-dracula-red to-red-900 rounded-lg text-white text-sm font-semibold hover:shadow-lg hover:shadow-red-500/25 transition-all flex items-center gap-2">
                            <i class="fas fa-envelope"></i>
                            Contact Support
                        </a>
                        <a href="#" class="px-4 py-2 bg-white/5 border border-white/10 rounded-lg text-white/50 text-sm hover:bg-white/10 hover:text-white/60 transition-all flex items-center gap-2">
                            <i class="fas fa-file-alt"></i>
                            Documentation
                        </a>
                        <a href="#" class="px-4 py-2 bg-white/5 border border-white/10 rounded-lg text-white/50 text-sm hover:bg-white/10 hover:text-white/60 transition-all flex items-center gap-2">
                            <i class="fas fa-comments"></i>
                            Community Forum
                        </a>
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