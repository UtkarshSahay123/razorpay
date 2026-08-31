// config.js
const isLocal = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1' || window.location.protocol === 'file:';

// Replace the deployed URL below with your actual Render URL once you deploy the Backend!
const API_BASE_URL = isLocal 
    ? 'http://localhost:8080' 
    : 'https://eduflow-backend-uu9x.onrender.com';

// Add this so it can be imported cleanly if you ever switch to ES modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { API_BASE_URL };
}
