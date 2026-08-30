package com.eduflow.backend.service;

import com.eduflow.backend.dto.PaymentRequestDto;
import com.eduflow.backend.dto.PaymentOrderResponseDto;
import com.eduflow.backend.dto.PaymentVerifyDto;
import com.eduflow.backend.model.Activity;
import com.eduflow.backend.model.Course;
import com.eduflow.backend.model.Enrollment;
import com.eduflow.backend.model.PaymentOrder;
import com.eduflow.backend.model.User;
import com.eduflow.backend.repository.ActivityRepository;
import com.eduflow.backend.repository.CourseRepository;
import com.eduflow.backend.repository.EnrollmentRepository;
import com.eduflow.backend.repository.PaymentOrderRepository;
import com.razorpay.Order;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PaymentService {

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Value("${razorpay.webhook.secret}")
    private String razorpayWebhookSecret;

    @Autowired
    private PaymentOrderRepository paymentOrderRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private com.eduflow.backend.repository.InterventionRepository interventionRepository;

    public PaymentOrderResponseDto createOrder(PaymentRequestDto request, User user) throws RazorpayException {
        RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

        Optional<Course> courseOpt = courseRepository.findById(request.getCourseId());
        if (courseOpt.isEmpty()) {
            throw new IllegalArgumentException("Course not found");
        }

        int amountInPaise = (int) (request.getAmount() * 100); // Convert to paise

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

        Order order = razorpay.orders.create(orderRequest);

        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setRazorpayOrderId(order.get("id"));
        paymentOrder.setStatus("CREATED");
        paymentOrder.setUser(user);
        paymentOrder.setCourse(courseOpt.get());
        paymentOrder.setAmount(request.getAmount());
        paymentOrder.setCreatedAt(LocalDateTime.now());
        
        paymentOrderRepository.save(paymentOrder);

        PaymentOrderResponseDto response = new PaymentOrderResponseDto();
        response.setOrderId(order.get("id"));
        response.setAmount(amountInPaise);
        response.setKeyId(razorpayKeyId);
        
        return response;
    }

    @Transactional
    public boolean verifyPayment(PaymentVerifyDto verifyDto) {
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", verifyDto.getRazorpayOrderId());
            options.put("razorpay_payment_id", verifyDto.getRazorpayPaymentId());
            options.put("razorpay_signature", verifyDto.getRazorpaySignature());

            boolean isValid = Utils.verifyPaymentSignature(options, razorpayKeySecret);

            if (isValid) {
                PaymentOrder order = paymentOrderRepository.findByRazorpayOrderId(verifyDto.getRazorpayOrderId())
                        .orElseThrow(() -> new IllegalArgumentException("Order not found"));

                order.setStatus("SUCCESS");
                order.setRazorpayPaymentId(verifyDto.getRazorpayPaymentId());
                paymentOrderRepository.save(order);

                // Create Enrollment
                Enrollment enrollment = new Enrollment();
                enrollment.setUser(order.getUser());
                enrollment.setCourse(order.getCourse());
                enrollment.setProgressPercentage(0);
                enrollment.setCompleted(false);
                enrollmentRepository.save(enrollment);

                // Create Activity
                Activity activity = new Activity();
                activity.setDescription(order.getUser().getFullName() + " enrolled in '" + order.getCourse().getTitle() + "'");
                activity.setType("ENROLLMENT");
                activity.setTimestamp(LocalDateTime.now());
                activityRepository.save(activity);

                return true;
            }
            return false;
        } catch (RazorpayException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public void markPaymentFailed(String orderId, String failureCode, String failureReason) {
        paymentOrderRepository.findByRazorpayOrderId(orderId).ifPresent(order -> {
            order.setStatus("FAILED");
            order.setFailureCode(failureCode);
            order.setFailureReason(failureReason);
            paymentOrderRepository.save(order);
        });
    }

    @Transactional
    public void markCheckoutAbandoned(String orderId) {
        paymentOrderRepository.findByRazorpayOrderId(orderId).ifPresent(order -> {
            if ("CREATED".equals(order.getStatus())) {
                order.setStatus("FAILED");
                order.setFailureCode("ABANDONED");
                order.setFailureReason("User closed checkout modal");
                paymentOrderRepository.save(order);
            }
        });
    }

    @Transactional
    public String createRecoveryPaymentLink(PaymentOrder order) throws RazorpayException {
        RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

        JSONObject paymentLinkRequest = new JSONObject();
        paymentLinkRequest.put("amount", Math.round(order.getAmount() * 100));
        paymentLinkRequest.put("currency", "INR");
        paymentLinkRequest.put("accept_partial", false);
        paymentLinkRequest.put("description", "Payment Recovery for " + order.getCourse().getTitle());
        
        JSONObject customer = new JSONObject();
        customer.put("name", order.getUser().getFullName());
        customer.put("email", order.getUser().getEmail());
        paymentLinkRequest.put("customer", customer);
        
        JSONObject notify = new JSONObject();
        notify.put("sms", false);
        notify.put("email", false);
        paymentLinkRequest.put("notify", notify);
        
        paymentLinkRequest.put("reminder_enable", true);
        paymentLinkRequest.put("reference_id", order.getRazorpayOrderId()); // Link back to the original failed order

        PaymentLink paymentLink = razorpay.paymentLink.create(paymentLinkRequest);
        
        // Save the generated payment link ID to the order
        String paymentLinkId = paymentLink.get("id");
        String shortUrl = paymentLink.get("short_url");
        
        order.setRazorpayPaymentLinkId(paymentLinkId);
        paymentOrderRepository.save(order);
        
        return shortUrl;
    }

    @Transactional
    public boolean processWebhook(String payload, String signature) {
        try {
            boolean isValid = Utils.verifyWebhookSignature(payload, signature, razorpayWebhookSecret);
            if (!isValid) {
                return false;
            }

            JSONObject event = new JSONObject(payload);
            String eventName = event.getString("event");

            if ("payment_link.paid".equals(eventName)) {
                JSONObject paymentLink = event.getJSONObject("payload").getJSONObject("payment_link").getJSONObject("entity");
                String paymentLinkId = paymentLink.getString("id");
                
                // Find order by reference_id or paymentLinkId (we'll assume the payment link was created with reference_id = order_id or we stored it)
                // For this implementation, we assume we find it by razorpayPaymentLinkId
                Optional<PaymentOrder> orderOpt = paymentOrderRepository.findByRazorpayPaymentLinkId(paymentLinkId);
                
                if (orderOpt.isPresent()) {
                    PaymentOrder order = orderOpt.get();
                    
                    // Idempotency check
                    if ("SUCCESS".equals(order.getStatus())) {
                        return true; // Already processed
                    }

                    // Extract actual payment id from event
                    JSONObject paymentEntity = event.getJSONObject("payload").getJSONObject("payment").getJSONObject("entity");
                    String paymentId = paymentEntity.getString("id");
                    double amount = paymentEntity.getDouble("amount") / 100.0; // from paise to rupees

                    order.setStatus("SUCCESS");
                    order.setRazorpayPaymentId(paymentId);
                    order.setIsRecovered(true);
                    order.setRecoveredAmount(amount);
                    
                    paymentOrderRepository.save(order);

                    // Create Enrollment
                    Enrollment enrollment = new Enrollment();
                    enrollment.setUser(order.getUser());
                    enrollment.setCourse(order.getCourse());
                    enrollment.setProgressPercentage(0);
                    enrollment.setCompleted(false);
                    enrollmentRepository.save(enrollment);

                    // Create Activity
                    Activity activity = new Activity();
                    activity.setDescription(order.getUser().getFullName() + " enrolled in '" + order.getCourse().getTitle() + "' via Revenue Recovery");
                    activity.setType("ENROLLMENT");
                    activity.setTimestamp(LocalDateTime.now());
                    activityRepository.save(activity);
                    
                    // Update Intervention status to RECOVERED
                    java.util.List<com.eduflow.backend.model.Intervention> interventions = interventionRepository.findByPaymentOrder(order);
                    for (com.eduflow.backend.model.Intervention inv : interventions) {
                        inv.setStatus("RECOVERED");
                        inv.setCompletedAt(LocalDateTime.now());
                        interventionRepository.save(inv);
                    }
                    
                    return true;
                }
            }
            // Acknowledge other events too
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
