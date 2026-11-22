// GlobalTrade Logistics - Dashboard JavaScript
document.addEventListener('DOMContentLoaded', function() {
    'use strict';

    console.log('📊 Supply Chain Dashboard loaded with real data');

    // Quick action buttons
    const quickActions = document.querySelectorAll('.quick-action-btn');
    quickActions.forEach(btn => {
        btn.addEventListener('click', function() {
            const label = this.querySelector('.label')?.textContent || 'Action';
            console.log(`Quick action: ${label}`);
        });
    });

    // Export Report button
    const exportBtn = document.querySelector('.bg-white\\/5.border-white\\/5');
    if (exportBtn) {
        exportBtn.addEventListener('click', function() {
            if (typeof showNotification === 'function') {
                showNotification('Exporting report...', 'info', 1500);
                setTimeout(() => {
                    showNotification('Report exported successfully!', 'success');
                }, 1500);
            }
        });
    }

    // ===== STATS ANIMATION =====
    function animateStats() {
        const statElements = document.querySelectorAll('.balance-amount');
        statElements.forEach(el => {
            const text = el.textContent;
            const num = parseInt(text.replace(/,/g, ''));
            if (!isNaN(num) && num > 0) {
                let current = 0;
                const increment = Math.max(1, Math.floor(num / 30));
                const interval = setInterval(() => {
                    current += increment;
                    if (current >= num) {
                        current = num;
                        clearInterval(interval);
                    }
                    el.textContent = current;
                }, 30);
            }
        });
    }

    // Run animation after page load
    setTimeout(animateStats, 500);

    // ===== KEYBOARD SHORTCUTS =====
    document.addEventListener('keydown', function(e) {
        if (e.ctrlKey && e.shiftKey && e.key === 'D') {
            e.preventDefault();
            window.location.href = '/dashboard';
        }
        if (e.ctrlKey && e.shiftKey && e.key === 'S') {
            e.preventDefault();
            window.location.href = '/shipments';
        }
        if (e.ctrlKey && e.shiftKey && e.key === 'I') {
            e.preventDefault();
            window.location.href = '/inventory';
        }
        if (e.ctrlKey && e.shiftKey && e.key === 'H') {
            e.preventDefault();
            if (typeof showNotification === 'function') {
                showNotification('Help documentation available', 'info');
            }
        }
    });

    // ===== AUTO-REFRESH STATS (every 30 seconds) =====
    setInterval(function() {
        console.log('🔄 Refreshing dashboard stats...');
    }, 30000);

    console.log('👤 Welcome back!');
});