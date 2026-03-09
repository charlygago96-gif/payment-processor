package com.payment.infrastructure.adapter.in.camel;

import com.payment.domain.model.Payment;
import com.payment.domain.port.in.GetPaymentUseCase;
import org.apache.camel.ProducerTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final ProducerTemplate producerTemplate;
    private final GetPaymentUseCase getPaymentUseCase;

    public PaymentController(ProducerTemplate producerTemplate,
                             GetPaymentUseCase getPaymentUseCase) {
        this.producerTemplate = producerTemplate;
        this.getPaymentUseCase = getPaymentUseCase;
    }

    // Envía pago directo via REST
    @PostMapping
    public ResponseEntity<Object> createPayment(@RequestBody PaymentRequest request) {
        Object result = producerTemplate.requestBody("direct:payment", request);
        return ResponseEntity.ok(result);
    }

    // Envía pago a la cola ActiveMQ
    @PostMapping("/queue")
    public ResponseEntity<String> sendToQueue(@RequestBody String json) {
        producerTemplate.sendBody("activemq:queue:payments", json);
        return ResponseEntity.ok("Pago enviado a la cola");
    }

    // Consulta todos los pagos
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(getPaymentUseCase.findAll());
    }

    // Consulta un pago por ID
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPayment(@PathVariable String id) {
        return ResponseEntity.ok(getPaymentUseCase.findById(id));
    }
}