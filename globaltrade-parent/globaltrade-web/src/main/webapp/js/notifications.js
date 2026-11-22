// GlobalTrade Logistics - Shared Notification Handler
document.addEventListener('DOMContentLoaded', function() {
    'use strict';

    // ===== CHECK FOR SESSION NOTIFICATIONS =====
    function checkSessionNotifications() {
        // Check for error notification
        const errorMsgElement = document.getElementById('sessionErrorMsg');
        if (errorMsgElement) {
            const errorMsg = errorMsgElement.value;
            if (errorMsg && errorMsg.trim() !== '') {
                if (typeof showError === 'function') {
                    showError(errorMsg);
                } else {
                    console.error('❌ ' + errorMsg);
                }
            }
        }

        // Check for success notification
        const successMsgElement = document.getElementById('sessionSuccessMsg');
        if (successMsgElement) {
            const successMsg = successMsgElement.value;
            if (successMsg && successMsg.trim() !== '') {
                if (typeof showSuccess === 'function') {
                    showSuccess(successMsg);
                    // Check if we should redirect after success
                    const redirectAfter = successMsgElement.dataset.redirect;
                    if (redirectAfter && redirectAfter === 'true') {
                        const redirectUrl = successMsgElement.dataset.redirectUrl || '/globaltrade/dashboard';
                        setTimeout(function() {
                            window.location.href = redirectUrl;
                        }, 3000);
                    }
                } else {
                    console.log('✅ ' + successMsg);
                }
            }
        }
    }

    // Run after DOM is ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', function() {
            setTimeout(checkSessionNotifications, 500);
        });
    } else {
        setTimeout(checkSessionNotifications, 500);
    }

    // Expose functions for redirects
    window.checkSessionNotifications = checkSessionNotifications;
});

// Console logging helper
function logNotification(type, message) {
    const icons = {
        success: '✅',
        error: '❌',
        info: 'ℹ️',
        warning: '⚠️'
    };
    console.log(`${icons[type] || '📢'} ${message}`);
}