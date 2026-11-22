// Global Trade - Delete Modal Shared Functions
document.addEventListener('DOMContentLoaded', function() {
    'use strict';

    // ===== DELETE CONFIRMATION STATE =====
    let pendingDelete = null;

    // ===== SHOW DELETE MODAL =====
    window.showDeleteModal = function(entityId, entityName, contextPath, entityType) {
        pendingDelete = { entityId, entityName, contextPath, entityType };
        const messageEl = document.getElementById('deleteMessage');
        if (messageEl) {
            messageEl.textContent = `Are you sure you want to delete "${entityName}"?`;
        }
        const modal = document.getElementById('deleteModal');
        if (modal) {
            modal.classList.add('active');
        }
    };

    // ===== CLOSE DELETE MODAL =====
    window.closeDeleteModal = function() {
        const modal = document.getElementById('deleteModal');
        if (modal) {
            modal.classList.remove('active');
        }
        pendingDelete = null;
    };

    // ===== CONFIRM DELETE ACTION =====
    window.confirmDeleteAction = function() {
        if (!pendingDelete) return;

        console.log(`🗑️ Deleting ${pendingDelete.entityType}:`, pendingDelete);

        const form = document.createElement('form');
        form.method = 'POST';

        // Determine action URL based on entity type
        const basePath = pendingDelete.contextPath || '';
        const actionMap = {
            'warehouse': '/warehouses/delete',
            'inventory': '/inventory/delete',
            'carrier': '/carriers/delete',
            'shipment': '/shipments/delete'  // ← ADD THIS LINE
        };
        const action = actionMap[pendingDelete.entityType] || '/delete';
        form.action = basePath + action;

        // Add entity ID
        const input = document.createElement('input');
        input.type = 'hidden';

        const idMap = {
            'warehouse': 'warehouseId',
            'inventory': 'itemId',
            'carrier': 'carrierId',
            'shipment': 'shipmentId'  // ← ADD THIS LINE
        };
        input.name = idMap[pendingDelete.entityType] || 'id';
        input.value = pendingDelete.entityId;
        form.appendChild(input);

        document.body.appendChild(form);
        form.submit();

        closeDeleteModal();
    };

    // ===== CLOSE MODAL WITH ESCAPE KEY =====
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            closeDeleteModal();
        }
    });

    // ===== CLOSE MODAL BY CLICKING OUTSIDE =====
    document.addEventListener('click', function(e) {
        const modal = document.getElementById('deleteModal');
        if (modal && modal.classList.contains('active')) {
            if (e.target === modal || e.target.classList.contains('delete-modal-overlay')) {
                closeDeleteModal();
            }
        }
    });

    console.log('🗑️ Delete Modal shared functions loaded');
});