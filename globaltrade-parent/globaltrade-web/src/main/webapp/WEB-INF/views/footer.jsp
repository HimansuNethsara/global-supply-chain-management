<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 7/31/2026
  Time: 10:26 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<footer class="dashboard-footer">
    <div class="flex flex-col sm:flex-row justify-between items-center gap-3 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex flex-wrap items-center gap-3">
            <span class="text-white font-medium text-sm">© 2026 GlobalTrade Logistics Corporation</span>
            <span class="text-white font-medium text-sm">|</span>
            <a href="#" class="text-white/60 hover:text-white transition-all text-xs" onclick="openAboutModal(); return false;">About Us</a>
            <span class="text-white/20 hidden sm:inline">|</span>
            <a href="#" class="text-white/60 hover:text-white transition-all text-xs" onclick="openContactModal(); return false;">Contact Us</a>
            <span class="text-white/20 hidden sm:inline">|</span>
            <a href="#" class="text-white/60 hover:text-white transition-all text-xs" onclick="openTermsModal(); return false;">Terms & Conditions</a>
        </div>
        <div class="flex items-center gap-2">
            <span class="text-white/50 text-xs">Protected under Sri Lanka PDPA No. 9 of 2022</span>
            <i class="fas fa-shield-alt text-dracula-red text-xs"></i>
        </div>
    </div>
</footer>

<!-- Modals -->
<jsp:include page="/WEB-INF/views/components/about-modal.jsp" />
<jsp:include page="/WEB-INF/views/components/contact-modal.jsp" />
<jsp:include page="/WEB-INF/views/components/terms-modal.jsp" />
<jsp:include page="/WEB-INF/views/components/logout-modal.jsp" />

<!-- Notyf CSS & JS (for notifications) -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/notyf.min.css">
<script src="${pageContext.request.contextPath}/js/notyf.min.js"></script>
<script src="${pageContext.request.contextPath}/js/utils.js"></script>
<script src="${pageContext.request.contextPath}/js/common.js"></script>
<script src="${pageContext.request.contextPath}/js/miscellaneous.js"></script>
<script src="${pageContext.request.contextPath}/js/notifications.js"></script>