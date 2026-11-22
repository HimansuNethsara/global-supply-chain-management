// GlobalTrade Logistics - Miscellaneous JavaScript
// Modal controls for About, Contact, Terms & Conditions
// Page functions for Settings and Help

document.addEventListener('DOMContentLoaded', function() {
    'use strict';

    // =============================================
    // ABOUT MODAL
    // =============================================

    window.openAboutModal = function() {
        const modal = document.getElementById('aboutModal');
        if (modal) {
            modal.classList.add('active');
            document.body.style.overflow = 'hidden';
        }
    };

    window.closeAboutModal = function() {
        const modal = document.getElementById('aboutModal');
        if (modal) {
            modal.classList.remove('active');
            document.body.style.overflow = '';
        }
    };

    // =============================================
    // CONTACT MODAL
    // =============================================

    window.openContactModal = function() {
        const modal = document.getElementById('contactModal');
        if (modal) {
            modal.classList.add('active');
            document.body.style.overflow = 'hidden';
        }
    };

    window.closeContactModal = function() {
        const modal = document.getElementById('contactModal');
        if (modal) {
            modal.classList.remove('active');
            document.body.style.overflow = '';
        }
    };

    // =============================================
    // TERMS MODAL
    // =============================================

    window.openTermsModal = function() {
        const modal = document.getElementById('termsModal');
        if (modal) {
            modal.classList.add('active');
            document.body.style.overflow = 'hidden';
        }
    };

    window.closeTermsModal = function() {
        const modal = document.getElementById('termsModal');
        if (modal) {
            modal.classList.remove('active');
            document.body.style.overflow = '';
        }
    };

    // =============================================
    // CONTACT FORM SUBMISSION
    // =============================================

    window.submitContactForm = function(event) {
        event.preventDefault();

        const name = document.getElementById('contactName')?.value;
        const email = document.getElementById('contactEmail')?.value;
        const subject = document.getElementById('contactSubject')?.value;
        const message = document.getElementById('contactMessage')?.value;

        console.log('📧 Contact Form Submission:');
        console.log('  Name:', name);
        console.log('  Email:', email);
        console.log('  Subject:', subject);
        console.log('  Message:', message);

        const submitBtn = document.querySelector('.contact-form .btn-submit');
        if (submitBtn) {
            const originalText = submitBtn.innerHTML;
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Sending...';
            submitBtn.disabled = true;

            setTimeout(() => {
                submitBtn.innerHTML = originalText;
                submitBtn.disabled = false;
                document.getElementById('contactForm').reset();

                if (typeof showSuccess === 'function') {
                    showSuccess('Message sent successfully!');
                } else if (typeof showNotification === 'function') {
                    showNotification('Message sent successfully!', 'success');
                }

                setTimeout(closeContactModal, 2000);
            }, 1500);
        }
    };

    // =============================================
    // SETTINGS PAGE FUNCTIONS
    // =============================================

    window.toggleSetting = function(button) {
        const isEnabled = button.textContent.trim().includes('Enabled');
        if (isEnabled) {
            button.innerHTML = 'Disabled';
            button.className = 'px-4 py-1.5 bg-white/5 border border-white/10 rounded-lg text-white/40 text-xs hover:bg-white/10 transition-all';
            console.log('🔕 Setting disabled');
        } else {
            button.innerHTML = '<i class="fas fa-check"></i> Enabled';
            button.className = 'px-4 py-1.5 bg-green-500/20 border border-green-500/30 rounded-lg text-green-400 text-xs hover:bg-green-500/30 transition-all flex items-center gap-1';
            console.log('🔔 Setting enabled');
        }
    };

    // =============================================
    // HELP PAGE - FAQ Toggle
    // =============================================

    window.toggleFaq = function(button) {
        const answer = button.nextElementSibling;
        const icon = button.querySelector('.fa-chevron-down');
        if (answer) {
            answer.classList.toggle('hidden');
            if (icon) {
                icon.style.transform = answer.classList.contains('hidden') ? 'rotate(0deg)' : 'rotate(180deg)';
            }
        }
    };

    // =============================================
    // SETTINGS - Theme Selection
    // =============================================

    window.selectTheme = function(button) {
        document.querySelectorAll('.theme-option').forEach(btn => {
            btn.className = 'p-3 bg-white/5 border border-white/10 rounded-lg text-center hover:border-white/20 transition-all cursor-pointer';
        });
        button.className = 'p-3 bg-dracula-red/10 border-2 border-dracula-red rounded-lg text-center transition-all';
        console.log('🎨 Theme changed to:', button.querySelector('span')?.textContent || 'unknown');
    };

    // =============================================
    // MODAL CLOSE - Click Outside
    // =============================================

    document.addEventListener('click', function(e) {
        const aboutModal = document.getElementById('aboutModal');
        if (aboutModal && aboutModal.classList.contains('active')) {
            if (e.target === aboutModal) {
                closeAboutModal();
            }
        }
        const contactModal = document.getElementById('contactModal');
        if (contactModal && contactModal.classList.contains('active')) {
            if (e.target === contactModal) {
                closeContactModal();
            }
        }
        const termsModal = document.getElementById('termsModal');
        if (termsModal && termsModal.classList.contains('active')) {
            if (e.target === termsModal) {
                closeTermsModal();
            }
        }
    });

    // =============================================
    // MODAL CLOSE - Escape Key
    // =============================================

    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            closeAboutModal();
            closeContactModal();
            closeTermsModal();
        }
    });

    // =============================================
    // KEYBOARD SHORTCUTS
    // =============================================

    document.addEventListener('keydown', function(e) {
        if (e.ctrlKey && e.shiftKey && e.key === 'A') {
            e.preventDefault();
            openAboutModal();
        }
        if (e.ctrlKey && e.shiftKey && e.key === 'C') {
            e.preventDefault();
            openContactModal();
        }
        if (e.ctrlKey && e.shiftKey && e.key === 'T') {
            e.preventDefault();
            openTermsModal();
        }
        if (e.ctrlKey && e.shiftKey && e.key === 'H') {
            e.preventDefault();
            window.location.href = '/help.jsp';
        }
        if (e.ctrlKey && e.shiftKey && e.key === 'S') {
            e.preventDefault();
            window.location.href = '/user/settings.jsp';
        }
    });

    console.log('📋 Miscellaneous - Modals and Pages loaded');
});