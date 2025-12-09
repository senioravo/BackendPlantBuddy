# API de Compras - Backend PlantBuddy

## Endpoints Disponibles

### 1. Finalizar Compra (PUT)
**Endpoint**: `PUT /api/compras/finalizar`

Este es el endpoint principal para finalizar el flujo de compra desde la aplicación Android. Guarda la compra completa con todos sus detalles en la base de datos.

#### Request Body:
```json
{
  "userId": 1,
  "shippingAddress": "Av. Los Carrera 123, Santiago",
  "paymentMethod": "CREDIT_CARD",
  "items": [
    {
      "productoId": 1,
      "cantidad": 2
    },
    {
      "productoId": 3,
      "cantidad": 1
    }
  ]
}
```

#### Métodos de Pago Disponibles:
- `CREDIT_CARD`
- `DEBIT_CARD`
- `PAYPAL`
- `BANK_TRANSFER`
- `CASH_ON_DELIVERY`

#### Response (200 OK):
```json
{
  "id": 15,
  "userId": 1,
  "total": 45990.00,
  "shippingAddress": "Av. Los Carrera 123, Santiago",
  "paymentMethod": "CREDIT_CARD",
  "status": "PENDING",
  "createdAt": "2025-12-09T15:30:00",
  "detalles": [
    {
      "productoId": 1,
      "productoNombre": "Viburnum Lucidum",
      "cantidad": 2,
      "precioUnitario": 14990.00,
      "subtotal": 29980.00
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
```

#### Response (400 Bad Request):
```json
{
  "error": "Stock insuficiente para: Viburnum Lucidum"
}
```

---

### 2. Crear Compra (POST)
**Endpoint**: `POST /api/compras/crear`

Alternativa al endpoint PUT. Funciona exactamente igual.

---

### 3. Obtener Compras del Usuario
**Endpoint**: `GET /api/compras/usuario/{userId}`

Obtiene todas las compras realizadas por un usuario, ordenadas por fecha (más reciente primero).

#### Response:
```json
[
  {
    "id": 15,
    "userId": 1,
    "total": 45990.00,
    "shippingAddress": "Av. Los Carrera 123, Santiago",
    "paymentMethod": "CREDIT_CARD",
    "status": "PENDING",
    "createdAt": "2025-12-09T15:30:00",
    "detalles": [...]
  },
  {
    "id": 14,
    "userId": 1,
    "total": 32990.00,
    "shippingAddress": "Calle Falsa 456, Valparaíso",
    "paymentMethod": "DEBIT_CARD",
    "status": "COMPLETED",
    "createdAt": "2025-12-08T10:15:00",
    "detalles": [...]
  }
]
```

---

### 4. Obtener Detalle de una Compra
**Endpoint**: `GET /api/compras/{compraId}`

Obtiene el detalle completo de una compra específica.

---

### 5. Actualizar Estado de Compra
**Endpoint**: `PUT /api/compras/{compraId}/estado`

Cambia el estado de una compra.

#### Request Body:
```json
{
  "estado": "COMPLETED"
}
```

#### Estados Disponibles:
- `PENDING` - Pendiente
- `PROCESSING` - En proceso
- `SHIPPED` - Enviado
- `DELIVERED` - Entregado
- `COMPLETED` - Completado
- `CANCELLED` - Cancelado

---

### 6. Cancelar Compra
**Endpoint**: `PUT /api/compras/{compraId}/cancelar`

Cancela una compra y restaura el stock de los productos.

**Nota**: No se pueden cancelar compras con estado `COMPLETED`.

---

## Estructura de Base de Datos

### Tabla: `compras`
```sql
CREATE TABLE catalogo.compras (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES catalogo.usuarios(id),
    total NUMERIC(10, 2) NOT NULL,
    shipping_address VARCHAR(255) NOT NULL,
    payment_method VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### Tabla: `detalles_compra`
```sql
CREATE TABLE catalogo.detalles_compra (
    id SERIAL PRIMARY KEY,
    compra_id INTEGER NOT NULL REFERENCES catalogo.compras(id),
    producto_id INTEGER NOT NULL REFERENCES catalogo.productos(id),
    cantidad INTEGER NOT NULL,
    precio_unitario NUMERIC(10, 2) NOT NULL,
    subtotal NUMERIC(10, 2) NOT NULL
);
```

---

## Flujo de Compra en Android

### 1. Usuario agrega productos al carrito
```kotlin
// En el CartViewModel o CartRepository
val cartItems = listOf(
    CartItem(productId = 1, cantidad = 2),
    CartItem(productId = 3, cantidad = 1)
)
```

### 2. Usuario procede al checkout
```kotlin
// En CheckoutScreen o CheckoutViewModel
val purchaseRequest = CreateCompraRequest(
    userId = currentUserId,
    shippingAddress = "Av. Los Carrera 123, Santiago",
    paymentMethod = "CREDIT_CARD",
    items = cartItems
)
```

### 3. Finalizar compra
```kotlin
// Llamada al endpoint PUT
val response = apiService.finalizarCompra(purchaseRequest)

if (response.isSuccessful) {
    // Compra exitosa
    val compra = response.body()
    // Limpiar carrito
    // Mostrar confirmación
    // Navegar a pantalla de confirmación
} else {
    // Error en la compra
    val error = response.errorBody()?.string()
    // Mostrar mensaje de error
}
```

---

## Ejemplo Completo con Retrofit (Android)

### Interface API
```kotlin
interface CompraService {
    @PUT("compras/finalizar")
    suspend fun finalizarCompra(
        @Body request: CreateCompraRequest
    ): Response<CompraDTO>
    
    @GET("compras/usuario/{userId}")
    suspend fun obtenerComprasUsuario(
        @Path("userId") userId: Int
    ): Response<List<CompraDTO>>
    
    @GET("compras/{compraId}")
    suspend fun obtenerCompraDetalle(
        @Path("compraId") compraId: Int
    ): Response<CompraDTO>
}
```

### Data Classes
```kotlin
data class CreateCompraRequest(
    val userId: Int,
    val shippingAddress: String,
    val paymentMethod: String,
    val items: List<CartItemRequest>
)

data class CartItemRequest(
    val productoId: Int,
    val cantidad: Int
)

data class CompraDTO(
    val id: Int,
    val userId: Int,
    val total: Double,
    val shippingAddress: String,
    val paymentMethod: String,
    val status: String,
    val createdAt: String,
    val detalles: List<DetalleCompraDTO>
)

data class DetalleCompraDTO(
    val productoId: Int,
    val productoNombre: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double
)
```

---

## Validaciones Implementadas

1. **Usuario existe**: Verifica que el userId corresponda a un usuario registrado
2. **Productos existen**: Valida que todos los productos en el carrito existan
3. **Stock disponible**: Verifica que hay suficiente stock para cada producto
4. **Cantidad mínima**: Cada item debe tener cantidad >= 1
5. **Dirección requerida**: La dirección de envío no puede estar vacía
6. **Método de pago válido**: Debe ser uno de los métodos permitidos

---

## Funcionalidades Adicionales

### Actualización Automática de Stock
Cuando se crea una compra, el stock de cada producto se reduce automáticamente.

### Restauración de Stock
Cuando se cancela una compra, el stock se restaura automáticamente.

### Cálculo Automático de Total
El total de la compra se calcula automáticamente sumando todos los subtotales.

### Timestamp Automático
La fecha de creación se registra automáticamente con `@CreationTimestamp`.

---

## Errores Comunes

### "Usuario no encontrado"
- Verifica que el userId sea válido
- Asegúrate de que el usuario esté registrado

### "Producto no encontrado"
- Verifica que el productoId sea válido
- El producto debe existir en la base de datos

### "Stock insuficiente"
- La cantidad solicitada excede el stock disponible
- Actualiza la cantidad o espera a que se reponga el stock

### "No se puede cancelar una compra completada"
- Las compras completadas no pueden cancelarse
- Solo las compras PENDING, PROCESSING o SHIPPED pueden cancelarse

---

## Testing con cURL

### Finalizar Compra
```bash
curl -X PUT http://localhost:8080/api/compras/finalizar \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "shippingAddress": "Av. Los Carrera 123, Santiago",
    "paymentMethod": "CREDIT_CARD",
    "items": [
      {"productoId": 1, "cantidad": 2},
      {"productoId": 3, "cantidad": 1}
    ]
  }'
```

### Obtener Compras de Usuario
```bash
curl -X GET http://localhost:8080/api/compras/usuario/1
```

### Obtener Detalle de Compra
```bash
curl -X GET http://localhost:8080/api/compras/15
```

### Cancelar Compra
```bash
curl -X PUT http://localhost:8080/api/compras/15/cancelar
```
