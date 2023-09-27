/*
*** DEPENDENCIES *** Make sure to include those in any html files that use this file before including this file.
- get_form_data_as_json.js for getFormDataAsJSON(form)
*/

// Get the modal
var modal = document.getElementById("myModal");

// Get the button that opens the modal
var btn = document.getElementById("openModalBtn");

// Get the <span> element that closes the modal
var span = document.getElementsByClassName("close")[0];

// When the user clicks the button, open the modal
btn.onclick = function() {
  modal.style.display = "block";
}

// When the user clicks on <span> (x), close the modal
span.onclick = function() {
  modal.style.display = "none";
}

// When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
  if (event.target == modal) {
    modal.style.display = "none";
  }
}

// Function to fetch user preferences from the backend and update the form elements
function fetchUserPreferences() {
  fetch('/prefs')
    .then(response => {
      if (!response.ok) {
        throw new Error('Network response was not ok' + response.statusText);
      }
      return response.json();
    })
    .then(data => {
    console.log('got data:');
    console.log(data.logLanding);
    console.log(data.guessSimType);
    console.log(data.defaultFunction);
      // Update the form elements with the fetched preferences
      document.getElementById('logLanding').checked = data.logLanding;
      document.getElementById('guessSimType').checked = data.guessSimType;
      document.getElementById('removeSimTypes').checked = data.removeSimTypes;
      document.getElementById('multiCrewTimes').checked = data.multiCrewTimes;
      document.querySelector(`select[name="defaultFunction"]`).value = data.defaultFunction;
    })
    .catch(error => {
      console.error('There has been a problem with your fetch operation:', error);
    });
}

// Call the function when the page loads
document.addEventListener('DOMContentLoaded', fetchUserPreferences);

function submitForm() {
    var form = document.getElementById('userChoicesForm');
    var jsonData = getFormDataAsJSON(form);

  fetch('/update_preferences', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: jsonData
  })
    .then(response => {
        if (!response.ok) {
            throw Error(`HTTP error! Status: ${response.status}`);
        }
    })
    .catch((error) => console.error('Error:', error));

    // Close the modal after submitting the form
    modal.style.display = "none";
}

// Function to show help modal with a given message
/*
    NOTE only use this with the hardcoded messages in strings.js as the message allows for HTML
    which can expose the site to Cross-Site Scripting (XSS) attacks
 */
function showHelpModal(message) {
  // Get the help modal and the element to display the help message
  var helpModal = document.getElementById("helpModal");
  var helpMessage = document.getElementById("helpMessage");

  // Set the help message and show the modal
  helpMessage.innerHTML = message;
  helpModal.style.display = "block";
}

// Attach event listeners to help buttons
document.querySelectorAll('.helpBtn').forEach(item => {
  item.addEventListener('click', event => {
    // Get the help message from the data-help attribute of the clicked button
    var message = event.target.getAttribute('data-help');

    // Show the help modal with the retrieved message
    showHelpModal(message);
  })
});

// Get the <span> element that closes the help modal and attach a click event listener to it
var closeHelp = document.querySelector("#helpModal .close");
closeHelp.addEventListener('click', function() {
  document.getElementById("helpModal").style.display = "none";
});

// Close the help modal when the user clicks anywhere outside of it
window.addEventListener('click', function(event) {
  var helpModal = document.getElementById("helpModal");
  if (event.target == helpModal) {
    helpModal.style.display = "none";
  }
});


// Attach event listeners to help links
document.querySelectorAll('.helpBtn').forEach(item => {
  item.addEventListener('click', event => {
    // Prevent the default action of the link
    event.preventDefault();

    // Get the help message from the data-help attribute of the clicked link
    var message = STRINGS[event.target.getAttribute('data-help')];

    // Show the help modal with the retrieved message
    showHelpModal(message);
  })
});