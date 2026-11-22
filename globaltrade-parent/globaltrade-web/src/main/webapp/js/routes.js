// Global Trade - Routes JavaScript
document.addEventListener('DOMContentLoaded', function() {
    'use strict';

    function getContextPath() {
        const path = window.location.pathname;
        const parts = path.split('/');
        if (parts.length > 1 && parts[0] === '') {
            return parts[1] ? '/' + parts[1] : '';
        }
        return '';
    }

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

    // ===== VIEW ROUTE =====
    const viewRouteBtn = document.getElementById('viewRouteBtn');
    if (viewRouteBtn) {
        viewRouteBtn.addEventListener('click', function() {
            const shipmentSelect = document.getElementById('shipmentSelect');
            const shipmentId = shipmentSelect ? shipmentSelect.value : '';

            if (!shipmentId) {
                if (typeof showWarning === 'function') {
                    showWarning('Please select a shipment first');
                } else {
                    alert('Please select a shipment first');
                }
                return;
            }

            console.log(`📍 Viewing route for shipment: ${shipmentId}`);

            const contextPath = getContextPath();
            window.location.href = contextPath + '/route-plan?shipmentId=' + shipmentId;
        });
    }

    // ===== OPTIMIZE ROUTE - Open Modal =====
    const optimizeRouteBtn = document.getElementById('optimizeRouteBtn');
    if (optimizeRouteBtn) {
        // Remove any existing click listeners and use onclick attribute from HTML
        // The onclick is now handled by the inline onclick attribute
    }

    // ===== SHIPMENT SELECT CHANGE =====
    const shipmentSelect = document.getElementById('shipmentSelect');
    if (shipmentSelect) {
        shipmentSelect.addEventListener('change', function() {
            if (this.value) {
                console.log(`📦 Shipment selected: ${this.value}`);
            }
        });
    }

    // ===== KEYBOARD SHORTCUTS =====
    document.addEventListener('keydown', function(e) {
        // Ctrl+Shift+V - View Route
        if (e.ctrlKey && e.shiftKey && e.key === 'V') {
            e.preventDefault();
            if (viewRouteBtn) viewRouteBtn.click();
        }
        // Ctrl+Shift+O - Open Optimize Modal
        if (e.ctrlKey && e.shiftKey && e.key === 'O') {
            e.preventDefault();
            const optimizeBtn = document.getElementById('optimizeRouteBtn');
            if (optimizeBtn) optimizeBtn.click();
        }
        // Escape - Close modal
        if (e.key === 'Escape') {
            closeOptimizeRouteModal();
        }
    });

    console.log('📍 Routes page loaded');
});

// ============================================================
// ===== GLOBAL FUNCTIONS (accessible from HTML onclick) =====
// ============================================================

// ===== OPEN OPTIMIZE ROUTE MODAL =====
window.openOptimizeRouteModal = function(shipmentId, routePlanId, currentDistance, serviceType) {
    'use strict';
    console.log('🚀 Opening optimize route modal for shipment:', shipmentId);
    console.log('  Route Plan ID:', routePlanId);
    console.log('  Current Distance:', currentDistance);
    console.log('  Service Type:', serviceType);

    // Set form values
    const shipmentInput = document.getElementById('optimizeShipmentId');
    const routePlanInput = document.getElementById('optimizeRoutePlanId');
    const serviceTypeInput = document.getElementById('optimizeServiceType');
    const distanceInput = document.getElementById('optimizeDistanceKm');
    const routeTextarea = document.getElementById('optimizedRoute');
    const estimatedTimeDisplay = document.getElementById('optimizeEstimatedTime');

    if (shipmentInput) shipmentInput.value = shipmentId || '';
    if (routePlanInput) routePlanInput.value = routePlanId || '';
    if (serviceTypeInput) serviceTypeInput.value = serviceType || 'Standard';

    // Set current distance as default
    if (distanceInput) {
        if (currentDistance) {
            distanceInput.value = currentDistance;
        } else {
            distanceInput.value = '';
        }
        // Trigger calculation
        distanceInput.dispatchEvent(new Event('input'));
    }

    // Clear previous route text
    if (routeTextarea) {
        routeTextarea.value = '';
    }

    // Reset estimated time
    if (estimatedTimeDisplay) {
        estimatedTimeDisplay.textContent = '--';
    }

    // Show modal
    const modal = document.getElementById('optimizeRouteModal');
    if (modal) {
        modal.classList.remove('hidden');
        modal.classList.add('active');
        document.body.style.overflow = 'hidden';
        // Focus on the first input
        setTimeout(function() {
            if (routeTextarea) routeTextarea.focus();
        }, 100);
    }
};

// ===== CLOSE OPTIMIZE ROUTE MODAL =====
window.closeOptimizeRouteModal = function() {
    'use strict';
    const modal = document.getElementById('optimizeRouteModal');
    if (modal) {
        modal.classList.add('hidden');
        modal.classList.remove('active');
        document.body.style.overflow = '';
    }
};

// ===== AUTO-CALCULATE ESTIMATED TIME ON DISTANCE CHANGE =====
document.addEventListener('DOMContentLoaded', function() {
    'use strict';

    const distanceInput = document.getElementById('optimizeDistanceKm');
    const estimatedTimeDisplay = document.getElementById('optimizeEstimatedTime');
    const serviceTypeInput = document.getElementById('optimizeServiceType');

    if (distanceInput && estimatedTimeDisplay) {
        distanceInput.addEventListener('input', function() {
            const distance = parseFloat(this.value);
            if (!distance || distance <= 0) {
                estimatedTimeDisplay.textContent = '--';
                return;
            }

            // Get service type from hidden input
            const serviceType = serviceTypeInput ? serviceTypeInput.value : 'Standard';

            // Speed mapping
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

            let speed = 50; // Default
            if (serviceType && serviceSpeeds[serviceType]) {
                speed = serviceSpeeds[serviceType];
            }

            const hours = distance / speed;
            estimatedTimeDisplay.textContent = hours.toFixed(2);
        });
    }

    // ===== CLOSE MODAL ON OUTSIDE CLICK =====
    document.addEventListener('click', function(e) {
        const modal = document.getElementById('optimizeRouteModal');
        if (modal && !modal.classList.contains('hidden') && modal.classList.contains('active')) {
            if (e.target === modal) {
                closeOptimizeRouteModal();
            }
        }
    });

    // ===== OPTIMIZE ROUTE FORM SUBMIT VALIDATION =====
    const optimizeForm = document.getElementById('optimizeRouteForm');
    if (optimizeForm) {
        optimizeForm.addEventListener('submit', function(e) {
            const optimizedRoute = document.getElementById('optimizedRoute');
            const distance = document.getElementById('optimizeDistanceKm');

            if (!optimizedRoute || !optimizedRoute.value.trim()) {
                e.preventDefault();
                if (typeof showError === 'function') {
                    showError('Please enter the optimized route description');
                } else {
                    alert('Please enter the optimized route description');
                }
                optimizedRoute.focus();
                return;
            }

            if (!distance || !distance.value || parseFloat(distance.value) <= 0) {
                e.preventDefault();
                if (typeof showError === 'function') {
                    showError('Please enter a valid distance (km)');
                } else {
                    alert('Please enter a valid distance (km)');
                }
                distance.focus();
                return;
            }

            // Show loading state
            const submitBtn = document.getElementById('optimizeRouteSubmitBtn');
            if (submitBtn) {
                submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin mr-1.5"></i> Optimizing...';
                submitBtn.disabled = true;
            }

            console.log('📦 Optimizing route...');
            // Form will submit normally
        });
    }
});