package com.payment.infrastructure.adapter.in.camel;

import com.payment.domain.model.PaymentType;
import com.payment.domain.port.in.ProcessPaymentUseCase;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PaymentRoute extends RouteBuilder {

    private static final Logger log = LoggerFactory.getLogger(PaymentRoute.class);

    private final ProcessPaymentUseCase processPaymentUseCase;

    public PaymentRoute(ProcessPaymentUseCase processPaymentUseCase) {
        this.processPaymentUseCase = processPaymentUseCase;
    }

    @Override
    public void configure() {

        // Gestión global de errores
        onException(Exception.class)
                .handled(true)
                .setBody(simple("Error procesando pago: ${exception.message}"))
                .log("ERROR: ${exception.message}");

        // Ruta de entrada — recibe el pago y lo enruta según el tipo
        from("direct:payment")
                .routeId("payment-entry-route")
                .log("Pago recibido: ${body}")
                .process(exchange -> {
                    PaymentRequest req = exchange.getIn().getBody(PaymentRequest.class);
                    exchange.getIn().setHeader("paymentType", req.getType().name());
                    exchange.getIn().setBody(req);
                })
                // Content Based Router — enruta según el tipo de pago
                .choice()
                .when(header("paymentType").isEqualTo("NACIONAL"))
                .to("direct:sepa")
                .when(header("paymentType").isEqualTo("INTERNACIONAL"))
                .to("direct:swift")
                .when(header("paymentType").isEqualTo("URGENTE"))
                .to("direct:express")
                .end();

        // Ruta SEPA — pagos nacionales
        from("direct:sepa")
                .routeId("sepa-route")
                .log(">>> Procesando pago NACIONAL via SEPA")
                .process(exchange -> {
                    PaymentRequest req = exchange.getIn().getBody(PaymentRequest.class);
                    var payment = processPaymentUseCase.process(
                            req.getSourceIban(), req.getTargetIban(),
                            req.getAmount(), req.getCurrency(), PaymentType.NACIONAL
                    );
                    exchange.getIn().setBody(payment);
                })
                .log(">>> Pago SEPA completado: ${body.id}");

        // Ruta SWIFT — pagos internacionales
        from("direct:swift")
                .routeId("swift-route")
                .log(">>> Procesando pago INTERNACIONAL via SWIFT")
                .process(exchange -> {
                    PaymentRequest req = exchange.getIn().getBody(PaymentRequest.class);
                    var payment = processPaymentUseCase.process(
                            req.getSourceIban(), req.getTargetIban(),
                            req.getAmount(), req.getCurrency(), PaymentType.INTERNACIONAL
                    );
                    exchange.getIn().setBody(payment);
                })
                .log(">>> Pago SWIFT completado: ${body.id}");

        // Ruta Express — pagos urgentes
        from("direct:express")
                .routeId("express-route")
                .log(">>> Procesando pago URGENTE via EXPRESS")
                .process(exchange -> {
                    PaymentRequest req = exchange.getIn().getBody(PaymentRequest.class);
                    var payment = processPaymentUseCase.process(
                            req.getSourceIban(), req.getTargetIban(),
                            req.getAmount(), req.getCurrency(), PaymentType.URGENTE
                    );
                    exchange.getIn().setBody(payment);
                })
                .log(">>> Pago EXPRESS completado: ${body.id}");

        // Ruta ActiveMQ — escucha mensajes de la cola
        from("activemq:queue:payments")
                .routeId("activemq-route")
                .log("Mensaje recibido desde cola ActiveMQ: ${body}")
                .unmarshal().json(PaymentRequest.class)
                .to("direct:payment");
    }
}