# loan_platform
#  Kredi Başvuru ve Değerlendirme Sistemi

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-336791?style=for-the-badge&logo=postgresql&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=JSON%20web%20tokens&logoColor=white)

## 📌 Proje Tanımı

**Kredi Başvuru ve Değerlendirme Sistemi**, bireysel müşterilerin kredi başvurularını alan, değerlendiren ve karar sürecini otomatikleştiren bir backend uygulamasıdır. RBAC tabanlı yetkilendirme, mesajlaşma altyapısı ve otomatik skor hesaplama gibi modüller içerir.

---

## 🎯 Amaç ve Kapsam

- Kredi başvuru sürecinin uçtan uca dijitalleştirilmesi
- Otomatik kredi skoru değerlendirmesi
- Spring Security ile RBAC tabanlı erişim kontrolü
- RabbitMQ ile asenkron bildirim altyapısı
- Dummy kredi skorlama motoru ile sahte değerlendirme simülasyonu

---

## 👥 Kullanıcı Rolleri

| Rol              | Açıklama |
|------------------|----------|
| `CUSTOMER`       | Başvuru yapar, süreci takip eder |
| `BANK_OFFICER`   | Başvuruları görüntüler ve değerlendirir |
| `ADMIN`          | Kullanıcıları ve rolleri yönetir |

---

## 🔄 İş Süreçleri

### 🧾 Kredi Başvuru Süreci

1. Müşteri kredi başvurusu yapar
2. Sistem başvuruyu değerlendirir ve dummy skorlama motorunu kullanarak skor hesaplar
3. Belirlenen eşik değere göre başvuru onaylanır veya reddedilir
4. Sonuç, RabbitMQ aracılığıyla asenkron olarak bildirilir

---

## 🛠️ Teknolojiler

| Katman     | Teknoloji                     |
|------------|-------------------------------|
| Backend    | Spring Boot, Spring Security  |
| Veritabanı | PostgreSQL                    |
| Mesajlaşma | RabbitMQ                      |
| Güvenlik   | Spring Security (JWT), BCrypt |

---

## 🔐 Güvenlik

- RBAC tabanlı yetki kontrolü (`@PreAuthorize`)
- JWT token doğrulama
- Parola şifreleme (BCrypt)
- E-posta maskeleme ile temel GDPR/KVKK uyumluluğu

---

## 🧪 Test ve Geliştirme

- DTO ve servis katmanlarında birim test altyapısı

---

## 📬 Katkıda Bulunmak

Proje ile ilgili hata bildirimi veya geliştirme önerileriniz için lütfen issue açın veya pull request gönderin.

---
