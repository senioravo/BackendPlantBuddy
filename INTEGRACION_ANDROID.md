# 🔌 GUÍA DE INTEGRACIÓN: Android App ↔ Backend

## 📋 Pasos para Conectar Plant Buddy Android con el Backend

---

## 1️⃣ Preparar el Backend

### A. Base de Datos
```sql
-- 1. Ejecutar el script original de la carpeta database/
psql -U usuario -d plantbuddy -f DAM_EVA2_Diego_Herrera/database/neon_plantbuddy_schema.sql

-- 2. Ejecutar el script complementario del backend
psql -U usuario -d plantbuddy -f BackendPlantBuddy/database-schema.sql
```

### B. Configurar application.properties
```properties
# En BackendPlantBuddy/src/main/resources/application.properties

# Tu conexión de Neon PostgreSQL
spring.datasource.url=jdbc:postgresql://ep-xxx-xxx.us-east-2.aws.neon.tech:5432/neondb
spring.datasource.username=tu_usuario
spring.datasource.password=tu_password

# JWT Secret (genera uno seguro)
jwt.secret=PlantBuddy2025SecretKeyMinimum256BitsRequired!
jwt.expiration=86400000
```

### C. Ejecutar el Backend
```bash
cd BackendPlantBuddy
./gradlew bootRun

# O en Windows:
gradlew.bat bootRun

# El servidor iniciará en: http://localhost:8080/api
```

---

## 2️⃣ Actualizar la App Android

### A. Actualizar RetrofitClient

Ubicar archivo: `DAM_EVA2_Diego_Herrera/app/src/main/java/cl/duoc/app/data/api/RetrofitClient.kt`

**Cambiar la BASE_URL:**

```kotlin
object RetrofitClient {
    // DESARROLLO LOCAL
    private const val BASE_URL = "http://10.0.2.2:8080/api/"  // Para emulador Android
    // private const val BASE_URL = "http://192.168.1.X:8080/api/"  // Para dispositivo físico (IP de tu PC)
    
    // PRODUCCIÓN (cuando despliegues)
    // private const val BASE_URL = "https://tu-app.railway.app/api/"
    
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val productApi: ProductApiService by lazy {
        retrofit.create(ProductApiService::class.java)
    }
}
```

**Notas importantes:**
- `10.0.2.2` es la IP que el emulador usa para conectar con localhost
- Si usas dispositivo físico, usa la IP de tu PC en la red local
- Para producción, usa la URL de tu servidor desplegado

---

### B. Crear/Actualizar API Services

#### 1. **AuthApiService.kt** (NUEVO)
```kotlin
package cl.duoc.app.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val confirmPassword: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val type: String,
    val usuario: UsuarioDTO
)

data class UsuarioDTO(
    val id: Int,
    val username: String,
    val email: String,
    val profileImageUrl: String?
)

interface AuthApiService {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
}
```

#### 2. **ProductApiService.kt** (Actualizar)
```kotlin
package cl.duoc.app.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApiService {
    @GET("productos")
    suspend fun getProducts(): Response<List<ProductoDTO>>
    
    @GET("productos/{id}")
    suspend fun getProductById(@Path("id") id: Int): Response<ProductoDTO>
    
    @GET("productos/buscar")
    suspend fun searchProducts(
        @Query("query") query: String?,
        @Query("categoria") categoria: String?
    ): Response<List<ProductoDTO>>
    
    @GET("productos/destacados")
    suspend fun getFeaturedProducts(): Response<List<ProductoDTO>>
}
```

#### 3. **PlantelApiService.kt** (NUEVO)
```kotlin
package cl.duoc.app.data.api

import retrofit2.Response
import retrofit2.http.*

data class AddPlantRequest(
    val userId: Int,
    val productoId: Int,
    val customTitle: String?,
    val notes: String?
)

data class WaterPlantRequest(
    val userId: Int,
    val productoId: Int
)

interface PlantelApiService {
    @GET("plantel/usuario/{userId}")
    suspend fun getPlantelByUser(@Path("userId") userId: Int): Response<List<PlantelPlantDTO>>
    
    @POST("plantel/agregar")
    suspend fun addPlant(@Body request: AddPlantRequest): Response<PlantelPlantDTO>
    
    @PUT("plantel/regar")
    suspend fun waterPlant(@Body request: WaterPlantRequest): Response<PlantelPlantDTO>
    
    @DELETE("plantel/eliminar")
    suspend fun removePlant(@Body request: Map<String, Int>): Response<Unit>
}
```

#### 4. **CompraApiService.kt** (NUEVO)
```kotlin
package cl.duoc.app.data.api

import retrofit2.Response
import retrofit2.http.*

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

interface CompraApiService {
    @GET("compras/usuario/{userId}")
    suspend fun getComprasByUser(@Path("userId") userId: Int): Response<List<CompraDTO>>
    
    @GET("compras/{compraId}")
    suspend fun getCompraById(@Path("compraId") compraId: Int): Response<CompraDTO>
    
    @POST("compras/crear")
    suspend fun createCompra(@Body request: CreateCompraRequest): Response<CompraDTO>
    
    @PUT("compras/{compraId}/cancelar")
    suspend fun cancelCompra(@Path("compraId") compraId: Int): Response<CompraDTO>
}
```

---

### C. Actualizar RetrofitClient con todos los servicios

```kotlin
object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/api/"
    
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val authApi: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }
    
    val productApi: ProductApiService by lazy {
        retrofit.create(ProductApiService::class.java)
    }
    
    val plantelApi: PlantelApiService by lazy {
        retrofit.create(PlantelApiService::class.java)
    }
    
    val compraApi: CompraApiService by lazy {
        retrofit.create(CompraApiService::class.java)
    }
}
```

---

## 3️⃣ Actualizar Repositorios

### ProductRepository.kt
```kotlin
class ProductRepository {
    private val api = RetrofitClient.productApi
    
    suspend fun getProducts(): Result<List<Product>> {
        return try {
            val response = api.getProducts()
            if (response.isSuccessful && response.body() != null) {
                val products = response.body()!!.map { it.toDomain() }
                Result.success(products)
            } else {
                Result.failure(Exception("Error al cargar productos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun searchProducts(query: String, categoria: String?): Result<List<Product>> {
        return try {
            val response = api.searchProducts(query, categoria)
            if (response.isSuccessful && response.body() != null) {
                val products = response.body()!!.map { it.toDomain() }
                Result.success(products)
            } else {
                Result.failure(Exception("Error en búsqueda"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Mapper Extension
fun ProductoDTO.toDomain(): Product {
    return Product(
        id = this.id,
        name = this.nombre,
        description = this.descripcion,
        price = this.precio.toDouble(),
        imageUrl = this.imagenUrl ?: "",
        category = this.categoria ?: "General",
        stock = this.stock,
        rating = this.rating.toFloat()
    )
}
```

---

## 4️⃣ Agregar Permisos de Internet

En `AndroidManifest.xml`:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    
    <!-- Agregar estos permisos -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    
    <!-- Para desarrollo, permitir tráfico HTTP -->
    <application
        android:usesCleartextTraffic="true"
        ...>
        ...
    </application>
</manifest>
```

---

## 5️⃣ Gestión de JWT Token

### Crear TokenManager.kt
```kotlin
package cl.duoc.app.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.tokenDataStore by preferencesDataStore(name = "token_prefs")

class TokenManager(private val context: Context) {
    
    private val TOKEN_KEY = stringPreferencesKey("jwt_token")
    
    suspend fun saveToken(token: String) {
        context.tokenDataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }
    
    fun getToken(): Flow<String?> {
        return context.tokenDataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }
    }
    
    suspend fun clearToken() {
        context.tokenDataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }
}
```

### Actualizar AuthViewModel.kt
```kotlin
class AuthViewModel(
    private val context: Context
) : ViewModel() {
    
    private val authApi = RetrofitClient.authApi
    private val tokenManager = TokenManager(context)
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val request = LoginRequest(email, password)
                val response = authApi.login(request)
                
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    
                    // Guardar token
                    tokenManager.saveToken(authResponse.token)
                    
                    // Actualizar estado
                    _authState.value = AuthState(
                        isLoggedIn = true,
                        currentUser = authResponse.usuario.toDomain()
                    )
                } else {
                    _authState.value = AuthState(
                        error = "Credenciales inválidas"
                    )
                }
            } catch (e: Exception) {
                _authState.value = AuthState(
                    error = "Error de conexión: ${e.message}"
                )
            }
        }
    }
}
```

---

## 6️⃣ Testing

### Probar la Conexión

```kotlin
// En algún ViewModel o Repository de prueba
viewModelScope.launch {
    try {
        val response = RetrofitClient.productApi.getProducts()
        if (response.isSuccessful) {
            Log.d("Backend", "✅ Conexión exitosa! Productos: ${response.body()?.size}")
        } else {
            Log.e("Backend", "❌ Error: ${response.code()}")
        }
    } catch (e: Exception) {
        Log.e("Backend", "❌ Excepción: ${e.message}")
    }
}
```

---

## 7️⃣ Checklist de Integración

- [ ] Backend ejecutándose en `localhost:8080`
- [ ] Base de datos PostgreSQL configurada y con datos
- [ ] RetrofitClient actualizado con la URL correcta
- [ ] API Services creados (Auth, Product, Plantel, Compra)
- [ ] Permisos de Internet en AndroidManifest
- [ ] TokenManager implementado
- [ ] Repositorios actualizados para usar API
- [ ] ViewModels actualizados
- [ ] Probar login desde la app
- [ ] Probar carga de productos
- [ ] Verificar logs en Logcat

---

## 🐛 Troubleshooting Común

### Error: "Unable to resolve host"
- Verificar que el backend esté corriendo
- Verificar la URL en RetrofitClient
- Para emulador: usar `10.0.2.2` en lugar de `localhost`

### Error: "Cleartext HTTP traffic not permitted"
- Agregar `android:usesCleartextTraffic="true"` en AndroidManifest

### Error 401: Unauthorized
- Verificar que el token JWT se esté enviando correctamente
- Verificar que el token no haya expirado (24h)

### Error 404: Not Found
- Verificar que la ruta del endpoint sea correcta
- Verificar que el backend tenga el contexto `/api`

### Datos no se cargan
- Verificar conexión a Internet
- Verificar logs en Logcat con filtro "Backend"
- Verificar respuesta del servidor con Postman

---

## 📱 URLs según Entorno

| Entorno | URL Base |
|---------|----------|
| **Emulador Android** | `http://10.0.2.2:8080/api/` |
| **Dispositivo Físico** | `http://192.168.1.X:8080/api/` (IP de tu PC) |
| **Producción Railway** | `https://backendplantbuddy.railway.app/api/` |
| **Producción Render** | `https://backendplantbuddy.onrender.com/api/` |

---

## 🚀 Desplegar a Producción

### Opción 1: Railway.app
```bash
# 1. Crear cuenta en railway.app
# 2. Crear nuevo proyecto
# 3. Conectar con GitHub (BackendPlantBuddy)
# 4. Railway detectará Spring Boot automáticamente
# 5. Agregar PostgreSQL como servicio
# 6. Configurar variables de entorno
# 7. Deploy automático
```

### Opción 2: Render.com
```bash
# 1. Crear cuenta en render.com
# 2. New > Web Service
# 3. Conectar repositorio GitHub
# 4. Build Command: ./gradlew build
# 5. Start Command: java -jar build/libs/*.jar
# 6. Agregar PostgreSQL Database
# 7. Configurar variables de entorno
```

---

**🎉 ¡Listo! Tu app Android ahora está conectada al backend**
