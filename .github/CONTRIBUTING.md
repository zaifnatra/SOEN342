# Team workflow

1. Open an issue for a **use case or requirement**, a **project task**, or a **bug**.
   Assign an owner and describe what completing the work looks like.
2. Work on a separate branch and open a PR into `main`. Link the issue, summarize
   the changes, and include test results, a CLI example, or an artifact walkthrough.
   Use `Closes #123` when the PR completes the issue.
3. Request a teammate's review. Merging requires **one approving review** from a
   collaborator with write access. The PR author cannot approve their own PR.
   New commits that change the diff require a fresh approval.

The review rule also applies to administrators. Its configuration is recorded in
[rulesets/main.json](rulesets/main.json); editing that file does not automatically
update the active GitHub setting.

## Course context

Based on the Fall 2026 Iteration 0 handout (pages 2-4) and course outline:
produce analysis/design before implementation, keep requirements, models, and code consistent, and track
individual contributions across project activities. The system description comes
in Iteration I; update issues as requirements evolve and consult the current
assignment for all course requirements.
