// Global Trade - Vendors JavaScript
document.addEventListener('DOMContentLoaded', function() {
    'use strict';

    // Get context path
    function getContextPath() {
        const path = window.location.pathname;
        const parts = path.split('/');
        if (parts.length > 1 && parts[0] === '') {
            return parts[1] ? '/' + parts[1] : '';
        }
        return '';
    }

    // ===== VENDORS LIST - Apply Filters =====
    document.getElementById('applyFiltersBtn')?.addEventListener('click', function() {
        const searchInput = document.getElementById('searchInput');
        const countryFilter = document.getElementById('countryFilter');
        const statusFilter = document.getElementById('statusFilter');
        const ratingFilter = document.getElementById('ratingFilter');

        const searchValue = searchInput ? searchInput.value.toLowerCase().trim() : '';
        const countryValue = countryFilter ? countryFilter.value : '';
        const statusValue = statusFilter ? statusFilter.value : '';
        const ratingValue = ratingFilter ? ratingFilter.value : '';

        const rows = document.querySelectorAll('tbody tr:not(.no-results)');
        let visibleCount = 0;

        rows.forEach(row => {
            const vendorName = row.querySelector('td:nth-child(2) .text-white\\/70')?.textContent?.toLowerCase() || '';
            const vendorEmail = row.querySelector('td:nth-child(3)')?.textContent?.toLowerCase() || '';
            const vendorCountry = row.querySelector('td:nth-child(5) span')?.textContent?.trim() || '';
            const vendorStatus = row.querySelector('td:nth-child(7) .transaction-status')?.textContent?.trim() || '';
            const vendorRating = row.querySelector('td:nth-child(6)')?.textContent?.trim() || '';

            let show = true;

            // Search filter
            if (searchValue) {
                const searchable = vendorName + ' ' + vendorEmail;
                if (!searchable.includes(searchValue)) {
                    show = false;
                }
            }

            // Country filter - case-insensitive
            if (countryValue && vendorCountry.toLowerCase() !== countryValue.toLowerCase()) {
                show = false;
            }

            // Status filter - case-insensitive
            if (statusValue && vendorStatus.toLowerCase() !== statusValue.toLowerCase()) {
                show = false;
            }

            // Rating filter
            if (ratingValue) {
                const minRating = parseFloat(ratingValue);
                const ratingMatch = vendorRating.match(/^([\d.]+)/);
                const vendorRatingNum = ratingMatch ? parseFloat(ratingMatch[1]) : 0;
                if (vendorRatingNum < minRating) {
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
                const colspan = document.querySelector('thead tr')?.children?.length || 9;
                noResultsRow.innerHTML = `
                <td colspan="${colspan}" class="text-center text-white/20 py-8">
                    <div class="flex flex-col items-center gap-2">
                        <i class="fas fa-search text-3xl text-white/10"></i>
                        <span class="text-sm">No vendors match your filters</span>
                        <button class="text-dracula-red text-xs hover:text-red-400 transition-all mt-2" onclick="clearVendorFilters()">
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
        const countDisplay = document.querySelector('.table-footer-count');
        if (countDisplay) {
            countDisplay.textContent = `Showing ${visibleCount} vendors`;
        }

        console.log(`✅ Filters applied: ${visibleCount} vendors visible`);
    });

    // ===== CLEAR FILTERS =====
    window.clearVendorFilters = function() {
        document.getElementById('searchInput').value = '';
        document.getElementById('countryFilter').value = '';
        document.getElementById('statusFilter').value = '';
        document.getElementById('ratingFilter').value = '';
        document.getElementById('applyFiltersBtn').click();
    };

    // Submit handler to handle both create and edit
    const submitBtn = document.getElementById('submitBtn');
    if (submitBtn) {
        submitBtn.addEventListener('click', function(e) {
            e.preventDefault();
            const form = document.getElementById('vendorForm');
            const formData = new FormData(form);

            // Validation
            const vendorName = formData.get('vendorName')?.trim();
            const contactEmail = formData.get('contactEmail')?.trim();
            const country = formData.get('country');
            const editMode = formData.get('editMode') === 'true';
            const buttonText = editMode ? 'Updating...' : 'Creating...';

            if (!vendorName) {
                showErrorNotification('Vendor name is required');
                document.getElementById('vendorName')?.focus();
                return;
            }
            if (!contactEmail) {
                showErrorNotification('Contact email is required');
                document.getElementById('contactEmail')?.focus();
                return;
            }
            if (!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(contactEmail)) {
                showErrorNotification('Please enter a valid email address');
                document.getElementById('contactEmail')?.focus();
                return;
            }
            if (!country) {
                showErrorNotification('Please select a country');
                document.getElementById('country')?.focus();
                return;
            }

            console.log(`📝 ${editMode ? 'Updating' : 'Creating'} vendor...`);
            console.log('  Name:', vendorName);
            console.log('  Email:', contactEmail);
            console.log('  Phone:', formData.get('contactPhone') || 'N/A');
            console.log('  Country:', country);
            console.log('  Status:', formData.get('status'));

            const originalText = this.innerHTML;
            this.innerHTML = `<i class="fas fa-spinner fa-spin mr-1.5"></i> ${buttonText}`;
            this.disabled = true;

            // Submit the form
            form.submit();
        });
    }

    // ===== RESET BUTTON =====
    const resetBtn = document.getElementById('resetBtn');
    if (resetBtn) {
        resetBtn.addEventListener('click', function(e) {
            e.preventDefault();
            const form = document.getElementById('vendorCreateForm');
            if (form) form.reset();
            console.log('🔄 Form reset');
        });
    }

    // ===== DELETE VENDOR CONFIRMATION WITH CUSTOM MODAL =====
    let pendingDelete = null;

    window.showDeleteModal = function(entityId, entityName, contextPath) {
        // Auto-detect entity type from the URL
        const isCustomer = window.location.pathname.includes('/customers');
        const entityType = isCustomer ? 'customer' : 'vendor';

        pendingDelete = {
            entityId: entityId,
            entityName: entityName,
            entityType: entityType,
            contextPath: contextPath
        };

        const messageEl = document.getElementById('deleteMessage');
        if (messageEl) {
            messageEl.textContent = `Are you sure you want to delete ${entityType} "${entityName}"? This action cannot be undone.`;
        }

        const modal = document.getElementById('deleteModal');
        if (modal) {
            modal.classList.remove('hidden');
            modal.classList.add('active');
            document.body.style.overflow = 'hidden';
        }
    };

    window.closeDeleteModal = function() {
        const modal = document.getElementById('deleteModal');
        if (modal) {
            modal.classList.add('hidden');
            modal.classList.remove('active');
        }
        pendingDelete = null;
        document.body.style.overflow = '';
    };

    window.confirmDeleteAction = function() {
        console.log('Delete confirmed for:', pendingDelete);
        if (pendingDelete) {
            const form = document.createElement('form');
            form.method = 'POST';
            const type = pendingDelete.entityType || 'vendor';
            form.action = (pendingDelete.contextPath || '') + '/' + type + 's/delete';

            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = type + 'Id';
            input.value = pendingDelete.entityId;
            form.appendChild(input);

            document.body.appendChild(form);
            console.log('Submitting form to:', form.action);
            form.submit();
        }
        closeDeleteModal();
    };

    // Close modal with Escape key
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            closeDeleteModal();
        }
    });

    // Close modal by clicking outside
    document.addEventListener('click', function(e) {
        const modal = document.getElementById('deleteModal');
        if (modal && !modal.classList.contains('hidden')) {
            if (e.target === modal) {
                closeDeleteModal();
            }
        }
    });

    // ===== LEGACY SUPPORT - Keep old function for backward compatibility =====
    window.confirmDelete = function(vendorId, vendorName, contextPath) {
        showDeleteModal(vendorId, vendorName, contextPath);
    };

    // ===== KEYBOARD SHORTCUTS =====
    document.addEventListener('keydown', function(e) {
        if (e.ctrlKey && e.shiftKey && e.key === 'Enter') {
            e.preventDefault();
            if (submitBtn) submitBtn.click();
        }
        if (e.ctrlKey && e.shiftKey && e.key === 'R') {
            e.preventDefault();
            if (resetBtn) resetBtn.click();
        }
    });

    // ===== NOTIFICATION HELPER =====
    function showErrorNotification(message) {
        if (typeof showError === 'function') {
            showError(message);
        } else {
            alert('❌ ' + message);
        }
    }

    console.log('📊 Vendors page loaded');
});