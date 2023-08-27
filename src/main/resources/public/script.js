function validateEmail() {
    const emailInput = document.getElementById('email');
    const errorMsg = document.getElementById('error-message');

    if (!emailInput.value.endsWith('@klm.com')) {
        errorMsg.textContent = 'Please enter an email address ending with @klm.com';
        return false;
    } else {
        errorMsg.textContent = '';
        return true;
    }
}