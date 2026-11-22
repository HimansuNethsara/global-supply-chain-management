// Global Trade - Warehouses JavaScript
document.addEventListener('DOMContentLoaded', function() {
    'use strict';

    // ===== APPLY FILTERS =====
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
                const name = row.querySelector('td:nth-child(2) .text-white\\/70')?.textContent?.toLowerCase() || '';
                const location = row.querySelector('td:nth-child(3)')?.textContent?.toLowerCase() || '';
                const country = row.querySelector('td:nth-child(4) span')?.textContent || '';

                let show = true;
                if (searchValue) {
                    const searchable = name + ' ' + location;
                    if (!searchable.includes(searchValue)) show = false;
                }
                if (countryValue && country !== countryValue) show = false;

                row.style.display = show ? '' : 'none';
                if (show) visibleCount++;
            });

            const countDisplay = document.querySelector('.text-white\\/20.text-xs');
            if (countDisplay) {
                countDisplay.textContent = `Showing ${visibleCount} warehouses`;
            }
            console.log(`✅ ${visibleCount} warehouses visible`);
        });
    }

    console.log('🏭 Warehouses page loaded');
});