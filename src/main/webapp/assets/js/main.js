/**
 * KG Grand Hotel & Suites - Main Interactive Script
 */

let pendingFormToSubmit = null;

function getOrCreateConfirmModal() {
    let modalOverlay = document.getElementById('kgConfirmModal');
    if (modalOverlay) {
        return modalOverlay;
    }

    modalOverlay = document.createElement('div');
    modalOverlay.id = 'kgConfirmModal';
    modalOverlay.className = 'kg-modal-overlay';
    modalOverlay.innerHTML = `
        <div class="kg-modal-dialog" role="dialog" aria-modal="true" aria-labelledby="kgModalTitle">
            <div class="kg-modal-header">
                <div class="kg-modal-icon" id="kgModalIcon">
                    <svg width="24" height="24" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                              d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/>
                    </svg>
                </div>
                <h3 class="kg-modal-title" id="kgModalTitle">Confirm Action</h3>
            </div>
            <div class="kg-modal-body" id="kgModalMessage">
                Are you sure you want to proceed?
            </div>
            <div class="kg-modal-footer">
                <button type="button" class="kg-modal-btn-cancel" id="kgModalCancelBtn">Cancel</button>
                <button type="button" class="kg-modal-btn-confirm" id="kgModalConfirmBtn">Yes, Proceed</button>
            </div>
        </div>
    `;

    document.body.appendChild(modalOverlay);

    function closeModal() {
        modalOverlay.classList.remove('active');
        pendingFormToSubmit = null;
    }

    modalOverlay.querySelector('#kgModalCancelBtn').addEventListener('click', closeModal);

    modalOverlay.addEventListener('click', function (e) {
        if (e.target === modalOverlay) {
            closeModal();
        }
    });

    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape' && modalOverlay.classList.contains('active')) {
            closeModal();
        }
    });

    modalOverlay.querySelector('#kgModalConfirmBtn').addEventListener('click', function () {
        if (pendingFormToSubmit) {
            const form = pendingFormToSubmit;
            closeModal();
            form.submit();
        }
    });

    return modalOverlay;
}

// Global modal trigger function accessible from inline onclick handlers
window.showConfirmModal = function (form, title, message, confirmText, confirmColor) {
    const modal = getOrCreateConfirmModal();
    const titleEl = modal.querySelector('#kgModalTitle');
    const messageEl = modal.querySelector('#kgModalMessage');
    const confirmBtn = modal.querySelector('#kgModalConfirmBtn');
    const iconEl = modal.querySelector('#kgModalIcon');

    titleEl.textContent = title || 'Confirm Action';
    messageEl.textContent = message || 'Are you sure you want to proceed?';
    confirmBtn.textContent = confirmText || 'Yes, Proceed';

    if (confirmColor) {
        confirmBtn.style.backgroundColor = confirmColor;
    } else {
        confirmBtn.style.backgroundColor = '#DC2626';
    }

    if (confirmColor && (confirmColor.includes('D97706') || confirmColor.includes('gold'))) {
        iconEl.style.backgroundColor = '#FEF3C7';
        iconEl.style.color = '#D97706';
    } else {
        iconEl.style.backgroundColor = '#FEE2E2';
        iconEl.style.color = '#DC2626';
    }

    pendingFormToSubmit = form;
    modal.classList.add('active');
};

document.addEventListener('DOMContentLoaded', function () {
    // 1. Image fallback handler
    const images = document.querySelectorAll('img');
    images.forEach(img => {
        img.addEventListener('error', function () {
            this.src = 'data:image/svg+xml;charset=UTF-8,%3Csvg%20width%3D%22800%22%20height%3D%22600%22%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20viewBox%3D%220%200%20800%20600%22%20preserveAspectRatio%3D%22none%22%3E%3Cdefs%3E%3Cstyle%20type%3D%22text%2Fcss%22%3E%23holder_1%20text%20%7B%20fill%3A%23C5A46D%3Bfont-weight%3Abold%3Bfont-family%3AGeorgia%2C%20serif%3Bfont-size%3A32pt%20%7D%20%3C%2Fstyle%3E%3C%2Fdefs%3E%3Cg%20id%3D%22holder_1%22%3E%3Crect%20width%3D%22800%22%20height%3D%22600%22%20fill%3D%22%23101A2C%22%3E%3C%2Frect%3E%3Cg%3E%3Ctext%20x%3D%22260%22%20y%3D%22310%22%3EKG%20Grand%20Hotel%3C%2Ftext%3E%3C%2Fg%3E%3C%2Fg%3E%3C%2Fsvg%3E';
        });
    });

    // 2. Print confirmation handler
    const printButtons = document.querySelectorAll('.btn-print');
    printButtons.forEach(btn => {
        btn.addEventListener('click', function (e) {
            e.preventDefault();
            window.print();
        });
    });

    // 3. Delegation fallback for any data-confirm button
    document.addEventListener('click', function (e) {
        const btn = e.target.closest('[data-confirm]');
        if (btn) {
            e.preventDefault();
            e.stopPropagation();

            const message = btn.getAttribute('data-confirm') || 'Are you sure you want to proceed?';
            const isDelete = message.toLowerCase().includes('delete') || 
                             (btn.name === 'action' && btn.value === 'delete') || 
                             (btn.form && btn.form.querySelector('input[name="action"][value="delete"]'));

            const title = isDelete ? 'Confirm Permanent Deletion' : 'Confirm Cancellation';
            const confirmText = isDelete ? 'Yes, Delete Record' : 'Yes, Cancel Reservation';
            const confirmColor = isDelete ? '#DC2626' : '#D97706';

            window.showConfirmModal(btn.closest('form'), title, message, confirmText, confirmColor);
        }
    });

    // 4. Auto-dismiss alerts after 10 seconds
    const alerts = document.querySelectorAll('.alert-auto-dismiss');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s ease';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 500);
        }, 10000);
    });
});
