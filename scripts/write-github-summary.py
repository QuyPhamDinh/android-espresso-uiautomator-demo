#!/usr/bin/env python3
"""Render JUnit XML instrumentation results into a GitHub Actions step summary."""

from __future__ import annotations

import glob
import os
import sys
import xml.etree.ElementTree as ET
from pathlib import Path


def parse_xml_reports(results_root: Path) -> list[dict]:
    """Parse all JUnit TEST-*.xml files grouped by module and suite."""
    pattern = str(results_root / "**" / "xml" / "**" / "TEST-*.xml")
    xml_files = sorted(glob.glob(pattern, recursive=True))
    suites: list[dict] = []

    for xml_path in xml_files:
        path = Path(xml_path)
        # Path shape: build/device-test-results/<run_id>/<module>/xml/.../TEST-*.xml
        rel_parts = path.relative_to(results_root).parts
        module = rel_parts[1] if len(rel_parts) > 1 else "unknown"

        root = ET.parse(path).getroot()
        suite_nodes = [root] if root.tag == "testsuite" else root.findall(".//testsuite")

        for suite_node in suite_nodes:
            suite_name = suite_node.attrib.get("name", path.stem)
            short_name = suite_name.rsplit(".", 1)[-1]
            duration = float(suite_node.attrib.get("time", "0") or 0)

            passed = 0
            failed = 0
            skipped = 0
            failures: list[dict] = []
            skipped_cases: list[str] = []

            testcases = suite_node.findall("testcase")
            if testcases:
                for case in testcases:
                    case_name = case.attrib.get("name", "unknown")
                    failure_node = case.find("failure")
                    error_node = case.find("error")
                    skipped_node = case.find("skipped")

                    if failure_node is not None or error_node is not None:
                        failed += 1
                        detail_node = failure_node if failure_node is not None else error_node
                        raw_text = (
                            detail_node.attrib.get("message")
                            or (detail_node.text or "").strip().splitlines()[0]
                            if (detail_node.attrib.get("message") or (detail_node.text or "").strip())
                            else "Test failed"
                        )
                        first_line = raw_text.splitlines()[0][:200]
                        failures.append(
                            {
                                "test": f"{short_name}#{case_name}",
                                "summary": first_line,
                            }
                        )
                    elif skipped_node is not None:
                        skipped += 1
                        skipped_cases.append(f"{short_name}#{case_name}")
                    else:
                        passed += 1
            else:
                total = int(suite_node.attrib.get("tests", "0") or 0)
                failed = int(suite_node.attrib.get("failures", "0") or 0) + int(
                    suite_node.attrib.get("errors", "0") or 0
                )
                skipped = int(suite_node.attrib.get("skipped", "0") or 0)
                passed = max(0, total - failed - skipped)

            suites.append(
                {
                    "module": module,
                    "suite": short_name,
                    "passed": passed,
                    "failed": failed,
                    "skipped": skipped,
                    "time": duration,
                    "failures": failures,
                    "skipped_cases": skipped_cases,
                }
            )

    return suites


def render_markdown(suites: list[dict], api_level: str) -> str:
    """Format parsed test suites into a Markdown report for $GITHUB_STEP_SUMMARY."""
    heading = f"### Instrumentation Test Results (API {api_level})" if api_level else "### Instrumentation Test Results"
    lines = [heading, ""]

    if not suites:
        lines.append("No JUnit XML test reports were generated for this run.")
        lines.append("")
        return "\n".join(lines)

    total_passed = sum(s["passed"] for s in suites)
    total_failed = sum(s["failed"] for s in suites)
    total_skipped = sum(s["skipped"] for s in suites)
    total_tests = total_passed + total_failed + total_skipped
    total_time = sum(s["time"] for s in suites)
    status_badge = "PASS" if total_failed == 0 else "FAIL"

    lines.append(
        f"**Status:** {status_badge} | **Total:** {total_tests} | "
        f"**Passed:** {total_passed} | **Failed:** {total_failed} | "
        f"**Skipped:** {total_skipped} | **Duration:** {total_time:.1f}s"
    )
    lines.append("")
    lines.append("| Module | Test Class | Passed | Failed | Skipped | Time |")
    lines.append("| :--- | :--- | ---: | ---: | ---: | ---: |")
    for s in suites:
        lines.append(
            f"| `{s['module']}` | `{s['suite']}` | {s['passed']} | {s['failed']} | {s['skipped']} | {s['time']:.1f}s |"
        )
    lines.append("")

    all_failures = [f for s in suites for f in s["failures"]]
    if all_failures:
        lines.append("#### Failed Tests")
        for failure in all_failures:
            msg = failure["summary"].replace("`", "'")
            lines.append(f"- `{failure['test']}` — `{msg}`")
        lines.append("")

    all_skipped = [c for s in suites for c in s["skipped_cases"]]
    if all_skipped:
        lines.append("#### Skipped Tests")
        for skipped_case in all_skipped:
            lines.append(f"- `{skipped_case}`")
        lines.append("")

    return "\n".join(lines)


def main() -> int:
    results_dir = Path(sys.argv[1] if len(sys.argv) > 1 else "build/device-test-results")
    api_level = os.environ.get("API_LEVEL", "")
    suites = parse_xml_reports(results_dir) if results_dir.exists() else []
    markdown = render_markdown(suites, api_level)

    summary_path = os.environ.get("GITHUB_STEP_SUMMARY")
    if summary_path:
        with open(summary_path, "a", encoding="utf-8") as fh:
            fh.write(markdown)
    else:
        print(markdown)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
