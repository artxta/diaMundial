# GitHub Copilot Instructions — Día Mundial

## 1. Objetivo del proyecto

Este proyecto consiste en migrar/reimplementar la aplicación Android existente en:

`dia_mundial_old/`

La nueva aplicación debe estar en:

`diaMundial/`

La aplicación antigua está desarrollada en Kotlin/Android y debe utilizarse como referencia funcional y visual.

El objetivo es conseguir una nueva aplicación equivalente utilizando:

- React
- TypeScript
- Ionic
- Capacitor
- Android
- SQLite
- pnpm

La nueva aplicación debe mantener las funcionalidades de la aplicación antigua, adaptándolas correctamente a la nueva arquitectura.

---

## 2. Regla crítica: analizar primero la aplicación antigua

ANTES DE IMPLEMENTAR LA NUEVA APLICACIÓN:

1. Analiza completamente `dia_mundial_old/`.
2. Revisa su estructura de carpetas y archivos.
3. Identifica todas las pantallas.
4. Identifica toda la navegación.
5. Identifica todos los componentes y funcionalidades.
6. Identifica cómo se almacenan actualmente los datos.
7. Identifica países, idiomas y traducciones.
8. Identifica el funcionamiento de los días mundiales.
9. Identifica cómo funcionan los días personalizados.
10. Identifica búsqueda, filtros y selección de países.
11. Identifica configuración y preferencias.
12. Identifica notificaciones, enlaces externos y cualquier integración.
13. Revisa recursos gráficos, iconos, textos y assets.
14. Revisa las reglas de negocio existentes.

Antes de comenzar una implementación importante, crea mentalmente o en documentación de trabajo un inventario de funcionalidades que deben existir en la nueva aplicación.

NO empieces creando una aplicación genérica de calendario.

La nueva aplicación debe basarse en el comportamiento real de `dia_mundial_old/`.

---

## 3. No modificar la aplicación antigua

`dia_mundial_old/` es únicamente la fuente de referencia.

NO modificar:

- archivos Kotlin
- recursos
- bases de datos
- configuraciones
- assets
- código existente

Cualquier cambio debe realizarse exclusivamente en la nueva aplicación.

---

## 4. Tecnologías obligatorias

Utilizar exclusivamente:

- React
- TypeScript
- Ionic
- Capacitor
- Android
- SQLite
- pnpm

No introducir tecnologías alternativas innecesarias.

No utilizar:

- npm
- yarn

Todos los comandos de Node deben utilizar `pnpm`.

Ejemplos:

```bash
pnpm install
pnpm add paquete
pnpm remove paquete
pnpm run build
pnpm run lint
pnpm test
pnpm exec cap sync android
```

No ejecutar:

```bash
npm install
npm i
npm run
yarn
yarn install
```

Si existe `package.json`, debe utilizarse `pnpm` como gestor del proyecto y, cuando corresponda, declarar:

```json
{
  "packageManager": "pnpm"
}
```

---

## 5. Arquitectura

Utilizar una arquitectura organizada y mantenible.

Como mínimo, separar responsabilidades mediante estructuras similares a:

```text
src/
├── components/
├── pages/
├── hooks/
├── services/
├── database/
├── models/
├── utils/
├── data/
├── assets/
└── theme/
```

La estructura exacta puede adaptarse a las necesidades reales de la aplicación.

### Regla importante

No crear páginas gigantes que contengan toda la aplicación.

Cada funcionalidad importante debe estar separada en componentes, servicios, hooks o módulos adecuados.

---

## 6. Un componente por funcionalidad

Crear componentes independientes para las diferentes características de la aplicación.

Por ejemplo, si existen estas funcionalidades:

- lista de días
- calendario
- búsqueda
- filtros
- selección de país
- selección de idioma
- creación de día personalizado
- edición de día personalizado
- eliminación de día personalizado
- detalle de un día
- configuración

cada una debe tener una separación lógica adecuada.

No crear un único componente con cientos o miles de líneas.

Los componentes deben ser:

- reutilizables
- pequeños cuando sea posible
- fáciles de probar
- fáciles de mantener
- claramente nombrados

---

## 7. TypeScript

Utilizar TypeScript de forma estricta.

Preferir:

- interfaces
- tipos
- enums cuando aporten valor
- tipos para modelos de base de datos
- tipos para props
- tipos para servicios
- tipos para respuestas

Evitar `any`.

No utilizar `any` para ocultar errores de tipado.

Si es necesario utilizar un tipo desconocido, preferir `unknown` y realizar una comprobación adecuada.

---

## 8. SQLite: almacenamiento permanente

La aplicación debe ser completamente funcional offline.

La base de datos debe utilizar SQLite.

IMPORTANTE:

"SQLite en memoria interna permanente" significa una base de datos SQLite almacenada como archivo dentro del almacenamiento interno de la aplicación.

NO significa:

```text
SQLite :memory:
```

NO utilizar una base de datos SQLite temporal en RAM.

Los datos deben permanecer después de:

- cerrar la aplicación
- abrirla nuevamente
- reiniciar Android
- apagar y encender el teléfono
- actualizar la aplicación

Los días personalizados creados por el usuario deben conservarse.

---

## 9. Base de datos

Separar la lógica de SQLite de la interfaz.

Por ejemplo:

```text
database/
├── connection/
├── migrations/
├── repositories/
├── schema/
└── database.ts
```

La estructura exacta puede adaptarse.

No realizar consultas SQL directamente desde componentes visuales si puede evitarse.

Utilizar una capa de acceso a datos.

Ejemplo conceptual:

```text
Page
 ↓
Hook
 ↓
Service
 ↓
Repository
 ↓
SQLite
```

---

## 10. Migraciones de base de datos

La base de datos debe tener control de versión.

Cuando cambie el esquema:

1. Detectar la versión existente.
2. Ejecutar las migraciones necesarias.
3. Actualizar la versión.
4. Mantener los datos existentes.

NO borrar la base de datos durante una actualización simplemente porque cambió el esquema.

NO ejecutar automáticamente:

```sql
DROP DATABASE
```

ni eliminar todas las tablas para solucionar problemas de estructura.

Las migraciones deben ser seguras y reproducibles.

---

## 11. Días personalizados

Los días personalizados son una funcionalidad crítica.

Debe ser posible, según lo que haga la aplicación antigua:

- crear un día personalizado
- editarlo
- eliminarlo
- visualizarlo
- asociarlo a una fecha
- almacenar su información
- recuperarlo después de cerrar la aplicación

Antes de implementar esta funcionalidad, comprobar exactamente cómo funciona en `dia_mundial_old/`.

Los datos personalizados deben almacenarse en SQLite.

No depender exclusivamente de:

- variables React
- `localStorage`
- memoria RAM

para almacenar información que deba persistir.

---

## 12. Offline-first

La aplicación debe funcionar sin conexión a Internet siempre que la funcionalidad original lo permita.

Los datos locales deben estar disponibles sin conexión.

No diseñar la aplicación suponiendo que existe un servidor remoto.

Si una funcionalidad requiere Internet, identificarla explícitamente y manejar la ausencia de conexión correctamente.

No mostrar errores genéricos cuando simplemente no exista conexión.

---

## 13. React + Ionic

Utilizar correctamente:

- componentes Ionic
- páginas Ionic
- navegación de Ionic
- hooks de React
- estado de React
- ciclo de vida de Ionic cuando corresponda

Evitar mezclar patrones propios de Android/Kotlin con React si no son necesarios.

La navegación debe ser clara y coherente.

---

## 14. Capacitor

Utilizar Capacitor para integrar la aplicación web con Android.

Mantener separadas las responsabilidades:

```text
React
  ↓
Ionic
  ↓
Capacitor
  ↓
Android
```

Cuando se necesite una funcionalidad nativa, utilizar Capacitor o un plugin adecuado.

Después de cambios relacionados con Capacitor comprobar:

```bash
pnpm exec cap sync android
```

---

## 15. Android

La aplicación final debe compilar correctamente como aplicación Android.

Comprobar:

- configuración de Capacitor
- package/application ID
- permisos
- almacenamiento
- SQLite
- recursos
- icono
- splash screen
- orientación si corresponde
- navegación
- comportamiento al cerrar/reabrir
- compilación de Android

No introducir permisos innecesarios.

---

## 16. Comentarios en el código

Añadir comentarios cuando aporten información útil.

Especialmente comentar:

- lógica compleja
- decisiones arquitectónicas
- inicialización de SQLite
- migraciones
- integración con Capacitor
- comportamiento específico de Android
- conversiones de datos
- lógica de negocio no evidente

NO llenar el código de comentarios obvios.

Malo:

```ts
// Incrementa i
i++;
```

Bueno:

```ts
// La migración se ejecuta antes de abrir los repositorios para
// garantizar que todas las tablas tengan el esquema esperado.
await runMigrations();
```

---

## 17. No inventar funcionalidades

La aplicación nueva debe reproducir las funcionalidades existentes.

No inventar características importantes sin comprobar primero si existen en la aplicación antigua.

Si detectas una posible mejora, puedes identificarla como mejora futura, pero no sustituir el comportamiento original sin motivo.

---

## 18. Mantener el comportamiento

Cuando exista una funcionalidad equivalente en la aplicación antigua, intentar mantener:

- comportamiento
- navegación
- textos
- estructura
- reglas de negocio
- validaciones
- estados
- resultados

La tecnología cambia.

El comportamiento funcional debe mantenerse.

---

## 19. Diseño y UI

La nueva interfaz debe reproducir razonablemente la experiencia de la aplicación antigua.

Analizar:

- colores
- tipografías
- tamaños
- márgenes
- botones
- iconos
- tarjetas
- listas
- navegación
- formularios
- estados vacíos
- mensajes de error
- mensajes de confirmación

No crear una UI genérica si la aplicación antigua proporciona información suficiente para reproducirla.

---

## 20. Gestión de errores

Implementar manejo de errores apropiado.

Especialmente para:

- SQLite
- lectura de datos
- escritura de datos
- migraciones
- navegación
- Capacitor
- permisos
- Android

Los errores deben proporcionar información útil para depuración.

No utilizar silenciosamente:

```ts
try {
  ...
} catch {
}
```

Si un error se captura, debe tratarse o registrarse adecuadamente.

---

## 21. Validación de datos

Validar los datos introducidos por el usuario.

Especialmente:

- fechas
- nombres
- campos obligatorios
- valores vacíos
- valores inválidos
- duplicados cuando corresponda

No confiar exclusivamente en la interfaz.

La capa de datos también debe proteger la integridad de la información.

---

## 22. Rendimiento

Evitar:

- consultas SQLite innecesarias
- renders innecesarios
- operaciones costosas dentro del render
- cargar grandes cantidades de datos sin necesidad

Si la aplicación antigua contiene muchos días, países o traducciones, diseñar la carga de datos de forma eficiente.

---

## 23. Seguridad

No almacenar información sensible innecesariamente.

No incluir:

- contraseñas
- claves privadas
- tokens secretos
- credenciales

directamente en el código fuente.

---

## 24. Dependencias

Antes de añadir una dependencia nueva:

1. Comprobar si ya existe una solución en el proyecto.
2. Comprobar si Ionic, React o Capacitor proporcionan la funcionalidad.
3. Añadir una dependencia solamente si aporta un beneficio real.

No llenar el proyecto de dependencias innecesarias.

Instalar dependencias únicamente mediante:

```bash
pnpm add ...
```

---

## 25. Proceso de implementación

Seguir este orden:

### Fase 1 — Auditoría

Analizar `dia_mundial_old/`.

### Fase 2 — Inventario

Identificar:

- pantallas
- funcionalidades
- modelos
- datos
- navegación
- idiomas
- países
- días
- días personalizados
- configuración
- recursos

### Fase 3 — Arquitectura

Diseñar la estructura React/Ionic/Capacitor.

### Fase 4 — Base de datos

Crear SQLite y sus migraciones.

### Fase 5 — Modelos y servicios

Crear:

- modelos TypeScript
- repositorios
- servicios
- hooks

### Fase 6 — Interfaz

Crear las páginas y componentes.

### Fase 7 — Funcionalidades

Implementar todas las funciones identificadas.

### Fase 8 — Capacitor/Android

Sincronizar y comprobar Android.

### Fase 9 — Pruebas

Probar las funcionalidades.

### Fase 10 — Comparación

Comparar el resultado con `dia_mundial_old/`.

### Fase 11 — Corrección

Corregir cualquier diferencia o error encontrado.

---

## 26. Comandos de verificación

Antes de considerar terminada una tarea importante, ejecutar cuando existan los scripts correspondientes:

```bash
pnpm install
pnpm run build
pnpm run lint
pnpm test
pnpm exec cap sync android
```

Si existe un comando específico de compilación Android, ejecutarlo también.

No declarar que algo funciona correctamente sin haberlo comprobado.

---

## 27. Comprobación funcional mínima

Comprobar como mínimo:

- aplicación inicia
- navegación funciona
- páginas cargan
- días se muestran correctamente
- países funcionan
- idiomas funcionan
- búsqueda funciona
- filtros funcionan
- detalles funcionan
- creación de días personalizados funciona
- edición funciona
- eliminación funciona
- datos se guardan en SQLite
- datos permanecen después de cerrar la aplicación
- datos permanecen después de reiniciar la aplicación
- no se pierden datos al actualizar el esquema
- aplicación funciona offline
- Android compila

Además, comprobar todas las funcionalidades adicionales descubiertas durante la auditoría de `dia_mundial_old/`.

---

## 28. No dar por terminado el trabajo prematuramente

No considerar terminado el proyecto simplemente porque:

```bash
pnpm run build
```

funciona.

Un build correcto NO garantiza que la aplicación funcione.

Hay que comprobar también:

- navegación
- datos
- SQLite
- persistencia
- formularios
- funcionalidades
- Android
- comportamiento offline

---

## 29. Si encuentras errores

Cuando aparezca un error:

1. Identificar la causa.
2. Explicar internamente qué está fallando.
3. Corregirlo.
4. Volver a ejecutar la comprobación correspondiente.
5. Comprobar que la corrección no rompe otra funcionalidad.

No ocultar errores simplemente eliminando código o desactivando comprobaciones.

---

## 30. Regla final

El objetivo no es simplemente crear una aplicación React que se parezca a "Día Mundial".

El objetivo es:

> Crear una nueva versión de la aplicación existente en `dia_mundial_old/`, utilizando React + Ionic + Capacitor + TypeScript + SQLite, manteniendo sus funcionalidades y comportamiento, funcionando offline y conservando permanentemente los días personalizados.

Prioridades:

1. Fidelidad funcional a `dia_mundial_old/`
2. Persistencia correcta de SQLite
3. Arquitectura limpia
4. Código mantenible
5. Componentes separados por funcionalidad
6. TypeScript estricto
7. Comentarios útiles
8. Funcionamiento offline
9. Compatibilidad Android
10. Verificación real antes de finalizar
