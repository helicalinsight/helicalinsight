# Understanding the Application Base URL and Secure Access

## Introduction

Helical Insight needs a **base URL** — the main web address people use to open the application (for example, `https://your-company.com/hi-ee/`).

Getting this address right matters because the product uses it for:

- Opening reports and dashboards in the browser
- Background tasks such as scheduled reports and exports
- Links and redirects that must match the address users actually type

This guide explains, in plain language:

1. What happens **before** and **after** the application loads its settings
2. How **nginx** (the secure front door in Docker) fits in
3. How **forwarded headers** (and a component called `ForwardedHeaderFilter`) help when traffic is secured with HTTPS

You do not need to be a developer to follow this guide. If you install, configure, or support Helical Insight, this page is for you.

---



## Before You Start

Have the following ready:

- Access to your Helical Insight deployment (for example, Docker)
- The public address users should use (hostname and whether it is `http` or `https`)
- If you use Docker with HTTPS: confirmation that nginx is part of your setup
- Permission to review or update application configuration ([Confirm who in your organization may edit Base URL settings])

---



## Key Terms and Concepts

**Base URL**  
The main address of Helical Insight, usually ending with `/hi-ee/` (or including `hi.html` in the stored setting). Example: `https://reports.example.com/hi-ee/`.

**Default Base URL setting (`defaultBaseurl`)**  
A yes/no switch that controls **how** the app chooses the address it sends back when settings load. See [What Base URL and defaultBaseurl do](#what-base-url-and-defaultbaseurl-do) below.

**Application settings load**  
When you open the app, the browser asks the server for configuration and session information. Internally this request is named `applicationSettings`. You normally do not click a button for it; it runs automatically during startup and login.

**Browser starting address**  
Before settings finish loading, the web page already knows a starting address so it can talk to the server. After settings load, the server also sends back its own view of the base URL.

**nginx**  
In the Docker package, nginx is the component that accepts secure HTTPS connections from browsers and passes traffic to the application server behind it.

**HTTPS / secure access**  
The padlock address (`https://...`). Encryption often stops at nginx; the application behind it may speak plain HTTP on an internal network.

**Forwarded headers**  
Extra notes nginx adds to each request, such as “the user came in with HTTPS on port 443.” They tell the application what the **public** address looked like.

**ForwardedHeaderFilter**  
A server component that can read those notes and make the application treat the public HTTPS address as the real one. In the current product build this filter is **not** turned on by default; the usual approach is to set the Base URL explicitly instead (see below).

---

## What Base URL and defaultBaseurl do

Helical Insight uses two related settings:

| Setting | What it is |
|---------|------------|
| **Base URL** (`BaseUrl`) | The saved public address of the product (for example `https://your-server/hi-ee/hi.html`). Used for schedules, exports, and (when the switch below is off) for the address returned when settings load. |
| **Default Base URL** (`defaultBaseurl`) | A true/false switch that decides whether the address in the settings response is **taken from the saved Base URL** or **built from the current browser request**. |

### When `defaultBaseurl` is **true**

- The server builds the address from the **current request** (how the request arrived at the application).
- The saved Base URL is **not** used for that settings field.
- This can work on a simple direct install.
- Behind nginx/HTTPS it often goes wrong unless the app is taught to trust the public address (see ForwardedHeaderFilter later). You may see an internal or HTTP address instead of your public HTTPS URL.

**Example result in settings:** `http://internal-server:8080/hi-ee/` (unwanted if users open `https://your-server/hi-ee/`).

### When `defaultBaseurl` is **false** (recommended for Docker / HTTPS)

- The server uses the **saved Base URL**, and removes `hi.html` for the settings response.
- Schedules and exports also rely on that same saved Base URL.
- You must keep the saved value equal to the public address people type in the browser.

**Example:** Saved `https://your-server/hi-ee/hi.html` → settings return `https://your-server/hi-ee/`.

### Quick comparison

| `defaultBaseurl` | Address returned when settings load | Best used when |
|------------------|-------------------------------------|----------------|
| **true** | From the current request | Direct access to the app; or when public URL rewriting is correctly enabled |
| **false** | From the saved Base URL | Docker with nginx/HTTPS (usual production setup) |

---

## How the Base URL Transition Works

Think of opening Helical Insight as two moments: **before** settings arrive, and **after** settings arrive.

### Before application settings load

1. You open Helical Insight in the browser (for example, `https://your-server/hi-ee/`).
2. The web page loads and already has a **starting address** so it can contact the server.
3. The server, from its own configuration file, already knows a stored **Base URL** (used especially for schedules and exports).

[Insert Screenshot: Browser address bar showing the Helical Insight URL you use to open the product]

At this stage you may still see the login screen loading. The automatic settings request has not finished yet.

### When application settings load

The product automatically requests configuration from the server (`applicationSettings`).

The server decides which base URL to send back using `defaultBaseurl` (**true** = from the current request, **false** = from the saved Base URL). See [What Base URL and defaultBaseurl do](#what-base-url-and-defaultbaseurl-do).

[Insert Screenshot: Login page after the application has finished loading (settings available)]

### After application settings load

1. The browser stores the settings response, including the base URL the server reported.
2. You can sign in and use the product as usual.
3. Scheduled jobs and exports continue to rely on the **configured** Base URL on the server, so that address must be reachable and correct.

**What you should expect:** Links, redirects, and background jobs should match the same public address users type in the browser (for example, always `https://your-server/...`, not an internal name like a container hostname).

---



## How nginx Works with the Base URL



### The simple picture

```text
You (browser)  →  https://your-server  →  nginx (secure front door)
                                              ↓
                                    Helical Insight application
                                    (internal HTTP)
```

- You connect with **HTTPS** to nginx.
- nginx decrypts the traffic and forwards it to Helical Insight.
- Helical Insight itself may not “see” HTTPS unless it is told about the public address.



### Why this affects the Base URL

If the product only looked at the **internal** request, it might believe the address is something like `http://internal-server:8080/...`.

That is wrong for users. They opened `https://your-server/...`.

Docker setups normally avoid that problem by **saving the public Base URL** in configuration (and turning off “derive from request”), so schedules, exports, and settings agree with the address in the browser.

[Insert Screenshot: Successful open of Helical Insight over https:// in the browser address bar]

### What nginx adds to each request

nginx can attach notes such as:

- The connection was HTTPS
- The public port was 443
- The public host name the user used

Those notes are the **forwarded headers**. They are only useful if the application is set up to trust and read them.

---



## How ForwardedHeaderFilter Helps



### In plain language

When traffic goes through nginx:

1. The browser uses a **public, secure** address.
2. The application may receive an **internal** address.
3. **ForwardedHeaderFilter** (when enabled) teaches the application to prefer the public address described in nginx’s notes.

That helps most when configuration says: “build the base URL from the current request.”

### How this relates to today’s recommended setup


| Approach                                                                                   | When it works well                                                                                            |
| ------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------- |
| **Set the Base URL explicitly** to your public HTTPS address (typical Docker approach)     | Always matches what users type; does not depend on the filter                                                 |
| **Derive the base URL from the request** + **ForwardedHeaderFilter** reading nginx’s notes | Can auto-adapt to the public HTTPS address; requires the filter to be enabled and nginx headers to be trusted |


**Current product behavior:** The filter is not registered in the standard application filter list. Deployments that use nginx therefore rely mainly on an explicit public Base URL in settings, not on automatic request detection alone.

If engineering enables ForwardedHeaderFilter later, ask them to confirm that only your reverse proxy (nginx) is trusted, so outside clients cannot fake those headers.

---



## What You Should Configure



### Step 1: Decide the public address

Choose the exact address users will use, including `https` if you use nginx with TLS.

Example: `https://analytics.example.com/hi-ee/hi.html` (stored form) → users often browse `https://analytics.example.com/hi-ee/`.

### Step 2: Align Docker or server settings

For Docker:

1. Set your machine or server host value used for the Base URL (commonly `HOST_IP` in the Docker `.env` file) to a name or address browsers can reach.
2. Start (or restart) the stack so configuration is applied.
3. Open the product with **https://** and the same host.

[Insert Screenshot: Example .env or deployment notes showing HOST_IP set to your server name — redact secrets]

For non-Docker installs:

1. Update the Base URL in application settings to your public address.
2. Restart the application if your process requires it.
3. Open the product using that same address.

[Confirm the exact admin screen or file your organization uses to edit Base URL on non-Docker installs]

### Step 3: Verify the result

1. Sign in to Helical Insight.
2. Open a report or dashboard.
3. If you use scheduling or export, run a simple test job and confirm links or generated output point to your **public** address, not an internal hostname.

---



## Best Practices and Pro Tips

- **Use one public address everywhere.** The URL in the browser, the configured Base URL, and links in scheduled emails should match.
- **Prefer HTTPS in production.** With the Docker package, open the app through nginx’s HTTPS address rather than an internal HTTP port.
- **Change host carefully.** If users move from `http://server:8080` to `https://server`, update the stored Base URL (or redeploy Docker with the correct host) so background jobs do not keep calling the old address.
- **Do not expose the internal application port** as the main user entry point when nginx is meant to be the secure front door.

---



## Troubleshooting and FAQs



### Why do scheduled reports or exports fail after we switched to HTTPS?

**Possible cause:** The stored Base URL still uses `http` or an old host/port.

**Resolution:**

1. Check the configured Base URL / Docker host setting.
2. Update it to the public `https://...` address users use.
3. Restart or redeploy if required, then rerun a small schedule or export to verify.



### The browser works on HTTPS, but some links show an internal name or port

**Possible cause:** The product is deriving the address from the internal request, or the stored Base URL does not match the public site.

**Resolution:**

1. Confirm users only enter through the nginx HTTPS address.
2. Set the Base URL explicitly to that public HTTPS address (recommended with the current product).
3. Contact support or your engineering team if you were told to rely on request detection — they may need forwarded-header support enabled.



### Do I need to know about ForwardedHeaderFilter?

**Usually no.** For standard Docker HTTPS setups, setting the correct public Base URL is enough.

Ask about ForwardedHeaderFilter only if your team wants the application to **auto-detect** the public URL from each request behind nginx.

### Why can’t I open the app without typing a port number?

**Possible cause:** You may be bypassing nginx and hitting the application port directly, or HTTPS is not bound to the default secure port.

**Resolution:**

1. Use the HTTPS URL that goes through nginx (often `https://your-server` without a port).
2. If that fails, ask your administrator to confirm nginx is running and listening on the expected HTTPS port.

---



## Related Topics and Next Steps

- Engineering reference (internal): [BaseUrl, nginx, and forwarded headers](baseurl-config-document-engineer.md)
- Service / support runbook: [Base URL issues — service team](baseurl-config-document-service.md)
- Docker start and HTTPS port notes: [Docker readme](../../docker/readme/readme.md) and [Changing the HTTPS port](../../docker/readme/portchange.md)
- Product documentation site: [Helical Insight documentation](https://www.helicalinsight.com/)

If Base URL or HTTPS problems continue after the checks above, contact your Helical Insight administrator or support with:

- The exact URL you type in the browser  
- Whether you use Docker with nginx  
- Whether schedules/exports fail or only the browser UI is wrong

