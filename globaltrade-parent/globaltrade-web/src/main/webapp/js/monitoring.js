// Global Trade - Monitoring JavaScript
document.addEventListener('DOMContentLoaded', function() {
    'use strict';

    // =============================================
    // SYSTEM TIMERS
    // =============================================

    // ===== REFRESH TIMERS =====
    window.refreshTimers = function() {
        console.log('🔄 Refreshing timers...');
        const btn = document.querySelector('.bg-dracula-red\\/20');
        if (btn) {
            const originalText = btn.innerHTML;
            btn.innerHTML = '<i class="fas fa-spinner fa-spin mr-1.5"></i> Refreshing...';
            btn.disabled = true;
            setTimeout(() => {
                btn.innerHTML = originalText;
                btn.disabled = false;
                console.log('✅ Timers refreshed (placeholder)');
                showNotification('Timers refreshed successfully', 'success');
            }, 1000);
        }
    };

    // ===== UPDATE TIMER STATUS =====
    window.updateTimerStatus = function(timerId, newStatus) {
        const action = newStatus === 'ACTIVE' ? 'resume' : 'pause';
        console.log(`⏱️ ${action}ing timer ${timerId} to ${newStatus}`);

        if (confirm(`Are you sure you want to ${action} this timer?`)) {
            showNotification(`Timer ${action}ed successfully`, 'success');
        }
    };

    // ===== RUN TIMER NOW =====
    window.runTimerNow = function(timerId) {
        console.log(`▶️ Running timer ${timerId} now...`);
        if (confirm('Run this timer immediately?')) {
            showNotification('Timer triggered successfully', 'success');
        }
    };

    // ===== DELETE TIMER =====
    window.deleteTimer = function(timerId, timerName) {
        console.log(`🗑️ Deleting timer ${timerId}: ${timerName}`);
        if (confirm(`Are you sure you want to delete timer "${timerName}"?`)) {
            showNotification(`Timer "${timerName}" deleted`, 'success');
        }
    };

    // =============================================
    // ALERTS
    // =============================================

    // ===== APPLY ALERT FILTERS =====
    const applyFiltersBtn = document.getElementById('applyFiltersBtn');
    if (applyFiltersBtn) {
        applyFiltersBtn.addEventListener('click', function() {
            const alertTypeFilter = document.getElementById('alertTypeFilter');
            const severityFilter = document.getElementById('severityFilter');
            const statusFilter = document.getElementById('statusFilter');

            const alertTypeValue = alertTypeFilter ? alertTypeFilter.value : '';
            const severityValue = severityFilter ? severityFilter.value : '';
            const statusValue = statusFilter ? statusFilter.value : '';

            const rows = document.querySelectorAll('tbody tr:not(.no-results)');
            let visibleCount = 0;

            rows.forEach(row => {
                // Get type from the type column (td:nth-child(2))
                const typeCell = row.querySelector('td:nth-child(2) span');
                const typeText = typeCell ? typeCell.textContent.trim() : '';

                // Get severity from severity column (td:nth-child(3))
                const severityCell = row.querySelector('td:nth-child(3) span');
                const severityText = severityCell ? severityCell.textContent.trim() : '';

                // Get status from status column (td:nth-child(7))
                const statusCell = row.querySelector('td:nth-child(7) span');
                const statusText = statusCell ? statusCell.textContent.trim() : '';

                let show = true;

                // Alert Type filter
                if (alertTypeValue) {
                    const typeMap = {
                        'SHIPMENT_DELAY': 'Delay',
                        'LOW_INVENTORY': 'Low Stock',
                        'VENDOR_ISSUE': 'Vendor',
                        'CUSTOMS_DEADLINE': 'Customs'
                    };
                    if (!typeText.includes(typeMap[alertTypeValue])) {
                        show = false;
                    }
                }

                // Severity filter
                if (severityValue && severityText !== severityValue) {
                    show = false;
                }

                // Status filter
                if (statusValue) {
                    const statusMap = { 'true': 'Resolved', 'false': 'Active' };
                    if (statusText !== statusMap[statusValue]) {
                        show = false;
                    }
                }

                row.style.display = show ? '' : 'none';
                if (show) visibleCount++;
            });

            // Show/hide no results
            let noResultsRow = document.querySelector('.no-results-row');
            if (visibleCount === 0 && rows.length > 0) {
                if (!noResultsRow) {
                    noResultsRow = document.createElement('tr');
                    noResultsRow.className = 'no-results-row';
                    const colspan = document.querySelector('thead tr')?.children?.length || 8;
                    noResultsRow.innerHTML = `
                        <td colspan="${colspan}" class="text-center text-white/20 py-8">
                            <div class="flex flex-col items-center gap-2">
                                <i class="fas fa-search text-3xl text-white/10"></i>
                                <span class="text-sm">No alerts match your filters</span>
                                <button class="text-dracula-red text-xs hover:text-red-400 transition-all mt-2" onclick="clearAlertFilters()">
                                    <i class="fas fa-undo mr-1"></i>Clear filters
                                </button>
                            </div>
                        </td>
                    `;
                    document.querySelector('tbody')?.appendChild(noResultsRow);
                }
                noResultsRow.style.display = '';
            } else if (noResultsRow) {
                noResultsRow.style.display = 'none';
            }

            const countDisplay = document.querySelector('.text-white\\/20.text-xs');
            if (countDisplay) {
                countDisplay.textContent = `Showing ${visibleCount} alerts`;
            }
            console.log(`✅ ${visibleCount} alerts visible`);
        });
    }

    // ===== CLEAR ALERT FILTERS =====
    window.clearAlertFilters = function() {
        document.getElementById('alertTypeFilter').value = '';
        document.getElementById('severityFilter').value = '';
        document.getElementById('statusFilter').value = '';
        document.getElementById('applyFiltersBtn').click();
    };

    // ===== RESOLVE ALERT =====
    window.resolveAlert = function(alertId) {
        console.log(`✅ Resolving alert ${alertId}...`);
        if (confirm('Mark this alert as resolved?')) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = getContextPath() + '/monitoring/alert/action';

            const actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = 'resolve';
            form.appendChild(actionInput);

            const idInput = document.createElement('input');
            idInput.type = 'hidden';
            idInput.name = 'alertId';
            idInput.value = alertId;
            form.appendChild(idInput);

            document.body.appendChild(form);
            form.submit();
        }
    };

    // ===== RESOLVE ALL ALERTS =====
    window.resolveAllAlerts = function() {
        console.log('✅ Resolving all alerts...');
        // Count active alerts using class-based detection
        const rows = document.querySelectorAll('tbody tr');
        let activeCount = 0;
        rows.forEach(row => {
            const statusCell = row.querySelector('td:nth-child(7) span');
            if (statusCell && statusCell.textContent.trim() === 'Active') {
                activeCount++;
            }
        });

        if (activeCount === 0) {
            if (typeof showNotification === 'function') {
                showNotification('No active alerts to resolve', 'info');
            }
            return;
        }
        if (confirm(`Resolve all ${activeCount} active alerts?`)) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = getContextPath() + '/monitoring/alert/action';

            const actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = 'resolveAll';
            form.appendChild(actionInput);

            document.body.appendChild(form);
            form.submit();
        }
    };

    // ===== DELETE ALERT =====
    window.deleteAlert = function(alertId) {
        console.log(`🗑️ Deleting alert ${alertId}...`);
        if (confirm('Are you sure you want to delete this alert?')) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = getContextPath() + '/monitoring/alert/action';

            const actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = 'delete';
            form.appendChild(actionInput);

            const idInput = document.createElement('input');
            idInput.type = 'hidden';
            idInput.name = 'alertId';
            idInput.value = alertId;
            form.appendChild(idInput);

            document.body.appendChild(form);
            form.submit();
        }
    };

    // =============================================
    // AUDIT LOGS
    // =============================================

    // ===== APPLY AUDIT FILTERS =====
    const auditApplyBtn = document.getElementById('applyFiltersBtn');
    if (auditApplyBtn) {
        auditApplyBtn.addEventListener('click', function() {
            const searchInput = document.getElementById('searchInput');
            const actionFilter = document.getElementById('actionFilter');
            const entityFilter = document.getElementById('entityFilter');

            const searchValue = searchInput ? searchInput.value.toLowerCase().trim() : '';
            const actionValue = actionFilter ? actionFilter.value : '';
            const entityValue = entityFilter ? entityFilter.value : '';

            const rows = document.querySelectorAll('tbody tr:not(.no-results)');
            let visibleCount = 0;

            rows.forEach(row => {
                // Get data from each column
                const userCell = row.querySelector('td:nth-child(2)');
                const userText = userCell ? userCell.textContent.toLowerCase() : '';

                const actionCell = row.querySelector('td:nth-child(3) span');
                const actionText = actionCell ? actionCell.textContent.trim() : '';

                const entityCell = row.querySelector('td:nth-child(4) span');
                const entityText = entityCell ? entityCell.textContent.trim() : '';

                const detailsCell = row.querySelector('td:nth-child(6)');
                const detailsText = detailsCell ? detailsCell.textContent.toLowerCase() : '';

                const idCell = row.querySelector('td:nth-child(1)');
                const idText = idCell ? idCell.textContent : '';

                let show = true;

                // Search filter - search across multiple columns
                if (searchValue) {
                    const searchable = userText + ' ' + detailsText + ' ' + idText;
                    if (!searchable.includes(searchValue)) {
                        show = false;
                    }
                }

                // Action filter
                if (actionValue && show) {
                    // Map display text to action value
                    const actionMap = {
                        'Login': 'LOGIN',
                        'Logout': 'LOGOUT',
                        'Create': 'CREATE',
                        'Update': 'UPDATE',
                        'Delete': 'DELETE',
                        'View': 'VIEW',
                        'Timer': 'TIMER',
                        'Performance': 'PERFORMANCE'
                    };
                    const mappedAction = actionMap[actionText] || actionText.toUpperCase();
                    if (mappedAction !== actionValue) {
                        show = false;
                    }
                }

                // Entity filter
                if (entityValue && show) {
                    const entityMap = {
                        'User': 'USER',
                        'Vendor': 'VENDOR',
                        'Shipment': 'SHIPMENT',
                        'Inventory': 'INVENTORY',
                        'Customer': 'CUSTOMER',
                        'Order': 'ORDER',
                        'Customs': 'CUSTOMS',
                        'Warehouse': 'WAREHOUSE',
                        'Carrier': 'CARRIER'
                    };
                    const mappedEntity = entityMap[entityText] || entityText.toUpperCase();
                    if (mappedEntity !== entityValue) {
                        show = false;
                    }
                }

                row.style.display = show ? '' : 'none';
                if (show) visibleCount++;
            });

            // Show/hide no results
            let noResultsRow = document.querySelector('.no-results-row');
            const allRows = document.querySelectorAll('tbody tr');
            const totalRows = allRows.length - (noResultsRow ? 1 : 0);

            if (visibleCount === 0 && totalRows > 0) {
                if (!noResultsRow) {
                    noResultsRow = document.createElement('tr');
                    noResultsRow.className = 'no-results-row';
                    const colspan = document.querySelector('thead tr')?.children?.length || 7;
                    noResultsRow.innerHTML = `
                    <td colspan="${colspan}" class="text-center text-white/20 py-8">
                        <div class="flex flex-col items-center gap-2">
                            <i class="fas fa-search text-3xl text-white/10"></i>
                            <span class="text-sm">No audit logs match your filters</span>
                            <button class="text-dracula-red text-xs hover:text-red-400 transition-all mt-2" onclick="clearAuditFilters()">
                                <i class="fas fa-undo mr-1"></i>Clear filters
                            </button>
                        </div>
                    </td>
                `;
                    document.querySelector('tbody')?.appendChild(noResultsRow);
                }
                noResultsRow.style.display = '';
            } else if (noResultsRow) {
                noResultsRow.style.display = 'none';
            }

            // Update count
            const countDisplay = document.querySelector('.text-white\\/20.text-xs');
            if (countDisplay) {
                countDisplay.textContent = `Showing ${visibleCount} logs`;
            }
            console.log(`✅ ${visibleCount} audit logs visible`);
        });
    }

    // ===== CLEAR AUDIT FILTERS =====
    window.clearAuditFilters = function() {
        const searchInput = document.getElementById('searchInput');
        const actionFilter = document.getElementById('actionFilter');
        const entityFilter = document.getElementById('entityFilter');

        if (searchInput) searchInput.value = '';
        if (actionFilter) actionFilter.value = '';
        if (entityFilter) entityFilter.value = '';

        const applyBtn = document.getElementById('applyFiltersBtn');
        if (applyBtn) applyBtn.click();
    };

    // ===== REFRESH AUDIT LOGS =====
    window.refreshLogs = function() {
        console.log('🔄 Refreshing audit logs...');
        const btn = document.querySelector('.bg-dracula-red\\/20');
        if (btn) {
            const originalText = btn.innerHTML;
            btn.innerHTML = '<i class="fas fa-spinner fa-spin mr-1.5"></i> Refreshing...';
            btn.disabled = true;
            setTimeout(() => {
                btn.innerHTML = originalText;
                btn.disabled = false;
                // Reload the page to get fresh data
                window.location.reload();
            }, 1000);
        }
    };

    // =============================================
// EXCEPTION LOGS
// =============================================

// ===== VIEW EXCEPTION DETAILS =====
    window.viewExceptionDetails = function(exceptionId) {
        console.log(`🔍 Viewing exception ${exceptionId} details...`);

        // Populate modal with data from the row
        const rows = document.querySelectorAll('tbody tr');
        let targetRow = null;

        rows.forEach(row => {
            const idCell = row.querySelector('td:nth-child(1)');
            if (idCell && idCell.textContent.trim() === exceptionId.toString()) {
                targetRow = row;
            }
        });

        if (targetRow) {
            const typeCell = targetRow.querySelector('td:nth-child(2) span');
            const messageCell = targetRow.querySelector('td:nth-child(3)');
            const entityCell = targetRow.querySelector('td:nth-child(4)');
            const dateCell = targetRow.querySelector('td:nth-child(5)');
            const statusCell = targetRow.querySelector('td:nth-child(6) span');

            document.getElementById('exceptionId').textContent = exceptionId;
            document.getElementById('exceptionType').textContent = typeCell ? typeCell.textContent.trim() : 'N/A';
            document.getElementById('exceptionMessage').textContent = messageCell ? messageCell.textContent.trim() : 'N/A';
            document.getElementById('exceptionEntity').textContent = entityCell ? entityCell.textContent.trim() : '—';
            document.getElementById('exceptionOccurredAt').textContent = dateCell ? dateCell.textContent.trim() : 'N/A';
            document.getElementById('exceptionStatus').textContent = statusCell ? statusCell.textContent.trim() : 'N/A';
            document.getElementById('exceptionStackTrace').textContent = 'Stack trace details would be displayed here...';
        }

        const modal = document.getElementById('exceptionModal');
        if (modal) {
            modal.classList.remove('hidden');
            document.body.style.overflow = 'hidden';
        }
    };

// ===== CLOSE EXCEPTION MODAL =====
    window.closeExceptionModal = function() {
        const modal = document.getElementById('exceptionModal');
        if (modal) {
            modal.classList.add('hidden');
            document.body.style.overflow = '';
        }
    };

// ===== RESOLVE EXCEPTION =====
    window.resolveException = function(exceptionId) {
        console.log(`✅ Resolving exception ${exceptionId}...`);
        if (confirm('Mark this exception as resolved?')) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = getContextPath() + '/monitoring/exception/action';

            const actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = 'resolve';
            form.appendChild(actionInput);

            const idInput = document.createElement('input');
            idInput.type = 'hidden';
            idInput.name = 'exceptionId';
            idInput.value = exceptionId;
            form.appendChild(idInput);

            document.body.appendChild(form);
            form.submit();
        }
    };

// ===== DELETE EXCEPTION =====
    window.deleteException = function(exceptionId) {
        console.log(`🗑️ Deleting exception ${exceptionId}...`);
        if (confirm('Are you sure you want to delete this exception log?')) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = getContextPath() + '/monitoring/exception/action';

            const actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = 'delete';
            form.appendChild(actionInput);

            const idInput = document.createElement('input');
            idInput.type = 'hidden';
            idInput.name = 'exceptionId';
            idInput.value = exceptionId;
            form.appendChild(idInput);

            document.body.appendChild(form);
            form.submit();
        }
    };

// ===== APPLY EXCEPTION FILTERS =====
    const exceptionApplyBtn = document.getElementById('applyFiltersBtn');
    if (exceptionApplyBtn && document.querySelector('#exceptionTypeFilter')) {
        exceptionApplyBtn.addEventListener('click', function() {
            const searchInput = document.getElementById('searchInput');
            const exceptionTypeFilter = document.getElementById('exceptionTypeFilter');
            const statusFilter = document.getElementById('statusFilter');

            const searchValue = searchInput ? searchInput.value.toLowerCase().trim() : '';
            const exceptionTypeValue = exceptionTypeFilter ? exceptionTypeFilter.value : '';
            const statusValue = statusFilter ? statusFilter.value : '';

            const rows = document.querySelectorAll('tbody tr:not(.no-results)');
            let visibleCount = 0;

            rows.forEach(row => {
                const typeCell = row.querySelector('td:nth-child(2) span');
                const typeText = typeCell ? typeCell.textContent.trim() : '';

                const messageCell = row.querySelector('td:nth-child(3)');
                const messageText = messageCell ? messageCell.textContent.toLowerCase() : '';

                const statusCell = row.querySelector('td:nth-child(6) span');
                const statusText = statusCell ? statusCell.textContent.trim() : '';

                let show = true;

                if (searchValue) {
                    const searchable = typeText + ' ' + messageText;
                    if (!searchable.toLowerCase().includes(searchValue)) {
                        show = false;
                    }
                }

                if (exceptionTypeValue && show) {
                    // Match exception type (case-insensitive)
                    if (!typeText.toLowerCase().includes(exceptionTypeValue.toLowerCase())) {
                        show = false;
                    }
                }

                if (statusValue && show) {
                    const statusMap = { 'true': 'Resolved', 'false': 'Unresolved' };
                    if (statusText !== statusMap[statusValue]) {
                        show = false;
                    }
                }

                row.style.display = show ? '' : 'none';
                if (show) visibleCount++;
            });

            // Show/hide no results
            let noResultsRow = document.querySelector('.no-results-row');
            const allRows = document.querySelectorAll('tbody tr');
            const totalRows = allRows.length - (noResultsRow ? 1 : 0);

            if (visibleCount === 0 && totalRows > 0) {
                if (!noResultsRow) {
                    noResultsRow = document.createElement('tr');
                    noResultsRow.className = 'no-results-row';
                    const colspan = document.querySelector('thead tr')?.children?.length || 7;
                    noResultsRow.innerHTML = `
                    <td colspan="${colspan}" class="text-center text-white/20 py-8">
                        <div class="flex flex-col items-center gap-2">
                            <i class="fas fa-search text-3xl text-white/10"></i>
                            <span class="text-sm">No exceptions match your filters</span>
                            <button class="text-dracula-red text-xs hover:text-red-400 transition-all mt-2" onclick="clearExceptionFilters()">
                                <i class="fas fa-undo mr-1"></i>Clear filters
                            </button>
                        </div>
                    </td>
                `;
                    document.querySelector('tbody')?.appendChild(noResultsRow);
                }
                noResultsRow.style.display = '';
            } else if (noResultsRow) {
                noResultsRow.style.display = 'none';
            }

            const countDisplay = document.querySelector('.text-white\\/20.text-xs');
            if (countDisplay) {
                countDisplay.textContent = `Showing ${visibleCount} exceptions`;
            }
            console.log(`✅ ${visibleCount} exceptions visible`);
        });
    }

// ===== CLEAR EXCEPTION FILTERS =====
    window.clearExceptionFilters = function() {
        const searchInput = document.getElementById('searchInput');
        const exceptionTypeFilter = document.getElementById('exceptionTypeFilter');
        const statusFilter = document.getElementById('statusFilter');

        if (searchInput) searchInput.value = '';
        if (exceptionTypeFilter) exceptionTypeFilter.value = '';
        if (statusFilter) statusFilter.value = '';

        const applyBtn = document.getElementById('applyFiltersBtn');
        if (applyBtn) applyBtn.click();
    };

// Close modal with Escape key
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            closeExceptionModal();
        }
    });

// Close modal by clicking outside
    document.addEventListener('click', function(e) {
        const modal = document.getElementById('exceptionModal');
        if (modal && !modal.classList.contains('hidden')) {
            if (e.target === modal || e.target.classList.contains('bg-black/70')) {
                closeExceptionModal();
            }
        }
    });

    // ===== GET CONTEXT PATH =====
    function getContextPath() {
        const path = window.location.pathname;
        const parts = path.split('/');
        if (parts.length > 1 && parts[0] === '') {
            return parts[1] ? '/' + parts[1] : '';
        }
        return '';
    }


    // ===== KEYBOARD SHORTCUTS =====
    document.addEventListener('keydown', function(e) {
        // Ctrl+Shift+R - Refresh Timers (if on timers page)
        if (e.ctrlKey && e.shiftKey && e.key === 'R') {
            e.preventDefault();
            if (document.querySelector('.bg-dracula-red\\/20')) {
                refreshTimers();
            }
        }
        // Ctrl+Shift+A - Resolve All Alerts (if on alerts page)
        if (e.ctrlKey && e.shiftKey && e.key === 'A') {
            e.preventDefault();
            if (document.querySelector('.bg-green-500\\/20')) {
                resolveAllAlerts();
            }
        }
    });

    console.log('📊 Monitoring page loaded');
});