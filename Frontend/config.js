// config.js
const isLocal = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1' || window.location.protocol === 'file:';

const API_BASE_URL = isLocal 
    ? 'http://localhost:8080' 
    : 'https://eduflow-backend-uu9x.onrender.com';

if (typeof module !== 'undefined' && module.exports) {
    module.exports = { API_BASE_URL };
}
