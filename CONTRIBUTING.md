
## Contributing

We welcome contributions. Before opening a pull request:

1. Read the component README for the area you are changing ([server](server/README.md) or [client](client/README.md)).
2. Run the prerequisite check: `./scripts/check-prerequisites.sh` (or `.\scripts\check-prerequisites.ps1` on Windows).
3. Run the dev setup script once: `./scripts/setup-dev.sh` (or `.\scripts\setup-dev.ps1`).
4. Keep changes focused and follow existing code style.
5. Run tests where applicable (`mvn test` for backend, `npm test` for frontend).
6. Do not commit secrets, license files, environment-specific paths, or `.env`.

### Clone the repository

```bash
git clone https://github.com/helicalinsight/helicalinsight.git
cd helicalinsight
git checkout helicalinsight-ce-7.0
```

### Development workflows

**Docker (fastest):**

```bash
docker compose -f docker-compose.dev.yml up --build
```

**Native:**

```bash
cd server && mvn clean package -DskipTests
# Deploy presentation/target/hi-ce-7.0.0.war as hi-ee.war on Tomcat

cd client && npm ci --legacy-peer-deps && npm run start18
```

### Maven profiles

| Profile | When to use |
|---------|-------------|
| `dev` (default) | Day-to-day development with embedded Derby |
| `docker` (`-Denv=docker`) | Building the backend Docker image |

### What not to commit

- Machine-specific paths in `presentation/pom.xml`, `project.properties`, or `hi-repository/`
- `.env` files (use `.env.example` as the template)
- `server/db/` (Derby data files)
- License files (`hdi.licence`)
