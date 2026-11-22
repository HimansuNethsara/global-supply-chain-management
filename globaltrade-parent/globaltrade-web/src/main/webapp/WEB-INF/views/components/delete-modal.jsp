<%-- Delete Confirmation Modal - Shared Component --%>
<div id="deleteModal" class="delete-modal-overlay">
    <div class="delete-modal-content">
        <div class="delete-modal-header">
            <h3>Confirm Delete</h3>
            <button class="close-btn" onclick="closeDeleteModal()">
                <i class="fas fa-times"></i>
            </button>
        </div>
        <p class="delete-modal-subtitle">This action cannot be undone</p>

        <div class="delete-icon-wrapper">
            <i class="fas fa-trash-alt"></i>
        </div>

        <p class="delete-modal-message" id="deleteMessage">
            Are you sure you want to delete this item?
        </p>

        <div class="delete-modal-warning">
            <i class="fas fa-exclamation-triangle"></i>
            <span>All associated data will be permanently removed.</span>
        </div>

        <div class="delete-modal-actions">
            <button class="btn-cancel" onclick="closeDeleteModal()">Cancel</button>
            <button class="btn-delete" onclick="confirmDeleteAction()">
                <i class="fas fa-trash"></i> Delete
            </button>
        </div>
    </div>
</div>

