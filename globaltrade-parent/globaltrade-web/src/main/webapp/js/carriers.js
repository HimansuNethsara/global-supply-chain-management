// Global Trade - Carriers JavaScript
document.addEventListener('DOMContentLoaded', function() {
    'use strict';

    // ===== APPLY FILTERS =====
    const applyFiltersBtn = document.getElementById('applyFiltersBtn');
    if (applyFiltersBtn) {
        applyFiltersBtn.addEventListener('click', function() {
            const searchInput = document.getElementById('searchInput');
            const searchValue = searchInput ? searchInput.value.toLowerCase().trim() : '';

            const rows = document.querySelectorAll('tbody tr:not(.no-results)');
            let visibleCount = 0;

            rows.forEach(row => {
                const name = row.querySelector('td:nth-child(2) .text-white\\/70')?.textContent?.toLowerCase() || '';
                const serviceType = row.querySelector('td:nth-child(3) span')?.textContent?.toLowerCase() || '';

                let show = true;
                if (searchValue) {
                    const searchable = name + ' ' + serviceType;
                    if (!searchable.includes(searchValue)) show = false;
                }

                row.style.display = show ? '' : 'none';
                if (show) visibleCount++;
            });

            const countDisplay = document.querySelector('.text-white\\/20.text-xs');
            if (countDisplay) {
                countDisplay.textContent = `Showing ${visibleCount} carriers`;
            }
            console.log(`✅ ${visibleCount} carriers visible`);
        });
    }

    console.log('🚢 Carriers page loaded');
});