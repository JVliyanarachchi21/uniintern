function validateInternshipForm() {
  const title = document.querySelector('input[name="title"]').value.trim();
  const description = document.querySelector('textarea[name="description"]').value.trim();
  const vacancies = parseInt(document.querySelector('input[name="vacancies"]').value);
  const minGpaValue = document.querySelector('input[name="minGpa"]').value;

  const wSkills = parseInt(document.querySelector('input[name="wSkills"]').value) || 0;
  const wGpa = parseInt(document.querySelector('input[name="wGpa"]').value) || 0;
  const wExp = parseInt(document.querySelector('input[name="wExp"]').value) || 0;
  const wCert = parseInt(document.querySelector('input[name="wCert"]').value) || 0;

  if (title === "") {
    alert("Title is required");
    return false;
  }

  if (description === "") {
    alert("Description is required");
    return false;
  }

  if (isNaN(vacancies) || vacancies < 1) {
    alert("Vacancies must be at least 1");
    return false;
  }

  if (minGpaValue !== "") {
    const minGpa = parseFloat(minGpaValue);
    if (minGpa < 0 || minGpa > 4) {
      alert("GPA must be between 0 and 4");
      return false;
    }
  }

  const total = wSkills + wGpa + wExp + wCert;

  if (total !== 100) {
    alert("Weights must total 100%");
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
let selectedPromotionPlan = "Homepage Banner — 7 Days";
let selectedPromotionAmount = 15000;

function selectPromoCard(card, planName, amount) {
  document.querySelectorAll(".promo-card").forEach(c => c.classList.remove("active"));
  card.classList.add("active");

  selectedPromotionPlan = planName;
  selectedPromotionAmount = amount;
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