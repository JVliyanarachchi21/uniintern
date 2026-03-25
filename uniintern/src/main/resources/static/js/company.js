function validateInternshipForm() {
  const title = document.querySelector('input[name="title"]').value.trim();
  const description = document.querySelector('textarea[name="description"]').value.trim();
  const vacancies = parseInt(document.querySelector('input[name="vacancies"]').value);
  const minGpaValue = document.querySelector('input[name="minGpa"]').value;
  const deadlineInput = document.querySelector('input[name="deadline"]');

  const wSkills = parseInt(document.querySelector('input[name="wSkills"]').value) || 0;
  const wGpa = parseInt(document.querySelector('input[name="wGpa"]').value) || 0;
  const wExp = parseInt(document.querySelector('input[name="wExp"]').value) || 0;
  const wCert = parseInt(document.querySelector('input[name="wCert"]').value) || 0;

  if (title === "") {
    alert("Title is required");
    return false;
  }

  if (description.length < 100) {
    alert("Description must be at least 100 characters long to provide enough detail for applicants.");
    const descriptionField = document.querySelector('textarea[name="description"]');
    if (descriptionField) descriptionField.focus();
    return false;
  }

  if (isNaN(vacancies) || vacancies < 1) {
    alert("Vacancies must be at least 1");
    return false;
  }

  if (deadlineInput && deadlineInput.value) {
    const today = new Date().toISOString().split('T')[0];
    if (deadlineInput.value < today) {
        alert("Application Deadline cannot be in the past.");
        deadlineInput.focus();
        return false;
    }
  }

  if (minGpaValue !== "") {
    const minGpa = parseFloat(minGpaValue);
    const gpaInput = document.querySelector('input[name="minGpa"]');
    if (minGpa < 0 || minGpa > 4 || isNaN(minGpa)) {
      if(gpaInput) {
        gpaInput.style.borderColor = "red";
        gpaInput.style.color = "red";
        let errorMsg = gpaInput.nextElementSibling;
        if (!errorMsg || !errorMsg.classList.contains('gpa-error')) {
            errorMsg = document.createElement('div');
            errorMsg.classList.add('gpa-error');
            errorMsg.style.color = 'red';
            errorMsg.style.fontSize = '13px';
            errorMsg.style.marginTop = '4px';
            gpaInput.parentNode.appendChild(errorMsg);
        }
        errorMsg.textContent = "Valid GPA between 0.00 and 4.00 is required.";
        gpaInput.focus();
      }
      return false;
    }
  }

  const total = wSkills + wGpa + wExp + wCert;

  if (total !== 100) {
    const weightInputs = document.querySelectorAll('input[name="wSkills"], input[name="wGpa"], input[name="wExp"], input[name="wCert"]');
    weightInputs.forEach(input => {
        input.style.borderColor = "red";
        input.style.color = "red";
    });
    const firstWeight = document.querySelector('input[name="wSkills"]');
    if(firstWeight) firstWeight.focus();
    return false;
  }

  return true;
}

function shortlist(btn) {
  const row = btn.closest("tr");
  const statusCell = row.querySelector(".badge-status");

  statusCell.innerText = "SHORTLISTED";
  statusCell.className = "badge-status st-approved";
}

function reject(btn) {
  const row = btn.closest("tr");
  const statusCell = row.querySelector(".badge-status");

  statusCell.innerText = "REJECTED";
  statusCell.className = "badge-status st-rejected";
}

function saveCompanyProfile() {
  alert("Profile updated successfully!");
  return false;
}

function viewInternship() {
  alert("Viewing internship details (UI only).");
}

function editInternship() {
  alert("Edit internship form will open here (UI only).");
}

function closeInternship(button) {
  const row = button.closest("tr");
  const statusBadge = row.querySelector(".badge-status");
  statusBadge.textContent = "Closed";
  statusBadge.className = "badge-status st-closed";
  alert("Internship status changed to Closed.");
}

function viewInternship() {
  alert("View internship details (UI only)");
}

function editInternship() {
  alert("Edit internship (UI only)");
}

function copyInternship() {
  alert("Copy internship (UI only)");
}

function featureInternship() {
  alert("Request featured promotion (UI only)");
}

//applicants validations 
function updateScoreValue(value) {
  document.getElementById("scoreValue").textContent = value;
}

function exportShortlisted() {
  alert("Shortlisted applicants exported successfully. (UI only)");
}

function viewApplicant() {
  alert("View applicant details. (UI only)");
}

function shortlistApplicant(button) {
  const row = button.closest("tr");
  const statusBadge = row.querySelector(".badge-status");
  statusBadge.textContent = "Shortlisted";
  statusBadge.className = "badge-status st-approved";
  alert("Applicant shortlisted successfully.");
}

function rejectApplicant(button) {
  const row = button.closest("tr");
  const statusBadge = row.querySelector(".badge-status");
  statusBadge.textContent = "Rejected";
  statusBadge.className = "badge-status st-rejected";
  alert("Applicant rejected.");
}


//promotion ui js 
let selectedPromotionPlan = "";
let selectedPromotionAmount = 0;

function selectPromoCard(card, planName, amount) {
  document.querySelectorAll(".promo-card").forEach(c => c.classList.remove("active"));
  card.classList.add("active");

  selectedPromotionPlan = planName;
  selectedPromotionAmount = amount;

  const section = document.getElementById("promoSelectSection");
  if (section) section.style.display = "block";
}

function checkInternshipSelection() {
    const selector = document.getElementById("promoInternship");
    const btn = document.getElementById("proceedBtn");
    
    if (selector && btn) {
        if (selector.value !== "") {
            btn.style.opacity = "1";
            btn.style.pointerEvents = "auto";
        } else {
            btn.style.opacity = "0.5";
            btn.style.pointerEvents = "none";
        }
    }
}

function proceedToPayment() {
  const internship = document.getElementById("promoInternship").value;

  if (internship === "") {
    alert("Please select an internship first.");
    return;
  }

  alert(
    "Proceeding to payment for:\n" +
    internship +
    "\nPlan: " + selectedPromotionPlan +
    "\nAmount: LKR " + selectedPromotionAmount.toLocaleString()
  );
}

//promotion checkout js
function payNow() {
  const cardName = document.getElementById("cardName");
  const cardNumber = document.getElementById("cardNumber");
  const expiry = document.getElementById("expiry");
  const cvv = document.getElementById("cvv");
  const email = document.getElementById("billingEmail");

  let formValid = true;

  [cardName, cardNumber, expiry, cvv, email].forEach(input => {
      if (input && input.value.trim() === "") {
          input.style.borderColor = "red";
          formValid = false;
      } else if (input) {
          input.style.borderColor = "";
      }
  });

  if (!formValid) {
      alert("Please fill in all payment fields before proceeding.");
      return;
  }

  const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (email && !emailPattern.test(email.value.trim())) {
      alert("Please enter a valid billing email address.");
      email.style.borderColor = "red";
      email.focus();
      return;
  }

  if (cardNumber && cardNumber.value.replace(/\s/g, '').length < 16) {
      alert("Card Number must be precisely 16 digits.");
      cardNumber.style.borderColor = "red";
      cardNumber.focus();
      return;
  }

  if (expiry && expiry.value.length < 5) {
      alert("Expiry date must completely match MM/YY format.");
      expiry.style.borderColor = "red";
      expiry.focus();
      return;
  }

  if (cvv && cvv.value.length < 3) {
      alert("CVV must be 3 or 4 digits.");
      cvv.style.borderColor = "red";
      cvv.focus();
      return;
  }

  alert("Payment completed successfully! Promotion activated.");
}


//company history
function openReceiptModal(button) {
  const id = button.getAttribute("data-id");
  const date = button.getAttribute("data-date");
  const plan = button.getAttribute("data-plan");
  const amount = button.getAttribute("data-amount");
  const status = button.getAttribute("data-status");

  document.getElementById("receiptId").textContent = id;
  document.getElementById("receiptDate").textContent = date;
  document.getElementById("receiptPlan").textContent = plan;
  document.getElementById("receiptAmount").textContent = amount;

  const statusBadge = document.getElementById("receiptStatus");
  statusBadge.textContent = status;
  statusBadge.className = "badge-status " + (status === "Active" ? "st-approved" : "st-expired");

  document.getElementById("receiptModal").style.display = "flex";
}

function closeReceiptModal() {
  document.getElementById("receiptModal").style.display = "none";
}

function printReceipt() {
  window.print();
}

//secuirity js
function updatePassword() {
  const currentPassword = document.getElementById("currentPassword").value;
  const newPassword = document.getElementById("newPassword").value;
  const confirmPassword = document.getElementById("confirmPassword").value;

  if (!currentPassword || !newPassword || !confirmPassword) {
    alert("Please fill in all password fields.");
    return;
  }

  if (newPassword !== confirmPassword) {
    alert("New password and confirm password do not match.");
    return;
  }

  alert("Password updated successfully. (UI only)");
}

function saveSecurityPreferences() {
  alert("Security preferences saved successfully. (UI only)");
}


//topbar profile js
function toggleProfileMenu() {
  const menu = document.getElementById("profileMenu");
  menu.classList.toggle("show");
}

window.addEventListener("click", function (e) {
  const dropdown = document.querySelector(".profile-dropdown");
  const menu = document.getElementById("profileMenu");

  if (!dropdown || !menu) return;

  if (!dropdown.contains(e.target)) {
    menu.classList.remove("show");
  }
});


//notificayion

function markAsRead(btn) {
  const item = btn.closest(".notification-item");
  item.classList.remove("unread");
  btn.remove();
}

// Payment Checkout Formatting & Validation
document.addEventListener('DOMContentLoaded', () => {
    const cardName = document.getElementById('cardName');
    const cardNumber = document.getElementById('cardNumber');
    const expiry = document.getElementById('expiry');
    const cvv = document.getElementById('cvv');

    if (cardName) {
        // Block numbers in name
        cardName.addEventListener('input', function() {
            this.value = this.value.replace(/[0-9]/g, '');
        });
    }

    if (cardNumber) {
        // Auto-space every 4 digits, block letters
        cardNumber.addEventListener('input', function(e) {
            let val = this.value.replace(/[^\d]/g, '').substring(0, 16);
            if (val.length > 0) {
                val = val.match(/.{1,4}/g).join(' ');
            }
            this.value = val;
        });
    }

    if (expiry) {
        // MM/YY auto-slash and boundary checks
        expiry.addEventListener('input', function(e) {
            let val = this.value.replace(/[^\d]/g, '').substring(0, 4);
            
            if (e.inputType === 'deleteContentBackward') {
                this.value = val;
                return;
            }

            if (val.length === 1 && parseInt(val) > 1) {
                val = '0' + val + '/';
            } else if (val.length === 2) {
                let month = parseInt(val);
                if (month === 0) {
                    val = '01/';
                } else if (month > 12) {
                    val = '12/';
                } else {
                    val = val + '/';
                }
            } else if (val.length > 2) {
                let monthStr = val.substring(0, 2);
                let month = parseInt(monthStr);
                if (month === 0) monthStr = '01';
                else if (month > 12) monthStr = '12';
                val = monthStr + '/' + val.substring(2);
            }
            
            this.value = val;
        });

        expiry.addEventListener('blur', function() {
            if (this.value.length === 5) {
                let parts = this.value.split('/');
                let month = parseInt(parts[0]);
                let year = parseInt("20" + parts[1]);
                let now = new Date();
                let currentYear = now.getFullYear();
                let currentMonth = now.getMonth() + 1;

                if (year < currentYear || (year === currentYear && month < currentMonth)) {
                    alert("This card has expired. Please use a valid card.");
                    this.style.borderColor = "red";
                    this.style.color = "red";
                } else {
                    this.style.borderColor = "";
                    this.style.color = "";
                }
            }
        });
    }

    if (cvv) {
        // Max 4 digits, numbers only
        cvv.addEventListener('input', function() {
            this.value = this.value.replace(/[^\d]/g, '').substring(0, 4);
        });
    }
});

// Ensure GPA input doesn't accept -, +, e, E and validates on blur/input
document.addEventListener('DOMContentLoaded', () => {
    // Description length tracker
    const descInput = document.getElementById('description');
    const charCount = document.getElementById('char-count');
    if (descInput && charCount) {
        descInput.addEventListener('input', function() {
            const len = this.value.length;
            charCount.textContent = len;
            if (len < 100) {
                charCount.style.color = '#ef4444'; // Red
            } else {
                charCount.style.color = '#10b981'; // Green
            }
        });
        // Run once on load to set initial state if edited
        const initialLen = descInput.value.length;
        charCount.textContent = initialLen;
        if(initialLen >= 100) charCount.style.color = '#10b981';
    }
    // Date Constraint
    const deadlineInput = document.querySelector('input[name="deadline"]');
    if (deadlineInput) {
        const today = new Date().toISOString().split('T')[0];
        deadlineInput.setAttribute('min', today);
    }

    // Ranking Weights Live Tracker & Constraint
    const weightInputs = document.querySelectorAll('input[name="wSkills"], input[name="wGpa"], input[name="wExp"], input[name="wCert"]');
    const weightGrid = document.querySelector('.weight-grid');

    if (weightGrid && weightInputs.length > 0) {
        const tracker = document.createElement('div');
        tracker.classList.add('weight-tracker');
        tracker.style.marginTop = '12px';
        tracker.style.fontWeight = '600';
        tracker.style.fontSize = '14px';
        tracker.style.transition = 'color 0.3s ease';
        weightGrid.parentNode.insertBefore(tracker, weightGrid.nextSibling);

        function updateWeights(e) {
            let total = 0;
            weightInputs.forEach(input => {
                // If blur, auto-fill blank with 0
                if (e && e.type === 'blur' && input.value === '') {
                    input.value = 0;
                }
                
                let val = parseInt(input.value);
                if (isNaN(val)) val = 0;

                // Strict boundaries 0-100
                if (val > 100) { val = 100; input.value = 100; }
                if (val < 0) { val = 0; input.value = 0; }
                
                total += val;
            });

            if (total === 100) {
                tracker.textContent = "Total: 100%";
                tracker.style.color = "#10b981"; // Green
                weightInputs.forEach(i => { i.style.borderColor = ""; i.style.color = ""; });
            } else if (total < 100) {
                tracker.textContent = `Total: ${total}% (Need ${100 - total}% more)`;
                tracker.style.color = "#f59e0b"; // Orange
            } else {
                tracker.textContent = `Total: ${total}% (Exceeds by ${total - 100}%)`;
                tracker.style.color = "#ef4444"; // Red
            }
        }

        weightInputs.forEach(input => {
            // Prevent invalid chars including decimals for whole percentages
            input.addEventListener('keydown', function(evt) {
                if (['e', 'E', '-', '+', '.'].includes(evt.key)) {
                    evt.preventDefault();
                }
            });
            
            input.addEventListener('input', updateWeights);
            input.addEventListener('blur', updateWeights);
        });
        
        // Initialize tracker on load
        updateWeights();
    }

    // GPA Constraint
    const gpaInput = document.querySelector('input[name="minGpa"]');
    if (gpaInput) {
        // Prevent typing 'e', '-', '+' 
        gpaInput.addEventListener('keydown', function(e) {
            if (['e', 'E', '-', '+'].includes(e.key)) {
                e.preventDefault();
            }
        });

        // Live validation for red text
        gpaInput.addEventListener('input', function() {
            let errorMsg = this.nextElementSibling;
            if (!errorMsg || !errorMsg.classList.contains('gpa-error')) {
                errorMsg = document.createElement('div');
                errorMsg.classList.add('gpa-error');
                errorMsg.style.color = 'red';
                errorMsg.style.fontSize = '13px';
                errorMsg.style.marginTop = '4px';
                this.parentNode.appendChild(errorMsg);
            }

            const val = parseFloat(this.value);
            if (this.value !== "" && (isNaN(val) || val < 0 || val > 4)) {
                errorMsg.textContent = "GPA must be between 0.00 and 4.00";
                this.style.borderColor = "red";
                this.style.color = "red";
            } else {
                errorMsg.textContent = "";
                this.style.borderColor = "";
                this.style.color = "";
            }
        });
    }
});