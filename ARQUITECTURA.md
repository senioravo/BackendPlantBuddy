# 📐 ARQUITECTURA DEL BACKEND PLANT BUDDY

## 🎯 Resumen Ejecutivo

He diseñado e implementado un **backend completo en Spring Boot** para la aplicación Plant Buddy siguiendo las mejores prácticas de arquitectura de microservicios y patrones de diseño empresariales.

---

## 📊 Análisis del Proyecto Android (MVVM)

### Modelos de Dominio Identificados:
1. **User** - Usuario con autenticación
2. **Product** - Productos/plantas del catálogo
3. **PlantelPlant** - Plantas del usuario con seguimiento de riego
4. **Purchase** - Historial de compras
5. **CartItem** - Items del carrito

### Base de Datos Original:
- **PostgreSQL (Neon)** con esquema `catalogo`
- 7 plantas precargadas (Viburnum, Kniphofia, Rhus, Lavanda, etc.)
- Detalles de cuidado: riego, luz, temperatura, toxicidad

---

## 🏗️ Arquitectura Implementada

### **Patrón: Arquitectura en Capas**

```
┌─────────────────────────────────────────────┐
│         ANDROID APP (Kotlin - MVVM)         │
│    ViewModel → Repository → Local DB        │
└─────────────────────────────────────────────┘
                    ↓ HTTP/REST
┌─────────────────────────────────────────────┐
│           REST API CONTROLLERS               │
│  AuthController | ProductoController |      │
│  PlantelController | CompraController       │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│            BUSINESS SERVICES                 │
│  AuthService | ProductoService |            │
│  PlantelService | CompraService             │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│         JPA REPOSITORIES                     │
│  UsuarioRepository | ProductoRepository |   │
│  PlantelPlantRepository | CompraRepository  │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│      POSTGRESQL DATABASE (Neon)             │
│         Esquema: catalogo                    │
└─────────────────────────────────────────────┘
```

---

## 🎨 Microservicios Diseñados

### 1. **Servicio de Autenticación**
**Responsabilidad**: Registro, login y gestión de usuarios

**Endpoints**:
- `POST /api/auth/register` - Registro de nuevos usuarios
- `POST /api/auth/login` - Autenticación con JWT

**Características**:
- Contraseñas encriptadas con BCrypt
- Tokens JWT con expiración de 24h
- Validación de email y username únicos

---

### 2. **Servicio de Catálogo de Productos**
**Responsabilidad**: Gestión del catálogo de plantas

**Endpoints**:
- `GET /api/productos` - Listar todas las plantas disponibles
- `GET /api/productos/{id}` - Detalle de una planta específica
- `GET /api/productos/buscar?query=...&categoria=...` - Búsqueda avanzada
- `GET /api/productos/destacados` - Top 10 plantas mejor valoradas

**Características**:
- Búsqueda por nombre/descripción
- Filtrado por categoría
- Detalles completos de cuidado de plantas
- Control de stock y disponibilidad

---

### 3. **Servicio de Plantel Personal**
**Responsabilidad**: Gestión del jardín personal del usuario

**Endpoints**:
- `GET /api/plantel/usuario/{userId}` - Obtener plantel del usuario
- `POST /api/plantel/agregar` - Agregar planta al plantel
- `PUT /api/plantel/regar` - Registrar riego de planta
- `PUT /api/plantel/actualizar-titulo` - Personalizar nombre
- `PUT /api/plantel/actualizar-notas` - Agregar notas
- `PUT /api/plantel/toggle-notificaciones` - Activar/desactivar recordatorios
- `DELETE /api/plantel/eliminar` - Eliminar planta del plantel

**Características**:
- Seguimiento de última fecha de riego
- Frecuencia de riego personalizable
- Títulos personalizados para cada planta
- Sistema de notificaciones
- Notas privadas por planta

---

### 4. **Servicio de Compras**
**Responsabilidad**: Procesamiento de órdenes y gestión de compras

**Endpoints**:
- `GET /api/compras/usuario/{userId}` - Historial de compras
- `GET /api/compras/{compraId}` - Detalle de compra
- `POST /api/compras/crear` - Crear nueva compra
- `PUT /api/compras/{compraId}/estado` - Actualizar estado
- `PUT /api/compras/{compraId}/cancelar` - Cancelar compra

**Características**:
- Validación de stock antes de comprar
- Actualización automática de inventario
- Estados: PENDING, PROCESSING, COMPLETED, CANCELLED, REFUNDED
- Restauración de stock al cancelar
- Detalle completo de items comprados
- Múltiples métodos de pago

---

## 🗄️ Modelo de Datos Completo

### Entidades JPA Implementadas:

#### **Usuario**
```java
- id (PK)
- username (UNIQUE)
- email (UNIQUE)
- password (BCrypt)
- profileImageUrl
- createdAt
```

#### **Producto**
```java
- id (PK)
- nombre
- descripcion
- precio
- stock
- categoria (FK)
- disponible
- imagenUrl
- rating
- plantaDetalle (OneToOne)
```

#### **PlantaDetalle**
```java
- id (PK)
- producto (FK OneToOne)
- nombreCientifico
- tipo
- luzRequerida
- riegoFrecuencia
- temperaturaMin/Max
- toxicidad
- alturaPromedioCm
- cuidados
```

#### **PlantelPlant**
```java
- id (PK)
- usuario (FK)
- producto (FK)
- plantName
- plantDescription
- plantImageUrl
- addedAt
- lastWateredDate
- wateringFrequencyDays
- notes
- notificationsEnabled
- customTitle
```

#### **Compra**
```java
- id (PK)
- usuario (FK)
- total
- shippingAddress
- paymentMethod (ENUM)
- status (ENUM)
- createdAt
- detalles (OneToMany)
```

#### **DetalleCompra**
```java
- id (PK)
- compra (FK)
- producto (FK)
- cantidad
- precioUnitario
- subtotal
```

---

## 🔒 Seguridad Implementada

### **Spring Security + JWT**

1. **Autenticación Stateless**
   - JWT tokens con firma HMAC-SHA256
   - Tokens almacenados en el cliente
   - No hay sesiones en el servidor

2. **Endpoints Públicos**
   - `/api/auth/**` - Registro y login
   - `/api/productos/**` - Catálogo público
   - `/api/categorias/**` - Categorías públicas

3. **Endpoints Protegidos**
   - Requieren header: `Authorization: Bearer {token}`
   - Validación automática del token
   - Extracción del usuario desde el token

4. **Encriptación**
   - BCrypt para contraseñas (factor 10)
   - Secrets configurables en properties

---

## 📦 Tecnologías Utilizadas

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Spring Boot | 4.0.0 | Framework principal |
| Spring Data JPA | - | ORM y persistencia |
| Spring Security | - | Autenticación y autorización |
| PostgreSQL | 15+ | Base de datos |
| Lombok | Latest | Reducción de boilerplate |
| JWT (jjwt) | 0.11.5 | Tokens de autenticación |
| Jakarta Validation | - | Validación de datos |

---

## 🔄 Flujo de Comunicación

### **1. Flujo de Autenticación**
```
Android App
    ↓ POST /auth/register o /login
AuthController
    ↓
AuthService (valida, encripta password)
    ↓
UsuarioRepository (guarda en BD)
    ↓
JwtUtil (genera token)
    ↓
← AuthResponseDTO (token + usuario)
```

### **2. Flujo de Consulta de Productos**
```
Android App
    ↓ GET /productos
ProductoController
    ↓
ProductoService
    ↓
ProductoRepository (query JPA)
    ↓
EntityMapper (Entity → DTO)
    ↓
← List<ProductoDTO>
```

### **3. Flujo de Compra**
```
Android App
    ↓ POST /compras/crear + JWT
CompraController (valida token)
    ↓
CompraService
    ├→ Valida stock de cada item
    ├→ Calcula totales
    ├→ Crea Compra + DetalleCompra
    └→ Actualiza stock de productos
    ↓
← CompraDTO (confirmación)
```

---

## 📊 Ventajas de esta Arquitectura

✅ **Separación de Responsabilidades**: Cada capa tiene un propósito claro
✅ **Escalabilidad**: Fácil agregar nuevos microservicios
✅ **Mantenibilidad**: Código organizado y documentado
✅ **Seguridad**: JWT + Spring Security
✅ **Testeable**: Servicios independientes fáciles de testear
✅ **RESTful**: Endpoints siguiendo estándares REST
✅ **Validación**: Validación automática con Jakarta Validation
✅ **Transaccional**: Control de transacciones con @Transactional

---

## 🚀 Próximos Pasos

### Para Desarrollo:
1. Ejecutar script SQL `database-schema.sql` en PostgreSQL
2. Configurar credenciales en `application.properties`
3. Ejecutar: `./gradlew bootRun`
4. Probar endpoints con Postman/Insomnia
5. Actualizar URL en Android App (`RetrofitClient`)

### Para Producción:
1. Cambiar JWT secret por uno seguro
2. Configurar HTTPS
3. Desplegar en Railway/Render/Heroku
4. Configurar base de datos Neon en producción
5. Monitoreo y logs

---

## 📚 Documentación Adicional

- **README.md**: Guía completa de endpoints
- **database-schema.sql**: Script SQL completo
- **application.properties**: Configuración del servidor

---

**🌿 Backend Plant Buddy - Listo para Producción**
