import pandas as pd
import numpy as np
import random
import os
from datetime import datetime, timedelta

def generate_synthetic_dataset(num_records=1000, output_file='training_data.csv'):
    """
    Generates a synthetic dataset for XGBoost training mimicking the behavior 
    tracked in Phase 3 & 4 (User Events and Payment Orders).
    """
    data = []
    
    # Simulate feature distributions
    for _ in range(num_records):
        # Behavioral Features
        course_views = np.random.poisson(3) # average 3 views before action
        time_spent_mins = int(np.random.gamma(2, 5)) # Gamma distribution for skewed time spent
        
        # Payment History Features
        checkout_attempts = np.random.poisson(1.5)
        payment_attempts = max(0, checkout_attempts - np.random.poisson(0.5))
        payment_failures = max(0, payment_attempts - np.random.poisson(0.8))
        previous_successful_payments = np.random.poisson(0.2)
        
        # Subscription Features
        subscription_amount = random.choice([499, 999, 1499, 1999, 2999])
        days_since_last_activity = int(np.random.exponential(10))
        
        # Calculate Risk and Target
        # High failures, low previous success -> less likely to recover
        base_recovery_prob = 0.5
        if payment_failures > 1:
            base_recovery_prob -= 0.2
        if previous_successful_payments > 0:
            base_recovery_prob += 0.3
        if time_spent_mins > 30:
            base_recovery_prob += 0.1
        if days_since_last_activity > 14:
            base_recovery_prob -= 0.15
            
        base_recovery_prob = max(0.01, min(0.99, base_recovery_prob))
        
        # The target (1 = recovered, 0 = not recovered)
        recovered = np.random.binomial(1, base_recovery_prob)
        
        # Feature dictionary
        record = {
            'user_id': random.randint(1000, 9999),
            'course_views': course_views,
            'time_spent_mins': time_spent_mins,
            'checkout_attempts': checkout_attempts,
            'payment_attempts': payment_attempts,
            'payment_failures': payment_failures,
            'previous_successful_payments': previous_successful_payments,
            'days_since_last_activity': days_since_last_activity,
            'subscription_amount': subscription_amount,
            'failure_category': random.choice(['INSUFFICIENT_FUNDS', 'NETWORK_ERROR', 'BANK_DECLINE', 'USER_CANCELLED', 'UNKNOWN']),
            'recovered': recovered # Target Variable
        }
        data.append(record)

    df = pd.DataFrame(data)
    
    # Save to CSV
    df.to_csv(output_file, index=False)
    print(f"✅ Generated {num_records} synthetic records and saved to {output_file}")
    
    # Basic analysis
    recovery_rate = df['recovered'].mean() * 100
    print(f"📊 Dataset Recovery Rate: {recovery_rate:.2f}%")

if __name__ == "__main__":
    # Ensure directory exists
    os.makedirs(os.path.dirname(os.path.abspath(__file__)), exist_ok=True)
    
    output_path = os.path.join(os.path.dirname(__file__), 'training_data.csv')
    generate_synthetic_dataset(num_records=1500, output_file=output_path)
