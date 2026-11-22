<!-- ===== STATUS UPDATE MODAL ===== -->
<div id="statusUpdateModal" class="status-modal-overlay">
    <div class="status-modal-content">
        <div class="status-modal-header">
            <h3>Update Shipment Status</h3>
            <button class="close-btn" onclick="closeStatusModal()">
                <i class="fas fa-times"></i>
            </button>
        </div>
        <p class="status-modal-subtitle">Update the status of this shipment</p>

        <form id="statusUpdateForm" action="${pageContext.request.contextPath}/shipments/status-update" method="POST">
            <input type="hidden" name="shipmentId" id="statusShipmentId" value="">
            <input type="hidden" name="csrfToken" value="placeholder_token">

            <div class="status-modal-body">
                <!-- Current Status -->
                <div class="form-group">
                    <label>Current Status</label>
                    <div class="current-status-display">
                        <span class="status-badge" id="currentStatusBadge">Loading...</span>
                    </div>
                </div>

                <!-- New Status -->
                <div class="form-group">
                    <label for="newStatus">New Status <span class="text-dracula-red">*</span></label>
                    <div class="select-wrapper">
                        <select id="newStatus" name="newStatus" required>
                            <option value="" class="bg-black text-white/40" disabled selected>Select New Status</option>
                            <option value="PENDING">Pending</option>
                            <option value="IN_TRANSIT">In Transit</option>
                            <option value="DELIVERED">Delivered</option>
                            <option value="DELAYED">Delayed</option>
                            <option value="CANCELLED">Cancelled</option>
                        </select>
                        <span class="select-arrow">
                            <i class="fas fa-chevron-down"></i>
                        </span>
                    </div>
                </div>

                <!-- ===== ADD REMARKS FIELD ===== -->
                <div class="form-group">
                    <label for="modalRemarks">Remarks</label>
                    <textarea id="modalRemarks" name="remarks" rows="2"
                              placeholder="Add remarks about the status change..."
                              class="form-control w-full px-3 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:outline-none transition-all"></textarea>
                </div>
            </div>

            <div class="status-modal-actions">
                <button type="button" class="btn-cancel" onclick="closeStatusModal()">Cancel</button>
                <button type="submit" class="btn-update" id="statusUpdateBtn">
                    <i class="fas fa-save"></i> Update Status
                </button>
            </div>
        </form>
    </div>
</div>