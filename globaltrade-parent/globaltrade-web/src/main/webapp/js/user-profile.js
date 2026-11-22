// GlobalTrade Logistics - User Profile & Management JavaScript
document.addEventListener('DOMContentLoaded', function() {
    'use strict';

    // =============================================
    // USER PROFILE
    // =============================================

    // ===== EDIT PROFILE BUTTON =====
    window.editProfile = function() {
        console.log('📝 Edit Profile clicked');
        showNotification('Profile edit mode - Click on fields to edit', 'info');
    };

    // ===== CHANGE PASSWORD BUTTON =====
    window.changePassword = function() {
        console.log('🔑 Change Password clicked');
        showNotification('Password change feature coming soon!', 'warning');
    };

    // =============================================
    // USER MANAGEMENT - Tabs
    // =============================================

    window.switchTab = function(tab) {
        const tabs = document.querySelectorAll('.tab-btn');
        tabs.forEach(btn => {
            btn.classList.remove('text-white/60', 'border-dracula-red');
            btn.classList.add('text-white/40');
            btn.style.borderBottomColor = 'transparent';
        });

        const pendingTab = document.getElementById('pendingTab');
        const allTab = document.getElementById('allTab');

        if (pendingTab) pendingTab.classList.add('hidden');
        if (allTab) allTab.classList.add('hidden');

        if (tab === 'pending') {
            const tabPending = document.getElementById('tabPending');
            if (tabPending) {
                tabPending.classList.add('text-white/60', 'border-dracula-red');
                tabPending.classList.remove('text-white/40');
                tabPending.style.borderBottomColor = '#dc143c';
            }
            if (pendingTab) pendingTab.classList.remove('hidden');
        } else {
            const tabAll = document.getElementById('tabAll');
            if (tabAll) {
                tabAll.classList.add('text-white/60', 'border-dracula-red');
                tabAll.classList.remove('text-white/40');
                tabAll.style.borderBottomColor = '#dc143c';
            }
            if (allTab) allTab.classList.remove('hidden');
        }
    };

    // =============================================
    // USER MANAGEMENT - Pending Approvals (Modals)
    // =============================================

    let pendingApprove = null;
    let pendingReject = null;

    // ===== OPEN APPROVE MODAL =====
    window.openApproveModal = function(userId, fullName) {
        pendingApprove = { userId, fullName };
        const messageEl = document.getElementById('approveMessage');
        if (messageEl) {
            messageEl.textContent = `Are you sure you want to approve "${fullName}" as a Customs Agent?`;
        }
        const modal = document.getElementById('approveModal');
        if (modal) {
            modal.classList.add('active');
            document.body.style.overflow = 'hidden';
        }
    };

    // ===== CLOSE APPROVE MODAL =====
    window.closeApproveModal = function() {
        const modal = document.getElementById('approveModal');
        if (modal) {
            modal.classList.remove('active');
            document.body.style.overflow = '';
        }
        pendingApprove = null;
    };

    // ===== CONFIRM APPROVE ACTION =====
    window.confirmApproveAction = function() {
        if (pendingApprove) {
            console.log(`✅ Approving user ${pendingApprove.userId}: ${pendingApprove.fullName}`);
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = getContextPath() + '/admin/users/approve';
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'userId';
            input.value = pendingApprove.userId;
            form.appendChild(input);
            document.body.appendChild(form);
            form.submit();
        }
        closeApproveModal();
    };

    // ===== OPEN REJECT MODAL =====
    window.openRejectModal = function(userId, fullName) {
        pendingReject = { userId, fullName };
        const messageEl = document.getElementById('rejectMessage');
        if (messageEl) {
            messageEl.textContent = `Are you sure you want to reject "${fullName}"'s application?`;
        }
        const modal = document.getElementById('rejectModal');
        if (modal) {
            modal.classList.add('active');
            document.body.style.overflow = 'hidden';
        }
    };

    // ===== CLOSE REJECT MODAL =====
    window.closeRejectModal = function() {
        const modal = document.getElementById('rejectModal');
        if (modal) {
            modal.classList.remove('active');
            document.body.style.overflow = '';
        }
        pendingReject = null;
    };

    // ===== CONFIRM REJECT ACTION =====
    window.confirmRejectAction = function() {
        if (pendingReject) {
            console.log(`❌ Rejecting user ${pendingReject.userId}: ${pendingReject.fullName}`);
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = getContextPath() + '/admin/users/reject';
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'userId';
            input.value = pendingReject.userId;
            form.appendChild(input);
            document.body.appendChild(form);
            form.submit();
        }
        closeRejectModal();
    };

    // =============================================
    // USER MANAGEMENT - All Users
    // =============================================

    // ===== VIEW USER DETAILS =====
    window.viewUserDetails = function(userId) {
        console.log(`👤 Viewing user ${userId} details`);
        showNotification('User details coming soon!', 'info');
    };

    // ===== TOGGLE USER STATUS =====
    window.toggleUserStatus = function(userId, currentStatus) {
        const action = currentStatus === 'DISABLED' ? 'enable' : 'suspend';
        console.log(`🔄 ${action}ing user ${userId}`);
        if (confirm(`Are you sure you want to ${action} this user?`)) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = getContextPath() + '/admin/users/toggle-status';
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'userId';
            input.value = userId;
            form.appendChild(input);
            document.body.appendChild(form);
            form.submit();
        }
    };

    // =============================================
    // USER MANAGEMENT - Filters (All Users)
    // =============================================

    const applyUserFilters = document.getElementById('applyUserFilters');
    if (applyUserFilters) {
        applyUserFilters.addEventListener('click', function() {
            const searchInput = document.getElementById('userSearchInput');
            const roleFilter = document.getElementById('roleFilter');

            const searchValue = searchInput ? searchInput.value.toLowerCase().trim() : '';
            const roleValue = roleFilter ? roleFilter.value : '';

            const rows = document.querySelectorAll('#allTab tbody tr:not(.no-results)');
            let visibleCount = 0;

            rows.forEach(row => {
                const name = row.querySelector('td:nth-child(2) .text-white\\/70')?.textContent?.toLowerCase() || '';
                const username = row.querySelector('td:nth-child(3)')?.textContent?.toLowerCase() || '';
                const email = row.querySelector('td:nth-child(4)')?.textContent?.toLowerCase() || '';
                const role = row.querySelector('td:nth-child(5) span')?.textContent || '';

                let show = true;

                if (searchValue) {
                    const searchable = name + ' ' + username + ' ' + email;
                    if (!searchable.includes(searchValue)) show = false;
                }

                if (roleValue && !role.includes(roleValue)) show = false;

                row.style.display = show ? '' : 'none';
                if (show) visibleCount++;
            });

            const countDisplay = document.querySelector('#allTab .text-white\\/20.text-xs');
            if (countDisplay) {
                countDisplay.textContent = `Showing ${visibleCount} users`;
            }
            console.log(`✅ ${visibleCount} users visible`);
        });
    }

    // =============================================
    // MODAL CLOSE - Click Outside
    // =============================================

    document.addEventListener('click', function(e) {
        const approveModal = document.getElementById('approveModal');
        if (approveModal && approveModal.classList.contains('active')) {
            if (e.target === approveModal) {
                closeApproveModal();
            }
        }
        const rejectModal = document.getElementById('rejectModal');
        if (rejectModal && rejectModal.classList.contains('active')) {
            if (e.target === rejectModal) {
                closeRejectModal();
            }
        }
    });

    // =============================================
    // MODAL CLOSE - Escape Key
    // =============================================

    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            closeApproveModal();
            closeRejectModal();
        }
    });

    // =============================================
    // UTILITY: GET CONTEXT PATH
    // =============================================

    function getContextPath() {
        const path = window.location.pathname;
        const parts = path.split('/');
        if (parts.length > 1 && parts[0] === '') {
            return parts[1] ? '/' + parts[1] : '';
        }
        return '';
    }

    // =============================================
    // UTILITY: SHOW NOTIFICATION
    // =============================================

    function showNotification(message, type) {
        if (typeof showInfo === 'function') {
            if (type === 'info') showInfo(message);
            else if (type === 'warning') showWarning(message);
            else if (type === 'error') showError(message);
            else showSuccess(message);
        } else {
            console.log(`[${type}] ${message}`);
        }
    }

    // =============================================
    // KEYBOARD SHORTCUTS
    // =============================================

    document.addEventListener('keydown', function(e) {
        // Ctrl+Shift+E - Edit Profile
        if (e.ctrlKey && e.shiftKey && e.key === 'E') {
            e.preventDefault();
            editProfile();
        }
        // Ctrl+Shift+P - Change Password
        if (e.ctrlKey && e.shiftKey && e.key === 'P') {
            e.preventDefault();
            changePassword();
        }
        // Ctrl+Shift+1 - Switch to Pending Tab
        if (e.ctrlKey && e.shiftKey && e.key === '1') {
            e.preventDefault();
            switchTab('pending');
        }
        // Ctrl+Shift+2 - Switch to All Users Tab
        if (e.ctrlKey && e.shiftKey && e.key === '2') {
            e.preventDefault();
            switchTab('all');
        }
    });

    console.log('👤 GlobalTrade Logistics - User Profile Loaded');
});