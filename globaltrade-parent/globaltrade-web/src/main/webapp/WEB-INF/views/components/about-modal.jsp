<%-- About Us Modal - Shared Component --%>
<div id="aboutModal" class="about-modal-overlay">
    <div class="about-modal-content">
        <div class="about-modal-header">
            <h3><i class="fas fa-info-circle text-dracula-red"></i> About Us</h3>
            <button class="close-btn" onclick="closeAboutModal()">
                <i class="fas fa-times"></i>
            </button>
        </div>
        <p class="about-modal-subtitle">GlobalTrade Logistics Corporation</p>

        <div class="about-modal-body">
            <div class="about-logo">
                <i class="fas fa-ship"></i>
                <span>GLOBAL<span>®</span></span>
                <small>TRADE LOGISTICS</small>
            </div>

            <div class="about-section">
                <h4>Who We Are</h4>
                <p>GlobalTrade Logistics Corporation is a leading provider of supply chain management solutions, serving multinational corporations across 50+ countries. We specialize in end-to-end logistics, vendor performance monitoring, customs compliance, and real-time shipment tracking.</p>
            </div>

            <div class="about-section">
                <h4>Our Mission</h4>
                <p>To revolutionize global supply chain management through innovative technology, seamless integration, and unwavering commitment to operational excellence.</p>
            </div>

            <div class="about-section">
                <h4>Key Facts</h4>
                <ul class="about-facts">
                    <li><i class="fas fa-globe"></i> Operating in 50+ countries</li>
                    <li><i class="fas fa-users"></i> 500+ logistics professionals</li>
                    <li><i class="fas fa-truck"></i> 10,000+ shipments processed monthly</li>
                    <li><i class="fas fa-check-circle"></i> 99.9% service availability</li>
                </ul>
            </div>

            <div class="about-section">
                <h4>Technology Stack</h4>
                <div class="about-tech">
                    <span class="tech-tag">Java EE</span>
                    <span class="tech-tag">EJB</span>
                    <span class="tech-tag">WildFly</span>
                    <span class="tech-tag">PostgreSQL</span>
                    <span class="tech-tag">JSP</span>
                    <span class="tech-tag">Tailwind CSS</span>
                </div>
            </div>
        </div>

        <div class="about-modal-footer">
            <button class="btn-close" onclick="closeAboutModal()">Close</button>
        </div>
    </div>
</div>