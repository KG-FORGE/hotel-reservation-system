/**
 * KG Grand Hotel - Client-Side Reservation Validation & Live Calculation
 */

document.addEventListener('DOMContentLoaded', function () {
    const bookingForm = document.getElementById('reservationForm');
    const checkInInput = document.getElementById('checkIn');
    const checkOutInput = document.getElementById('checkOut');
    const roomSelect = document.getElementById('roomId');
    const guestsInput = document.getElementById('guests');
    const nightsDisplay = document.getElementById('previewNights');
    const totalDisplay = document.getElementById('previewTotal');
    const validationAlert = document.getElementById('clientValidationAlert');

    // Restrict check-in date to today or later
    if (checkInInput) {
        const today = new Date().toISOString().split('T')[0];
        checkInInput.min = today;

        if (checkOutInput && checkInInput.value) {
            checkOutInput.min = checkInInput.value;
        }

        checkInInput.addEventListener('change', function () {
            if (this.value) {
                // Check-out must be at least the next day
                const inDate = new Date(this.value);
                inDate.setDate(inDate.getDate() + 1);
                const nextDay = inDate.toISOString().split('T')[0];
                if (checkOutInput) {
                    checkOutInput.min = nextDay;
                    if (checkOutInput.value && checkOutInput.value <= this.value) {
                        checkOutInput.value = nextDay;
                    }
                }
            }
            updatePricePreview();
        });
    }

    if (checkOutInput) {
        checkOutInput.addEventListener('change', updatePricePreview);
    }

    if (roomSelect) {
        roomSelect.addEventListener('change', updatePricePreview);
    }

    function updatePricePreview() {
        if (!checkInInput || !checkOutInput || !roomSelect || !nightsDisplay || !totalDisplay) return;

        const checkInVal = checkInInput.value;
        const checkOutVal = checkOutInput.value;

        if (!checkInVal || !checkOutVal) return;

        const dIn = new Date(checkInVal);
        const dOut = new Date(checkOutVal);

        const diffTime = dOut - dIn;
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

        if (diffDays <= 0) {
            nightsDisplay.textContent = '0 nights';
            totalDisplay.textContent = '₹0';
            return;
        }

        nightsDisplay.textContent = diffDays + (diffDays === 1 ? ' night' : ' nights');

        // Extract price from selected room option dataset
        const selectedOption = roomSelect.options[roomSelect.selectedIndex];
        if (selectedOption) {
            const price = parseFloat(selectedOption.getAttribute('data-price') || '0');
            const total = diffDays * price;

            // Format in Indian currency
            totalDisplay.textContent = '₹' + total.toLocaleString('en-IN', {
                maximumFractionDigits: 2,
                minimumFractionDigits: 2
            });
        }
    }

    // Trigger initial calculation if form has prefilled values
    updatePricePreview();

    // Client form submission validation
    if (bookingForm) {
        bookingForm.addEventListener('submit', function (e) {
            const errors = [];

            const name = document.getElementById('customerName')?.value.trim();
            const email = document.getElementById('email')?.value.trim();
            const phone = document.getElementById('phone')?.value.trim();
            const checkInVal = checkInInput?.value;
            const checkOutVal = checkOutInput?.value;
            const guests = parseInt(guestsInput?.value || '1', 10);

            if (!name) errors.push('Please enter your full name.');

            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!email || !emailRegex.test(email)) {
                errors.push('Please enter a valid email address.');
            }

            const phoneDigits = phone ? phone.replace(/\D/g, '') : '';
            if (!phone || phoneDigits.length < 10 || phoneDigits.length > 15) {
                errors.push('Please enter a valid phone number (10 to 15 digits).');
            }

            if (!checkInVal || !checkOutVal) {
                errors.push('Please select both check-in and check-out dates.');
            } else {
                const today = new Date().toISOString().split('T')[0];
                if (checkInVal < today) {
                    errors.push('Check-in date cannot be in the past.');
                }
                if (checkOutVal <= checkInVal) {
                    errors.push('Check-out date must be strictly after the check-in date.');
                }
            }

            if (isNaN(guests) || guests <= 0) {
                errors.push('Guest count must be at least 1.');
            } else if (roomSelect) {
                const selectedOption = roomSelect.options[roomSelect.selectedIndex];
                if (selectedOption) {
                    const capacity = parseInt(selectedOption.getAttribute('data-capacity') || '10', 10);
                    if (guests > capacity) {
                        errors.push(`Guest count (${guests}) exceeds maximum capacity for this room (${capacity}).`);
                    }
                }
            }

            if (errors.length > 0) {
                e.preventDefault();
                if (validationAlert) {
                    validationAlert.innerHTML = '<strong>Please correct the following:</strong><ul style="margin-top:6px; padding-left:18px;">' +
                        errors.map(err => '<li>' + err + '</li>').join('') + '</ul>';
                    validationAlert.style.display = 'block';
                    validationAlert.scrollIntoView({ behavior: 'smooth', block: 'center' });
                } else {
                    alert(errors.join('\n'));
                }
            }
        });
    }
});
