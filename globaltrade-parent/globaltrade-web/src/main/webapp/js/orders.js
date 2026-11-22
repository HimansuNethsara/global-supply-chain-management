// Global Trade - Orders JavaScript
document.addEventListener('DOMContentLoaded', function() {
    'use strict';

    const statusMap = {
        'Pending': 'PENDING',
        'Assigned': 'ASSIGNED',
        'Fulfilled': 'FULFILLED',
        'Cancelled': 'CANCELLED'
    };

    // ===== ORDERS LIST - Apply Filters =====
    const applyFiltersBtn = document.getElementById('applyFiltersBtn');
    if (applyFiltersBtn) {
        applyFiltersBtn.addEventListener('click', function() {
            const searchInput = document.getElementById('searchInput');
            const statusFilter = document.getElementById('statusFilter');

            const searchValue = searchInput ? searchInput.value.toLowerCase().trim() : '';
            const statusValue = statusFilter ? statusFilter.value : '';

            const rows = document.querySelectorAll('tbody tr:not(.no-results)');
            let visibleCount = 0;

            rows.forEach(row => {
                const orderId = row.querySelector('td:nth-child(2)')?.textContent || '';
                const destination = row.querySelector('td:nth-child(3)')?.textContent || '';

                // ===== FIX: Get status from the status cell (column 4) =====
                const statusCell = row.querySelector('td:nth-child(4) .transaction-status');
                const statusDisplayText = statusCell?.textContent?.trim() || '';

                // ===== FIX: Map display text to actual status value =====
                const status = statusMap[statusDisplayText] || statusDisplayText.toUpperCase();

                let show = true;
                if (searchValue) {
                    const searchable = orderId + ' ' + destination;
                    if (!searchable.toLowerCase().includes(searchValue)) show = false;
                }

                // ===== FIX: Compare status value (not display text) =====
                if (statusValue && status !== statusValue) show = false;

                row.style.display = show ? '' : 'none';
                if (show) visibleCount++;
            });

            // Show/hide no results
            let noResultsRow = document.querySelector('.no-results-row');
            if (visibleCount === 0 && rows.length > 0) {
                if (!noResultsRow) {
                    noResultsRow = document.createElement('tr');
                    noResultsRow.className = 'no-results-row';
                    const colspan = document.querySelector('thead tr')?.children?.length || 7;
                    noResultsRow.innerHTML = `
                        <td colspan="${colspan}" class="text-center text-white/20 py-8">
                            <div class="flex flex-col items-center gap-2">
                                <i class="fas fa-search text-3xl text-white/10"></i>
                                <span class="text-sm">No orders match your filters</span>
                                <button class="text-dracula-red text-xs hover:text-red-400 transition-all mt-2" onclick="clearOrderFilters()">
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
                countDisplay.textContent = `Showing ${visibleCount} orders`;
            }
            console.log(`✅ ${visibleCount} orders visible`);
        });
    }

    // ===== CLEAR ORDER FILTERS =====
    window.clearOrderFilters = function() {
        document.getElementById('searchInput').value = '';
        document.getElementById('statusFilter').value = '';
        document.getElementById('applyFiltersBtn').click();
    };

    // ===== ORDER CREATE - Add Item Row =====
    const addItemBtn = document.getElementById('addItemBtn');
    if (addItemBtn) {
        addItemBtn.addEventListener('click', function() {
            const container = document.getElementById('orderItemsContainer');
            const firstRow = container.querySelector('.order-item-row');
            if (!firstRow) return;

            const newRow = firstRow.cloneNode(true);
            // Clear input values
            newRow.querySelectorAll('select').forEach(sel => sel.selectedIndex = 0);
            newRow.querySelectorAll('input[type="number"]').forEach(inp => inp.value = 1);
            // Update remove button
            const removeBtn = newRow.querySelector('.remove-item-btn');
            if (removeBtn) {
                removeBtn.addEventListener('click', function() {
                    if (container.querySelectorAll('.order-item-row').length > 1) {
                        this.closest('.order-item-row').remove();
                    } else {
                        showNotification('Must have at least one item', 'warning');
                    }
                });
            }
            container.appendChild(newRow);
        });
    }

    // ===== ORDER CREATE - Remove Item =====
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('remove-item-btn') || e.target.closest('.remove-item-btn')) {
            const btn = e.target.closest('.remove-item-btn');
            const container = document.getElementById('orderItemsContainer');
            if (container && container.querySelectorAll('.order-item-row').length > 1) {
                btn.closest('.order-item-row').remove();
            } else {
                showNotification('Must have at least one item', 'warning');
            }
        }
    });

    // ===== ORDER CREATE - Submit =====
    const submitBtn = document.getElementById('submitBtn');
    if (submitBtn) {
        submitBtn.addEventListener('click', function(e) {
            e.preventDefault();
            const form = document.getElementById('orderCreateForm');
            const formData = new FormData(form);

            // Validation
            const destination = formData.get('destination')?.trim();
            const itemIds = formData.getAll('itemIds');
            const quantities = formData.getAll('quantities');

            // Validate destination
            if (!destination) {
                if (typeof showError === 'function') {
                    showError('Destination is required');
                } else {
                    alert('Destination is required');
                }
                document.getElementById('destination')?.focus();
                return;
            }

            // Validate at least one item
            let hasValidItem = false;
            for (let i = 0; i < itemIds.length; i++) {
                if (itemIds[i] && parseInt(quantities[i] || 0) > 0) {
                    hasValidItem = true;
                    break;
                }
            }

            if (!hasValidItem) {
                if (typeof showError === 'function') {
                    showError('Please select at least one item with quantity');
                } else {
                    alert('Please select at least one item with quantity');
                }
                return;
            }

            // Log the data
            console.log('📦 Creating order...');
            console.log('  Destination:', destination);
            console.log('  Notes:', formData.get('notes') || 'N/A');
            itemIds.forEach((id, index) => {
                console.log(`    Item: ${id}, Qty: ${quantities[index] || 1}`);
            });

            // ===== ACTUALLY SUBMIT THE FORM =====
            const originalText = this.innerHTML;
            this.innerHTML = '<i class="fas fa-spinner fa-spin mr-1.5"></i> Creating...';
            this.disabled = true;

            // Submit the form
            form.submit();
        });
    }

    // ===== ORDER CREATE - Reset =====
    const resetBtn = document.getElementById('resetBtn');
    if (resetBtn) {
        resetBtn.addEventListener('click', function(e) {
            e.preventDefault();
            const form = document.getElementById('orderCreateForm');
            if (form) form.reset();
            // Reset items to one row
            const container = document.getElementById('orderItemsContainer');
            const rows = container.querySelectorAll('.order-item-row');
            rows.forEach((row, index) => {
                if (index > 0) row.remove();
            });
            if (rows[0]) {
                rows[0].querySelectorAll('select').forEach(sel => sel.selectedIndex = 0);
                rows[0].querySelectorAll('input[type="number"]').forEach(inp => inp.value = 1);
            }
            console.log('🔄 Form reset');
        });
    }

    // ============================================================
    // ===== CANCEL ORDER =====
    // ============================================================

    window.confirmCancelOrder = function(orderId, destination) {
        // Get context path
        function getContextPath() {
            const path = window.location.pathname;
            const parts = path.split('/');
            if (parts.length > 1 && parts[0] === '') {
                return parts[1] ? '/' + parts[1] : '';
            }
            return '';
        }

        const messageEl = document.getElementById('deleteMessage');
        if (messageEl) {
            messageEl.textContent = `Are you sure you want to cancel order to "${destination}"? This action cannot be undone.`;
        }

        const modal = document.getElementById('deleteModal');
        if (modal) {
            modal.classList.add('active');
            document.body.style.overflow = 'hidden';
        }

        // Store pending delete
        window._pendingOrderCancel = { orderId, destination, contextPath: getContextPath() };
    };

    // ===== CLOSE DELETE MODAL =====
    window.closeDeleteModal = function() {
        const modal = document.getElementById('deleteModal');
        if (modal) {
            modal.classList.remove('active');
            document.body.style.overflow = '';
        }
        window._pendingOrderCancel = null;
    };

    // ===== CONFIRM CANCEL ACTION =====
    window.confirmDeleteAction = function() {
        const pending = window._pendingOrderCancel;
        if (pending) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = (pending.contextPath || '') + '/customers/orders/cancel';

            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'orderId';
            input.value = pending.orderId;
            form.appendChild(input);

            document.body.appendChild(form);
            console.log('Cancelling order:', pending.orderId);
            form.submit();
        }
        closeDeleteModal();
    };

    // ===== KEYBOARD SHORTCUTS =====
    document.addEventListener('keydown', function(e) {
        // Ctrl+Shift+O - Create Order (if on orders page)
        if (e.ctrlKey && e.shiftKey && e.key === 'O') {
            e.preventDefault();
            const createBtn = document.querySelector('.bg-gradient-to-r.from-dracula-red a');
            if (createBtn) {
                window.location.href = createBtn.href;
            }
        }
        // Ctrl+Shift+R - Reset form (if on create page)
        if (e.ctrlKey && e.shiftKey && e.key === 'R') {
            e.preventDefault();
            if (resetBtn) resetBtn.click();
        }
        // Ctrl+Shift+Enter - Submit form (if on create page)
        if (e.ctrlKey && e.shiftKey && e.key === 'Enter') {
            e.preventDefault();
            if (submitBtn) submitBtn.click();
        }
        // Escape - Close modal
        if (e.key === 'Escape') {
            closeDeleteModal();
        }
    });

    // ===== CLOSE MODAL BY CLICKING OUTSIDE =====
    document.addEventListener('click', function(e) {
        const modal = document.getElementById('deleteModal');
        if (modal && modal.classList.contains('active')) {
            if (e.target === modal || e.target.classList.contains('delete-modal-overlay')) {
                closeDeleteModal();
            }
        }
    });

    console.log('📦 Orders page loaded');
});