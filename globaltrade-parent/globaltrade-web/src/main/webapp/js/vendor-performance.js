// Global Trade - Vendor Performance JavaScript
document.addEventListener('DOMContentLoaded', function() {
    'use strict';

    // ============================================================
    // ADD EVALUATION MODAL
    // ============================================================

    window.showAddEvaluationModal = function() {
        var modal = document.getElementById('addEvaluationModal');
        if (modal) {
            modal.classList.add('active');
            document.body.style.overflow = 'hidden';
        }

        // Set default date
        var dateInput = document.getElementById('evalDate');
        if (dateInput && !dateInput.value) {
            var today = new Date();
            var year = today.getFullYear();
            var month = String(today.getMonth() + 1).padStart(2, '0');
            var day = String(today.getDate()).padStart(2, '0');
            dateInput.value = year + '-' + month + '-' + day;
        }

        // Focus on score input
        setTimeout(function() {
            document.getElementById('evalScore')?.focus();
        }, 200);
    };

    window.closeAddEvaluationModal = function() {
        var modal = document.getElementById('addEvaluationModal');
        if (modal) {
            modal.classList.remove('active');
            document.body.style.overflow = '';
        }
    };

    // ============================================================
    // DELETE EVALUATION
    // ============================================================

    window.deleteEvaluation = function(evalId) {
        if (confirm('Are you sure you want to delete this evaluation?')) {
            // Create form to submit delete request
            var form = document.createElement('form');
            form.method = 'POST';
            form.action = window.location.pathname + '/delete';

            var input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'evalId';
            input.value = evalId;
            form.appendChild(input);

            document.body.appendChild(form);
            form.submit();
        }
    };

    // ============================================================
    // RECALCULATE RATING
    // ============================================================

    window.recalculateRating = function() {
        var vendorId = document.getElementById('evalVendorId')?.value ||
            new URLSearchParams(window.location.search).get('vendorId');

        if (!vendorId) {
            if (typeof showError === 'function') {
                showError('Please select a vendor first');
            } else {
                alert('Please select a vendor first');
            }
            return;
        }

        if (confirm('Recalculate vendor rating based on all evaluations?')) {
            // Create form to submit recalculate request
            var form = document.createElement('form');
            form.method = 'POST';
            form.action = window.location.pathname + '/recalculate';

            var input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'vendorId';
            input.value = vendorId;
            form.appendChild(input);

            document.body.appendChild(form);
            form.submit();
        }
    };

    // ============================================================
    // CLOSE MODAL ON ESCAPE
    // ============================================================

    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            closeAddEvaluationModal();
        }
    });

    // ============================================================
    // CLOSE MODAL BY CLICKING OUTSIDE
    // ============================================================

    document.addEventListener('click', function(e) {
        var modal = document.getElementById('addEvaluationModal');
        if (modal && modal.classList.contains('active')) {
            if (e.target === modal) {
                closeAddEvaluationModal();
            }
        }
    });

    // ============================================================
    // SCORE VALIDATION
    // ============================================================

    var scoreInput = document.getElementById('evalScore');
    if (scoreInput) {
        scoreInput.addEventListener('change', function() {
            var val = parseFloat(this.value);
            if (val < 0) this.value = 0;
            if (val > 5) this.value = 5;
        });
    }

    console.log('📊 Vendor Performance page loaded');
});