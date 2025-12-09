-- =====================================================
-- Script SQL complementario para Backend Plant Buddy
-- Tablas adicionales para usuarios, plantel y compras
-- =====================================================

-- Usar el esquema catalogo
SET search_path TO catalogo;

-- =====================================================
-- TABLA: usuarios
-- =====================================================
CREATE TABLE IF NOT EXISTS catalogo.usuarios (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    profile_image_url TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- TABLA: plantel_plants
-- =====================================================
CREATE TABLE IF NOT EXISTS catalogo.plantel_plants (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES catalogo.usuarios(id) ON DELETE CASCADE,
    product_id INTEGER NOT NULL REFERENCES catalogo.productos(id) ON DELETE CASCADE,
    plant_name VARCHAR(255) NOT NULL,
    plant_description TEXT,
    plant_image_url TEXT,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_watered_date TIMESTAMP,
    watering_frequency_days INTEGER DEFAULT 7,
    notes TEXT,
    notifications_enabled BOOLEAN DEFAULT TRUE,
    custom_title VARCHAR(255),
    UNIQUE(user_id, product_id)
);

-- =====================================================
-- TABLA: compras
-- =====================================================
CREATE TABLE IF NOT EXISTS catalogo.compras (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES catalogo.usuarios(id) ON DELETE CASCADE,
    total DECIMAL(10, 2) NOT NULL,
    shipping_address TEXT NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_total_positivo CHECK (total >= 0)
);

-- =====================================================
-- TABLA: detalles_compra
-- =====================================================
CREATE TABLE IF NOT EXISTS catalogo.detalles_compra (
    id SERIAL PRIMARY KEY,
    compra_id INTEGER NOT NULL REFERENCES catalogo.compras(id) ON DELETE CASCADE,
    producto_id INTEGER NOT NULL REFERENCES catalogo.productos(id),
    cantidad INTEGER NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    CONSTRAINT check_cantidad_positiva CHECK (cantidad > 0),
    CONSTRAINT check_precio_positivo CHECK (precio_unitario >= 0),
    CONSTRAINT check_subtotal_positivo CHECK (subtotal >= 0)
);

-- =====================================================
-- ÍNDICES PARA MEJORAR PERFORMANCE
-- =====================================================
CREATE INDEX IF NOT EXISTS idx_usuarios_email ON catalogo.usuarios(email);
CREATE INDEX IF NOT EXISTS idx_usuarios_username ON catalogo.usuarios(username);
CREATE INDEX IF NOT EXISTS idx_plantel_user ON catalogo.plantel_plants(user_id);
CREATE INDEX IF NOT EXISTS idx_plantel_product ON catalogo.plantel_plants(product_id);
CREATE INDEX IF NOT EXISTS idx_compras_user ON catalogo.compras(user_id);
CREATE INDEX IF NOT EXISTS idx_compras_status ON catalogo.compras(status);
CREATE INDEX IF NOT EXISTS idx_detalles_compra ON catalogo.detalles_compra(compra_id);

-- =====================================================
-- DATOS DE PRUEBA (OPCIONAL)
-- =====================================================

-- Insertar usuario de prueba (contraseña: "password123" encriptada con BCrypt)
INSERT INTO catalogo.usuarios (username, email, password) 
VALUES (
    'testuser',
    'test@plantbuddy.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'
)
ON CONFLICT (email) DO NOTHING;

-- =====================================================
-- VISTAS ÚTILES
-- =====================================================

-- Vista de plantel completo con información del producto
CREATE OR REPLACE VIEW catalogo.v_plantel_completo AS
SELECT 
    pp.id,
    pp.user_id,
    pp.product_id,
    pp.plant_name,
    pp.plant_description,
    pp.plant_image_url,
    pp.added_at,
    pp.last_watered_date,
    pp.watering_frequency_days,
    pp.notes,
    pp.notifications_enabled,
    pp.custom_title,
    p.nombre as producto_nombre,
    p.precio as producto_precio,
    p.rating as producto_rating,
    pd.riego_frecuencia,
    pd.luz_requerida,
    pd.cuidados
FROM catalogo.plantel_plants pp
INNER JOIN catalogo.productos p ON pp.product_id = p.id
LEFT JOIN catalogo.plantas_detalle pd ON p.id = pd.producto_id;

-- Vista de compras con totales
CREATE OR REPLACE VIEW catalogo.v_compras_resumen AS
SELECT 
    c.id,
    c.user_id,
    u.username,
    u.email,
    c.total,
    c.shipping_address,
    c.payment_method,
    c.status,
    c.created_at,
    COUNT(dc.id) as total_items,
    SUM(dc.cantidad) as total_productos
FROM catalogo.compras c
INNER JOIN catalogo.usuarios u ON c.user_id = u.id
LEFT JOIN catalogo.detalles_compra dc ON c.id = dc.compra_id
GROUP BY c.id, c.user_id, u.username, u.email, c.total, c.shipping_address, 
         c.payment_method, c.status, c.created_at;

-- =====================================================
-- FUNCIONES ÚTILES
-- =====================================================

-- Función para obtener el próximo riego de una planta
CREATE OR REPLACE FUNCTION catalogo.calcular_proximo_riego(
    p_plantel_id INTEGER
)
RETURNS TIMESTAMP AS $$
DECLARE
    v_last_watered TIMESTAMP;
    v_frequency INTEGER;
    v_next_watering TIMESTAMP;
BEGIN
    SELECT last_watered_date, watering_frequency_days
    INTO v_last_watered, v_frequency
    FROM catalogo.plantel_plants
    WHERE id = p_plantel_id;
    
    IF v_last_watered IS NULL THEN
        RETURN NULL;
    END IF;
    
    v_next_watering := v_last_watered + (v_frequency || ' days')::INTERVAL;
    
    RETURN v_next_watering;
END;
$$ LANGUAGE plpgsql;

-- Función para obtener plantas que necesitan riego
CREATE OR REPLACE FUNCTION catalogo.plantas_necesitan_riego(
    p_user_id INTEGER
)
RETURNS TABLE (
    plantel_id INTEGER,
    plant_name VARCHAR,
    last_watered_date TIMESTAMP,
    days_since_watered INTEGER,
    watering_frequency_days INTEGER
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        pp.id,
        pp.plant_name,
        pp.last_watered_date,
        EXTRACT(DAY FROM (NOW() - pp.last_watered_date))::INTEGER as days_since_watered,
        pp.watering_frequency_days
    FROM catalogo.plantel_plants pp
    WHERE pp.user_id = p_user_id
    AND pp.last_watered_date IS NOT NULL
    AND EXTRACT(DAY FROM (NOW() - pp.last_watered_date)) >= pp.watering_frequency_days
    ORDER BY days_since_watered DESC;
END;
$$ LANGUAGE plpgsql;

-- =====================================================
-- CONSULTAS DE VERIFICACIÓN
-- =====================================================

-- Ver todos los usuarios
-- SELECT * FROM catalogo.usuarios;

-- Ver plantel de un usuario
-- SELECT * FROM catalogo.v_plantel_completo WHERE user_id = 1;

-- Ver compras de un usuario
-- SELECT * FROM catalogo.v_compras_resumen WHERE user_id = 1;

-- Ver plantas que necesitan riego
-- SELECT * FROM catalogo.plantas_necesitan_riego(1);

-- =====================================================
-- SCRIPT COMPLETADO
-- =====================================================
