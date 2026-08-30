package com.eduflow.backend.dto;

public class PredictionRequestDto {
    private Long user_id;
    private int course_views;
    private int time_spent_mins;
    private int checkout_attempts;
    private int payment_attempts;
    private int payment_failures;
    private int previous_successful_payments;
    private int days_since_last_activity;
    private double subscription_amount;
    private String failure_category;

    // Getters and setters
    public Long getUser_id() { return user_id; }
    public void setUser_id(Long user_id) { this.user_id = user_id; }

    public int getCourse_views() { return course_views; }
    public void setCourse_views(int course_views) { this.course_views = course_views; }

    public int getTime_spent_mins() { return time_spent_mins; }
    public void setTime_spent_mins(int time_spent_mins) { this.time_spent_mins = time_spent_mins; }

    public int getCheckout_attempts() { return checkout_attempts; }
    public void setCheckout_attempts(int checkout_attempts) { this.checkout_attempts = checkout_attempts; }

    public int getPayment_attempts() { return payment_attempts; }
    public void setPayment_attempts(int payment_attempts) { this.payment_attempts = payment_attempts; }

    public int getPayment_failures() { return payment_failures; }
    public void setPayment_failures(int payment_failures) { this.payment_failures = payment_failures; }

    public int getPrevious_successful_payments() { return previous_successful_payments; }
    public void setPrevious_successful_payments(int previous_successful_payments) { this.previous_successful_payments = previous_successful_payments; }

    public int getDays_since_last_activity() { return days_since_last_activity; }
    public void setDays_since_last_activity(int days_since_last_activity) { this.days_since_last_activity = days_since_last_activity; }

    public double getSubscription_amount() { return subscription_amount; }
    public void setSubscription_amount(double subscription_amount) { this.subscription_amount = subscription_amount; }

    public String getFailure_category() { return failure_category; }
    public void setFailure_category(String failure_category) { this.failure_category = failure_category; }
}
