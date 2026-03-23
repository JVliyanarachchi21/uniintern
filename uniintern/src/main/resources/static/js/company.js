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