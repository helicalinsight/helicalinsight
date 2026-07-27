# Changing the HTTPS port

1. Stop the app if it is running:
   ```bash
   docker compose down
   ```

2. If you are updating an existing install, remove the lock file under `hi/hi-repository` if it exists:
   ```bash
   rm -f hi/hi-repository/hi.lock
   ```

3. In `.env`, set your machine IP (`HOST_IP`) and the port you want:
   ```env
   HOST_IP=10.23.44.244
   HTTPS_PORT=8085
   ```

4. Start again:
   ```bash
   docker compose up -d
   ```

5. Open `https://YOUR_IP:8085` (use the port you chose).

## Firewall

Allow the port you chose (for example `8085`) through the server firewall and any cloud security rules.
