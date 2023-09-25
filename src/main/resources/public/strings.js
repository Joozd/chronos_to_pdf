const STRINGS = {
    logLandingHelp: "The logbook will be filled with a random amount of landings, depending on function. The generated landings will be correct for night/day.<br>" +
                    "This replaces all landing data currently in the logbook.",
    guessSimTypeHelp: "If there is no information about the type of simulator used, the type will be set to the type of aircraft that was flown on the first flight found after this one.<br>" +
                      "If no flights found, this will be added as soon as a more recent monthly overview is added.",
    defaultFunctionHelp: "If there is no function information on the monthly overview (ie. a KLC monthly) this function will be applied.<ul>" +
                         "<li>Captain means flight will be logged as PIC</li>" +
                         "<li>FO means flights will be logged as copilot</li>" +
                         "<li>SO is still WIP, means FO for now.</li</ul>" +
                         "Only used for the files that are uploaded in this session",
    removeSimTypesHelp: "Remove all types from simulators so you can add them manually (with a pen or pdf editor).<br>" +
                        "If you check both this and \"Guess simulator type\" it will re-guess all types.<br>" +
                        "This replaces all simulator type data currently in the logbook.",
    // ... other strings ...
};