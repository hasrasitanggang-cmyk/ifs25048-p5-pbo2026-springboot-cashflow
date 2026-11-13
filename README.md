# Cashflow (Catatan Keuangan) — Spring Boot + Vaadin + JDBC PostgreSQL

Studi kasus Praktikum 5 PBO 2026 (Clean Architecture, Generics, Exceptions, JDBC, Auth, Role).

## 0. Identitas proyek
Proyek ini sudah disesuaikan untuk identitas `ifs25048` dengan nama `ifs25048-p5-pbo2026-springboot-cashflow` dan package Java `org.delcom.ifs25048springbootcashflow`. Tidak perlu menjalankan `rename.sh` untuk konfigurasi ini.

## 1. Prasyarat
- JDK 25, PostgreSQL, Maven (atau Maven Wrapper dari IntelliJ: `mvn -N wrapper:wrapper`)

## 2. Database & konfigurasi
```sql
CREATE DATABASE pbo2026_cashflow;
```
`cp .env.example .env`, sesuaikan `DB_*`, `ROOT_*`, dan isi `SESSION_SECRET` dengan string acak panjang.
Admin otomatis dibuat dari `ROOT_USERNAME` / `ROOT_PASSWORD` (default `admin` / `admin123`).

## 3. Jalankan
```powershell
.\mvnw.cmd spring-boot:run                       # development, http://localhost:8080
.\mvnw.cmd clean package -Pproduction            # build JAR (wajib profil production)
java -jar target\<artifact>-0.0.1-SNAPSHOT.jar
.\mvnw.cmd clean verify                          # tes + Jacoco (view/auth dikecualikan)
```

## 4. Struktur
`domain` (entity, exception, repository port, PasswordHasher) ← `usecase` ← `adapter` (InMemory, JDBC, presenter, FileStorage) / `framework` (AuthSession, view Vaadin) — wiring di `config/AppConfig`.

| Route | View | Akses |
|---|---|---|
| `/login`, `/register` | LoginView, RegisterView | Publik (sudah login → `/`) |
| `/` | FinanceView | USER & ADMIN |
| `/profile` | ProfileView | USER & ADMIN |
| `/users` | UsersView | ADMIN |
| `/user-transactions?userId=` | UserTransactionsView | ADMIN (read-only) |
| `/uploads/**` | FileStorage | Publik (statis) |

Catatan keamanan: cookie `cashflow_uid` ditandatangani HMAC-SHA256 (`SESSION_SECRET`) sehingga tidak bisa dipalsukan dengan mengganti id.
