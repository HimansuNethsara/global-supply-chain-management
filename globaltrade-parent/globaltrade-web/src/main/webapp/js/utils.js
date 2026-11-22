// GlobalTrade Logistics Corporation - Utility Functions
// Shared functions used across all pages

// ===== NOTYF INSTANCE =====
let notyfInstance = null;

function initNotyf() {
    if (typeof Notyf !== 'undefined' && !notyfInstance) {
        notyfInstance = new Notyf({
            duration: 3500,
            ripple: true,
            dismissible: true,
            position: { x: 'right', y: 'top' },
            dismiss: {
                enabled: true,
                label: '×',
                className: 'notyf__dismiss'
            }
        });
        console.log('✅ Notyf initialized');
    }
    return notyfInstance;
}

// ===== NOTYF NOTIFICATION =====
function showNotification(message, type = 'success', duration = 3500) {
    const notyf = initNotyf();
    if (!notyf) {
        console.warn('Notyf not loaded, using fallback console log');
        console.log(`[${type}] ${message}`);
        return;
    }

    const colors = {
        success: { background: '#22c55e', icon: 'fa-check-circle' },
        error: { background: '#ef4444', icon: 'fa-exclamation-circle' },
        info: { background: '#3b82f6', icon: 'fa-info-circle' },
        warning: { background: '#f59e0b', icon: 'fa-exclamation-triangle' }
    };

    const config = colors[type] || colors.info;
    const typeMap = { success: 'success', error: 'error', info: 'info', warning: 'warning' };

    // Open the toast
    const toast = notyf.open({
        type: typeMap[type] || 'info',
        message: message,
        duration: duration,
        backgroundColor: config.background,
        icon: {
            className: `fas ${config.icon}`,
            tagName: 'i',
            text: ''
        },
        className: `notyf__toast--${type}`
    });

    // Ensure proper removal after duration
    if (duration > 0) {
        setTimeout(() => {
            removeToastElement(toast);
        }, duration + 100);
    }

    return toast;
}

// ===== TOAST REMOVAL HELPER =====
function removeToastElement(toast) {
    if (!toast) return;

    try {
        // Try to find the DOM element
        let element = toast._element;
        if (!element && toast._root) {
            element = toast._root;
        }

        if (element && element.parentNode) {
            // Add removing class for animation
            element.classList.add('notyf__toast--removing');

            // Remove after animation
            setTimeout(() => {
                if (element.parentNode) {
                    element.parentNode.removeChild(element);
                }
            }, 350);
        }
    } catch (e) {
        // Fallback: try to remove via Notyf
        try {
            if (typeof toast._remove === 'function') {
                toast._remove();
            }
        } catch (err) {
            console.warn('Could not remove toast:', err);
        }
    }
}

// ===== QUICK NOTIFICATION SHORTCUTS =====
function showSuccess(message, duration = 3500) {
    return showNotification(message, 'success', duration);
}

function showError(message, duration = 3500) {
    return showNotification(message, 'error', duration);
}

function showInfo(message, duration = 3500) {
    return showNotification(message, 'info', duration);
}

function showWarning(message, duration = 3500) {
    return showNotification(message, 'warning', duration);
}

// ===== KEYBOARD SHORTCUTS =====
function setupGlobalShortcuts() {
    document.addEventListener('keydown', function(e) {
        if (e.ctrlKey && e.shiftKey && e.key === 'D') {
            e.preventDefault();
            window.location.href = '/dashboard.jsp';
        }
        if (e.ctrlKey && e.shiftKey && e.key === 'H') {
            e.preventDefault();
            showInfo('Help documentation coming soon!');
        }
        if (e.ctrlKey && e.shiftKey && e.key === 'Q') {
            e.preventDefault();
            if (confirm('Sign out?')) {
                showInfo('Signing out...', 1500);
                setTimeout(() => {
                    window.location.href = '/login.jsp';
                }, 1500);
            }
        }
    });
}

// ===== DATE UTILITIES =====
function getTomorrowDate() {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate() + 1).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

function formatDate(date) {
    const d = new Date(date);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

// ===== PAYMENT TYPE TOGGLE =====
function setupPaymentTypeToggle(paymentRadios, effectiveDateGroup, futureDateInfo) {
    if (!paymentRadios || paymentRadios.length === 0) return;
    paymentRadios.forEach(radio => {
        radio.addEventListener('change', function() {
            const isFuture = this.value === 'future';
            if (effectiveDateGroup) {
                effectiveDateGroup.style.display = isFuture ? 'block' : 'none';
            }
            if (futureDateInfo) {
                futureDateInfo.style.display = isFuture ? 'flex' : 'none';
            }
            if (isFuture && effectiveDateGroup) {
                const dateInput = effectiveDateGroup.querySelector('input[type="date"]');
                if (dateInput && !dateInput.value) {
                    dateInput.value = getTomorrowDate();
                }
            }
        });
    });
}

// ===== FORM RESET UTILITY =====
function resetFormWithUI(form, elementsToReset) {
    if (form) form.reset();
    elementsToReset.forEach(el => {
        if (el.element) {
            if (el.property === 'display') {
                el.element.style.display = el.value;
            } else if (el.property === 'textContent') {
                el.element.textContent = el.value;
            } else if (el.property === 'placeholder') {
                el.element.placeholder = el.value;
            } else if (el.property === 'checked') {
                el.element.checked = el.value;
            }
        }
    });
}

// ===== BUTTON LOADING STATE =====
function setButtonLoading(btn, isLoading, loadingText = 'Processing...') {
    if (!btn) return;
    if (isLoading) {
        btn.dataset.originalText = btn.innerHTML;
        btn.innerHTML = `<i class="fas fa-spinner fa-spin mr-1.5"></i> ${loadingText}`;
        btn.disabled = true;
    } else {
        btn.innerHTML = btn.dataset.originalText || btn.innerHTML;
        btn.disabled = false;
    }
}

// ===== CONSOLE LOGGING =====
function logFormData(formData, label) {
    console.log(`📋 ${label}:`);
    for (let [key, value] of formData.entries()) {
        console.log(`  ${key}:`, value || 'N/A');
    }
}

// ===== TOGGLE VISIBILITY =====
function toggleVisibility(element, show) {
    if (!element) return;
    element.style.display = show ? 'block' : 'none';
}

// ===== RADIO BUTTON GROUP HANDLER =====
function setupRadioToggle(radios, onChange) {
    if (!radios || radios.length === 0) return;
    radios.forEach(radio => {
        radio.addEventListener('change', function() {
            if (this.checked && onChange) {
                onChange(this);
            }
        });
    });
}

// ===== DROPDOWN OPTIONS UPDATER =====
function updateSelectOptions(select, options) {
    if (!select) return;
    select.innerHTML = '';
    options.forEach(opt => {
        const option = document.createElement('option');
        option.value = opt.value;
        option.textContent = opt.label;
        select.appendChild(option);
    });
}

// ===== INITIALIZE ALL UTILITIES =====
document.addEventListener('DOMContentLoaded', function() {
    initNotyf();
    setupGlobalShortcuts();
    console.log('🛠️ Dracula Bank - Utilities Loaded');
});