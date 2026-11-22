// Global Trade - Customs JavaScript
document.addEventListener('DOMContentLoaded', function() {
    'use strict';

    // ===== APPLY FILTERS =====
    const applyFiltersBtn = document.getElementById('applyFiltersBtn');
    if (applyFiltersBtn) {
        applyFiltersBtn.addEventListener('click', function() {
            const searchInput = document.getElementById('searchInput');
            const statusFilter = document.getElementById('statusFilter');
            const shipmentFilter = document.getElementById('shipmentFilter');

            const searchValue = searchInput ? searchInput.value.toLowerCase().trim() : '';
            const statusValue = statusFilter ? statusFilter.value : '';
            const shipmentValue = shipmentFilter ? shipmentFilter.value : '';

            const rows = document.querySelectorAll('tbody tr:not(.no-results)');
            let visibleCount = 0;

            rows.forEach(row => {
                const docType = row.querySelector('td:nth-child(2)')?.textContent?.toLowerCase() || '';
                const status = row.querySelector('td:nth-child(4) .transaction-status')?.textContent?.trim() || '';
                const shipmentLink = row.querySelector('td:nth-child(3) a');
                let shipmentId = '';
                if (shipmentLink) {
                    const href = shipmentLink.getAttribute('href');
                    const match = href?.match(/shipmentId=(\d+)/);
                    if (match) {
                        shipmentId = match[1];
                    }
                }

                let show = true;

                if (searchValue && !docType.includes(searchValue)) show = false;
                if (statusValue && status.toLowerCase() !== statusValue.toLowerCase()) show = false;
                if (shipmentValue && shipmentId !== shipmentValue) show = false;

                row.style.display = show ? '' : 'none';
                if (show) visibleCount++;
            });

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
                                <span class="text-sm">No documents match your filters</span>
                                <button class="text-dracula-red text-xs hover:text-red-400 transition-all mt-2" onclick="clearCustomsFilters()">
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
                countDisplay.textContent = `Showing ${visibleCount} documents`;
            }
            console.log(`✅ ${visibleCount} documents visible`);
        });
    }

    // ===== CLEAR CUSTOMS FILTERS =====
    window.clearCustomsFilters = function() {
        document.getElementById('searchInput').value = '';
        document.getElementById('statusFilter').value = '';
        document.getElementById('shipmentFilter').value = '';
        document.getElementById('applyFiltersBtn').click();
    };

    // ===== CREATE CUSTOMS DOCUMENT =====
    const submitBtn = document.getElementById('submitBtn');
    if (submitBtn && document.getElementById('customsDocumentCreateForm')) {
        submitBtn.addEventListener('click', function(e) {
            e.preventDefault();
            const form = document.getElementById('customsDocumentCreateForm');
            const formData = new FormData(form);

            const documentType = formData.get('documentType');
            const shipmentId = formData.get('shipmentId');

            if (!documentType) {
                showErrorNotification('Please select a document type');
                document.getElementById('documentType')?.focus();
                return;
            }
            if (!shipmentId) {
                showErrorNotification('Please select a shipment');
                document.getElementById('shipmentId')?.focus();
                return;
            }

            console.log('📄 Creating customs document...');
            form.submit();
        });
    }

    // ===== RESET =====
    const resetBtn = document.getElementById('resetBtn');
    if (resetBtn) {
        resetBtn.addEventListener('click', function(e) {
            e.preventDefault();
            const form = document.getElementById('customsDocumentCreateForm');
            if (form) form.reset();
            console.log('🔄 Form reset');
        });
    }

    // ===== STATUS UPDATE MODAL =====
    let pendingStatusUpdate = null;

    // ===== GET USER ROLE =====
    function getUserRole() {
        const roleInput = document.getElementById('userRole');
        return roleInput ? roleInput.value : 'LOGISTICS_COORDINATOR';
    }

    // ===== SHOW NOTIFICATION HELPER =====
    function showNotification(message, type) {
        if (typeof showInfo === 'function') {
            showInfo(message);
        } else if (type === 'error' && typeof showError === 'function') {
            showError(message);
        } else if (type === 'warning' && typeof showWarning === 'function') {
            showWarning(message);
        } else if (type === 'success' && typeof showSuccess === 'function') {
            showSuccess(message);
        } else {
            console.log(`[${type}] ${message}`);
        }
    }

    // ===== UPDATE DOCUMENT STATUS - Called from HTML onclick =====
    window.updateDocumentStatus = function(docId, currentStatus) {
        console.log(`📝 Opening status modal for document ${docId}, current status: ${currentStatus}`);
        openStatusModal(docId, currentStatus);
    };

    window.openStatusModal = function(docId, currentStatus, docType) {
        pendingStatusUpdate = { docId, currentStatus, docType };

        // Get user role
        const role = getUserRole();

        // Set document ID in form
        const docIdInput = document.getElementById('statusDocId');
        if (docIdInput) {
            docIdInput.value = docId;
        }

        // Display current status
        const statusBadge = document.getElementById('currentStatusBadge');
        if (statusBadge) {
            const statusClass = currentStatus === 'APPROVED' ? 'completed' :
                currentStatus === 'REJECTED' ? 'failed' : 'pending';
            statusBadge.className = `transaction-status ${statusClass}`;
            statusBadge.textContent = currentStatus || 'PENDING';
        }

        // Reset the status dropdown and enable/disable options based on role and current status
        const statusSelect = document.getElementById('newStatus');
        const helpMessage = document.getElementById('statusHelpMessage');

        if (statusSelect) {
            statusSelect.value = '';
            const options = statusSelect.options;

            // First, enable all options
            for (let i = 0; i < options.length; i++) {
                options[i].disabled = false;
            }

            // LOGISTICS_COORDINATOR: Can only change PENDING -> SUBMITTED
            if (role === 'LOGISTICS_COORDINATOR') {
                if (currentStatus === 'PENDING') {
                    // Only SUBMITTED is allowed
                    for (let i = 0; i < options.length; i++) {
                        const opt = options[i];
                        if (opt.value && opt.value !== 'SUBMITTED') {
                            opt.disabled = true;
                        }
                    }
                    if (helpMessage) {
                        helpMessage.textContent = 'As Logistics Coordinator, you can only submit pending documents (PENDING → SUBMITTED)';
                    }
                    // Enable the submit button
                    const updateBtn = document.getElementById('statusUpdateBtn');
                    if (updateBtn) updateBtn.disabled = false;

                } else if (currentStatus === 'SUBMITTED') {
                    // No options allowed - show message
                    for (let i = 0; i < options.length; i++) {
                        if (options[i].value) {
                            options[i].disabled = true;
                        }
                    }
                    if (helpMessage) {
                        helpMessage.textContent = 'Only Customs Agents can review submitted documents';
                    }
                    // Disable the submit button
                    const updateBtn = document.getElementById('statusUpdateBtn');
                    if (updateBtn) updateBtn.disabled = true;
                    showNotification('Only Customs Agents can approve or reject submitted documents', 'warning');

                } else {
                    // APPROVED or REJECTED - cannot change
                    for (let i = 0; i < options.length; i++) {
                        if (options[i].value) {
                            options[i].disabled = true;
                        }
                    }
                    if (helpMessage) {
                        helpMessage.textContent = 'This document has already been reviewed and cannot be changed';
                    }
                    const updateBtn = document.getElementById('statusUpdateBtn');
                    if (updateBtn) updateBtn.disabled = true;
                }
            }
            // CUSTOMS_AGENT: Can only change SUBMITTED -> APPROVED/REJECTED
            else if (role === 'CUSTOMS_AGENT') {
                if (currentStatus === 'SUBMITTED') {
                    // Only APPROVED and REJECTED are allowed
                    for (let i = 0; i < options.length; i++) {
                        const opt = options[i];
                        if (opt.value && opt.value !== 'APPROVED' && opt.value !== 'REJECTED') {
                            opt.disabled = true;
                        }
                    }
                    if (helpMessage) {
                        helpMessage.textContent = 'As Customs Agent, you can approve or reject submitted documents (SUBMITTED → APPROVED/REJECTED)';
                    }
                    const updateBtn = document.getElementById('statusUpdateBtn');
                    if (updateBtn) updateBtn.disabled = false;

                } else if (currentStatus === 'PENDING') {
                    // No options allowed - show message
                    for (let i = 0; i < options.length; i++) {
                        if (options[i].value) {
                            options[i].disabled = true;
                        }
                    }
                    if (helpMessage) {
                        helpMessage.textContent = 'Only Logistics Coordinators can submit pending documents';
                    }
                    const updateBtn = document.getElementById('statusUpdateBtn');
                    if (updateBtn) updateBtn.disabled = true;
                    showNotification('Only Logistics Coordinators can submit pending documents', 'warning');

                } else {
                    // APPROVED or REJECTED - cannot change
                    for (let i = 0; i < options.length; i++) {
                        if (options[i].value) {
                            options[i].disabled = true;
                        }
                    }
                    if (helpMessage) {
                        helpMessage.textContent = 'This document has already been reviewed and cannot be changed';
                    }
                    const updateBtn = document.getElementById('statusUpdateBtn');
                    if (updateBtn) updateBtn.disabled = true;
                }
            }
            // Other roles - no permissions
            else {
                for (let i = 0; i < options.length; i++) {
                    if (options[i].value) {
                        options[i].disabled = true;
                    }
                }
                if (helpMessage) {
                    helpMessage.textContent = 'You do not have permission to update document status';
                }
                const updateBtn = document.getElementById('statusUpdateBtn');
                if (updateBtn) updateBtn.disabled = true;
                showNotification('You do not have permission to update document status', 'error');
            }
        }

        // Show modal
        const modal = document.getElementById('statusUpdateModal');
        if (modal) {
            modal.classList.remove('hidden');
            modal.classList.add('active');
        }
        document.body.style.overflow = 'hidden';
    };

    window.closeStatusModal = function() {
        const modal = document.getElementById('statusUpdateModal');
        if (modal) {
            modal.classList.add('hidden');
            modal.classList.remove('active');
        }
        document.body.style.overflow = '';
        pendingStatusUpdate = null;
    };

    // ===== STATUS UPDATE SUBMIT =====
    const statusUpdateForm = document.getElementById('statusUpdateForm');
    if (statusUpdateForm) {
        statusUpdateForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const formData = new FormData(this);
            const status = formData.get('status');
            const docId = formData.get('docId');

            if (!status) {
                showErrorNotification('Please select a new status');
                document.getElementById('newStatus')?.focus();
                return;
            }

            console.log(`🔄 Updating document ${docId} status to ${status}`);

            // Check if submit button is disabled (prevent invalid submissions)
            const updateBtn = document.getElementById('statusUpdateBtn');
            if (updateBtn && updateBtn.disabled) {
                showErrorNotification('You cannot update this document with the current status and role');
                return;
            }

            // Submit the form
            this.submit();
        });
    }

    // Close modal with Escape key
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            closeStatusModal();
            closeDeleteModal();
        }
    });

    // Close modal by clicking outside
    document.addEventListener('click', function(e) {
        // Status modal
        const statusModal = document.getElementById('statusUpdateModal');
        if (statusModal && !statusModal.classList.contains('hidden') && statusModal.classList.contains('active')) {
            if (e.target === statusModal) {
                closeStatusModal();
            }
        }
        // Delete modal
        const deleteModal = document.getElementById('deleteModal');
        if (deleteModal && !deleteModal.classList.contains('hidden') && deleteModal.classList.contains('active')) {
            if (e.target === deleteModal) {
                closeDeleteModal();
            }
        }
    });

    // ===== DELETE CUSTOMS DOCUMENT =====
    window.confirmDelete = function(docId, docType, contextPath) {
        const messageEl = document.getElementById('deleteMessage');
        if (messageEl) {
            messageEl.textContent = `Are you sure you want to delete customs document "${docType}"? This action cannot be undone.`;
        }

        const modal = document.getElementById('deleteModal');
        if (modal) {
            modal.classList.remove('hidden');
            modal.classList.add('active');
        }

        // Store pending delete
        window._pendingDelete = { docId, docType, contextPath };
        document.body.style.overflow = 'hidden';
    };

    // ===== CLOSE DELETE MODAL =====
    window.closeDeleteModal = function() {
        const modal = document.getElementById('deleteModal');
        if (modal) {
            modal.classList.add('hidden');
            modal.classList.remove('active');
        }
        window._pendingDelete = null;
        document.body.style.overflow = '';
    };

    // ===== CONFIRM DELETE ACTION =====
    window.confirmDeleteAction = function() {
        const pending = window._pendingDelete;
        if (pending) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = (pending.contextPath || '') + '/customs/delete';

            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'docId';
            input.value = pending.docId;
            form.appendChild(input);

            document.body.appendChild(form);
            console.log('Deleting customs document:', pending.docId);
            form.submit();
        }
        closeDeleteModal();
    };

    // ===== NOTIFICATION HELPER =====
    function showErrorNotification(message) {
        if (typeof showError === 'function') {
            showError(message);
        } else {
            alert('❌ ' + message);
        }
    }

    // ===== KEYBOARD SHORTCUTS =====
    document.addEventListener('keydown', function(e) {
        // Ctrl+Shift+S - Submit/Update Status (if modal is open)
        if (e.ctrlKey && e.shiftKey && e.key === 'S') {
            const modal = document.getElementById('statusUpdateModal');
            if (modal && !modal.classList.contains('hidden')) {
                e.preventDefault();
                const form = document.getElementById('statusUpdateForm');
                if (form) {
                    const submitBtn = document.getElementById('statusUpdateBtn');
                    if (submitBtn) submitBtn.click();
                }
            }
        }
    });

    console.log('📄 Customs page loaded');
});