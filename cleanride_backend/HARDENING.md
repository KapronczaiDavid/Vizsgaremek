# Hardening checklist (do in this order)

## 1. Create new secrets (Windows PowerShell)
Run this THREE times and keep the outputs: they become JWT_SECRET, VERIFICATION_HMAC_SECRET and your new DB password.
(JWT_SECRET and VERIFICATION_HMAC_SECRET must be different values.)

    $b = New-Object byte[] 48; [Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($b); [Convert]::ToBase64String($b)

## 2. Gmail app password
Google Account > Security > 2-Step Verification > App passwords: delete the old one for
cleanrideinbox@gmail.com, create a new one, keep it for GMAIL_APP_PASSWORD.

## 3. Database: limited user + new root password (run in MySQL as root)

    -- use the password from step 1 instead of CHANGE_ME
    CREATE USER 'cleanride_app'@'localhost' IDENTIFIED BY 'CHANGE_ME';
    GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON cleanride.* TO 'cleanride_app'@'localhost';
    ALTER USER 'root'@'localhost' IDENTIFIED BY 'A_DIFFERENT_STRONG_ROOT_PASSWORD';
    FLUSH PRIVILEGES;

The app then uses DB_USERNAME=cleanride_app. It never needs root.

## 4. Run the SQL migration (as root, once)

    mysql -u root -p < migration_security_fixes.sql

Run the duplicate-plate SELECT at the top of that file first; it must return no rows.

## 5. New admin password
Generate a bcrypt hash locally (nothing is sent anywhere):

    pip install bcrypt
    python -c "import bcrypt,getpass; print(bcrypt.hashpw(getpass.getpass().encode(), bcrypt.gensalt(12, prefix=b'2a')).decode())"

Then in MySQL:

    UPDATE admins SET password = '<the hash>' WHERE id = 1;
    UPDATE auth_tokens SET used_at = NOW() WHERE type = 'session' AND used_at IS NULL;   -- log everyone out

## 6. Set the environment variables in IntelliJ
Run > Edit Configurations > CleanrideApplication > Modify options > Environment variables:

    GMAIL_USERNAME=cleanrideinbox@gmail.com;GMAIL_APP_PASSWORD=...;DB_USERNAME=cleanride_app;DB_PASSWORD=...;JWT_SECRET=...;VERIFICATION_HMAC_SECRET=...

(See env.example for the full list.) The app now refuses to start if a required one is missing.

## 7. Git
- If the repo was ever pushed: make it private now. The old secrets are in the history, so steps 1-5 (rotation) are what actually protects you.
- Optional history clean-up: `pip install git-filter-repo`, then
  `git filter-repo --path cleanride_backend/src/main/resources/application.yaml --invert-paths` (this rewrites history, back up first),
  then re-add the new application.yaml and force-push.
- GitHub > Settings > Code security: enable Secret scanning and Push protection.
- Check history for leaked secrets: `git log -p -- cleanride_backend/src/main/resources/application.yaml`

## 8. Verify
Open security-checks.http in IntelliJ and run the numbered requests; every "CHECK" line says the status you must see.

## 9. Before real users
- HTTPS everywhere (nginx + Let's Encrypt). Set CORS_ALLOWED_ORIGINS to your real domain.
- Behind your own reverse proxy only: FORWARD_HEADERS_STRATEGY=framework (otherwise leave it as none).
- Upgrade MySQL 5.7 to 8.x. Use DB_URL with sslMode=REQUIRED if the DB is on another host.
- Regular encrypted DB backups; keep the DB port closed to the internet.
- Privacy notice + a way to erase a user's data (GDPR).
- Update Spring Boot and dependencies regularly.
