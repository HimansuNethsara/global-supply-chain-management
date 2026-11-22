<%-- Logout Confirmation Modal - Shared Component --%>
<div id="logoutModal" class="delete-modal-overlay">
    <div class="delete-modal-content">
        <div class="delete-modal-header">
            <h3>Confirm Sign Out</h3>
            <button class="close-btn" onclick="closeLogoutModal()">
                <i class="fas fa-times"></i>
            </button>
        </div>
        <p class="delete-modal-subtitle">You will be signed out of your account</p>

        <div class="delete-icon-wrapper" style="background: rgba(251, 191, 36, 0.1); border-color: rgba(251, 191, 36, 0.2);">
            <i class="fas fa-sign-out-alt" style="color: #fbbf24;"></i>
        </div>

        <p class="delete-modal-message" id="logoutMessage">
            Are you sure you want to sign out?
        </p>

        <div class="delete-modal-warning" style="background: rgba(251, 191, 36, 0.06); border-color: rgba(251, 191, 36, 0.12);">
            <i class="fas fa-info-circle" style="color: #fbbf24;"></i>
            <span style="color: rgba(251, 191, 36, 0.6);">You will need to sign in again to access your account.</span>
        </div>

        <div class="delete-modal-actions">
            <button class="btn-cancel" onclick="closeLogoutModal()">Cancel</button>
            <button class="btn-delete" onclick="confirmLogout()" style="background: linear-gradient(135deg, #f59e0b, #d97706);">
                <i class="fas fa-sign-out-alt"></i> Sign Out
            </button>
        </div>
    </div>
</div>