import contextlib
import io
import tempfile
import unittest
from pathlib import Path

import project_audit as pa


def touch(root, *rel):
    f = Path(root, *rel)
    f.parent.mkdir(parents=True, exist_ok=True)
    f.write_text("x")


class ProjectAuditTest(unittest.TestCase):
    def setUp(self):
        self._tmp = tempfile.TemporaryDirectory()
        self.root = Path(self._tmp.name)

    def tearDown(self):
        self._tmp.cleanup()

    def run_main(self, *args):
        out = io.StringIO()
        with contextlib.redirect_stdout(out):
            code = pa.main([str(self.root), *args])
        return code, out.getvalue()

    def test_givenMixedProjects_whenAudit_thenReportsPassedOverTotal(self):
        # Given
        for f in ("pom.xml", "README.md", "CLAUDE.md"):
            touch(self.root, "good", f)
        for f in ("pom.xml", "README.md"):
            touch(self.root, "no-claude", f)

        # When
        code, out = self.run_main()

        # Then
        self.assertEqual(code, 1)
        self.assertIn("Passed: 1/2", out)

    def test_givenMixedProjects_whenAudit_thenReportsPerFileCounts(self):
        for f in ("pom.xml", "README.md", "CLAUDE.md"):
            touch(self.root, "a", f)
        for f in ("pom.xml", "CLAUDE.md"):
            touch(self.root, "b", f)

        _, out = self.run_main()

        self.assertIn("README.md 1/2", out)
        self.assertIn("CLAUDE.md 2/2", out)

    def test_givenAllPresent_whenAudit_thenExitZero(self):
        for f in ("pom.xml", "README.md", "CLAUDE.md"):
            touch(self.root, f)

        code, out = self.run_main()

        self.assertEqual(code, 0)
        self.assertIn("Passed: 1/1", out)

    def test_givenMultiModule_whenAudit_thenEachModuleIsARow(self):
        touch(self.root, "pom.xml")
        touch(self.root, "mod-a", "pom.xml")
        touch(self.root, "mod-b", "pom.xml")

        _, out = self.run_main()

        self.assertIn("Passed: 0/3", out)

    def test_givenPomsInSrcAndTarget_whenAudit_thenIgnored(self):
        touch(self.root, "app", "pom.xml")
        touch(self.root, "app", "src", "test", "resources", "pom.xml")
        touch(self.root, "app", "target", "pom.xml")
        touch(self.root, "node_modules", "dep", "pom.xml")

        _, out = self.run_main()

        self.assertIn("Passed: 0/1", out)

    def test_givenNoMarker_whenAudit_thenNoProjectsAndExitZero(self):
        touch(self.root, "docs", "README.md")

        code, out = self.run_main()

        self.assertEqual(code, 0)
        self.assertIn("No projects", out)

    def test_givenRequireFlag_whenAudit_thenCustomFilesChecked(self):
        touch(self.root, "pom.xml")
        touch(self.root, "AGENTS.md")

        code, out = self.run_main("--require", "AGENTS.md")

        self.assertEqual(code, 0)
        self.assertIn("Passed: 1/1", out)


if __name__ == "__main__":
    unittest.main()
