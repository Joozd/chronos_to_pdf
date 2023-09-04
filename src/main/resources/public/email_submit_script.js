// Wait for the document to be completely loaded
document.addEventListener("DOMContentLoaded", function() {
    // Get the form element
    const form = document.getElementById('emailForm');

    // Attach a submit event to the form
    form.addEventListener('submit', function(event) {
        // Prevent the default form submission
        event.preventDefault();

        // Call your validateEmail function
        validateEmail(event);
    });
});

async function validateEmail() {
    console.log("SOME TEXT HERE aub")
    const emailInput = document.getElementById('email');
    const errorMsg = document.getElementById('error-message');

    if (!emailInput.value.endsWith('@klm.com')) {
        errorMsg.textContent = 'Please enter an email address ending with @klm.com';
        return false;
    } else {
        errorMsg.textContent = '';

        // AJAX logic:

        // Send email address to /check_existing endpoint to check if user already exists
        const response = await fetch('/check_existing', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email: emailInput.value }),
        });

        const result = await response.json();

        if (result === true) {
            // User already exists, show confirmation dialog
            const proceed = window.confirm(
                "An account with this email address already exists. Creating a new one will overwrite the old account. You can still access your old data by using the login link originally sent to you. Do you still want to proceed?"
            );

            if (proceed) {
                // Redirect to /create_new_account
                window.location.href = '/create_new_account';
            } else {
                // Do nothing, effectively cancelling the form submission
                return false;
            }
        } else {
            // User does not exist, redirect to /create_new_account
            window.location.href = '/create_new_account';
        }

        // Prevent the default form submission
        return false;
    }
}
