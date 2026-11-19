# SOEN342
SOEN342: Software Requirements and Deployment

- Huzaifa Fareed (40272738)
- Sia Shah (40275251)
- Maëlys Arnaud (40278798)

## Run the PoC

Requires JDK 17 or newer. From the repository folder (or VS Code terminal):

**macOS / Linux:**

```bash
bash run.sh
```

**Windows (PowerShell or Command Prompt; no Git Bash needed):**

```powershell
.\run.bat
```

No VS Code extension is needed to run these terminal commands.

Choose option **1**, then enter a CSV filename from `data/`, without the folder path.
Press Enter for `availability.csv` (Main Clinic), or enter `availability-second-clinic.csv`
for Second Clinic. You can also place your own availability CSV in `data/`. Register a fictional patient
with **3**, then book with **6**. Set `JAVA_HOME` if your JDK is not on PATH.

Data stays in memory. CSV exports availability only, not bookings or patients.
Each availability interval is one bookable slot. This demo assumes authorized staff.

## Use Cases

The [initial requirements](artifacts/initial-requirements.md) list the critical and
non-critical use cases, their descriptions, and actors.

[SSD assumptions are here](artifacts/assumptions-and-notes.md).

## Contributing

See the [team workflow](.github/CONTRIBUTING.md) for issue templates, PR guidance,
and the one-approval review requirement for `main`.
