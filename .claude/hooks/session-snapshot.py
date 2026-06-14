#!/usr/bin/env python3
"""
项目会话快照脚本 — 仅在有实际文件变更时才记录
由 Stop Hook 触发，无变更则静默退出

目录结构：
    docs/project-memory/sessions/               ← 会话记录根目录
    ├── .session-log.jsonl                      ← 所有会话的追加日志
    ├── 2026-06-14/                             ← 按日期分子目录
    │   ├── session-2026-06-14T11-29-11+08-00.json
    │   └── ...
    └── 2026-06-15/
        └── ...
"""
import subprocess
import json
import os
import sys
from datetime import datetime, timezone, timedelta

if sys.platform == "win32":
    import io
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

PROJECT_ROOT = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
MEMORY_DIR = os.path.join(PROJECT_ROOT, "docs", "project-memory")
SESSION_DIR = os.path.join(MEMORY_DIR, "sessions")   # 会话记录独立目录


def run(cmd):
    try:
        return subprocess.check_output(
            cmd, shell=True, text=True, cwd=PROJECT_ROOT,
            stderr=subprocess.DEVNULL
        ).strip()
    except Exception:
        return ""


def main():
    # 先检查是否有实际变更（含 untracked）
    changed = run("git diff --name-only")
    staged = run("git diff --cached --name-only")
    untracked = run("git ls-files --others --exclude-standard")

    has_changes = bool(changed or staged or untracked)

    if not has_changes:
        print("[project-snapshot] SKIP - no file changes in this session")
        return

    tz_asia = timezone(timedelta(hours=8))
    now = datetime.now(tz_asia)
    ts = now.strftime("%Y-%m-%dT%H:%M:%S+08:00")
    date_str = now.strftime("%Y-%m-%d")

    snapshot = {
        "timestamp": ts,
        "date": date_str,
        "time": now.strftime("%H:%M"),
        "branch": run("git branch --show-current"),
        "last_commit": run("git log -1 --format='%h %s (%an, %ar)'"),
        "changed_files": changed.split("\n") if changed else [],
        "staged_files": staged.split("\n") if staged else [],
        "untracked_files": untracked.split("\n") if untracked else [],
    }

    # 按日期分子目录
    date_dir = os.path.join(SESSION_DIR, date_str)
    os.makedirs(date_dir, exist_ok=True)

    # 追加到 session log（JSONL，放在 sessions 根目录）
    log_path = os.path.join(SESSION_DIR, ".session-log.jsonl")
    with open(log_path, "a", encoding="utf-8") as f:
        f.write(json.dumps(snapshot, ensure_ascii=False) + "\n")

    # 生成详细快照（按日期目录存放）
    safe_ts = ts.replace(":", "-")
    snap_path = os.path.join(date_dir, f"session-{safe_ts}.json")
    with open(snap_path, "w", encoding="utf-8") as f:
        json.dump(snapshot, f, ensure_ascii=False, indent=2)

    print(f"[project-snapshot] OK - {len(snapshot['changed_files'])} changed, "
          f"{len(snapshot['staged_files'])} staged, "
          f"{len(snapshot['untracked_files'])} untracked")
    print(f"[project-snapshot] Log: {log_path}")
    print(f"[project-snapshot] Snapshot: {snap_path}")


if __name__ == "__main__":
    main()
