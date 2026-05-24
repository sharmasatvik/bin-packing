# 📦 BinPack - 3D Bin Packing Optimizer

A full-stack project that determines the optimal way to pack SKU items into boxes using the **First Fit Decreasing (FFD)** algorithm.

---

## 🧱 Tech Stack

| Layer    | Technology                      |
|----------|---------------------------------|
| Frontend | Angular 18 · TypeScript · CSS   |
| Backend  | Java 24 · Spring Boot 3.4.5     |
| Build    | Maven 3.9+ · exec-maven-plugin  |
| Native   | GraalVM Native Image (optional) |

## ⚙️ Prerequisites

| Tool        | Version                              |
|-------------|--------------------------------------|
| Java JDK    | 24                                   |
| Maven       | 3.9+                                 |
| Node.js     | ≥ 20                                 |
| Angular CLI | 18.x (`npm install -g @angular/cli`) |

---

## 🚀 Running the Project

### Option A - Full Maven Build *(recommended)*

Runs `npm install` → `ng build` → starts Spring Boot in one command.

```bash
cd bin-packing
./mvnw spring-boot:run
```

Open **http://localhost:8080**

---

### Option B - Dev Mode *(hot reload on both sides)*

```bash
# Terminal 1 - Spring Boot backend
cd bin-packing
./mvnw spring-boot:run

# Terminal 2 - Angular dev server
cd bin-packing/src/webapp
npm install
ng serve
```

Open **http://localhost:4200**

> The Angular dev server proxies `/api/**` calls to Spring Boot on port 8080.

---

### Option C - GraalVM Native Image *(optional)*

Requires GraalVM JDK 24 installed.

```bash
./mvnw -Pnative package
./target/bin-packing
```

---

## 🌐 URLs

| URL                                        | Description                        |
|--------------------------------------------|------------------------------------|
| `http://localhost:8080`                    | Angular app served by Spring Boot  |
| `http://localhost:4200`                    | Angular dev server (dev mode only) |
| `http://localhost:8080/api/packing/health` | API health check                   |
---

## 🖥️ Opening in IntelliJ IDEA

1. `File → Open` → select the **`bin-packing/`** root folder
2. IntelliJ will detect it as a Maven project and import it automatically
3. Set SDK: `File → Project Structure → Project → SDK → Java 24`
4. Enable Lombok: `Settings → Build → Compiler → Annotation Processors → ✅ Enable annotation processing`
5. Install the **Lombok** IntelliJ plugin if not already present (`Settings → Plugins → search "Lombok"`)

---

## 📐 How It Works - User Flow

```
Step 1 - Box Config
  |- User enters the box dimensions (L × W × H) and max weight

Step 2 - Add SKUs
  |- User adds items one by one: SKU code, L × W × H, weight
  |- Items can be edited or removed before packing

Step 3 - Optimize
  |- Click "Optimize Packing"
  |- Backend runs FFD algorithm
  |- Results animate in showing box assignments, utilization, and weight
```

---

## 📏 Measurement Systems

The app supports both **metric** and **imperial** units, switchable via the toggle in the header.

|            | Metric | Imperial |
|------------|--------|----------|
| Dimensions | cm     | in       |
| Weight     | kg     | lb       |
| Volume     | cm³    | in³      |

> All values are stored and sent to the API in **metric** internally. Conversion happens in the frontend only.

| Conversion | Factor     |
|------------|------------|
| 1 inch     | 2.54 cm    |
| 1 lb       | 0.4536 kg  |
| 1 in³      | 16.387 cm³ |

---

## 🧮 Algorithm - First Fit Decreasing (FFD)

FFD is a well-known approximation algorithm for the bin packing problem.

**Steps:**
1. **Reject** items that exceed the box in any single dimension or exceed max weight → marked as `unpackedItems`
2. **Sort** remaining items by volume **descending** (largest first - the "Decreasing" in FFD)
3. For each item, scan open boxes and place it in the **first box that fits** (both volume and weight)
4. If no existing box can fit the item, **open a new box**
5. Return all box assignments with per-box utilization metrics

**Complexity:** O(n²) in the worst case, O(n log n) dominated by sorting.

**Approximation guarantee:** FFD uses at most `(11/9) OPT + 6/9` bins, making it one of the tightest classical heuristics.

---

## 🔌 REST API

### `POST /api/packing/optimize`

**Request body:**
```json
{
  "box": {
    "length": 100,
    "width": 100,
    "height": 100,
    "maxWeight": 50
  },
  "items": [
    { "sku": "SKU-001", "length": 30, "width": 20, "height": 15, "weight": 2.5 },
    { "sku": "SKU-002", "length": 60, "width": 50, "height": 40, "weight": 12.0 },
    { "sku": "SKU-003", "length": 10, "width": 10, "height": 10, "weight": 0.5 }
  ]
}
```

**Response:**
```json
{
  "totalBoxes": 2,
  "totalItems": 3,
  "algorithm": "First Fit Decreasing (FFD)",
  "unpackedItems": [],
  "packedBoxes": [
    {
      "boxNumber": 1,
      "totalWeight": 12.0,
      "utilizationPercent": 12.0,
      "remainingVolume": 880000.0,
      "items": [
        { "sku": "SKU-002", "length": 60, "width": 50, "height": 40, "weight": 12.0, "volume": 120000.0 }
      ]
    },
    {
      "boxNumber": 2,
      "totalWeight": 3.0,
      "utilizationPercent": 9.9,
      "remainingVolume": 900100.0,
      "items": [
        { "sku": "SKU-001", "length": 30, "width": 20, "height": 15, "weight": 2.5, "volume": 9000.0 },
        { "sku": "SKU-003", "length": 10, "width": 10, "height": 10, "weight": 0.5, "volume": 1000.0 }
      ]
    }
  ]
}
```

### `GET /api/packing/health`

```
200 OK
BinPack API is running ✓
```
---

## 🧪 Running Tests

```bash
./mvnw test
```

The test suite covers the FFD algorithm with these cases:

- ✅ All items fit in a single box
- ✅ Items split across multiple boxes (volume constraint)
- ✅ Items split across multiple boxes (weight constraint)
- ✅ Oversized item (exceeds box dimensions) → `unpackedItems`
- ✅ Overweight item (exceeds max weight) → `unpackedItems`
- ✅ Empty item list → empty result
- ✅ Utilization percentage calculated correctly

---

## 🎨 Frontend Architecture

| Concern          | Detail                                             |
|------------------|----------------------------------------------------|
| State management | Angular signals (`MeasurementService`)             |
| HTTP             | `HttpClient` with `Observable`                     |
| Forms            | Template-driven (`ngModel`) with manual validation |
| Styling          | Pure CSS with `--css-variables` for full theming   |
| Animations       | CSS `@keyframes` with staggered box reveal         |
| Unit system      | `MeasurementService` - metric ↔ imperial, reactive |

---

## 📄 License

MIT - free to use, modify, and distribute.