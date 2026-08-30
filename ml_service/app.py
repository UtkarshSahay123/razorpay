import json
import xgboost as xgb
import pandas as pd
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel

app = FastAPI(title="Revenue Recovery AI Service")

# Load model and label encoder globally
model = xgb.XGBClassifier()
model.load_model('xgboost_model.json')

with open('label_encoder.json', 'r') as f:
    failure_category_classes = json.load(f)

class PredictionRequest(BaseModel):
    user_id: int
    course_views: int
    time_spent_mins: int
    checkout_attempts: int
    payment_attempts: int
    payment_failures: int
    previous_successful_payments: int
    days_since_last_activity: int
    subscription_amount: float
    failure_category: str

class PredictionResponse(BaseModel):
    user_id: int
    recovery_probability: float

@app.post("/predict", response_model=PredictionResponse)
def predict(req: PredictionRequest):
    try:
        # Encode failure_category
        try:
            encoded_category = failure_category_classes.index(req.failure_category)
        except ValueError:
            # default to an unknown/first class if not found
            encoded_category = 0
            
        features = pd.DataFrame([{
            'course_views': req.course_views,
            'time_spent_mins': req.time_spent_mins,
            'checkout_attempts': req.checkout_attempts,
            'payment_attempts': req.payment_attempts,
            'payment_failures': req.payment_failures,
            'previous_successful_payments': req.previous_successful_payments,
            'days_since_last_activity': req.days_since_last_activity,
            'subscription_amount': req.subscription_amount,
            'failure_category': encoded_category
        }])
        
        prob = model.predict_proba(features)[0][1]
        
        return PredictionResponse(
            user_id=req.user_id,
            recovery_probability=float(prob)
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
