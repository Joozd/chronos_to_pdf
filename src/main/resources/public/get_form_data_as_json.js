/**
 * This takes the data in [form] and returns it as a JSON String.
 */
function getFormDataAsJSON(form) {
    let formData = new FormData(form);
    let jsonObject = {};
    for (const [key, value] of formData.entries()) {
        jsonObject[key] = value;
    }
    return JSON.stringify(jsonObject);
}