/*
function escape(str) {
    "use strict";
    if (!str) return '';
    return str.replace(/&/g, '&amp;')
              .replace(/</g, '&lt;')
              .replace(/>/g, '&gt;')
              .replace(/"/g, '&quot;')
              .replace(/'/g, '&#039;');
}
*/

function JobSearchHandler() {
    "use strict";

    this.displaySearchResults = function(req, res, next) {
        
        var searchQuery = req.query.keyword || "";

        var jobListings = [];

        return res.render("search_results", {
            searchQuery: searchQuery,
            jobs: jobListings
        });
    };
}

module.exports = JobSearchHandler;