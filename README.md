# Backend Plant Buddy - API REST

## 📋 Descripción

Backend desarrollado en Spring Boot para la aplicación móvil **Plant Buddy**. Proporciona una API REST completa para gestionar el catálogo de plantas, autenticación de usuarios, plantel personal y sistema de compras.

## 🏗️ Arquitectura

El proyecto sigue una arquitectura en capas basada en microservicios:

```
BackendPlantBuddy/
├── entity/          # Entidades JPA (mapeo con BD PostgreSQL)
├── dto/             # Data Transfer Objects
├── repository/      # Repositorios JPA
├── service/         # Lógica de negocio
├── controller/      # Controladores REST
├── security/        # Configuración JWT y seguridad
└── mapper/          # Mapeo entre entidades y DTOs
```

## 🗄️ Modelo de Datos

### Entidades Principales:

1. **Usuario** - Gestión de usuarios
2. **Producto** - Catálogo de plantas
3. **PlantaDetalle** - Detalles específicos de cuidado de plantas
4. **Categoria** - Categorías de productos
5. **PlantelPlant** - Plantas agregadas al plantel del usuario
6. **Compra** - Órdenes de compra
7. **DetalleCompra** - Detalle de items en cada compra

## 🔌 Endpoints API

### 🔐 Autenticación (`/api/auth`)

#### Registro
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "string",
  "email": "string",
  "password": "string",
  "confirmPassword": "string"
}

Response: 200 OK
{
  "token": "jwt-token",
  "type": "Bearer",
  "usuario": {
    "id": 1,
    "username": "string",
    "email": "string",
    "profileImageUrl": "string"
  }
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "string",
  "password": "string"
}

Response: 200 OK (mismo formato que register)
```

---

### 🌱 Productos (`/api/productos`)

#### Obtener todos los productos
```http
GET /api/productos

Response: 200 OK
[
  {
    "id": 1,
    "nombre": "Viburnum Lucidum",
    "descripcion": "string",
    "precio": 24990,
    "stock": 10,
    "categoria": "Planta",
    "disponible": true,
    "imagenUrl": "string",
    "rating": 4.8,
    "plantaDetalle": {
      "nombreCientifico": "Viburnum tinus 'Lucidum'",
      "tipo": "arbusto perenne",
      "luzRequerida": "media-alta",
      "riegoFrecuencia": "semanal",
      "temperaturaMin": -8.0,
      "temperaturaMax": 30.0,
      "toxicidad": false,
      "alturaPromedioCm": 250,
      "cuidados": "string"
    }
  }
]
```

#### Obtener producto por ID
```http
GET /api/productos/{id}
```

#### Buscar productos
```http
GET /api/productos/buscar?query={texto}&categoria={categoria}
```

#### Obtener productos destacados
```http
GET /api/productos/destacados
```

---

### 🏡 Plantel (`/api/plantel`)

#### Obtener plantel del usuario
```http
GET /api/plantel/usuario/{userId}

Response: 200 OK
[
  {
    "id": 1,
    "userId": 1,
    "productId": 5,
    "plantName": "string",
    "plantDescription": "string",
    "plantImageUrl": "string",
    "addedAt": "2025-12-08T10:00:00",
    "lastWateredDate": "2025-12-08T10:00:00",
    "wateringFrequencyDays": 7,
    "notes": "string",
    "notificationsEnabled": true,
    "customTitle": "Mi planta favorita"
  }
]
```

#### Agregar planta al plantel
```http
POST /api/plantel/agregar
Content-Type: application/json

{
  "userId": 1,
  "productoId": 5,
  "customTitle": "Mi Lavanda",
  "notes": "Comprada en primavera"
}
```

#### Regar planta
```http
PUT /api/plantel/regar
Content-Type: application/json

{
  "userId": 1,
  "productoId": 5
}
```

#### Actualizar título personalizado
```http
PUT /api/plantel/actualizar-titulo
Content-Type: application/json

{
  "userId": 1,
  "productoId": 5,
  "customTitle": "Nuevo título"
}
```

#### Actualizar notas
```http
PUT /api/plantel/actualizar-notas
Content-Type: application/json

{
  "userId": 1,
  "productoId": 5,
  "notes": "Nuevas notas"
}
```

#### Alternar notificaciones
```http
PUT /api/plantel/toggle-notificaciones
Content-Type: application/json

{
  "userId": 1,
  "productoId": 5
}
```

#### Eliminar planta del plantel
```http
DELETE /api/plantel/eliminar
Content-Type: application/json

{
  "userId": 1,
  "productoId": 5
}
```

---

### 🛒 Compras (`/api/compras`)

#### Obtener compras del usuario
```http
GET /api/compras/usuario/{userId}
```

#### Obtener compra por ID
```http
GET /api/compras/{compraId}
```

#### Crear compra
```http
POST /api/compras/crear
Content-Type: application/json

{
  "userId": 1,
  "shippingAddress": "Calle Falsa 123, Santiago",
  "paymentMethod": "DEBIT_CARD",
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

Response: 200 OK
{
  "id": 1,
  "userId": 1,
  "total": 67970,
  "shippingAddress": "string",
  "paymentMethod": "DEBIT_CARD",
  "status": "PENDING",
  "createdAt": "2025-12-08T10:00:00",
  "detalles": [
    {
      "productoId": 1,
      "productoNombre": "Viburnum Lucidum",
      "cantidad": 2,
      "precioUnitario": 24990,
      "subtotal": 49980
    }
  ]
}
```

#### Actualizar estado de compra
```http
PUT /api/compras/{compraId}/estado
Content-Type: application/json

{
  "estado": "COMPLETED"
}
```

Estados disponibles:
- `PENDING` - Pendiente
- `PROCESSING` - En proceso
- `COMPLETED` - Completada
- `CANCELLED` - Cancelada
- `REFUNDED` - Reembolsada

#### Cancelar compra
```http
PUT /api/compras/{compraId}/cancelar
```

---

## ⚙️ Configuración

### 1. Base de Datos (PostgreSQL/Neon)

Editar `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://your-neon-host:5432/plantbuddy
spring.datasource.username=your-username
spring.datasource.password=your-password

# JWT Secret (cambiar en producción)
jwt.secret=your-secret-key-change-this-in-production-min-256-bits
jwt.expiration=86400000
```

### 2. Ejecutar el proyecto

#### Usando Gradle:
```bash
# Windows
gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

#### Usando IDE:
Ejecutar la clase `BackendPlantBuddyApplication.java`

### 3. Acceder a la API
```
http://localhost:8080/api
```

## 📦 Dependencias Principales

- **Spring Boot 4.0.0** - Framework principal
- **Spring Data JPA** - ORM y acceso a datos
- **Spring Security** - Autenticación y autorización
- **PostgreSQL Driver** - Conexión a PostgreSQL/Neon
- **Lombok** - Reducción de código boilerplate
- **JWT (jjwt)** - Tokens de autenticación
- **Jakarta Validation** - Validación de datos

## 🔒 Seguridad

- Autenticación basada en **JWT (JSON Web Tokens)**
- Contraseñas encriptadas con **BCrypt**
- CORS configurado para permitir acceso desde la app móvil
- Endpoints públicos: `/auth/**`, `/productos/**`, `/categorias/**`
- Endpoints protegidos: Requieren token JWT en header `Authorization: Bearer {token}`

## 📊 Flujo de Datos

```
Android App (Kotlin)
      ↓
REST API (Spring Boot)
      ↓
JPA Repository
      ↓
PostgreSQL (Neon)
```

## 🚀 Despliegue

### Opciones recomendadas:

1. **Railway.app** - Despliegue automático desde GitHub
2. **Render.com** - Free tier disponible
3. **Heroku** - Plataforma consolidada
4. **AWS Elastic Beanstalk** - Escalable

### Pasos generales:

1. Configurar base de datos PostgreSQL (Neon, Supabase, etc.)
2. Actualizar `application.properties` con credenciales
3. Generar JAR: `./gradlew build`
4. Desplegar JAR en plataforma elegida

## 📝 Notas Importantes

- El esquema de BD debe existir antes de ejecutar (`catalogo`)
- Los datos iniciales deben cargarse ejecutando el script SQL
- El token JWT expira en 24 horas por defecto
- La app móvil debe actualizar la URL base del `RetrofitClient` apuntando a tu servidor

## 🔧 Troubleshooting

### Error: "Lombok not resolved"
```bash
# Asegúrate de que Lombok esté instalado en tu IDE
# Para IntelliJ IDEA: Settings > Plugins > Install "Lombok"
# Habilitar annotation processing
```

### Error de conexión a BD
- Verificar que PostgreSQL esté corriendo
- Verificar credenciales en `application.properties`
- Verificar que el esquema `catalogo` exista

### Error CORS
- Verificar que `@CrossOrigin(origins = "*")` esté en los controllers
- O configurar CORS globalmente en `SecurityConfig`

## 📧 Contacto y Soporte

Para dudas o problemas, contactar al equipo de desarrollo.

---

**Desarrollado para PlantBuddy** 🌿
