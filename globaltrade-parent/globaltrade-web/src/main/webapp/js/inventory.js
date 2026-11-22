// Global Trade - Inventory JavaScript
document.addEventListener('DOMContentLoaded', function() {
    'use strict';

    // ===== APPLY FILTERS =====
    const applyFiltersBtn = document.getElementById('applyFiltersBtn');
    if (applyFiltersBtn) {
        applyFiltersBtn.addEventListener('click', function() {
            const searchInput = document.getElementById('searchInput');
            const warehouseFilter = document.getElementById('warehouseFilter');
            const statusFilter = document.getElementById('statusFilter');

            const searchValue = searchInput ? searchInput.value.toLowerCase().trim() : '';
            const warehouseValue = warehouseFilter ? warehouseFilter.value : '';
            const statusValue = statusFilter ? statusFilter.value : '';

            const rows = document.querySelectorAll('tbody tr:not(.no-results)');
            let visibleCount = 0;

            rows.forEach(row => {
                const name = row.querySelector('td:nth-child(3) .text-white\\/70')?.textContent?.toLowerCase() || '';
                const sku = row.querySelector('td:nth-child(2)')?.textContent?.toLowerCase() || '';
                // Get warehouse name and ID from the warehouse column
                const warehouseCell = row.querySelector('td:nth-child(4)');
                const warehouseName = warehouseCell?.textContent?.trim() || '';
                const warehouseId = warehouseCell?.dataset?.warehouseId || '';
                const quantityText = row.querySelector('td:nth-child(5)')?.textContent || '0';
                const quantity = parseInt(quantityText.match(/\d+/)?.[0] || '0');
                const reorderText = row.querySelector('td:nth-child(6)')?.textContent || '0';
                const reorderLevel = parseInt(reorderText || '0');

                let show = true;

                // Search filter
                if (searchValue) {
                    const searchable = name + ' ' + sku;
                    if (!searchable.includes(searchValue)) show = false;
                }

                // Warehouse filter - compare by ID first, then by name
                if (warehouseValue && show) {
                    // Check if warehouseValue is a number (ID)
                    const isNumeric = /^\d+$/.test(warehouseValue);
                    if (isNumeric) {
                        // Compare by ID
                        if (warehouseId !== warehouseValue) {
                            show = false;
                        }
                    } else {
                        // Compare by name
                        if (warehouseName !== warehouseValue) {
                            show = false;
                        }
                    }
                }

                // Status filter
                if (statusValue && show) {
                    if (statusValue === 'low' && quantity > reorderLevel) show = false;
                    if (statusValue === 'normal' && quantity <= reorderLevel) show = false;
                }

                row.style.display = show ? '' : 'none';
                if (show) visibleCount++;
            });

            const countDisplay = document.querySelector('.text-white\\/20.text-xs');
            if (countDisplay) {
                countDisplay.textContent = `Showing ${visibleCount} items`;
            }
            console.log(`✅ ${visibleCount} items visible`);
        });
    }

    // ===== INVENTORY CREATE - ACTUAL SUBMIT =====
    const submitBtn = document.getElementById('submitBtn');
    if (submitBtn) {
        submitBtn.addEventListener('click', function(e) {
            e.preventDefault();
            const form = document.getElementById('inventoryCreateForm');
            const formData = new FormData(form);

            const name = formData.get('name')?.trim();
            const sku = formData.get('sku')?.trim();
            const warehouseId = formData.get('warehouseId');
            const quantity = parseInt(formData.get('quantity'));
            const unitPrice = parseFloat(formData.get('unitPrice'));

            if (!name) {
                showErrorNotification('Item name is required');
                document.getElementById('name')?.focus();
                return;
            }
            if (!sku) {
                showErrorNotification('SKU is required');
                document.getElementById('sku')?.focus();
                return;
            }
            if (!warehouseId) {
                showErrorNotification('Please select a warehouse');
                document.getElementById('warehouseId')?.focus();
                return;
            }
            if (isNaN(quantity) || quantity < 0) {
                showErrorNotification('Quantity must be 0 or greater');
                document.getElementById('quantity')?.focus();
                return;
            }
            if (isNaN(unitPrice) || unitPrice < 0) {
                showErrorNotification('Unit price must be 0 or greater');
                document.getElementById('unitPrice')?.focus();
                return;
            }

            console.log('📦 Creating inventory item...');
            console.log('  Name:', name);
            console.log('  SKU:', sku);
            console.log('  Warehouse:', warehouseId);
            console.log('  Quantity:', quantity);
            console.log('  Reorder Level:', formData.get('reorderLevel') || 10);
            console.log('  Unit Price:', unitPrice);

            form.submit();
        });
    }

    // ===== RESET =====
    const resetBtn = document.getElementById('resetBtn');
    if (resetBtn) {
        resetBtn.addEventListener('click', function(e) {
            e.preventDefault();
            const form = document.getElementById('inventoryCreateForm');
            if (form) form.reset();
            console.log('🔄 Form reset');
        });
    }

    // =============================================
// UPDATE QUANTITY MODAL FUNCTIONS
// =============================================

// Store current item data for the modal
    let currentUpdateItem = null;

// Show update quantity modal
    // Show update quantity modal
    function showUpdateQuantityModal(itemId, itemName, sku, warehouseName, currentQuantity, reorderLevel, unitPrice) {
        console.log('📦 Opening update quantity modal for item:', itemId);

        // Store item data
        currentUpdateItem = {
            id: itemId,
            name: itemName,
            sku: sku,
            warehouse: warehouseName,
            currentQty: currentQuantity,
            reorderLevel: reorderLevel,
            unitPrice: unitPrice
        };

        // Populate item info
        document.getElementById('updateItemId').value = itemId;
        document.getElementById('updateItemName').textContent = itemName;
        document.getElementById('updateItemSku').textContent = sku;
        document.getElementById('updateItemWarehouse').textContent = warehouseName;
        document.getElementById('updateCurrentQuantity').textContent = currentQuantity;
        document.getElementById('updateReorderLevel').textContent = reorderLevel;
        document.getElementById('updateUnitPrice').textContent = 'LKR ' + unitPrice;

        // Set quantity field
        const quantityInput = document.getElementById('updateQuantity');
        if (quantityInput) {
            quantityInput.value = currentQuantity;
            quantityInput.focus();
            quantityInput.select();
        }

        // Check and show stock status
        checkStockStatus(currentQuantity, reorderLevel);

        // Show modal - USE CLASS instead of style
        const modal = document.getElementById('updateQuantityModal');
        if (modal) {
            // Remove style if it was set inline
            modal.style.display = '';
            // Add the active/open class
            modal.classList.add('active');
            document.body.style.overflow = 'hidden';
            console.log('✅ Modal should now be visible');
        } else {
            console.error('❌ Modal element not found!');
        }
    }

// Close update quantity modal
    function closeUpdateQuantityModal() {
        const modal = document.getElementById('updateQuantityModal');
        if (modal) {
            modal.classList.remove('active');
            document.body.style.overflow = '';
            // Reset inline style
            modal.style.display = '';
        }
        // Reset any error states
        const indicator = document.getElementById('stockStatusIndicator');
        if (indicator) {
            indicator.style.display = 'none';
        }
        currentUpdateItem = null;
    }

// Quick add to quantity
    function quickAddQuantity(amount) {
        const input = document.getElementById('updateQuantity');
        if (input) {
            const current = parseInt(input.value) || 0;
            const newValue = current + amount;
            input.value = newValue;
            // Trigger change event to update stock status
            input.dispatchEvent(new Event('change'));
        }
    }

// Quick set quantity to specific value
    function quickSetQuantity(value) {
        const input = document.getElementById('updateQuantity');
        if (input) {
            input.value = value;
            input.dispatchEvent(new Event('change'));
        }
    }

// Check stock status and show indicator
    function checkStockStatus(quantity, reorderLevel) {
        const indicator = document.getElementById('stockStatusIndicator');
        const message = document.getElementById('stockStatusMessage');

        if (!indicator || !message) return;

        // Remove previous classes
        indicator.classList.remove('warning', 'success', 'danger');

        if (quantity <= 0) {
            indicator.className = 'stock-status-indicator danger';
            message.textContent = '⚠️ Item is OUT OF STOCK!';
            indicator.style.display = 'flex';
        } else if (quantity <= reorderLevel) {
            indicator.className = 'stock-status-indicator warning';
            message.textContent = '⚠️ LOW STOCK! Current quantity (' + quantity + ') is at or below reorder level (' + reorderLevel + ')';
            indicator.style.display = 'flex';
        } else {
            indicator.className = 'stock-status-indicator success';
            message.textContent = '✅ Stock level is healthy';
            indicator.style.display = 'flex';
        }
    }

// Validate quantity before submit
    document.addEventListener('DOMContentLoaded', function() {
        // Quantity input validation on change
        const quantityInput = document.getElementById('updateQuantity');
        if (quantityInput) {
            quantityInput.addEventListener('change', function() {
                const value = parseInt(this.value);
                if (!isNaN(value) && currentUpdateItem) {
                    checkStockStatus(value, currentUpdateItem.reorderLevel);
                }
            });

            quantityInput.addEventListener('input', function() {
                // Real-time validation
                const value = parseInt(this.value);
                const submitBtn = document.getElementById('updateQuantityBtn');
                if (submitBtn) {
                    if (isNaN(value) || value < 0) {
                        submitBtn.disabled = true;
                        submitBtn.textContent = 'Invalid quantity';
                    } else {
                        submitBtn.disabled = false;
                        submitBtn.innerHTML = '<i class="fas fa-save mr-1.5"></i>Update Quantity';
                    }
                }
            });
        }

        // Close modal on Escape key
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape') {
                const modal = document.getElementById('updateQuantityModal');
                if (modal && modal.style.display === 'flex') {
                    closeUpdateQuantityModal();
                }
            }
        });

        // Close modal on overlay click
        const modal = document.getElementById('updateQuantityModal');
        if (modal) {
            modal.addEventListener('click', function(e) {
                if (e.target === this) {
                    closeUpdateQuantityModal();
                }
            });
        }
    });

// Expose functions globally
    window.showUpdateQuantityModal = showUpdateQuantityModal;
    window.closeUpdateQuantityModal = closeUpdateQuantityModal;
    window.quickAddQuantity = quickAddQuantity;
    window.quickSetQuantity = quickSetQuantity;

    // ===== NOTIFICATION HELPER =====
    function showErrorNotification(message) {
        if (typeof showError === 'function') {
            showError(message);
        } else {
            alert('❌ ' + message);
        }
    }

    console.log('📦 Inventory page loaded');
});