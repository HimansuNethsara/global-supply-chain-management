// Global Trade - Customers JavaScript
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

    // ===== CUSTOMERS LIST - Apply Filters =====
    const applyFiltersBtn = document.getElementById('applyFiltersBtn');
    if (applyFiltersBtn) {
        applyFiltersBtn.addEventListener('click', function() {
            const searchInput = document.getElementById('searchInput');
            const countryFilter = document.getElementById('countryFilter');

            const searchValue = searchInput ? searchInput.value.toLowerCase().trim() : '';
            const countryValue = countryFilter ? countryFilter.value : '';

            const rows = document.querySelectorAll('tbody tr:not(.no-results)');
            let visibleCount = 0;

            rows.forEach(row => {
                const customerName = row.querySelector('td:nth-child(2) .text-white\\/70')?.textContent?.toLowerCase() || '';
                const customerCompany = row.querySelector('td:nth-child(3)')?.textContent?.toLowerCase() || '';
                const customerCountry = row.querySelector('td:nth-child(5) span')?.textContent || '';

                let show = true;

                if (searchValue) {
                    const searchable = customerName + ' ' + customerCompany;
                    if (!searchable.includes(searchValue)) {
                        show = false;
                    }
                }

                if (countryValue && customerCountry !== countryValue) {
                    show = false;
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
                    const colspan = document.querySelector('thead tr')?.children?.length || 7;
                    noResultsRow.innerHTML = `
                        <td colspan="${colspan}" class="text-center text-white/20 py-8">
                            <div class="flex flex-col items-center gap-2">
                                <i class="fas fa-search text-3xl text-white/10"></i>
                                <span class="text-sm">No customers match your filters</span>
                                <button class="text-dracula-red text-xs hover:text-red-400 transition-all mt-2" onclick="clearCustomerFilters()">
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
                countDisplay.textContent = `Showing ${visibleCount} customers`;
            }

            console.log(`✅ Customer filters applied: ${visibleCount} customers visible`);
        });
    }

    // ===== CLEAR CUSTOMER FILTERS =====
    window.clearCustomerFilters = function() {
        document.getElementById('searchInput').value = '';
        document.getElementById('countryFilter').value = '';
        document.getElementById('applyFiltersBtn').click();
    };

    // ===== DELETE CONFIRMATION WITH CUSTOM MODAL =====
    let pendingDelete = null;

    window.showDeleteModal = function(entityId, entityName, contextPath) {
        pendingDelete = { entityId, entityName, contextPath };
        const messageEl = document.getElementById('deleteMessage');
        if (messageEl) {
            messageEl.textContent = `Are you sure you want to delete customer "${entityName}"? This action cannot be undone.`;
        }
        const modal = document.getElementById('deleteModal');
        if (modal) {
            modal.classList.add('active');
        }
        // Prevent body scroll
        document.body.style.overflow = 'hidden';
    };

    window.closeDeleteModal = function() {
        const modal = document.getElementById('deleteModal');
        if (modal) {
            modal.classList.remove('active');
        }
        pendingDelete = null;
        // Restore body scroll
        document.body.style.overflow = '';
    };

    window.confirmDeleteAction = function() {
        console.log('Delete confirmed for:', pendingDelete);
        if (pendingDelete) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = (pendingDelete.contextPath || '') + '/customers/delete';

            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'customerId';
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

    // Close modal by clicking outside (on the backdrop)
    document.addEventListener('click', function(e) {
        const modal = document.getElementById('deleteModal');
        if (modal && modal.classList.contains('active')) {
            if (e.target === modal) {
                closeDeleteModal();
            }
        }
    });

    console.log('👥 Customers page loaded');
});