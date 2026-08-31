/**
 * Tracking module for AI Revenue Recovery
 */
const Tracking = (function() {
    const API_URL = API_BASE_URL + '/api/tracking/event';
    
    // Attempt to get user token
    function getToken() {
        return localStorage.getItem('token');
    }

    // Generate or get session ID
    function getSessionId() {
        let sessionId = sessionStorage.getItem('ai_session_id');
        if (!sessionId) {
            sessionId = 'sess_' + Math.random().toString(36).substr(2, 9);
            sessionStorage.setItem('ai_session_id', sessionId);
        }
        return sessionId;
    }

    function track(eventType, courseId = null, metadata = {}) {
        const payload = {
            eventType: eventType,
            courseId: courseId,
            sessionId: getSessionId(),
            metadata: JSON.stringify(metadata)
        };

        const headers = {
            'Content-Type': 'application/json'
        };

        const token = getToken();
        if (token) {
            headers['Authorization'] = 'Bearer ' + token;
        }

        fetch(API_URL, {
            method: 'POST',
            headers: headers,
            body: JSON.stringify(payload)
        }).catch(err => console.warn('Tracking failed:', err));
    }

    return {
        trackPageView: function() {
            let page = window.location.pathname.split('/').pop() || 'index.html';
            let eventType = 'page_view';
            let courseId = null;

            // Map specific pages to events
            if (page.includes('course.html')) {
                eventType = 'course_view';
                // Try to extract course ID from URL params
                const params = new URLSearchParams(window.location.search);
                if (params.has('id')) {
                    courseId = parseInt(params.get('id'));
                }
            } else if (page.includes('payment.html')) {
                eventType = 'checkout_started';
                const params = new URLSearchParams(window.location.search);
                if (params.has('courseId')) {
                    courseId = parseInt(params.get('courseId'));
                }
            } else if (page.includes('index.html')) {
                eventType = 'home_view';
            }

            track(eventType, courseId, { page: page, url: window.location.href });
        },
        trackEvent: track
    };
})();

// Auto-track page view on load
window.addEventListener('DOMContentLoaded', () => {
    Tracking.trackPageView();
});
