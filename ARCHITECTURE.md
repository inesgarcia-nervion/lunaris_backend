# Arquitectura de Integración con Open Library

## Diagrama General

```
┌─────────────────────────────────────────────────────────────────┐
│                        FRONTEND (Angular)                       │
├─────────────────────────────────────────────────────────────────┤
│  BookSearchComponent                                             │
│  ├── book-search.component.ts (Lógica)                          │
│  ├── book-search.component.html (Interfaz)                      │
│  └── book-search.component.css (Estilos)                        │
│                                                                 │
│  Utiliza: BookSearchService                                     │
│  ├── searchBooks(query)                                         │
│  ├── searchByTitle(title)                                       │
│  ├── searchByAuthor(author)                                     │
│  └── importBook(book)                                           │
└────────────────────┬──────────────────────────────────────────┘
                     │ HTTP Request/Response
                     ▼
┌─────────────────────────────────────────────────────────────────┐
│                    BACKEND (Spring Boot)                        │
├─────────────────────────────────────────────────────────────────┤
│                     REST Controllers                            │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ OpenLibraryController                                    │  │
│  │ ├── GET /api/openlibrary/search                          │  │
│  │ ├── GET /api/openlibrary/search/title                    │  │
│  │ └── GET /api/openlibrary/search/author                   │  │
│  │                                                           │  │
│  │ BookController                                            │  │
│  │ ├── GET /books                                           │  │
│  │ ├── GET /books/{id}                                      │  │
│  │ ├── POST /books                                          │  │
│  │ ├── PUT /books/{id}                                      │  │
│  │ ├── DELETE /books/{id}                                   │  │
│  │ └── POST /books/import/openlibrary                       │  │
│  └──────────────────────────────────────────────────────────┘  │
│                          ▲                                       │
│                          │                                       │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                   Services Layer                         │  │
│  │  ┌──────────────────────────────────────────────────┐   │  │
│  │  │ OpenLibraryService                              │   │  │
│  │  │ ├── searchBooks(query, limit, offset)          │   │  │
│  │  │ ├── searchByTitle(title, limit, offset)        │   │  │
│  │  │ └── searchByAuthor(author, limit, offset)      │   │  │
│  │  │                                                 │   │  │
│  │  │ BookService                                     │   │  │
│  │  │ ├── getAllBooks()                              │   │  │
│  │  │ ├── getBookById(id)                            │   │  │
│  │  │ ├── createBook(book)                           │   │  │
│  │  │ ├── updateBook(id, bookDetails)                │   │  │
│  │  │ ├── deleteBook(id)                             │   │  │
│  │  │ └── importFromOpenLibrary(openLibraryBook)     │   │  │
│  │  └──────────────────────────────────────────────────┘   │  │
│  └──────────────────────────────────────────────────────────┘  │
│                          ▲                                       │
│                          │                                       │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                    DTO Layer                            │  │
│  │  ├── OpenLibraryBookDto                                 │  │
│  │  └── OpenLibrarySearchResponseDto                       │  │
│  └──────────────────────────────────────────────────────────┘  │
│                          ▲                                       │
│                          │                                       │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                 Repository Layer                        │  │
│  │  └── BookRepository (JPA)                              │  │
│  └──────────────────────────────────────────────────────────┘  │
│                          ▲                                       │
│                          │ (Persistence)                         │
│                          ▼                                       │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                   Database                             │  │
│  │  └── books table                                       │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                          ▲
                          │ (RestTemplate)
                          │
┌─────────────────────────────────────────────────────────────────┐
│               External: Open Library API                        │
├─────────────────────────────────────────────────────────────────┤
│  https://openlibrary.org/search.json                           │
│                                                                 │
│  Responde con:                                                 │
│  ├── Metadatos de libros                                       │
│  ├── URLs de portadas                                          │
│  ├── Información de autores                                    │
│  └── IDs de Internet Archive                                   │
└─────────────────────────────────────────────────────────────────┘
```

## Flujo de datos

### Búsqueda en Open Library

```
[Usuario ingresa término] 
        ↓
[BookSearchComponent] 
        ↓
[bookSearchService.searchBooks()] (HTTP GET)
        ↓
[OpenLibraryController.search()]
        ↓
[OpenLibraryService.searchBooks()]
        ↓
[RestTemplate.getForObject()]
        ↓
[Open Library API - https://openlibrary.org/search.json]
        ↓
[JSON Response] → [OpenLibrarySearchResponseDto]
        ↓
[OpenLibraryController] → [Response HTTP]
        ↓
[BookSearchComponent recibe resultados]
        ↓
[Mostrar en UI]
```

### Importación a Base de Datos

```
[Usuario hace click "Importar"]
        ↓
[bookSearchService.importBook(book)] (HTTP POST)
        ↓
[BookController.importFromOpenLibrary()]
        ↓
[BookService.importFromOpenLibrary()]
        ↓
[Verificar si existe en DB]
        ↓
[Si no existe: crear nuevo registro]
        ↓
[bookRepository.save(book)]
        ↓
[Base de datos - INSERT/UPDATE]
        ↓
[Retornar Book (Local)]
        ↓
[BookSearchComponent muestra confirmación]
```

## Estructura de archivos creados

```
lunaris_backend/
├── src/main/java/com/tfg/lunaris_backend/
│   ├── config/
│   │   └── RestTemplateConfig.java          [NEW] - Bean de RestTemplate
│   ├── controller/
│   │   ├── OpenLibraryController.java        [NEW] - Endpoints de búsqueda
│   │   └── BookController.java               [MODIFIED] - Agregar importación
│   ├── dto/
│   │   ├── OpenLibraryBookDto.java           [NEW] - Modelo del libro
│   │   └── OpenLibrarySearchResponseDto.java [NEW] - Respuesta de búsqueda
│   ├── service/
│   │   ├── OpenLibraryService.java           [NEW] - Llamadas a API
│   │   └── BookService.java                  [MODIFIED] - Importación
│   └── ...
└── OPEN_LIBRARY_INTEGRATION.md               [NEW] - Documentación

lunaris_frontend/
├── src/app/
│   ├── services/
│   │   └── book-search.service.ts            [NEW] - Servicio HTTP
│   ├── components/
│   │   └── book-search/
│   │       ├── book-search.component.ts      [NEW] - Lógica
│   │       ├── book-search.component.html    [NEW] - Interfaz
│   │       └── book-search.component.css     [NEW] - Estilos
│   └── ...
└── ...
```

## Componentes principales

### 1. RestTemplateConfig.java
**Propósito**: Proporcionar un bean de `RestTemplate` inyectable para realizar llamadas HTTP

```java
@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

### 2. OpenLibraryService.java
**Propósito**: Manejar todas las comunicaciones con la API de Open Library

```
Métodos principales:
├── searchBooks(query, limit, offset)
├── searchByTitle(title, limit, offset)
└── searchByAuthor(author, limit, offset)
```

### 3. OpenLibraryController.java
**Propósito**: Exponer endpoints REST para búsquedas en Open Library

```
Endpoints:
├── GET /api/openlibrary/search
├── GET /api/openlibrary/search/title
└── GET /api/openlibrary/search/author
```

### 4. BookService.java (Enhanced)
**Propósito**: Agregar funcionalidad de importación desde Open Library

```
Método nuevo:
└── importFromOpenLibrary(openLibraryBook)
```

### 5. BookSearchService.ts
**Propósito**: Interfaz HTTP desde Angular hacia los endpoints del backend

```typescript
Métodos:
├── searchBooks(query, limit, offset)
├── searchByTitle(title, limit, offset)
├── searchByAuthor(author, limit, offset)
└── importBook(book)
```

### 6. BookSearchComponent
**Propósito**: Componente UI para búsqueda e importación de libros

```
Características:
├── Búsqueda de libros (general, por título, por autor)
├── Visualización en grid
├── Paginación
├── Importación a BD local
└── Manejo de errores y estados de carga
```

## Decisiones de diseño

### ✅ Ventajas del enfoque implementado:

1. **Separación de responsabilidades**: Cada capa tiene un propósito específico
2. **Reutilización**: El servicio de Open Library puede usarse en múltiples controladores
3. **DTOs específicos**: Los modelos JSON de Open Library no contaminan el modelo de dominio
4. **Prevención de duplicados**: Al importar, se verifica si el libro ya existe
5. **Frontend desacoplado**: Angular no consulta directamente Open Library, usa tu API
6. **Escalabilidad**: Fácil de agregar más opciones de búsqueda o filtros

### 🔒 Ventajas de seguridad:

- El frontend no expone directamente la API de Open Library
- Tu backend puede agregar autenticación/autorización en el futuro
- Puedes agregar rate limiting o caché

## Próximos pasos (opcional)

1. **Agregar caché** de búsquedas frecuentes
2. **Implementar búsqueda avanzada** (ISBN, ISBN13, etc.)
3. **Agregar sincronización periódica** de metadatos
4. **Filtros por idioma** usando el parámetro `lang` de Open Library
5. **Obtener ratings y reviews** desde Open Library
6. **Optimizar importación en lote** de múltiples libros
7. **CORS configuration** si frontend y backend están en hosts diferentes
