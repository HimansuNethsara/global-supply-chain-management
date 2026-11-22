<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 8/28/2026
  Time: 12:00 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!-- Update Quantity Modal -->
<div id="updateQuantityModal" class="update-quantity-modal-overlay" style="display:none;">
    <div class="update-quantity-modal-content">

        <!-- Modal Header -->
        <div class="modal-header">
            <h3>
                <i class="fas fa-boxes text-dracula-red mr-2"></i>
                Update Stock Quantity
            </h3>
            <button type="button" class="close-btn" onclick="closeUpdateQuantityModal()">
                <i class="fas fa-times"></i>
            </button>
        </div>

        <p class="modal-subtitle">Adjust the stock quantity for the selected inventory item</p>

        <!-- Item Info Display -->
        <div class="update-quantity-item-info">
            <div class="item-info-grid">
                <div class="item-info-field">
                    <label>Item Name</label>
                    <span id="updateItemName" class="text-white/80">--</span>
                </div>
                <div class="item-info-field">
                    <label>SKU</label>
                    <span id="updateItemSku" class="text-white/80">--</span>
                </div>
                <div class="item-info-field">
                    <label>Warehouse</label>
                    <span id="updateItemWarehouse" class="text-white/80">--</span>
                </div>
                <div class="item-info-field">
                    <label>Current Quantity</label>
                    <span id="updateCurrentQuantity" class="text-white/80 font-medium">--</span>
                </div>
                <div class="item-info-field">
                    <label>Reorder Level</label>
                    <span id="updateReorderLevel" class="text-white/80">--</span>
                </div>
                <div class="item-info-field">
                    <label>Unit Price</label>
                    <span id="updateUnitPrice" class="text-white/80">--</span>
                </div>
            </div>
        </div>

        <!-- Quantity Update Form -->
        <form id="updateQuantityForm" action="${pageContext.request.contextPath}/inventory/update-quantity" method="POST">
            <input type="hidden" id="updateItemId" name="itemId" value="">

            <div class="update-quantity-form-group">
                <label for="updateQuantity" class="text-white/40 text-xs font-medium block mb-1.5">
                    New Quantity <span class="text-dracula-red">*</span>
                </label>
                <div class="quantity-input-wrapper">
                    <input type="number" id="updateQuantity" name="quantity"
                           placeholder="Enter new quantity" min="0" step="1"
                           class="form-control w-full px-3 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:outline-none transition-all"
                           required>
                    <span class="quantity-input-suffix">units</span>
                </div>
                <div class="text-[8px] sm:text-[9px] text-white/20 font-light mt-1">
                    <i class="fas fa-info-circle mr-1"></i>
                    Enter the total quantity after adjustment
                </div>
            </div>

            <!-- Quick Action Buttons -->
            <div class="quick-action-buttons">
                <span class="text-white/20 text-[10px]">Quick adjust:</span>
                <button type="button" class="quick-action-btn" onclick="quickAddQuantity(10)">+10</button>
                <button type="button" class="quick-action-btn" onclick="quickAddQuantity(25)">+25</button>
                <button type="button" class="quick-action-btn" onclick="quickAddQuantity(50)">+50</button>
                <button type="button" class="quick-action-btn" onclick="quickAddQuantity(100)">+100</button>
                <button type="button" class="quick-action-btn" onclick="quickSetQuantity(0)">Set to 0</button>
            </div>

            <!-- Stock Status Indicator -->
            <div id="stockStatusIndicator" class="stock-status-indicator" style="display:none;">
                <i class="fas fa-info-circle"></i>
                <span id="stockStatusMessage"></span>
            </div>

            <!-- Form Actions -->
            <div class="modal-actions">
                <button type="button" class="btn-cancel" onclick="closeUpdateQuantityModal()">Cancel</button>
                <button type="submit" class="btn-update" id="updateQuantityBtn">
                    <i class="fas fa-save mr-1.5"></i>
                    Update Quantity
                </button>
            </div>
        </form>

    </div>
</div>