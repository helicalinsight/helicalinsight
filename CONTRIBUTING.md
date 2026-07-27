# Contributing

We welcome contributions. Prefer shipping product value over wrestling with machine-specific configuration — the scripts and docs below are meant to keep setup boring.

## How to contribute (fork + PR)

The best way to contribute is to **fork the repository**, work on a branch, and open a **pull request**.

1. **Fork** [helicalinsight/helicalinsight](https://github.com/helicalinsight/helicalinsight) on GitHub.
2. **Clone your fork** and add the upstream remote:

   ```bash
   git clone https://github.com/<your-username>/helicalinsight.git
   cd helicalinsight
   git remote add upstream https://github.com/helicalinsight/helicalinsight.git
   ```

3. **Create a branch** from an up-to-date `master` (or the branch named in the issue):

   ```bash
   git fetch upstream
   git checkout -b my-feature upstream/master
   ```

4. **Set up and develop** — see [Before you open a PR](#before-you-open-a-pr) and [Developer setup](README.md#developer-setup).
5. **Push** to your fork and **open a pull request** against `helicalinsight/helicalinsight` (`master`).

   ```bash
   git push -u origin my-feature
   ```

   Then use GitHub: *Compare & pull request*. Fill in the PR template (summary, related issue, test plan, checklist).

6. Respond to review comments; keep the PR focused on one change.

Do **not** commit directly to the upstream `master` branch. Maintainers merge via PR after review and CI.

## Before you open a PR

1. Follow the [Developer setup](README.md#developer-setup) in the root README (or the component README for the area you change).
2. Run the prerequisite check: `./scripts/check-prerequisites.sh` (or `.\scripts\check-prerequisites.ps1` on Windows).
3. Run the one-time setup: `./scripts/setup-dev.sh` (or `.\scripts\setup-dev.ps1`).
4. Keep changes focused and match existing code style.
5. Run tests where they apply (`mvn test`, `npm test`, or Instant BI `pytest`).
6. Do not commit secrets, license files, environment-specific paths, or `.env`.

### Fastest full stack

```bash
./scripts/setup-dev.sh   # once — paths + Instant BI link + .env
cd docker
docker compose up -d
# https://localhost  →  hiadmin / hiadmin
```

First start can take several minutes (images, Chrome, Instant BI `pip install`). That is expected; later starts are faster.

Build backend from source in Docker:

```bash
docker compose -f docker-compose.dev.yml up --build
```

### Native (per component)

| Area | Docs | Typical commands |
|------|------|------------------|
| Backend | [server/README.md](server/README.md) | **Eclipse + Tomcat** ([guide](server/README.md#eclipse--tomcat-recommended)), or `cd server && mvn clean package -DskipTests` → deploy as `hi-ee.war` |
| Frontend | [client/README.md](client/README.md) | `cd client && npm ci --legacy-peer-deps && npm run start18` |
| Instant BI | [ib/README.md](ib/README.md) | `cd ib/helicalbi && pip install -r requirements.txt && python app.py` |

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

## Filing issues (templates)

When creating GitHub issues, please use the built-in templates in `.github/ISSUE_TEMPLATE/`:
- **Bug report** for reproducible problems (include steps, expected vs actual, and logs)
- **Feature request** for enhancements
- **Question / How do I...?** for help using the product or setup

## Pull requests (template)

Opening a PR auto-loads [`.github/pull_request_template.md`](.github/pull_request_template.md). Please complete:
- **Summary** — what and why
- **Related issue** — link the GitHub issue (and Bugzilla ID if you have one)
- **Type / Area** — so reviewers know where to look
- **How was this tested?** — commands or manual steps
- **Checklist** — no secrets, focused change, docs if needed
