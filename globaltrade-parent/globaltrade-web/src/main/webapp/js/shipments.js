// Global Trade - Shipments JavaScript
document.addEventListener('DOMContentLoaded', function() {
    'use strict';

    // ===== SERVICE TYPE SPEEDS (km/h) =====
    const serviceSpeeds = {
        'Air Freight': 800,
        'Air': 800,
        'Cold Chain Logistics': 50,
        'Cold Chain': 50,
        'Customs Brokerage': 50,
        'Express': 100,
        'Freight Forwarding': 40,
        'Ground Shipping': 60,
        'Ground': 60,
        'Intermodal Transport': 45,
        'Intermodal': 45,
        'Sea Freight': 30,
        'Sea': 30,
        'Standard': 50
    };

    // ===== GET SERVICE TYPE FROM CARRIER SELECTION =====
    function getServiceTypeFromCarrier() {
        const carrierSelect = document.getElementById('carrierId');
        if (!carrierSelect || !carrierSelect.value) return 'Standard';

        const selectedOption = carrierSelect.options[carrierSelect.selectedIndex];
        const serviceType = selectedOption?.dataset?.serviceType || 'Standard';
        return serviceType;
    }

    // ===== CALCULATE ESTIMATED TIME =====
    function calculateEstimatedTime() {
        const distanceInput = document.getElementById('distanceKm');
        const estimatedTimeDisplay = document.getElementById('estimatedTimeValue');

        if (!distanceInput || !estimatedTimeDisplay) return;

        const distance = parseFloat(distanceInput.value);
        const serviceType = getServiceTypeFromCarrier();

        if (!distance || distance <= 0) {
            estimatedTimeDisplay.textContent = '--';
            return;
        }

        let speed = 50; // Default speed
        if (serviceType && serviceSpeeds[serviceType]) {
            speed = serviceSpeeds[serviceType];
        }

        const hours = distance / speed;
        estimatedTimeDisplay.textContent = hours.toFixed(2);
    }

    // ===== APPLY FILTERS =====
    const applyFiltersBtn = document.getElementById('applyFiltersBtn');
    if (applyFiltersBtn) {
        applyFiltersBtn.addEventListener('click', function() {
            const searchInput = document.getElementById('searchInput');
            const statusFilter = document.getElementById('statusFilter');
            const vendorFilter = document.getElementById('vendorFilter');

            const searchValue = searchInput ? searchInput.value.toLowerCase().trim() : '';
            const statusValue = statusFilter ? statusFilter.value : '';
            const vendorValue = vendorFilter ? vendorFilter.value : '';

            const rows = document.querySelectorAll('tbody tr:not(.no-results)');
            let visibleCount = 0;

            rows.forEach(row => {
                const tracking = row.querySelector('td:nth-child(2)')?.textContent?.toLowerCase() || '';
                // Fix: Get status from the status cell (column 8) using the span text
                const statusCell = row.querySelector('td:nth-child(8) .transaction-status');
                let statusText = statusCell?.textContent?.trim() || '';
                    // Convert display text to enum value
                    const statusMap = {
                        'Delivered': 'DELIVERED',
                        'In Transit': 'IN_TRANSIT',
                        'Pending': 'PENDING',
                        'Delayed': 'DELAYED',
                        'Cancelled': 'CANCELLED'
                    };
                const status = statusMap[statusText] || statusText.toUpperCase().replace(' ', '_');
                const vendorName = row.querySelector('td:nth-child(6)')?.textContent?.trim() || '';

                let show = true;
                if (searchValue && !tracking.includes(searchValue)) show = false;
                if (statusValue && status !== statusValue) show = false;
                if (vendorValue) {
                    // Get vendor name from the option text
                    const vendorSelect = document.getElementById('vendorFilter');
                    const selectedOption = vendorSelect?.options[vendorSelect.selectedIndex];
                    const selectedText = selectedOption?.text || '';
                    // Compare the vendor name from the row with the selected vendor name
                    if (vendorName !== selectedText && vendorValue !== String(vendorName)) {
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
                    const colspan = document.querySelector('thead tr')?.children?.length || 10;
                    noResultsRow.innerHTML = `
                    <td colspan="${colspan}" class="text-center text-white/20 py-8">
                        <div class="flex flex-col items-center gap-2">
                            <i class="fas fa-search text-3xl text-white/10"></i>
                            <span class="text-sm">No shipments match your filters</span>
                            <button class="text-dracula-red text-xs hover:text-red-400 transition-all mt-2" onclick="clearShipmentFilters()">
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
                countDisplay.textContent = `Showing ${visibleCount} shipments`;
            }
            console.log(`✅ ${visibleCount} shipments visible`);
        });
    }

    // ===== CLEAR SHIPMENT FILTERS =====
    window.clearShipmentFilters = function() {
        document.getElementById('searchInput').value = '';
        document.getElementById('statusFilter').value = '';
        document.getElementById('vendorFilter').value = '';
        document.getElementById('applyFiltersBtn').click();
    };

    // ===== TRACKING NUMBER GENERATION =====
    function updateTrackingNumber() {
        console.log('🔄 Generating tracking number...');

        const warehouseSelect = document.getElementById('originWarehouseId');
        const vendorSelect = document.getElementById('vendorId');
        const carrierSelect = document.getElementById('carrierId');
        const trackingInput = document.getElementById('trackingNumber');

        // Get selected values
        const warehouseId = warehouseSelect ? warehouseSelect.value : '';
        const vendorId = vendorSelect ? vendorSelect.value : '';
        const carrierId = carrierSelect ? carrierSelect.value : '';

        // Get selected text values
        const warehouseText = warehouseSelect ? warehouseSelect.options[warehouseSelect.selectedIndex]?.text || '' : '';
        const vendorText = vendorSelect ? vendorSelect.options[vendorSelect.selectedIndex]?.text || '' : '';
        const carrierText = carrierSelect ? carrierSelect.options[carrierSelect.selectedIndex]?.text || '' : '';

        console.log('  Warehouse:', warehouseText, 'ID:', warehouseId);
        console.log('  Vendor:', vendorText, 'ID:', vendorId);
        console.log('  Carrier:', carrierText, 'ID:', carrierId);

        // Extract codes (first 3 letters of first word)
        function getCode(text) {
            if (!text || text === 'Select Warehouse' || text === 'Select Vendor' ||
                text === 'Select Carrier' || text === 'Select Service Type' ||
                text.includes('disabled selected')) {
                return '';
            }
            // Remove extra info like " - Location" or "(Service Type)"
            const cleanText = text.replace(/\s*[-–].*$/, '').replace(/\s*\(.*\)$/, '').trim();
            // Get first 3 chars, uppercase
            let code = cleanText.substring(0, 3).toUpperCase();
            // Replace non-alphanumeric with X
            code = code.replace(/[^A-Z0-9]/g, 'X');
            // Ensure exactly 3 characters
            while (code.length < 3) {
                code += 'X';
            }
            return code.substring(0, 3);
        }

        const warehouseCode = getCode(warehouseText);
        const vendorCode = getCode(vendorText);
        let carrierCode = getCode(carrierText);

        console.log('  Codes - WH:', warehouseCode, 'VEN:', vendorCode, 'CAR:', carrierCode);

        // If carrier has service type in parentheses, use that
        if (carrierText && carrierText.includes('(')) {
            const match = carrierText.match(/\(([^)]+)\)/);
            if (match) {
                const serviceCode = match[1].substring(0, 3).toUpperCase().replace(/[^A-Z0-9]/g, 'X');
                while (serviceCode.length < 3) {
                    // pad with X
                }
                carrierCode = serviceCode.substring(0, 3);
                console.log('  Using service type code:', carrierCode);
            }
        }

        // If carrier code is empty or invalid, use 'NA'
        if (!carrierCode || carrierCode.length !== 3 ||
            carrierCode === 'SEL' || carrierCode === 'CAR' || carrierCode === 'XXX') {
            carrierCode = 'NA';
        }

        console.log('  Final codes - WH:', warehouseCode, 'VEN:', vendorCode, 'CAR:', carrierCode);

        // Generate timestamp (YYMMDDHHmmss)
        const now = new Date();
        const timestamp =
            String(now.getFullYear()).slice(2) +
            String(now.getMonth() + 1).padStart(2, '0') +
            String(now.getDate()).padStart(2, '0') +
            String(now.getHours()).padStart(2, '0') +
            String(now.getMinutes()).padStart(2, '0') +
            String(now.getSeconds()).padStart(2, '0');

        console.log('  Timestamp:', timestamp);

        // Generate tracking number - ONLY if warehouse, vendor AND carrier are selected
        let trackingNumber = '';
        if (warehouseCode && vendorCode && carrierCode && carrierCode !== 'NA') {
            trackingNumber = `${warehouseCode}-${vendorCode}-${carrierCode}-${timestamp}`;
        } else if (warehouseCode && vendorCode) {
            // Show placeholder that carrier is needed
            trackingNumber = '';
            trackingInput.placeholder = 'Select Carrier to generate tracking number';
        } else {
            trackingNumber = '';
            trackingInput.placeholder = 'Select Warehouse and Vendor first';
        }

        console.log('  Generated tracking number:', trackingNumber || '(not generated - missing selections)');

        if (trackingInput) {
            trackingInput.value = trackingNumber;
            // Update placeholder based on missing selections
            if (!trackingNumber) {
                if (!warehouseCode || !vendorCode) {
                    trackingInput.placeholder = 'Select Warehouse and Vendor first';
                } else if (!carrierCode || carrierCode === 'NA') {
                    trackingInput.placeholder = 'Select Carrier to generate tracking number';
                }
            } else {
                trackingInput.placeholder = ''; // Clear placeholder when tracking number is set
            }
        }

        return trackingNumber;
    }

    // ===== REGENERATE TRACKING NUMBER BUTTON =====
    const regenerateBtn = document.getElementById('regenerateTrackingBtn');
    if (regenerateBtn) {
        regenerateBtn.addEventListener('click', function() {
            const trackingNumber = updateTrackingNumber();
            if (trackingNumber && !trackingNumber.startsWith('TRK-') && !trackingNumber.includes('NA')) {
                if (typeof showSuccess === 'function') {
                    showSuccess('Tracking number regenerated');
                }
                console.log('🔄 Tracking number regenerated:', trackingNumber);
            } else {
                const warehouseSelect = document.getElementById('originWarehouseId');
                const vendorSelect = document.getElementById('vendorId');
                const carrierSelect = document.getElementById('carrierId');

                if (!warehouseSelect || !warehouseSelect.value) {
                    if (typeof showError === 'function') {
                        showError('Please select a Warehouse first');
                    }
                } else if (!vendorSelect || !vendorSelect.value) {
                    if (typeof showError === 'function') {
                        showError('Please select a Vendor first');
                    }
                } else if (!carrierSelect || !carrierSelect.value) {
                    if (typeof showError === 'function') {
                        showError('Please select a Carrier to generate tracking number');
                    }
                } else {
                    if (typeof showError === 'function') {
                        showError('Cannot generate tracking number. Please check your selections.');
                    }
                }
                console.log('⚠️ Cannot generate tracking number - missing selections');
            }
        });
    }

    // ===== AUTO-GENERATE TRACKING NUMBER ON SELECTION CHANGE =====
    const selects = ['originWarehouseId', 'vendorId', 'carrierId'];
    selects.forEach(id => {
        const element = document.getElementById(id);
        if (element) {
            element.addEventListener('change', function() {
                const warehouseSelect = document.getElementById('originWarehouseId');
                const vendorSelect = document.getElementById('vendorId');
                const carrierSelect = document.getElementById('carrierId');
                // Only generate if warehouse, vendor AND carrier are selected
                if (warehouseSelect && warehouseSelect.value &&
                    vendorSelect && vendorSelect.value &&
                    carrierSelect && carrierSelect.value) {
                    updateTrackingNumber();
                } else {
                    // If missing selections, show placeholder
                    const trackingInput = document.getElementById('trackingNumber');
                    if (trackingInput) {
                        trackingInput.value = '';
                        if (!warehouseSelect || !warehouseSelect.value) {
                            trackingInput.placeholder = 'Select Warehouse first';
                        } else if (!vendorSelect || !vendorSelect.value) {
                            trackingInput.placeholder = 'Select Vendor first';
                        } else if (!carrierSelect || !carrierSelect.value) {
                            trackingInput.placeholder = 'Select Carrier to generate tracking number';
                        }
                    }
                }
            });
        }
    });

    // ===== INITIAL GENERATION =====
    setTimeout(function() {
        const warehouseSelect = document.getElementById('originWarehouseId');
        const vendorSelect = document.getElementById('vendorId');
        const carrierSelect = document.getElementById('carrierId');

        console.log('🔄 Initial tracking number generation check...');
        console.log('  Warehouse value:', warehouseSelect ? warehouseSelect.value : 'null');
        console.log('  Vendor value:', vendorSelect ? vendorSelect.value : 'null');
        console.log('  Carrier value:', carrierSelect ? carrierSelect.value : 'null');

        if (warehouseSelect && warehouseSelect.value &&
            vendorSelect && vendorSelect.value &&
            carrierSelect && carrierSelect.value) {
            updateTrackingNumber();
        } else {
            // Set placeholder message
            const trackingInput = document.getElementById('trackingNumber');
            if (trackingInput) {
                trackingInput.value = '';
                if (!warehouseSelect || !warehouseSelect.value) {
                    trackingInput.placeholder = 'Select Warehouse first';
                } else if (!vendorSelect || !vendorSelect.value) {
                    trackingInput.placeholder = 'Select Vendor first';
                } else if (!carrierSelect || !carrierSelect.value) {
                    trackingInput.placeholder = 'Select Carrier to generate tracking number';
                }
            }
        }
    }, 500);

    // ===== UPDATE ESTIMATED DELIVERY DATE =====
    function updateEstimatedDeliveryDate() {
        const scheduledDateInput = document.getElementById('scheduledDate');
        const distanceInput = document.getElementById('distanceKm');
        const estimatedDeliveryInput = document.getElementById('estimatedDeliveryDate');

        if (!scheduledDateInput || !distanceInput || !estimatedDeliveryInput) return;

        const scheduledDate = scheduledDateInput.value;
        const distance = parseFloat(distanceInput.value);

        // Debug logging
        console.log('🔄 Updating estimated delivery date...');
        console.log('  Scheduled date:', scheduledDate);
        console.log('  Distance:', distance);

        if (!scheduledDate || !distance || distance <= 0) {
            console.log('  Missing date or distance, skipping calculation');
            return;
        }

        const serviceType = getServiceTypeFromCarrier();
        console.log('  Service type:', serviceType);

        let speed = 50; // Default speed
        if (serviceType && serviceSpeeds[serviceType]) {
            speed = serviceSpeeds[serviceType];
        }
        console.log('  Speed:', speed, 'km/h');

        const hours = distance / speed;
        const days = hours / 24;
        console.log('  Estimated hours:', hours, 'days:', days);

        const date = new Date(scheduledDate);
        date.setDate(date.getDate() + Math.ceil(days));

        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hoursStr = String(date.getHours()).padStart(2, '0');
        const minutesStr = String(date.getMinutes()).padStart(2, '0');

        const estimatedDate = `${year}-${month}-${day}T${hoursStr}:${minutesStr}`;
        console.log('  Estimated delivery date:', estimatedDate);
        estimatedDeliveryInput.value = estimatedDate;
    }

    // ===== ADD EVENT LISTENERS FOR AUTO-CALCULATE =====
    const scheduledDate = document.getElementById('scheduledDate');
    const distanceKm = document.getElementById('distanceKm');
    const carrierSelect = document.getElementById('carrierId');

    if (distanceKm) {
        distanceKm.addEventListener('input', function() {
            calculateEstimatedTime();
            updateEstimatedDeliveryDate();
        });
        distanceKm.addEventListener('change', function() {
            calculateEstimatedTime();
            updateEstimatedDeliveryDate();
        });
    }

    if (carrierSelect) {
        carrierSelect.addEventListener('change', function() {
            calculateEstimatedTime();
            updateEstimatedDeliveryDate();
            // Also update tracking number when carrier changes
            const warehouseSelect = document.getElementById('originWarehouseId');
            const vendorSelect = document.getElementById('vendorId');
            if (warehouseSelect && warehouseSelect.value && vendorSelect && vendorSelect.value) {
                updateTrackingNumber();
            }
        });
    }

    if (scheduledDate) {
        scheduledDate.addEventListener('change', updateEstimatedDeliveryDate);
        scheduledDate.addEventListener('input', updateEstimatedDeliveryDate);
    }

    // ===== INITIAL AUTO-CALCULATION =====
    setTimeout(function() {
        if (document.getElementById('scheduledDate')?.value &&
            document.getElementById('distanceKm')?.value) {
            calculateEstimatedTime();
            updateEstimatedDeliveryDate();
        }
        // Check for pre-selected carrier and update tracking if needed
        const warehouseSelect = document.getElementById('originWarehouseId');
        const vendorSelect = document.getElementById('vendorId');
        const carrierSelect = document.getElementById('carrierId');
        if (warehouseSelect && warehouseSelect.value &&
            vendorSelect && vendorSelect.value &&
            carrierSelect && carrierSelect.value) {
            updateTrackingNumber();
        }
    }, 500);

    // ===== SHIPMENT CREATE - ACTUAL SUBMIT =====
    const submitBtn = document.getElementById('submitBtn');
    if (submitBtn && document.getElementById('shipmentCreateForm')) {
        submitBtn.addEventListener('click', function(e) {
            e.preventDefault();
            const form = document.getElementById('shipmentCreateForm');
            const formData = new FormData(form);

            // Validation
            const trackingNumber = formData.get('trackingNumber')?.trim();
            const originWarehouseId = formData.get('originWarehouseId');
            const destination = formData.get('destination')?.trim();
            const vendorId = formData.get('vendorId');
            const carrierId = formData.get('carrierId');
            const scheduledDate = formData.get('scheduledDate');
            const plannedRoute = formData.get('plannedRoute')?.trim();
            const distanceKm = formData.get('distanceKm');

            // Check if tracking number contains 'NA' (invalid)
            if (trackingNumber && trackingNumber.includes('NA')) {
                showErrorNotification('Please select a Carrier to generate a valid tracking number');
                document.getElementById('carrierId')?.focus();
                return;
            }

            if (!trackingNumber) {
                showErrorNotification('Please select Warehouse, Vendor, and Carrier to generate a tracking number');
                document.getElementById('originWarehouseId')?.focus();
                return;
            }
            if (!originWarehouseId) {
                showErrorNotification('Please select an origin warehouse');
                document.getElementById('originWarehouseId')?.focus();
                return;
            }
            if (!destination) {
                showErrorNotification('Destination is required');
                document.getElementById('destination')?.focus();
                return;
            }
            if (!vendorId) {
                showErrorNotification('Please select a vendor');
                document.getElementById('vendorId')?.focus();
                return;
            }
            if (!carrierId) {
                showErrorNotification('Please select a carrier');
                document.getElementById('carrierId')?.focus();
                return;
            }
            if (!scheduledDate) {
                showErrorNotification('Scheduled date is required');
                document.getElementById('scheduledDate')?.focus();
                return;
            }

            // If planned route is provided, validate distance
            if (plannedRoute) {
                if (!distanceKm || parseFloat(distanceKm) <= 0) {
                    showErrorNotification('Please enter a valid distance (km) for the planned route');
                    document.getElementById('distanceKm')?.focus();
                    return;
                }
            }

            console.log('📦 Creating shipment...');
            console.log('  Tracking:', trackingNumber);
            console.log('  Origin:', originWarehouseId);
            console.log('  Destination:', destination);
            console.log('  Vendor:', vendorId);
            console.log('  Carrier:', carrierId);
            console.log('  Status:', formData.get('status'));
            console.log('  Scheduled:', scheduledDate);
            console.log('  Estimated:', formData.get('estimatedDeliveryDate') || 'N/A');
            console.log('  Planned Route:', plannedRoute || 'N/A');
            console.log('  Distance:', distanceKm || 'N/A');

            form.submit();
        });
    }

    // ===== RESET =====
    const resetBtn = document.getElementById('resetBtn');
    if (resetBtn) {
        resetBtn.addEventListener('click', function(e) {
            e.preventDefault();
            const form = document.getElementById('shipmentCreateForm');
            if (form) form.reset();
            // Clear tracking number
            const trackingInput = document.getElementById('trackingNumber');
            if (trackingInput) {
                trackingInput.value = '';
                trackingInput.placeholder = 'Select Warehouse, Vendor, and Carrier';
            }
            // Reset estimated time display
            const estimatedTimeDisplay = document.getElementById('estimatedTimeValue');
            if (estimatedTimeDisplay) {
                estimatedTimeDisplay.textContent = '--';
            }
            console.log('🔄 Form reset');
        });
    }

    // ===== STATUS UPDATE MODAL FUNCTIONS =====
    let pendingStatusUpdate = null;

    window.showStatusModal = function(shipmentId, trackingNumber, currentStatus) {
        pendingStatusUpdate = { shipmentId, trackingNumber, currentStatus };

        // Set shipment ID in hidden field
        document.getElementById('statusShipmentId').value = shipmentId;

        // Update current status display
        const badge = document.getElementById('currentStatusBadge');
        if (badge) {
            let statusText = currentStatus || 'Unknown';
            let statusClass = 'pending';
            if (statusText === 'DELIVERED') statusClass = 'completed';
            else if (statusText === 'DELAYED' || statusText === 'CANCELLED') statusClass = 'failed';

            // Format status text for display
            const displayText = statusText.replace('_', ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase());
            badge.textContent = displayText;
            badge.className = `status-badge ${statusClass}`;
        }

        // Reset new status select
        document.getElementById('newStatus').value = '';

        // Show modal
        const modal = document.getElementById('statusUpdateModal');
        if (modal) {
            modal.classList.add('active');
            document.body.style.overflow = 'hidden';
        }
    };

    window.closeStatusModal = function() {
        const modal = document.getElementById('statusUpdateModal');
        if (modal) {
            modal.classList.remove('active');
            document.body.style.overflow = '';
        }
        pendingStatusUpdate = null;
    };

    // ===== STATUS UPDATE FORM SUBMIT =====
    const statusForm = document.getElementById('statusUpdateForm');
    if (statusForm) {
        statusForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const formData = new FormData(this);
            const newStatus = formData.get('newStatus');

            if (!newStatus) {
                if (typeof showError === 'function') {
                    showError('Please select a new status');
                } else {
                    alert('Please select a new status');
                }
                document.getElementById('newStatus')?.focus();
                return;
            }

            const shipmentId = formData.get('shipmentId');

            console.log('🔄 Updating shipment status...');
            console.log('  Shipment ID:', shipmentId);
            console.log('  New Status:', newStatus);

            // Disable button and show loading
            const btn = document.getElementById('statusUpdateBtn');
            const originalText = btn.innerHTML;
            btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Updating...';
            btn.disabled = true;

            // Submit the form
            this.submit();
        });
    }

    // ===== CLOSE STATUS MODAL WITH ESCAPE KEY =====
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            closeStatusModal();
        }
    });

    // ===== CLOSE STATUS MODAL BY CLICKING OUTSIDE =====
    document.addEventListener('click', function(e) {
        const modal = document.getElementById('statusUpdateModal');
        if (modal && modal.classList.contains('active')) {
            if (e.target === modal || e.target.classList.contains('status-modal-overlay')) {
                closeStatusModal();
            }
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

    console.log('🚢 Shipments page loaded');
});