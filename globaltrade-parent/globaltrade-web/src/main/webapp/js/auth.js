// GlobalTrade Logistics - Authentication JavaScript
document.addEventListener('DOMContentLoaded', function() {
    'use strict';

    // ===== PASSWORD TOGGLE =====
    function setupPasswordToggle(toggleBtn, passwordInput) {
        if (!toggleBtn || !passwordInput) return;
        toggleBtn.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);
            const icon = this.querySelector('i');
            icon.classList.toggle('fa-eye');
            icon.classList.toggle('fa-eye-slash');
        });
    }

    document.querySelectorAll('.toggle-password').forEach(btn => {
        const input = btn.closest('.password-wrapper')?.querySelector('input');
        if (input) {
            setupPasswordToggle(btn, input);
        }
    });

    // ===== ROLE SELECTION MODAL =====
    const roleModal = document.getElementById('roleModal');
    const selectRoleBtn = document.getElementById('selectRoleBtn');
    const modalCloseBtn = document.getElementById('modalCloseBtn');
    const roleOptions = document.querySelectorAll('.role-option-btn');
    const selectedRoleDisplay = document.getElementById('selectedRoleDisplay');
    const selectedRoleInput = document.getElementById('selectedRole');
    const submitBtn = document.getElementById('submitBtn');
    const vendorSelection = document.getElementById('vendorSelection');
    const staffVerification = document.getElementById('staffVerification');
    const roleInfoText = document.getElementById('roleInfoText');

    // Role config
    const roleConfig = {
        'CUSTOMER': {
            label: 'Customer',
            icon: 'fa-user',
            vendor: false,
            nic: false,
            approval: false,
            message: 'Customer accounts are created immediately.',
            btnText: 'Create Account'
        },
        'VENDOR_REP': {
            label: 'Vendor Representative',
            icon: 'fa-building',
            vendor: true,
            nic: false,
            approval: false,
            message: 'Vendor Representative accounts are created immediately.',
            btnText: 'Create Account'
        },
        'CUSTOMS_AGENT': {
            label: 'Customs Agent',
            icon: 'fa-passport',
            vendor: false,
            nic: false,
            approval: true,
            message: 'Customs Agent accounts require approval. You will be notified once approved.',
            btnText: 'Request Account'
        },
        'LOGISTICS_COORDINATOR': {
            label: 'Logistics Coordinator',
            icon: 'fa-truck',
            vendor: false,
            nic: true,
            approval: false,
            message: 'Staff role - NIC verification required against employee records.',
            btnText: 'Verify & Create'
        },
        'WAREHOUSE_MANAGER': {
            label: 'Warehouse Manager',
            icon: 'fa-warehouse',
            vendor: false,
            nic: true,
            approval: false,
            message: 'Staff role - NIC verification required against employee records.',
            btnText: 'Verify & Create'
        }
    };

    // Open modal
    function openModal() {
        if (roleModal) {
            roleModal.classList.add('active');
            document.body.style.overflow = 'hidden';
        }
    }

    // Close modal
    function closeModal() {
        if (roleModal) {
            roleModal.classList.remove('active');
            document.body.style.overflow = '';
        }
    }

    // Select role
    function selectRole(role) {
        const config = roleConfig[role];
        if (!config) return;

        selectedRoleDisplay.textContent = config.label;
        selectedRoleInput.value = role;

        if (roleInfoText) {
            roleInfoText.textContent = config.message;
        }

        if (vendorSelection) {
            vendorSelection.style.display = config.vendor ? 'block' : 'none';
        }

        if (staffVerification) {
            staffVerification.style.display = config.nic ? 'block' : 'none';
        }

        if (submitBtn) {
            submitBtn.disabled = false;
            submitBtn.classList.remove('opacity-50', 'cursor-not-allowed');
            submitBtn.innerHTML = `<i class="fas fa-user-plus text-xs mr-1.5"></i> ${config.btnText}`;
        }

        closeModal();
        console.log(`📝 Role selected: ${role}`);
    }

    // Event: Select role button click
    if (selectRoleBtn) {
        selectRoleBtn.addEventListener('click', openModal);
    }

    // Event: Modal close button
    if (modalCloseBtn) {
        modalCloseBtn.addEventListener('click', closeModal);
    }

    // Event: Click outside modal to close
    if (roleModal) {
        roleModal.addEventListener('click', function(e) {
            if (e.target === this) {
                closeModal();
            }
        });
    }

    // Event: Role option buttons
    roleOptions.forEach(btn => {
        btn.addEventListener('click', function() {
            const role = this.dataset.role;
            selectRole(role);
        });
    });

    // =============================================
    // ===== UPDATED FORM VALIDATION =====
    // =============================================
    const registerForm = document.getElementById('registerForm');

    if (registerForm) {
        registerForm.addEventListener('submit', function(e) {
            // Get form values
            const fullName = document.getElementById('fullName')?.value?.trim();
            const username = document.getElementById('username')?.value?.trim();
            const email = document.getElementById('email')?.value?.trim();
            const password = document.getElementById('password')?.value;
            const confirmPassword = document.getElementById('confirmPassword')?.value;
            const role = selectedRoleInput?.value;
            const config = role ? roleConfig[role] : null;
            const nicInput = document.getElementById('nicNumber');
            const vendorSelect = document.getElementById('vendorId');

            // --- ROLE VALIDATION ---
            if (!role) {
                e.preventDefault();
                showErrorNotification('Please select a role before registering.');
                openModal();
                return;
            }

            // --- FULL NAME VALIDATION ---
            if (!fullName) {
                e.preventDefault();
                showErrorNotification('Please enter your full name.');
                document.getElementById('fullName')?.focus();
                return;
            }
            if (fullName.length < 2 || fullName.length > 100) {
                e.preventDefault();
                showErrorNotification('Full name must be 2-100 characters.');
                document.getElementById('fullName')?.focus();
                return;
            }
            // Letters, spaces, hyphens, apostrophes only
            if (!/^[a-zA-Z\s\-']{2,100}$/.test(fullName)) {
                e.preventDefault();
                showErrorNotification('Full name can only contain letters, spaces, hyphens, and apostrophes.');
                document.getElementById('fullName')?.focus();
                return;
            }

            // --- USERNAME VALIDATION ---
            if (!username) {
                e.preventDefault();
                showErrorNotification('Please choose a username.');
                document.getElementById('username')?.focus();
                return;
            }
            if (!/^[a-zA-Z0-9_]{3,20}$/.test(username)) {
                e.preventDefault();
                showErrorNotification('Username must be 3-20 characters (letters, numbers, underscore only).');
                document.getElementById('username')?.focus();
                return;
            }

            // --- EMAIL VALIDATION ---
            if (!email) {
                e.preventDefault();
                showErrorNotification('Please enter your email address.');
                document.getElementById('email')?.focus();
                return;
            }
            if (!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(email)) {
                e.preventDefault();
                showErrorNotification('Please enter a valid email address.');
                document.getElementById('email')?.focus();
                return;
            }

            // --- PASSWORD VALIDATION ---
            if (!password) {
                e.preventDefault();
                showErrorNotification('Please create a password.');
                document.getElementById('password')?.focus();
                return;
            }
            if (!/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/.test(password)) {
                e.preventDefault();
                showErrorNotification('Password must be 8+ chars with uppercase, lowercase, number, and special character.');
                document.getElementById('password')?.focus();
                return;
            }

            // --- CONFIRM PASSWORD VALIDATION ---
            if (!confirmPassword) {
                e.preventDefault();
                showErrorNotification('Please confirm your password.');
                document.getElementById('confirmPassword')?.focus();
                return;
            }
            if (password !== confirmPassword) {
                e.preventDefault();
                showErrorNotification('Passwords do not match.');
                document.getElementById('confirmPassword')?.focus();
                return;
            }

            // --- NIC VALIDATION (Staff roles) ---
            if (config && config.nic) {
                const nic = nicInput?.value?.trim();
                if (!nic) {
                    e.preventDefault();
                    showErrorNotification('NIC number is required for staff registration.');
                    nicInput?.focus();
                    return;
                }
                if (!/^[0-9]{9}[VXvx]$|^[0-9]{12}$/.test(nic)) {
                    e.preventDefault();
                    showErrorNotification('Invalid NIC format. Use 9 digits + V/X or 12 digits.');
                    nicInput?.focus();
                    return;
                }
            }

            // --- VENDOR VALIDATION (VENDOR_REP) ---
            if (config && config.vendor) {
                if (!vendorSelect?.value) {
                    e.preventDefault();
                    showErrorNotification('Please select a vendor.');
                    vendorSelect?.focus();
                    return;
                }
            }

            // --- CUSTOMS AGENT - Show info notification ---
            if (role === 'CUSTOMS_AGENT') {
                showInfoNotification('Your account will require approval before you can login.');
            }

            console.log('✅ Form validation passed');
            // Allow form to submit
        });
    }

    // =============================================
    // ===== NOTIFICATION FUNCTIONS =====
    // =============================================

    // Show error notification using Notyf
    function showErrorNotification(message) {
        if (typeof showError === 'function') {
            showError(message);
        } else {
            alert('❌ ' + message);
        }
        console.error('❌ Validation Error:', message);
    }

    // Show info notification using Notyf
    function showInfoNotification(message) {
        if (typeof showInfo === 'function') {
            showInfo(message);
        } else {
            console.log('ℹ️ ' + message);
        }
    }

    // ===== LEGACY VALIDATION ERROR HELPER =====
    function showValidationError(message) {
        const roleInfo = document.getElementById('roleInfo');
        if (roleInfo) {
            const textSpan = roleInfo.querySelector('span');
            if (textSpan) {
                textSpan.textContent = message;
                textSpan.style.color = '#dc143c';
            }
            setTimeout(() => {
                if (textSpan) {
                    textSpan.style.color = '';
                    textSpan.textContent = roleConfig[selectedRoleInput?.value]?.message || 'Please select a role to continue';
                }
            }, 4000);
        }
        showErrorNotification(message);
    }

    // ===== GLOW PAGE TRANSITION =====
    const TRANSITION_MS = 500;

    function animatePageTransition(destination) {
        const container = document.querySelector('.container');
        if (container) {
            container.style.pointerEvents = 'none';
            container.classList.remove('page-transition-in');
            void container.offsetWidth;
            container.classList.add('page-transition-out');
            setTimeout(() => {
                window.location.href = destination;
            }, TRANSITION_MS);
        } else {
            window.location.href = destination;
        }
    }

    // ===== HANDLE PAGE LOAD =====
    let pageLoadHandled = false;

    function handlePageLoad() {
        if (pageLoadHandled) return;
        pageLoadHandled = true;
        const container = document.querySelector('.container');
        if (container) {
            container.classList.remove('page-transition-out');
            container.style.pointerEvents = '';
            void container.offsetWidth;
            container.classList.add('page-transition-in');
            setTimeout(() => {
                container.style.opacity = '1';
                container.style.transform = 'none';
                container.style.boxShadow = '';
                container.style.borderColor = '';
                container.classList.remove('page-transition-in');
            }, TRANSITION_MS + 100);
        }
    }

    // ===== SETUP NAVIGATION LINKS =====
    document.querySelectorAll('a[href*="login.jsp"], a[href*="register.jsp"], a[href*="signup.jsp"]').forEach(link => {
        link.addEventListener('click', function(e) {
            const href = this.getAttribute('href');
            if (href && (href.includes('.jsp') || href === 'login' || href === 'register' || href === 'signup')) {
                e.preventDefault();
                animatePageTransition(href);
            }
        });
    });

    // ===== HANDLE PAGE LOAD EVENTS =====
    window.addEventListener('pageshow', function(event) {
        setTimeout(handlePageLoad, event.persisted ? 50 : 0);
    });

    // ===== KEYBOARD SHORTCUTS =====
    document.addEventListener('keydown', function(e) {
        if (e.ctrlKey && e.shiftKey && e.key === 'L') {
            e.preventDefault();
            const toggleBtns = document.querySelectorAll('.toggle-password');
            if (toggleBtns.length > 0) {
                toggleBtns[0].click();
            }
        }
        if (e.key === 'Escape' && roleModal && roleModal.classList.contains('active')) {
            closeModal();
        }
        if (e.key === 'Enter' && roleModal && roleModal.classList.contains('active')) {
            const firstOption = document.querySelector('.role-option-btn');
            if (firstOption) {
                firstOption.click();
            }
        }
    });

    // ===== AUTO-FOCUS =====
    const firstInput = document.querySelector('input:not([type="hidden"])');
    if (firstInput) {
        setTimeout(() => firstInput.focus(), 300);
    }

    // ===== CHECK IF ROLE IS ALREADY SELECTED =====
    if (selectedRoleInput && selectedRoleInput.value) {
        const role = selectedRoleInput.value;
        const config = roleConfig[role];
        if (config) {
            selectedRoleDisplay.textContent = config.label;
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.classList.remove('opacity-50', 'cursor-not-allowed');
                submitBtn.innerHTML = `<i class="fas fa-user-plus text-xs mr-1.5"></i> ${config.btnText}`;
            }
            if (roleInfoText) {
                roleInfoText.textContent = config.message;
            }
            if (vendorSelection) {
                vendorSelection.style.display = config.vendor ? 'block' : 'none';
            }
            if (staffVerification) {
                staffVerification.style.display = config.nic ? 'block' : 'none';
            }
        }
    }

    // =============================================
    // ===== NOTIFICATION FALLBACKS =====
    // =============================================
    // Ensure showInfo exists even if utils.js didn't load
    if (typeof showInfo !== 'function') {
        window.showInfo = function(message) {
            console.log('ℹ️ ' + message);
        };
    }
    if (typeof showError !== 'function') {
        window.showError = function(message) {
            console.error('❌ ' + message);
        };
    }
    if (typeof showSuccess !== 'function') {
        window.showSuccess = function(message) {
            console.log('✅ ' + message);
        };
    }

    console.log('🚢 GlobalTrade Logistics - UI Ready');
});