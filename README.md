# order-delivery-app

## **Kurulum**

### 1. **Proje kurulum**
```bash
git clone https://github.com/Goktug8810/order-delivery-case.git
cd order-delivery
docker-compose up -d
mvn clean install
mvn spring-boot:run
```

!! içerdeki docker-compose.yml dosyası 3 brokerlı yapıdadır, 
broker bağlantısında bir problem çıkması durumunda, tek brokerla calısacak bir yedek docker-compose.yml dosyası da proje içerisinde mevcut.

### 2. **Veritabanı Ayarları**
docker compose dosyası ile otomatik olarak başlatabilirsiniz. Veritabanı tabloları ve oluşacak örnek datalar, proje dizinindeki `docker/postgres/initDb.sql` dosyası ile otomatik olarak oluşturuluyor.
**`initDb.sql` İçeriği:**
```sql
CREATE TABLE orders (
    id BIGINT PRIMARY KEY,
    created_at TIMESTAMP,
    last_updated_at TIMESTAMP,
    collection_duration INTEGER,
    delivery_duration INTEGER,
    eta INTEGER,
    lead_time INTEGER,
    order_in_time BOOLEAN
);

--------------------------------------------------------------------------------
-- Örnek 1: Sipariş zamanında teslim edilmiş (orderInTime = true)
--   Lead Time: 35 dk, ETA: 40 dk  (35 <= 40)
--------------------------------------------------------------------------------
INSERT INTO "orders"
(created_at, last_updated_at, collection_started_at, collected_at, delivery_started_at, delivered_at, eta, customer_id)
VALUES
    (NOW(), NOW(),
     NOW() + INTERVAL '2 minutes',   -- collection_started_at
     NOW() + INTERVAL '5 minutes',   -- collected_at
     NOW() + INTERVAL '10 minutes',  -- delivery_started_at
     NOW() + INTERVAL '35 minutes',  -- delivered_at
     40, 1001);

--------------------------------------------------------------------------------
-- Örnek 2: Sipariş geç teslim edilmiş (orderInTime = false)
--   Lead Time: 50 dk, ETA: 40 dk  (50 > 40)
--------------------------------------------------------------------------------
INSERT INTO "orders"
(created_at, last_updated_at, collection_started_at, collected_at, delivery_started_at, delivered_at, eta, customer_id)
VALUES
    (NOW(), NOW(),
     NOW() + INTERVAL '3 minutes',   -- collection_started_at
     NOW() + INTERVAL '8 minutes',   -- collected_at
     NOW() + INTERVAL '15 minutes',  -- delivery_started_at
     NOW() + INTERVAL '50 minutes',  -- delivered_at
     40, 1002);

--------------------------------------------------------------------------------
-- Örnek 3: Sipariş tam sınırda zamanında teslim edilmiş (orderInTime = true)
--   Lead Time: 30 dk, ETA: 30 dk  (30 <= 30)
--------------------------------------------------------------------------------
INSERT INTO "orders"
(created_at, last_updated_at, collection_started_at, collected_at, delivery_started_at, delivered_at, eta, customer_id)
VALUES
    (NOW(), NOW(),
     NOW() + INTERVAL '1 minute',    -- collection_started_at
     NOW() + INTERVAL '2 minutes',    -- collected_at
     NOW() + INTERVAL '5 minutes',    -- delivery_started_at
     NOW() + INTERVAL '30 minutes',   -- delivered_at
     30, 1003);

--------------------------------------------------------------------------------
-- Örnek 4: Sipariş teslim edilmiş ancak toplama (collection) bilgileri eksik (orderInTime = true)
--   Collection alanları NULL, ancak teslim süresi yine de 20 dk (ETA 25 dk)
--------------------------------------------------------------------------------
INSERT INTO "orders"
(created_at, last_updated_at, collection_started_at, collected_at, delivery_started_at, delivered_at, eta, customer_id)
VALUES
    (NOW(), NOW(),
     NULL,                           -- collection_started_at
     NULL,                           -- collected_at
     NOW() + INTERVAL '5 minutes',   -- delivery_started_at
     NOW() + INTERVAL '20 minutes',  -- delivered_at
     25, 1004);

--------------------------------------------------------------------------------
-- Örnek 5: Sipariş teslim edilmiş ancak teslimat başlangıç zamanı eksik (orderInTime = true)
--   Delivery Duration NULL olacak, ancak Lead Time: 45 dk, ETA: 50 dk
--------------------------------------------------------------------------------
INSERT INTO "orders"
(created_at, last_updated_at, collection_started_at, collected_at, delivery_started_at, delivered_at, eta, customer_id)
VALUES
    (NOW(), NOW(),
     NOW() + INTERVAL '4 minutes',   -- collection_started_at
     NOW() + INTERVAL '7 minutes',   -- collected_at
     NULL,                           -- delivery_started_at
     NOW() + INTERVAL '45 minutes',  -- delivered_at
     50, 1005);

--------------------------------------------------------------------------------
-- Örnek 6: Sipariş eksik bilgilerle teslim edilmiş (orderInTime = false)
--   Lead Time: 100 dk, ETA: 80 dk (100 > 80); Collection alanları NULL
--------------------------------------------------------------------------------
INSERT INTO "orders"
(created_at, last_updated_at, collection_started_at, collected_at, delivery_started_at, delivered_at, eta, customer_id)
VALUES
    (NOW(), NOW(),
     NULL,                           -- collection_started_at
     NULL,                           -- collected_at
     NOW() + INTERVAL '20 minutes',  -- delivery_started_at
     NOW() + INTERVAL '100 minutes', -- delivered_at
     80, 1006);


```


### 3. **API Test**
```
GET http://localhost:8080/orders/process/{date}
```
Örnek:
```
GET http://localhost:8080/orders/process/2025-02-09 
```

---

## **Project Structure**
```
├── docker-compose.yml
├── docker
│   └── postgres
│       └── initDb.sql
├── src
│   └── main
│       └── java
│           └── com.goktug.order_delivery
│               ├── config
│               ├── controller
│               ├── dto
│               ├── entity
│               ├── exception
│               ├── repository
│               └── service
├── .mvn
├── Dockerfile
└── README.md
```

---

## **DeliveredOrder Nesnesi**
Bu nesne, **orders** tablosundan dönüştürülerek Kafka’ya gönderilen veri yapısını temsil eder.

| **Field**                               | **Type**          | **Description**                                         |
|-----------------------------------------|-------------------|---------------------------------------------------------|
| orderId                                 | BIGINT            | Sipariş ID'si                                           |
| createdAt                               | STRING (Timestamp)| Siparişin oluşturulma tarihi                            |
| lastUpdatedAt                           | STRING (Timestamp)| Siparişin son güncellenme tarihi                        |
| collectionDuration                      | INTEGER           | Siparişin toplama süreci                                |
| deliveryDuration                        | INTEGER           | Teslimatın gerçekleşme süreci                           |
| eta                                     | INTEGER           | Tahmini varış süreci                                    |
| leadTime                                | INTEGER           | Gerçekleşen varış süreci                                |
| orderInTime                             | BOOLEAN           | Siparişin tahmin edilen sürede teslim edilip edilmediği |
| event_timestamp (ekstra olarak eklendi) | TIMESTAMP         | Siparişe dair olayın oluşma zamanı                      |

---

## **Kafka Mesajları**
Mesajlar, **order_delivery_statistics** topic’ine **DeliveredOrder** yapısında gönderilecektir. Kafka UI üzerinden gönderilen mesajları izlemek için:
```
http://localhost:8090
```
---
