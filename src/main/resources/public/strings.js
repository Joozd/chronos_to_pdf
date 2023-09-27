const STRINGS = {
    logLandingHelp: "<p>The logbook will be filled with a random amount of landings, depending on function. The generated landings will be correct for night/day.</p>" +
                    "<p>This replaces all landing data currently in the logbook.</p>",
    guessSimTypeHelp: "<p>If there is no information about the type of simulator used, the type will be set to the type of aircraft that was flown on the first flight found after this one.</p>" +
                      "<p>If no flights found, this will be added as soon as a more recent monthly overview is added.</p>",
    defaultFunctionHelp: "<p>If there is no function information on the monthly overview (ie. a KLC monthly) this function will be applied.</p><ul>" +
                         "<li>Captain means flight will be logged as PIC</li>" +
                         "<li>FO means flights will be logged as copilot</li>" +
                         "<li>SO is still WIP, means FO for now.</li></ul>" +
                         "<p>Only used for the files that are uploaded in this session</p>",
    removeSimTypesHelp: "<p>Remove all types from simulators so you can add them manually (with a pen or pdf editor).</p>" +
                        "<p>If you check both this and \"Guess simulator type\" it will re-guess all types.</p>" +
                        "<p>This replaces all simulator type data currently in the logbook.</p>",
    multiCrewTimesHelp: "<p>For operations with inflight rest, non-PIC pilots can only log the time actually in seat. This option takes a guess at that according to the folowing rules:</p><ul>" +
                        "<li>&lt;9 hours block time: 2 crew, log all unless rank is SO</li>" +
                        "<li>9-12 hours block time: 3 crew, log 2/3 unless rank is Captain</li>" +
                        "<li>&gt;12 hours block time: 4 crew, log 1/2 unless rank is Captain</li></ul>" +
                        "<p>This option is only used for the currently generated pdf logbook; stored flights are not changed.</p>"

    // ... other strings ...
};
