# RT Business Book (Rio Tech AI) - Phase 1: Offline Foundation

Package: com.riotech.rtbusinessbook

## Phase 1 mein kya hai
- Dashboard (net profit, total/month/today income & expense)
- Income aur Expense add / edit / delete (PKR, paisa-level accuracy)
- Transaction history + filter
- Recycle Bin (restore / permanent delete confirmation ke saath)
- Room (SQLite) offline database
- Day/Night theme, blue-yellow branding
- GitHub Actions debug APK build

## Abhi NAHI hai (agle phases)
Urdu/RTL, multiple business, customers/suppliers, double-entry, invoices,
inventory, reports, PIN/biometric, roles, backup, cloud sync.

## APK kaise banayein
1. github.com par new repository banayein.
2. Is folder ki saari files upload karein (.github folder samait).
3. Repo > Actions > "Build Debug APK" > Run workflow.
4. 5-10 minute baad run kholein > neeche "Artifacts" se RTBusinessBook-debug-apk download karein.
5. ZIP kholein, APK phone mein install karein.
