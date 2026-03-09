# 💳 Payment Processor

Microservicio bancario de procesamiento de pagos construido con **Spring Boot**, **Apache Camel** y **Oracle Database**, con mensajería asíncrona via **ActiveMQ**.

---

## 📋 Índice

- [Descripción](#descripción)
- [Stack tecnológico](#stack-tecnológico)
- [Arquitectura](#arquitectura)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Cómo arrancar](#cómo-arrancar)
- [API REST](#api-rest)
- [Flujo interno](#flujo-interno)
- [Mejoras futuras](#mejoras-futuras)

---

## 📖 Descripción

Payment Processor es un microservicio bancario que gestiona el enrutamiento inteligente de pagos según su tipo. Utiliza el patrón **Content Based Router** de Apache Camel para dirigir cada pago al procesador correcto: SEPA para pagos nacionales, SWIFT para internacionales y EXPRESS para urgentes.

Los pagos pueden entrar por dos vías: directamente via REST o de forma asíncrona a través de una cola **ActiveMQ**, simulando un entorno bancario real donde los sistemas se comunican mediante mensajería.

---

## 🛠️ Stack tecnológico

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 25 | Lenguaje |
| Spring Boot | 4.0.3 | Framework principal |
| Apache Camel | 4.8.0 | Orquestación y enrutamiento |
| ActiveMQ | Latest | Cola de mensajes asíncrona |
| Oracle XE | 21c | Base de datos |
| Docker | - | Oracle y ActiveMQ en local |
| Hibernate | 7.2.4 | ORM |
| Maven | - | Gestión de dependencias |

---

## 🏗️ Arquitectura

### Patrón Content Based Router

El componente más importante del proyecto es el **Content Based Router** de Camel. Analiza cada pago entrante y lo dirige automáticamente al procesador correcto:
```
                    ┌─────────────────┐
                    │   REST / Queue  │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │  payment-entry  │
                    │     -route      │
                    └────────┬────────┘
                             │
              ┌──────────────┼──────────────┐
              │              │              │
     NACIONAL │   INTERNACIONAL│     URGENTE │
              │              │              │
    ┌─────────▼──┐  ┌────────▼───┐  ┌──────▼──────┐
    │    SEPA    │  │   SWIFT    │  │   EXPRESS   │
    │ PROCESSOR  │  │ PROCESSOR  │  │  PROCESSOR  │
    └─────────┬──┘  └────────┬───┘  └──────┬──────┘
              │              │              │
              └──────────────┼──────────────┘
                             │
                    ┌────────▼────────┐
                    │  Oracle Database│
                    └─────────────────┘
```

### Arquitectura Hexagonal
```
com/payment/
├── domain/                  # Núcleo — sin dependencias externas
│   ├── model/               # Entidades y value objects
│   ├── port/in/             # Casos de uso (interfaces)
│   ├── port/out/            # Repositorios (interfaces)
│   └── exception/           # Excepciones de dominio
├── application/             # Orquestación de casos de uso
│   └── usecase/
└── infrastructure/          # Detalles técnicos
    ├── adapter/in/camel/    # Rutas Camel + REST
    └── adapter/out/         # JPA + Oracle
```

---

## 📁 Estructura del proyecto
```
com/payment/
├── domain/
│   ├── model/
│   │   ├── Payment.java         # Entidad pago
│   │   ├── Money.java           # Value object importe
│   │   ├── PaymentType.java     # NACIONAL / INTERNACIONAL / URGENTE
│   │   └── PaymentStatus.java   # RECIBIDO / PROCESANDO / COMPLETADO / FALLIDO
│   ├── port/
│   │   ├── in/
│   │   │   ├── ProcessPaymentUseCase.java
│   │   │   └── GetPaymentUseCase.java
│   │   └── out/
│   │       └── PaymentRepository.java
│   └── exception/
│       ├── PaymentNotFoundException.java
│       └── InvalidPaymentException.java
├── application/
│   └── usecase/
│       ├── ProcessPaymentUseCaseImpl.java
│       └── GetPaymentUseCaseImpl.java
└── infrastructure/
    ├── adapter/
    │   ├── in/camel/
    │   │   ├── PaymentRoute.java       # Content Based Router
    │   │   ├── PaymentController.java  # Endpoints REST
    │   │   └── PaymentRequest.java     # DTO entrada
    │   └── out/persistence/
    │       ├── PaymentEntity.java
    │       ├── PaymentJpaRepository.java
    │       └── PaymentRepositoryAdapter.java
    └── config/
```

---

## 🚀 Cómo arrancar

### Requisitos previos

- Java 25
- Maven
- Docker Desktop

### 1. Levantar Oracle con Docker
```powershell
docker run -d --name oracle-xe -p 1521:1521 -e ORACLE_PASSWORD=Password123 gvenzl/oracle-xe:21-slim
```

Espera hasta ver:
```powershell
docker logs -f oracle-xe
# DATABASE IS READY TO USE!
```

### 2. Levantar ActiveMQ con Docker
```powershell
docker run -d --name activemq -p 61616:61616 -p 8161:8161 apache/activemq-classic:latest
```

Consola web ActiveMQ: `http://localhost:8161` (admin/admin)

### 3. Arrancar la aplicación
```powershell
mvn spring-boot:run
```

---

## 🔌 API REST

### POST /api/payments — Pago directo via REST
```json
{
    "sourceIban": "ES1234567890",
    "targetIban": "ES0987654321",
    "amount": 500.00,
    "currency": "EUR",
    "type": "NACIONAL"
}
```

Tipos disponibles: `NACIONAL`, `INTERNACIONAL`, `URGENTE`

### POST /api/payments/queue — Pago via cola ActiveMQ
```powershell
$body = '{"sourceIban":"ES1234567890","targetIban":"ES0987654321","amount":750.00,"currency":"EUR","type":"NACIONAL"}'
Invoke-WebRequest -Uri "http://localhost:8080/api/payments/queue" -Method POST -ContentType "application/json" -Body $body -UseBasicParsing
```

### GET /api/payments — Consultar todos los pagos
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/payments" -Method GET -UseBasicParsing
```

### GET /api/payments/{id} — Consultar pago por ID
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/payments/{id}" -Method GET -UseBasicParsing
```

---

## 🔄 Flujo interno
```
1. PaymentController     Recibe la petición HTTP
2. payment-entry-route   Camel lee el tipo de pago
3. Content Based Router  Enruta según NACIONAL/INTERNACIONAL/URGENTE
4. sepa/swift/express    Ruta específica procesa el pago
5. ProcessPaymentUseCase Aplica lógica de negocio
6. PaymentRepository     Persiste en Oracle
7. Respuesta HTTP        Devuelve el pago procesado
```

### Flujo asíncrono via ActiveMQ
```
1. POST /api/payments/queue   Envía JSON a la cola
2. activemq-route             Camel escucha la cola
3. payment-entry-route        Mismo flujo que REST
```

---

## 🔮 Mejoras futuras

- **Frontend** — Dashboard visual con métricas y formulario de pagos
- **Seguridad** — JWT/OAuth2
- **Transacciones** — `@Transactional` para atomicidad
- **Dead Letter Queue** — Cola de mensajes fallidos en ActiveMQ
- **Reintentos** — Política de reintentos automáticos con Camel
- **Tests** — Unitarios y de integración con Camel Test Kit
- **Métricas** — Micrometer + Prometheus + Grafana
