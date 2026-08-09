# Framework Enhancement - Implementation Todo

## Phase 1 — Documentation (.md files)
- [x] 1. Rewrite README.md with accurate current project structure, features, setup
- [x] 2. Create docs/ARCHITECTURE.md
- [x] 3. Create docs/CONTRIBUTING.md
- [x] 4. Create docs/SETUP.md
- [x] 5. Create docs/REPORTING.md
- [x] 6. Create docs/ADDING_FEATURES.md

## Phase 2 — Playwright MCP Integration
- [x] 7. Create .mcp.json configuring @playwright/mcp
- [x] 8. Create docs/MCP_SETUP.md
- [x] 8b. Verify @playwright/mcp package installs & runs
- [ ] 8c. Reload MCP client (VS Code/Copilot) so the `playwright` MCP server connects - required for live UI inspection

## Phase 3 — Framework Fixes
- [x] 9. Fix browser=chrome -> browser=chromium in config.properties
- [x] 10. Create docs/FEATURES_ROADMAP.md

## Phase 4 — New Feature Guide
- [x] 11. Provide step-by-step guide for adding new features (e.g., AddProject) - docs/ADDING_FEATURES.md

## Verification
- [x] 12. Verify compilation (mvn clean compile) + test-compile
- [x] 20. Fix pom.xml: removed cucumber-messages:22.0.0 pin -> resolves NoSuchMethodError Exception.getStackTrace() in XML report writer
- [x] 21. Verify `mvn clean test "-Dcucumber.filter.tags=@Completeflow"` runs (PowerShell needs quotes around -D value)
- [ ] 22. Re-run tests after pom fix to confirm XML report writer no longer throws NoSuchMethodError

## Phase 5 — Add Project Feature Implementation
- [x] 14. Create AddProjectPage + register in PageManager
- [x] 15. Create AddProjectSteps
- [x] 16. Add AddProject sheet generator in TestDataGenerator
- [x] 17. Update TestRunner tags to `@Completeflow or @addproject`
- [x] 18. Compile & verify (main + test classes compile)
- [x] 23. Complete empty Background precondition code in AddProjectSteps (userLoggedIn + userHostedBusinessSuccessfully)
- [x] 24. Verify/Add required Excel data: confirmed RegistrationData & HostYourBusiness sheets; added missing AddProject sheet (ProjectTitle, ProjectDescription, ProjectCategory, Budget, Location, ProjectStatus, ProjectImagePath)
- [ ] 13. Inspect DialinArch UI via Playwright MCP to verify/tune Add Project selectors (deferred - MCP server not connected; selectors use defensive fallbacks)
- [ ] 19. Run `mvn clean test -Dcucumber.filter.tags="@addproject"` to execute the flow (requires live environment)
