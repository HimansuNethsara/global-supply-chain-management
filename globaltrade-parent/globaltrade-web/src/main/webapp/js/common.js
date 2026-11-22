// GlobalTrade Logistics - Common JavaScript
document.addEventListener('DOMContentLoaded', function() {
    'use strict';

    // ===== CONTEXT PATH HELPER =====
    function getContextPath() {
        const path = window.location.pathname;
        const parts = path.split('/');
        if (parts.length > 1 && parts[0] === '') {
            return parts[1] ? '/' + parts[1] : '';
        }
        return '';
    }

    // =============================================
    // LOGOUT MODAL
    // =============================================

    window.openLogoutModal = function() {
        const modal = document.getElementById('logoutModal');
        if (modal) {
            modal.classList.add('active');
            document.body.style.overflow = 'hidden';
        }
    };

    window.closeLogoutModal = function() {
        const modal = document.getElementById('logoutModal');
        if (modal) {
            modal.classList.remove('active');
            document.body.style.overflow = '';
        }
    };

    window.confirmLogout = function() {
        console.log('🚪 User confirmed logout');
        if (typeof showNotification === 'function') {
            showNotification('Signing out...', 'info', 1500);
        }
        closeLogoutModal();
        const ctxPath = getContextPath() || '';
        setTimeout(function() {
            window.location.href = ctxPath + '/logout';
        }, 1500);
    };

    // Close logout modal with Escape key
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            closeLogoutModal();
        }
    });

    // Close logout modal by clicking outside
    document.addEventListener('click', function(e) {
        const logoutModal = document.getElementById('logoutModal');
        if (logoutModal && logoutModal.classList.contains('active')) {
            if (e.target === logoutModal) {
                closeLogoutModal();
            }
        }
    });

    // ===== ORIGINAL LOGOUT FUNCTION (kept for backward compatibility) =====
    function performLogout() {
        openLogoutModal();
    }
    window.performLogout = performLogout;

    // ===== SIDEBAR TOGGLE (Mobile) =====
    const sidebar = document.getElementById('sidebar');
    const sidebarOverlay = document.getElementById('sidebarOverlay');
    const sidebarToggle = document.getElementById('sidebarToggle');
    const sidebarClose = document.getElementById('sidebarClose');

    window.toggleSidebar = function() {
        if (!sidebar) return;
        sidebar.classList.toggle('open');
        if (sidebarOverlay) {
            sidebarOverlay.classList.toggle('open');
        }
        document.body.style.overflow = sidebar.classList.contains('open') ? 'hidden' : '';
    };

    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', function(e) {
            e.stopPropagation();
            window.toggleSidebar();
        });
    }

    if (sidebarClose) {
        sidebarClose.addEventListener('click', function() {
            window.toggleSidebar();
        });
    }

    if (sidebarOverlay) {
        sidebarOverlay.addEventListener('click', function() {
            window.toggleSidebar();
        });
    }

    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape' && sidebar && sidebar.classList.contains('open')) {
            window.toggleSidebar();
        }
        if (e.ctrlKey && e.shiftKey && e.key === 'S') {
            e.preventDefault();
            window.toggleSidebar();
        }
    });

    let lastWidth = window.innerWidth;
    window.addEventListener('resize', function() {
        const currentWidth = window.innerWidth;
        if (currentWidth > 1024 && lastWidth <= 1024 && sidebar) {
            sidebar.classList.remove('open');
            if (sidebarOverlay) sidebarOverlay.classList.remove('open');
            document.body.style.overflow = '';
        }
        lastWidth = currentWidth;
    });

    // ===== SUBMENU TOGGLE =====
    window.toggleSubmenu = function(submenuId) {
        const submenu = document.getElementById(submenuId);
        if (!submenu) return;
        submenu.classList.toggle('hidden');
        const arrow = document.getElementById(submenuId.replace('Submenu', 'Arrow'));
        if (arrow) {
            arrow.style.transform = submenu.classList.contains('hidden') ? 'rotate(0deg)' : 'rotate(90deg)';
        }
    };

    // ===== CURRENT TIME UPDATE =====
    function updateCurrentTime() {
        const timeElement = document.getElementById('currentTime');
        if (!timeElement) return;
        const now = new Date();
        const options = {
            weekday: 'short',
            month: 'short',
            day: 'numeric',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
            hour12: false
        };
        timeElement.textContent = now.toLocaleString('en-US', options);
    }
    updateCurrentTime();
    setInterval(updateCurrentTime, 1000);

    // ===== SIDEBAR ACTIVE STATES =====
    function setActiveSidebarStates() {
        const currentPath = window.location.pathname;

        const links = document.querySelectorAll('.sidebar-item[href]');
        links.forEach(link => {
            const href = link.getAttribute('href');
            if (href && currentPath.includes(href)) {
                link.classList.add('active');
            } else {
                link.classList.remove('active');
            }
        });

        // Remove banking-specific submenu references
        const accountsSubmenu = document.getElementById('accountsSubmenu');
        const accountsArrow = document.getElementById('accountsArrow');
        if (accountsSubmenu && accountsArrow) {
            accountsSubmenu.classList.add('hidden');
            accountsArrow.style.transform = 'rotate(0deg)';
        }

        const transferSubmenu = document.getElementById('transferSubmenu');
        const transferArrow = document.getElementById('transferArrow');
        if (transferSubmenu && transferArrow) {
            transferSubmenu.classList.add('hidden');
            transferArrow.style.transform = 'rotate(0deg)';
        }

        const userProfileLink = document.getElementById('sidebarUserProfile');
        if (userProfileLink) {
            if (currentPath.includes('user-profile')) {
                userProfileLink.classList.add('active');
            } else {
                userProfileLink.classList.remove('active');
            }
        }

        const settingsLink = document.getElementById('sidebarSettings');
        if (settingsLink) {
            if (currentPath.includes('settings')) {
                settingsLink.classList.add('active');
            } else {
                settingsLink.classList.remove('active');
            }
        }

        const pageMap = {
            'sidebarDashboard': ['dashboard', '/', '/globaltrade/'],
            'sidebarShipmentsList': ['shipments'],
            'sidebarShipmentCreate': ['shipments/create'],
            'sidebarInventoryList': ['inventory'],
            'sidebarInventoryItemCreate': ['inventory/create'],
            'sidebarVendorsList': ['vendors'],
            'sidebarVendorCreate': ['vendors/create'],
            'sidebarVendorPerformance': ['vendors/performance'],
            'sidebarCustomersList': ['customers'],
            'sidebarCustomerOrders': ['customers/orders'],
            'sidebarCustomerOrderCreate': ['customers/orders/create'],
            'sidebarPendingOrders': ['orders/pending'],
            'sidebarWarehousesList': ['warehouses'],
            'sidebarWarehouseCreate': ['warehouses/create'],
            'sidebarCarriersList': ['carriers'],
            'sidebarCarrierCreate': ['carriers/create'],
            'sidebarCustomsDocumentsList': ['customs'],
            'sidebarCustomsDocumentCreate': ['customs/create'],
            'sidebarRoutePlan': ['route-plan'],
            'sidebarAlerts': ['monitoring/alerts'],
            'sidebarAuditLogs': ['monitoring/audit-logs'],
            'sidebarExceptionLogs': ['monitoring/exception-logs'],
            'sidebarSystemTimers': ['monitoring/timers'],
            'sidebarAdminUsers': ['admin/users-list'],
            'sidebarProfile': ['profile'],
            'sidebarSettings': ['settings']
        };

        Object.keys(pageMap).forEach(id => {
            const link = document.getElementById(id);
            if (link) {
                const matches = pageMap[id].some(path => currentPath.includes(path));
                link.classList.toggle('active', matches);
            }
        });
    }

    setActiveSidebarStates();

    // ===== HEADER BUTTONS =====
    const headerBtns = document.querySelectorAll('.header-btn:not(.header-btn-logout):not(.header-btn-notif)');
    headerBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            headerBtns.forEach(b => b.classList.remove('active'));
            this.classList.add('active');
        });
    });

    // ===== NOTIFICATION BELL =====
    const notifBtn = document.querySelector('.header-btn-notif');
    if (notifBtn) {
        notifBtn.addEventListener('click', function() {
            console.log('🔔 Notifications clicked');
            if (typeof showNotification === 'function') {
                showNotification('You have 3 new notifications', 'info', 3000);
            }
        });
    }

    // ===== ALERT BADGE UPDATE =====
    window.updateAlertBadge = function() {
        const badge = document.getElementById('alertBadge');
        if (!badge) {
            console.warn('Alert badge element not found');
            return;
        }

        const ctxPath = getContextPath() || '';

        fetch(ctxPath + '/monitoring/alert-count')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok: ' + response.status);
                }
                return response.json();
            })
            .then(data => {
                const count = data.activeCount || 0;
                badge.textContent = count;
                if (count > 0) {
                    badge.style.display = 'inline-block';
                    if (count > 5) {
                        badge.style.background = '#ef4444';
                    } else {
                        badge.style.background = '#f59e0b';
                    }
                } else {
                    badge.style.display = 'none';
                }
                console.log('🔔 Alert badge updated: ' + count + ' active alerts');
            })
            .catch(error => {
                console.warn('Failed to fetch alert count:', error);
                badge.style.display = 'none';
            });
    };

    setTimeout(function() {
        if (typeof window.updateAlertBadge === 'function') {
            window.updateAlertBadge();
        }
    }, 1500);

    setInterval(function() {
        if (typeof window.updateAlertBadge === 'function') {
            window.updateAlertBadge();
        }
    }, 30000);

    // ===== LOGOUT BUTTON HANDLER (Header) =====
    const logoutBtn = document.querySelector('.header-btn-logout');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(e) {
            e.preventDefault();
            openLogoutModal();
        });
    }

    // ===== LOGOUT BUTTON HANDLER (Sidebar) =====
    const sidebarLogoutBtn = document.querySelector('.sidebar-item .fa-right-from-bracket')?.closest('button');
    if (sidebarLogoutBtn) {
        sidebarLogoutBtn.addEventListener('click', function(e) {
            e.preventDefault();
            openLogoutModal();
        });
    }

    // ===== ACCESS DENIED HANDLER =====
    function handleAccessDenied() {
        const urlParams = new URLSearchParams(window.location.search);
        const errorCode = urlParams.get('error');
        if (errorCode === '403') {
            if (typeof showNotification === 'function') {
                showNotification('Access denied. You do not have permission for this action.', 'error');
            }
        }
    }
    handleAccessDenied();

    // ===== ACCESS DENIED COUNTDOWN TIMER =====
    function setupAccessDeniedCountdown() {
        const countdownEl = document.getElementById('countdown');
        if (!countdownEl) return;

        let seconds = parseInt(countdownEl.textContent) || 10;
        const interval = setInterval(() => {
            seconds--;
            countdownEl.textContent = seconds;
            if (seconds <= 0) {
                clearInterval(interval);
                const ctxPath = getContextPath() || '';
                const redirectUrl = countdownEl.dataset.redirect || ctxPath + '/dashboard';
                window.location.href = redirectUrl;
            }
        }, 1000);
    }
    setupAccessDeniedCountdown();

    // ===== ROLE ICON HELPER =====
    function getRoleIcon(role) {
        const roleMap = {
            'LOGISTICS_COORDINATOR': 'fa-truck',
            'CUSTOMS_AGENT': 'fa-passport',
            'WAREHOUSE_MANAGER': 'fa-warehouse',
            'VENDOR_REP': 'fa-building',
            'CUSTOMER': 'fa-user'
        };
        return roleMap[role] || 'fa-user-tag';
    }

    // ===== UPDATE ROLE DISPLAY =====
    function updateRoleDisplay() {
        const roleDisplayEl = document.querySelector('.role-display');
        const roleIconEl = document.querySelector('.role-icon');
        if (roleDisplayEl) {
            const role = roleDisplayEl.dataset.role;
            if (roleIconEl) {
                const iconClass = getRoleIcon(role);
                roleIconEl.className = 'fas ' + iconClass + ' text-dracula-red text-sm';
            }
        }
    }
    updateRoleDisplay();

    // ===== EXPOSE FUNCTIONS GLOBALLY =====
    window.getContextPath = getContextPath;
    window.performLogout = performLogout;
    window.openLogoutModal = openLogoutModal;
    window.closeLogoutModal = closeLogoutModal;
    window.confirmLogout = confirmLogout;

    console.log('🚢 GlobalTrade Logistics - Common JS Loaded');
});