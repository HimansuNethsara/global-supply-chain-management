<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 8/24/2026
  Time: 12:00 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!-- ===== STATUS UPDATE MODAL ===== -->
<div id="statusUpdateModal" class="status-modal-overlay hidden">
    <div class="status-modal-content">
        <div class="status-modal-header">
            <h3>Update Document Status</h3>
            <button class="close-btn" onclick="closeStatusModal()">
                <i class="fas fa-times"></i>
            </button>
        </div>
        <p class="status-modal-subtitle">Update the status of this customs document</p>

        <form id="statusUpdateForm" action="${pageContext.request.contextPath}/customs/submit" method="POST">
            <input type="hidden" name="docId" id="statusDocId" value="">

            <div class="status-modal-body">
                <!-- Current Status -->
                <div class="form-group">
                    <label class="text-white/40 text-xs font-medium block mb-1.5">Current Status</label>
                    <div class="current-status-display">
                        <span class="transaction-status" id="currentStatusBadge">Loading...</span>
                    </div>
                </div>

                <!-- New Status -->
                <div class="form-group">
                    <label for="newStatus" class="text-white/40 text-xs font-medium block mb-1.5">New Status <span class="text-dracula-red">*</span></label>
                    <div class="select-wrapper">
                        <select id="newStatus" name="status" class="form-select w-full px-3 py-2.5 pr-10 bg-white/5 border border-white/10 rounded-lg text-white text-sm focus:border-dracula-red focus:outline-none transition-all appearance-none cursor-pointer" required>
                            <option value="" class="bg-black text-white/40" disabled selected>Select New Status</option>
                            <option value="PENDING" class="bg-black">Pending</option>
                            <option value="SUBMITTED" class="bg-black">Submitted</option>
                            <option value="APPROVED" class="bg-black">Approved</option>
                            <option value="REJECTED" class="bg-black">Rejected</option>
                        </select>
                        <span class="absolute right-3 top-1/2 -translate-y-1/2 text-white/20 pointer-events-none">
                            <i class="fas fa-chevron-down text-xs"></i>
                        </span>
                    </div>
                    <div id="statusHelpText" class="text-[10px] text-white/20 mt-1">
                        <i class="fas fa-info-circle mr-1"></i>
                        <span id="statusHelpMessage">Select the new status for this document</span>
                    </div>
                </div>

            </div>

            <div class="status-modal-actions">
                <button type="button" class="btn-cancel" onclick="closeStatusModal()">Cancel</button>
                <button type="submit" class="btn-update" id="statusUpdateBtn">
                    <i class="fas fa-save mr-1.5"></i> Update Status
                </button>
            </div>
        </form>
    </div>
</div>