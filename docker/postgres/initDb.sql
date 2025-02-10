DROP TABLE IF EXISTS "orders";

CREATE TABLE "orders" (
  id SERIAL PRIMARY KEY,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  last_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  collection_started_at TIMESTAMP NULL,
  collected_at TIMESTAMP NULL,
  delivery_started_at TIMESTAMP NULL,
  delivered_at TIMESTAMP NULL,
  eta INT,
  customer_id BIGINT
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
