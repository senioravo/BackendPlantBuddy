# 🧪 EJEMPLOS DE PRUEBA - API REST Plant Buddy

## 📋 Colección de Requests para Testing

---

## 1️⃣ AUTENTICACIÓN

### 1.1 Registro de Usuario

**cURL:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "juan_perez",
    "email": "juan@ejemplo.com",
    "password": "password123",
    "confirmPassword": "password123"
  }'
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "usuario": {
    "id": 1,
    "username": "juan_perez",
    "email": "juan@ejemplo.com",
    "profileImageUrl": null
  }
}
```

---

### 1.2 Login

**cURL:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "juan@ejemplo.com",
    "password": "password123"
  }'
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "usuario": {
    "id": 1,
    "username": "juan_perez",
    "email": "juan@ejemplo.com",
    "profileImageUrl": null
  }
}
```

---

## 2️⃣ PRODUCTOS

### 2.1 Obtener Todos los Productos

**cURL:**
```bash
curl -X GET http://localhost:8080/api/productos
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "nombre": "Viburnum Lucidum",
    "descripcion": "Arbusto perenne de hojas brillantes...",
    "precio": 24990.00,
    "stock": 10,
    "categoria": "Planta",
    "disponible": true,
    "imagenUrl": null,
    "rating": 4.8,
    "plantaDetalle": {
      "nombreCientifico": "Viburnum tinus 'Lucidum'",
      "tipo": "arbusto perenne",
      "luzRequerida": "media-alta",
      "riegoFrecuencia": "semanal",
      "temperaturaMin": -8.00,
      "temperaturaMax": 30.00,
      "toxicidad": false,
      "alturaPromedioCm": 250,
      "cuidados": "Ubicación al sol o sombra parcial..."
    }
  }
]
```

---

### 2.2 Obtener Producto por ID

**cURL:**
```bash
curl -X GET http://localhost:8080/api/productos/1
```

---

### 2.3 Buscar Productos

**Por Nombre:**
```bash
curl -X GET "http://localhost:8080/api/productos/buscar?query=lavanda"
```

**Por Categoría:**
```bash
curl -X GET "http://localhost:8080/api/productos/buscar?categoria=Planta"
```

**Por Nombre y Categoría:**
```bash
curl -X GET "http://localhost:8080/api/productos/buscar?query=arbusto&categoria=Planta"
```

---

### 2.4 Productos Destacados (Top 10)

**cURL:**
```bash
curl -X GET http://localhost:8080/api/productos/destacados
```

---

## 3️⃣ PLANTEL PERSONAL

### 3.1 Obtener Plantel del Usuario

**cURL:**
```bash
curl -X GET http://localhost:8080/api/plantel/usuario/1
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "userId": 1,
    "productId": 4,
    "plantName": "Lavanda Dentata",
    "plantDescription": "Lavanda francesa de hojas dentadas...",
    "plantImageUrl": null,
    "addedAt": "2025-12-08T10:30:00",
    "lastWateredDate": "2025-12-08T10:30:00",
    "wateringFrequencyDays": 7,
    "notes": "Comprada en el vivero local",
    "notificationsEnabled": true,
    "customTitle": "Mi Lavanda Favorita"
  }
]
```

---

### 3.2 Agregar Planta al Plantel

**cURL:**
```bash
curl -X POST http://localhost:8080/api/plantel/agregar \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "productoId": 4,
    "customTitle": "Mi Lavanda Favorita",
    "notes": "Comprada en el vivero local"
  }'
```

**Response (200 OK):**
```json
{
  "id": 1,
  "userId": 1,
  "productId": 4,
  "plantName": "Lavanda Dentata",
  "plantDescription": "Lavanda francesa...",
  "plantImageUrl": null,
  "addedAt": "2025-12-08T10:30:00",
  "lastWateredDate": null,
  "wateringFrequencyDays": 7,
  "notes": "Comprada en el vivero local",
  "notificationsEnabled": true,
  "customTitle": "Mi Lavanda Favorita"
}
```

---

### 3.3 Regar Planta

**cURL:**
```bash
curl -X PUT http://localhost:8080/api/plantel/regar \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "productoId": 4
  }'
```

**Response (200 OK):** Retorna el plantel actualizado con nueva `lastWateredDate`

---

### 3.4 Actualizar Título Personalizado

**cURL:**
```bash
curl -X PUT http://localhost:8080/api/plantel/actualizar-titulo \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "productoId": 4,
    "customTitle": "Lavanda del Jardín Delantero"
  }'
```

---

### 3.5 Actualizar Notas

**cURL:**
```bash
curl -X PUT http://localhost:8080/api/plantel/actualizar-notas \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "productoId": 4,
    "notes": "Necesita poda en primavera. Florece en verano."
  }'
```

---

### 3.6 Alternar Notificaciones

**cURL:**
```bash
curl -X PUT http://localhost:8080/api/plantel/toggle-notificaciones \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "productoId": 4
  }'
```

---

### 3.7 Eliminar Planta del Plantel

**cURL:**
```bash
curl -X DELETE http://localhost:8080/api/plantel/eliminar \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "productoId": 4
  }'
```

**Response (204 No Content)**

---

## 4️⃣ COMPRAS

### 4.1 Obtener Compras del Usuario

**cURL:**
```bash
curl -X GET http://localhost:8080/api/compras/usuario/1
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "userId": 1,
    "total": 67970.00,
    "shippingAddress": "Av. Providencia 1234, Santiago",
    "paymentMethod": "DEBIT_CARD",
    "status": "COMPLETED",
    "createdAt": "2025-12-08T11:00:00",
    "detalles": [
      {
        "productoId": 1,
        "productoNombre": "Viburnum Lucidum",
        "cantidad": 2,
        "precioUnitario": 24990.00,
        "subtotal": 49980.00
      },
      {
        "productoId": 3,
        "productoNombre": "Rhus Crenata",
        "cantidad": 1,
        "precioUnitario": 17990.00,
        "subtotal": 17990.00
      }
    ]
  }
]
```

---

### 4.2 Obtener Compra por ID

**cURL:**
```bash
curl -X GET http://localhost:8080/api/compras/1
```

---

### 4.3 Crear Compra

**cURL:**
```bash
curl -X POST http://localhost:8080/api/compras/crear \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "shippingAddress": "Av. Providencia 1234, Depto 501, Santiago",
    "paymentMethod": "CREDIT_CARD",
    "items": [
      {
        "productoId": 1,
        "cantidad": 2
      },
      {
        "productoId": 4,
        "cantidad": 1
      }
    ]
  }'
```

**Response (200 OK):**
```json
{
  "id": 2,
  "userId": 1,
  "total": 65970.00,
  "shippingAddress": "Av. Providencia 1234, Depto 501, Santiago",
  "paymentMethod": "CREDIT_CARD",
  "status": "PENDING",
  "createdAt": "2025-12-08T14:30:00",
  "detalles": [
    {
      "productoId": 1,
      "productoNombre": "Viburnum Lucidum",
      "cantidad": 2,
      "precioUnitario": 24990.00,
      "subtotal": 49980.00
    },
    {
      "productoId": 4,
      "productoNombre": "Lavanda Dentata",
      "cantidad": 1,
      "precioUnitario": 15990.00,
      "subtotal": 15990.00
    }
  ]
}
```

---

### 4.4 Actualizar Estado de Compra

**cURL:**
```bash
curl -X PUT http://localhost:8080/api/compras/2/estado \
  -H "Content-Type: application/json" \
  -d '{
    "estado": "PROCESSING"
  }'
```

**Estados Disponibles:**
- `PENDING`
- `PROCESSING`
- `COMPLETED`
- `CANCELLED`
- `REFUNDED`

---

### 4.5 Cancelar Compra

**cURL:**
```bash
curl -X PUT http://localhost:8080/api/compras/2/cancelar
```

**Response (200 OK):** Retorna la compra con estado `CANCELLED` y stock restaurado

---

## 5️⃣ CASOS DE ERROR

### Error 400: Bad Request

**Ejemplo: Contraseñas no coinciden en registro**
```json
{
  "timestamp": "2025-12-08T15:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Las contraseñas no coinciden"
}
```

---

### Error 401: Unauthorized

**Ejemplo: Credenciales inválidas**
```json
{
  "timestamp": "2025-12-08T15:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Email o contraseña incorrectos"
}
```

---

### Error 404: Not Found

**Ejemplo: Producto no existe**
```json
{
  "timestamp": "2025-12-08T15:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Producto no encontrado con ID: 999"
}
```

---

### Error 409: Conflict

**Ejemplo: Planta ya está en el plantel**
```json
{
  "timestamp": "2025-12-08T15:00:00",
  "status": 409,
  "error": "Conflict",
  "message": "La planta ya está en tu plantel"
}
```

---

## 6️⃣ POSTMAN COLLECTION

### Importar en Postman

1. Crear nueva Colección: "Plant Buddy API"
2. Agregar variable de entorno: `baseUrl = http://localhost:8080/api`
3. Crear carpetas:
   - Auth
   - Productos
   - Plantel
   - Compras

### Variables Globales

```json
{
  "baseUrl": "http://localhost:8080/api",
  "token": "",
  "userId": 1
}
```

### Request Template

**URL:** `{{baseUrl}}/productos`  
**Headers:** 
- `Content-Type: application/json`
- `Authorization: Bearer {{token}}` (para endpoints protegidos)

---

## 7️⃣ SCRIPTS DE PRUEBA AUTOMATIZADA

### Script Bash para Pruebas Completas

```bash
#!/bin/bash

BASE_URL="http://localhost:8080/api"
TOKEN=""

echo "=== PRUEBAS API PLANT BUDDY ==="

# 1. Registro
echo -e "\n1. Registrando usuario..."
RESPONSE=$(curl -s -X POST $BASE_URL/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test_user",
    "email": "test@ejemplo.com",
    "password": "test123",
    "confirmPassword": "test123"
  }')

TOKEN=$(echo $RESPONSE | jq -r '.token')
echo "Token obtenido: ${TOKEN:0:20}..."

# 2. Listar productos
echo -e "\n2. Obteniendo productos..."
curl -s -X GET $BASE_URL/productos | jq '.[] | {id, nombre, precio}'

# 3. Agregar al plantel
echo -e "\n3. Agregando planta al plantel..."
curl -s -X POST $BASE_URL/plantel/agregar \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "productoId": 4,
    "customTitle": "Test Plant",
    "notes": "Test"
  }' | jq '.'

# 4. Crear compra
echo -e "\n4. Creando compra..."
curl -s -X POST $BASE_URL/compras/crear \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "shippingAddress": "Test Address",
    "paymentMethod": "DEBIT_CARD",
    "items": [
      {"productoId": 1, "cantidad": 1}
    ]
  }' | jq '.'

echo -e "\n=== PRUEBAS COMPLETADAS ==="
```

---

## 8️⃣ HEALTHCHECK

### Verificar que el Backend está vivo

**cURL:**
```bash
curl -X GET http://localhost:8080/api/productos
```

Si devuelve una lista (aunque sea vacía), el backend está operativo.

---

**📝 Nota:** Reemplazar `localhost:8080` con tu URL de producción cuando despliegues.

**🎉 ¡Listo para testear!**
