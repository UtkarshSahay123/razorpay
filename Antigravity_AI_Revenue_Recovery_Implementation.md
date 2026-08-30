# Antigravity IDE Implementation Specification
## AI-Powered Revenue Recovery System for the Education Platform

## 1. Purpose

This document is the implementation specification for Google Antigravity IDE Agent.

Part A of the project is already completed:
- Education website
- User registration/login
- Course browsing
- Subscription flow
- Basic admin portal

DO NOT rebuild Part A unless a change is required for integration.

The Agent must implement Part B and Part C:
- User behaviour and transaction tracking
- XGBoost-based recovery prediction
- Revenue-at-risk calculation
- Root-cause analysis
- AI intervention selection
- Razorpay Payment Link recovery
- Email/payment notification
- Razorpay webhook handling
- PostgreSQL analytics
- Admin revenue dashboard
- Graphs and AI-generated revenue improvement suggestions

The implementation must work with the existing project instead of replacing the current architecture unnecessarily.

---

## 2. FIRST TASK: INSPECT THE EXISTING PROJECT

Before writing code:

1. Inspect the complete repository structure.
2. Identify the existing frontend framework.
3. Identify the existing backend framework.
4. Identify the existing PostgreSQL connection.
5. Identify the current user, course, subscription and payment tables/models.
6. Identify the existing authentication system.
7. Identify the current Razorpay integration, if any.
8. Identify the current admin dashboard.
9. Identify existing environment variables.
10. Do not create duplicate authentication, database connections, payment systems or dashboards.

Create an implementation plan first.

Do not make destructive changes to working Part A functionality.

---

## 3. TARGET ARCHITECTURE

Use the existing application architecture wherever possible.

Recommended logical architecture:

Education Website
        |
        v
User Activity + Payment Events
        |
        v
Existing Backend
        |
        v
PostgreSQL
        |
        +--------------------+
        |                    |
        v                    v
Feature Engineering     Transaction Analysis
        |
        v
XGBoost Model
        |
        v
Recovery Probability
        |
        v
Revenue at Risk
        |
        v
AI Decision Layer
        |
        v
Intervention Selection
        |
        v
Razorpay Payment Link
        |
        v
Email / Notification
        |
        v
Customer Payment
        |
        v
Razorpay Webhook
        |
        v
PostgreSQL
        |
        v
Admin Analytics Dashboard
        |
        v
AI Revenue Suggestions

If the existing backend is JavaScript/TypeScript, keep it as the main application backend. Use a small Python/FastAPI ML service only if Python is required for XGBoost execution. Do not introduce unnecessary microservices.

---

## 4. DATABASE REQUIREMENTS

Reuse existing tables where possible.

Add only the required tables/fields.

The system should be able to store:

### User Activity
- user_id
- event_type
- course_id
- session_id
- timestamp
- metadata

Possible event types:
- registration
- course_view
- course_detail_view
- subscription_view
- checkout_started
- payment_attempted
- payment_failed
- payment_success
- checkout_abandoned

### Payment/Subscription Information
- user_id
- subscription_id
- amount
- currency
- payment_status
- razorpay_order_id
- razorpay_payment_id
- razorpay_payment_link_id
- failure_reason
- failure_code
- created_at
- updated_at

### AI Prediction
- user_id
- subscription_id
- recovery_probability
- risk_score
- predicted_at
- model_version

### Intervention
- user_id
- subscription_id
- intervention_type
- reason
- expected_recovery
- action_cost
- expected_value
- status
- sent_at
- completed_at

### Recovery Result
- user_id
- subscription_id
- payment_link_id
- recovered_amount
- recovery_status
- recovered_at

All database changes must use migrations if the existing project supports migrations.

---

## 5. USER EVENT TRACKING

Instrument the existing website without changing its UI unnecessarily.

Track:

1. User registration
2. Course view
3. Subscription page view
4. Checkout start
5. Payment attempt
6. Payment success
7. Payment failure
8. Checkout abandonment where it can be reliably inferred

Do not infer technical failure without evidence.

Every event should contain:
- user ID
- event type
- timestamp
- relevant course/subscription ID
- relevant transaction information

Avoid storing sensitive payment credentials.

---

## 6. XGBOOST MODEL

Use XGBoost as the primary ML model.

Goal:

Predict:

P(recovery | user behaviour, subscription and payment features)

Recommended features:

- course_views
- course_visits
- time_spent
- subscription_page_views
- checkout_attempts
- payment_attempts
- payment_failure_count
- previous_successful_payments
- days_since_last_activity
- subscription_amount
- payment_method
- previous_purchase_count
- previous_recovery_success
- failure_category

Target:
- 1 = subscription successfully recovered/converted
- 0 = not recovered/converted

The Agent must NOT train a model using fabricated production results.

For the demo, create a clearly labelled synthetic/demo dataset only if a real labelled dataset is unavailable.

The model pipeline must include:
1. Data loading
2. Validation
3. Feature preprocessing
4. Train/validation split
5. XGBoost training
6. Evaluation
7. Model persistence
8. Prediction endpoint/function

Report suitable metrics:
- Accuracy
- Precision
- Recall
- F1
- ROC-AUC

Use class balancing or appropriate evaluation if the dataset is imbalanced.

Store model version information.

---

## 7. REVENUE AT RISK

For every relevant incomplete/failed subscription:

Expected Recovery =
Subscription Amount × Recovery Probability

Example:

Amount = Rs. 2,000
Recovery Probability = 0.82

Expected Recovery = Rs. 1,640

Aggregate:

Total Expected Recovery =
SUM(Amount × Recovery Probability)

Revenue at Risk should represent potential subscription revenue associated with incomplete/failed opportunities according to the project's defined business rules.

Do not double-count the same subscription.

---

## 8. ROOT-CAUSE ANALYSIS

Implement an explainable rule layer before using an LLM.

Examples:

Payment attempted = true
Payment successful = false
Known payment failure code
-> Payment Failure

Checkout started = true
Payment attempted = false
-> Checkout Abandonment

Known network/technical error
-> Technical/Network Issue

No sufficient evidence
-> Unknown

The system must not claim a reason when the data does not support it.

---

## 9. AI DECISION LAYER

The LLM/AI Agent is not the primary prediction model.

It should receive structured information from the ML and business layers.

Example input:

User ID: U1001
Subscription Amount: Rs. 2,000
Payment Failures: 2
Checkout Attempts: 3
Recovery Probability: 0.82
Reason: Payment Failure
Expected Recovery: Rs. 1,640

The agent should select from approved actions:

- PAYMENT_RETRY
- EMAIL_REMINDER
- SUPPORT_ASSISTANCE
- COURSE_RECOMMENDATION
- NO_ACTION

For payment failure with high recovery probability, select PAYMENT_RETRY.

The agent must return structured JSON, not free-form text, for backend execution.

Example:

{
  "action": "PAYMENT_RETRY",
  "reason": "Payment failure with high recovery probability",
  "priority": "HIGH",
  "confidence": 0.82
}

Never allow the LLM to directly execute arbitrary payment operations.

The backend must validate the action before execution.

---

## 10. INTERVENTION SELECTION

Use expected value:

Expected Value =
Probability of Success × Recoverable Revenue - Action Cost

The intervention engine should compare available actions when enough historical data exists.

For the initial MVP, use deterministic business rules combined with XGBoost probability.

Do not implement reinforcement learning in the first version.

---

## 11. RAZORPAY PAYMENT LINK

Use Razorpay Payment Links for recovery.

The backend should:

1. Detect an eligible failed/incomplete payment.
2. Verify the user and subscription.
3. Obtain the correct subscription amount.
4. Create a Razorpay Payment Link.
5. Store the Payment Link ID.
6. Store the associated user/subscription/reference ID.
7. Send the payment link using the selected notification method.

Use Razorpay TEST MODE during development.

Never hard-code:
- Razorpay key ID
- Razorpay secret
- API credentials
- email credentials

Use environment variables.

Example environment variables:

RAZORPAY_KEY_ID=
RAZORPAY_KEY_SECRET=
RAZORPAY_WEBHOOK_SECRET=

Do not expose secrets in frontend code.

---

## 12. EMAIL/PAYMENT NOTIFICATION

Preferred MVP:

Razorpay Payment Link + Razorpay notification system.

Alternative:

Razorpay Payment Link -> backend -> transactional email provider.

If custom email is implemented, keep the email service behind a backend service abstraction.

Example message:

Subject:
Complete Your Course Subscription

Body:

Hi {{name}},

Your previous payment attempt was unsuccessful.

You can complete your course subscription using the secure payment link below:

{{payment_link}}

Thank you.

Do not expose internal AI scores, risk scores, or model reasoning to the customer.

---

## 13. RAZORPAY WEBHOOK

Implement a secure webhook endpoint for Razorpay events.

Important flow:

Customer pays
-> Razorpay
-> webhook
-> verify webhook signature
-> identify payment/subscription
-> update database
-> mark subscription successful
-> record recovered revenue
-> record recovery timestamp
-> update dashboard metrics

Handle duplicate webhook deliveries idempotently.

Do not mark a payment successful based only on a frontend redirect.

The server-side verified payment/webhook result is authoritative.

---

## 14. ADMIN DASHBOARD

Extend the existing admin dashboard.

Add KPI cards:

- Total Users
- Course Visitors
- Subscription Attempts
- Successful Subscriptions
- Failed Payments
- Conversion Rate
- Revenue Generated
- Revenue at Risk
- Revenue Recovered
- Recovery Rate
- High-Risk Users

Graphs:

1. User Conversion Funnel
2. Revenue Trend
3. Revenue Leak Categories
4. Recovery Rate
5. Risk Distribution
6. Payment Failure vs Conversion/Churn
7. Intervention Performance
8. Revenue Forecast
9. Payment Failure Anomalies

Do not use fake values in the live dashboard.

If demo data is used, clearly label it as DEMO DATA.

---

## 15. CUSTOMER-LEVEL AI EXPLANATION

Admin should be able to open a user/subscription record and see:

- Risk Score
- Recovery Probability
- Subscription Amount
- Revenue at Risk
- Expected Recovery
- Root Cause
- Previous Payment Attempts
- Recommended Intervention
- Intervention Status
- Payment Link Status
- Recovery Result

Example:

User: U1001
Risk Score: 91%
Recovery Probability: 82%
Subscription: Rs. 2,000
Revenue at Risk: Rs. 2,000
Expected Recovery: Rs. 1,640
Reason: Payment Failure

Recommendation:
PAYMENT_RETRY

---

## 16. AI REVENUE SUGGESTIONS

Create an admin insight section.

Suggestions must be generated from actual aggregated metrics.

Example:

"Failed payments account for a significant share of potential revenue loss. Users with repeated payment failures have a high estimated recovery probability. Prioritizing payment retry interventions for this segment may improve recovery."

Each suggestion should include supporting metrics.

Do not claim that an intervention caused an improvement unless the project has appropriate comparison data.

---

## 17. ANOMALY DETECTION

Implement a simple anomaly layer for important operational metrics.

Example:

Normal failed payments = 50–80/day
Current failed payments = 470/day

Flag:

ANOMALY DETECTED

Potential investigation categories:
- Payment gateway issue
- Bank/payment processor issue
- Application issue
- Unexpected user behaviour

Use a transparent method such as rolling statistics/Z-score for the MVP.

---

## 18. API REQUIREMENTS

Create backend endpoints according to the existing project's conventions.

Suggested logical endpoints:

GET /api/admin/analytics/overview
GET /api/admin/analytics/revenue
GET /api/admin/analytics/conversion
GET /api/admin/analytics/risk
GET /api/admin/analytics/recovery
GET /api/admin/analytics/interventions

POST /api/ai/predict-recovery
POST /api/ai/intervention

POST /api/payment-links/create
POST /api/webhooks/razorpay

GET /api/admin/users/:id/revenue-risk

Use the project's existing routing conventions if they differ.

Protect admin endpoints with existing admin authentication.

---

## 19. FRONTEND REQUIREMENTS

Do not rebuild the existing UI.

Add the AI analytics section to the current admin portal.

The UI should show:

KPI cards
+
Charts
+
Risk table
+
AI insights
+
User-level explanation
+
Intervention history

Use loading states, empty states and error states.

Make charts responsive.

---

## 20. SECURITY REQUIREMENTS

Mandatory:

- Never expose Razorpay secret keys in frontend code.
- Store secrets in environment variables.
- Validate all webhook signatures.
- Authenticate admin analytics APIs.
- Validate user ownership before exposing user-specific information.
- Never log payment credentials.
- Do not store card numbers, CVV or other sensitive payment credentials.
- Validate all AI-generated actions against an allowlist.
- Add idempotency to payment recovery operations.
- Sanitize and validate API inputs.

---

## 21. TESTING REQUIREMENTS

The Agent must test:

### User flow
Registration -> course browsing -> subscription -> payment.

### Failed payment flow
Payment failure -> event recorded -> prediction -> intervention -> payment link.

### Recovery flow
Payment Link -> successful payment -> webhook -> database update -> dashboard update.

### AI flow
Features -> XGBoost -> probability -> expected recovery -> intervention.

### Security flow
Invalid webhook -> rejected.
Duplicate webhook -> no duplicate recovery.
Unauthenticated admin request -> rejected.

### UI flow
Dashboard loads real database data.
Charts update after recovery.
User-level AI explanation displays correct values.

Use the browser agent to verify the running application where possible.

---

## 22. IMPLEMENTATION ORDER

The Agent should implement in this exact order:

PHASE 1
Inspect existing repository and architecture.

PHASE 2
Create/modify database schema and migrations.

PHASE 3
Add user activity tracking.

PHASE 4
Implement payment/subscription event tracking.

PHASE 5
Create demo/training dataset if required.

PHASE 6
Implement XGBoost training and prediction.

PHASE 7
Implement revenue-at-risk calculations.

PHASE 8
Implement root-cause analysis.

PHASE 9
Implement intervention decision layer.

PHASE 10
Implement Razorpay Payment Link integration in TEST MODE.

PHASE 11
Implement email/notification flow.

PHASE 12
Implement verified Razorpay webhook handling.

PHASE 13
Extend admin dashboard.

PHASE 14
Add graphs and analytics.

PHASE 15
Add AI-generated revenue suggestions.

PHASE 16
Run complete end-to-end tests.

PHASE 17
Fix errors and verify the final workflow in the browser.

---

## 23. ACCEPTANCE CRITERIA

The implementation is complete only when:

- Existing Part A still works.
- User activity is recorded.
- Payment failures are recorded.
- XGBoost produces a recovery probability.
- Revenue at risk is calculated.
- Root cause is determined when evidence exists.
- AI intervention is selected from an allowed action list.
- Razorpay Payment Link can be created in TEST MODE.
- Payment notification can be sent.
- Razorpay webhook is verified and processed.
- Successful recovery updates PostgreSQL.
- Admin KPIs use real database values.
- Dashboard graphs use real database values.
- User-level AI explanation works.
- Revenue suggestions are based on actual metrics.
- Duplicate webhooks do not duplicate revenue.
- Secrets are not exposed.
- The complete workflow works end-to-end.

---

## 24. FINAL END-TO-END WORKFLOW

USER CREATES ACCOUNT
        |
        v
BROWSES COURSES
        |
        v
ACTIVITY TRACKED
        |
        v
SUBSCRIPTION PAGE
        |
        v
CHECKOUT
        |
        v
PAYMENT ATTEMPT
        |
        +----------------------+
        |                      |
      SUCCESS                FAILURE
        |                      |
        v                      v
SUBSCRIPTION            ROOT-CAUSE ANALYSIS
COMPLETED                     |
        |                      v
        |                XGBOOST PREDICTION
        |                      |
        |                      v
        |              RECOVERY PROBABILITY
        |                      |
        |                      v
        |                REVENUE AT RISK
        |                      |
        |                      v
        |                 AI DECISION
        |                      |
        |                      v
        |             INTERVENTION SELECTION
        |                      |
        |                      v
        |             RAZORPAY PAYMENT LINK
        |                      |
        |                      v
        |               EMAIL / NOTIFICATION
        |                      |
        |                      v
        |                  USER RETRIES
        |                      |
        |              +-------+-------+
        |              |               |
        |           SUCCESS          FAILURE
        |              |               |
        |              v               v
        |        REVENUE RECOVERED   STILL AT RISK
        |              |               |
        +--------------+---------------+
                       |
                       v
                 RAZORPAY WEBHOOK
                       |
                       v
                  POSTGRESQL
                       |
                       v
                ADMIN DASHBOARD
                       |
          +------------+------------+
          |            |            |
          v            v            v
       KPIs          GRAPHS     AI INSIGHTS
                       |
                       v
             REVENUE IMPROVEMENT
                RECOMMENDATIONS

---

## 25. ANTIGRAVITY EXECUTION RULE

Work incrementally.

Before changing code:
- inspect
- plan
- implement
- test
- verify
- report

After each major phase, verify that previously working functionality has not broken.

Prefer small, reversible changes.

Use artifacts to report:
- implementation plan
- architecture
- changed files
- database changes
- test results
- browser verification
- remaining issues

Do not claim a feature works unless it has been tested.

The final result must be a working integration with the existing education platform, not a separate demo application.
