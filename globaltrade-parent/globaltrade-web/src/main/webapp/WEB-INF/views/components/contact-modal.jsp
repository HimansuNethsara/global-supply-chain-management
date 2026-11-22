<%-- Contact Us Modal - Shared Component --%>
<div id="contactModal" class="contact-modal-overlay">
    <div class="contact-modal-content">
        <div class="contact-modal-header">
            <h3><i class="fas fa-envelope text-dracula-red"></i> Contact Us</h3>
            <button class="close-btn" onclick="closeContactModal()">
                <i class="fas fa-times"></i>
            </button>
        </div>
        <p class="contact-modal-subtitle">We'd love to hear from you</p>

        <div class="contact-modal-body">
            <!-- Contact Info -->
            <div class="contact-info-grid">
                <div class="contact-info-item">
                    <div class="contact-icon"><i class="fas fa-phone"></i></div>
                    <div>
                        <label>Phone</label>
                        <span>+94 11 2 00 88 88</span>
                    </div>
                </div>
                <div class="contact-info-item">
                    <div class="contact-icon"><i class="fas fa-envelope"></i></div>
                    <div>
                        <label>Email</label>
                        <span>info@globaltrade.com</span>
                    </div>
                </div>
                <div class="contact-info-item">
                    <div class="contact-icon"><i class="fas fa-map-marker-alt"></i></div>
                    <div>
                        <label>Address</label>
                        <span>123 Logistics Boulevard, Colombo 01, Sri Lanka</span>
                    </div>
                </div>
                <div class="contact-info-item">
                    <div class="contact-icon"><i class="fas fa-clock"></i></div>
                    <div>
                        <label>Working Hours</label>
                        <span>Mon - Fri: 8:00 AM - 6:00 PM</span>
                    </div>
                </div>
            </div>

            <!-- Contact Form -->
            <div class="contact-form">
                <h4>Send us a message</h4>
                <form id="contactForm" onsubmit="submitContactForm(event)">
                    <div class="contact-form-row">
                        <div class="form-group">
                            <label for="contactName">Full Name</label>
                            <input type="text" id="contactName" placeholder="Enter your name" required>
                        </div>
                        <div class="form-group">
                            <label for="contactEmail">Email Address</label>
                            <input type="email" id="contactEmail" placeholder="Enter your email" required>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="contactSubject">Subject</label>
                        <input type="text" id="contactSubject" placeholder="Enter subject" required>
                    </div>
                    <div class="form-group">
                        <label for="contactMessage">Message</label>
                        <textarea id="contactMessage" rows="4" placeholder="Enter your message" required></textarea>
                    </div>
                    <button type="submit" class="btn-submit">
                        <i class="fas fa-paper-plane"></i> Send Message
                    </button>
                </form>
            </div>
        </div>

        <div class="contact-modal-footer">
            <button class="btn-close" onclick="closeContactModal()">Close</button>
        </div>
    </div>
</div>