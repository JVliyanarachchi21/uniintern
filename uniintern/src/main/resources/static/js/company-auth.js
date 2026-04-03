document.addEventListener('DOMContentLoaded', () => {
    const form = document.querySelector('form');
    if (!form) return;

    const inputs = {
        companyName: document.getElementById('companyName'),
        email: document.getElementById('email'),
        industry: document.getElementById('industry'),
        description: document.getElementById('description'),
        password: document.getElementById('password'),
        confirmPassword: document.getElementById('confirmPassword')
    };

    const errors = {
        companyName: document.getElementById('companyNameError'),
        email: document.getElementById('emailError'),
        industry: document.getElementById('industryError'),
        description: document.getElementById('descriptionError'),
        password: document.getElementById('passwordError'),
        confirmPassword: document.getElementById('confirmPasswordError')
    };

    // Validation patterns
    const patterns = {
        companyName: /^[a-zA-Z][a-zA-Z0-9\s.,&'-]{1,99}$/,
        email: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
        password: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#])[A-Za-z\d@$!%*?&#]{8,}$/
    };

    const markError = (field, message) => {
        inputs[field].classList.add('input-error');
        errors[field].innerHTML = message; // Changed to innerHTML to support hint buttons
        errors[field].classList.add('visible');
    };

    const clearError = (field) => {
        inputs[field].classList.remove('input-error');
        errors[field].textContent = '';
        errors[field].classList.remove('visible');
    };

    const validateField = (field) => {
        clearError(field);
        const val = inputs[field].value.trim();

        if (val === '') {
            markError(field, 'This field is required');
            return false;
        }

        switch (field) {
            case 'companyName':
                if (val.length < 2) {
                    markError(field, 'Company name must be at least 2 characters');
                    return false;
                }
                if (!/^[a-zA-Z]/.test(val)) {
                    markError(field, 'Company name must start with a letter');
                    return false;
                }
                if (!patterns.companyName.test(val)) {
                    markError(field, 'Invalid characters in company name');
                    return false;
                }
                break;
            case 'email':
                if (!patterns.email.test(val)) {
                    markError(field, 'Please enter a valid email address');
                    return false;
                }
                break;
            case 'industry':
                if (!val) {
                    markError(field, 'Please select an industry');
                    return false;
                }
                break;
            case 'description':
                if (val.length < 20) {
                    markError(field, 'Description must be at least 20 characters');
                    return false;
                }
                break;
            case 'password':
                if (!patterns.password.test(val)) {
                    const hintBtn = '<button type="button" onclick="alert(\'Password Requirements:\\n\\n• At least 8 characters long\\n• 1 Uppercase letter (A-Z)\\n• 1 Lowercase letter (a-z)\\n• 1 Number (0-9)\\n• 1 Special character (@, $, !, %, *, ?, &, #)\')" style="background:none; border:none; color:#1d4ed8; text-decoration:underline; cursor:pointer; font-weight:700; padding:0; margin-left:4px;">[?] Hint</button>';
                    markError(field, 'Password does not meet security requirements. ' + hintBtn);
                    return false;
                }
                break;
            case 'confirmPassword':
                if (val !== inputs.password.value) {
                    markError(field, 'Passwords do not match');
                    return false;
                }
                break;
        }
        return true;
    };

    // Live validation
    Object.keys(inputs).forEach(field => {
        inputs[field].addEventListener('input', () => {
            if (errors[field].classList.contains('visible')) {
                validateField(field);
            }
        });
        inputs[field].addEventListener('blur', () => {
            validateField(field);
        });
    });

    // Prevent non-letters at the beginning of company name dynamically
    inputs.companyName.addEventListener('input', function() {
        if (/^[^a-zA-Z]+/.test(this.value)) {
            this.value = this.value.replace(/^[^a-zA-Z]+/, '');
        }
    });

    // Form submission
    form.addEventListener('submit', (e) => {
        let isValid = true;
        Object.keys(inputs).forEach(field => {
            if (!validateField(field)) {
                isValid = false;
            }
        });

        if (!isValid) {
            e.preventDefault();
        }
    });

});
