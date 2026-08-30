import pandas as pd
import xgboost as xgb
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score, roc_auc_score
from sklearn.preprocessing import LabelEncoder
import json

def train():
    print("Loading data...")
    df = pd.read_csv('training_data.csv')
    
    # Feature columns
    features = [
        'course_views', 'time_spent_mins', 'checkout_attempts', 
        'payment_attempts', 'payment_failures', 'previous_successful_payments', 
        'days_since_last_activity', 'subscription_amount', 'failure_category'
    ]
    
    X = df[features].copy()
    y = df['recovered']
    
    # Handle categorical variable
    le = LabelEncoder()
    X['failure_category'] = le.fit_transform(X['failure_category'])
    
    # Save the label classes for mapping during inference
    with open('label_encoder.json', 'w') as f:
        json.dump(list(le.classes_), f)
        
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
    
    print("Training XGBoost model...")
    model = xgb.XGBClassifier(
        n_estimators=100, 
        max_depth=5, 
        learning_rate=0.1, 
        random_state=42, 
        use_label_encoder=False, 
        eval_metric='logloss'
    )
    
    model.fit(X_train, y_train)
    
    print("Evaluating model...")
    y_pred = model.predict(X_test)
    y_pred_proba = model.predict_proba(X_test)[:, 1]
    
    print("Accuracy:", accuracy_score(y_test, y_pred))
    print("Precision:", precision_score(y_test, y_pred))
    print("Recall:", recall_score(y_test, y_pred))
    print("F1 Score:", f1_score(y_test, y_pred))
    print("ROC-AUC:", roc_auc_score(y_test, y_pred_proba))
    
    print("Saving model...")
    model.save_model('xgboost_model.json')
    print("Model saved to xgboost_model.json")

if __name__ == "__main__":
    train()
