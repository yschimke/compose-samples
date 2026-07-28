#!/usr/bin/env node

/**
 * Derive the compose-m3 `theme.colors` / `theme.fonts` knob payloads from each
 * sample's checked-in theme sources.
 *
 * This intentionally reads the Kotlin source of truth instead of committing a
 * second, hand-maintained palette JSON beside every sample. The emitted color
 * format is the public compose-m3 wire format:
 *
 *   scheme:l=<role>:<AARRGGBB>,…;d=<role>:<AARRGGBB>,…
 *
 * Usage:
 *   node .github/scripts/m3-theme-overrides.mjs <system> colors
 *   node .github/scripts/m3-theme-overrides.mjs <system> fonts
 *   node .github/scripts/m3-theme-overrides.mjs --check
 */

import { readFileSync } from "node:fs";
import { dirname, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const repoRoot = resolve(dirname(fileURLToPath(import.meta.url)), "../..");

const roleOrder = [
  "primary",
  "onPrimary",
  "primaryContainer",
  "onPrimaryContainer",
  "inversePrimary",
  "secondary",
  "onSecondary",
  "secondaryContainer",
  "onSecondaryContainer",
  "tertiary",
  "onTertiary",
  "tertiaryContainer",
  "onTertiaryContainer",
  "background",
  "onBackground",
  "surface",
  "onSurface",
  "surfaceVariant",
  "onSurfaceVariant",
  "surfaceTint",
  "inverseSurface",
  "inverseOnSurface",
  "error",
  "onError",
  "errorContainer",
  "onErrorContainer",
  "outline",
  "outlineVariant",
  "scrim",
  "surfaceBright",
  "surfaceDim",
  "surfaceContainer",
  "surfaceContainerHigh",
  "surfaceContainerHighest",
  "surfaceContainerLow",
  "surfaceContainerLowest",
  "primaryFixed",
  "primaryFixedDim",
  "onPrimaryFixed",
  "onPrimaryFixedVariant",
  "secondaryFixed",
  "secondaryFixedDim",
  "onSecondaryFixed",
  "onSecondaryFixedVariant",
  "tertiaryFixed",
  "tertiaryFixedDim",
  "onTertiaryFixed",
  "onTertiaryFixedVariant",
];
const supportedRoles = new Set(roleOrder);
const fontGroupOrder = ["display", "headline", "title", "body", "label"];

function read(relativePath) {
  return readFileSync(resolve(repoRoot, relativePath), "utf8");
}

function colorConstants(...relativePaths) {
  const result = new Map([
    ["Color.White", "FFFFFFFF"],
    ["Color.Black", "FF000000"],
  ]);
  const pattern = /\bval\s+([A-Za-z_]\w*)\s*=\s*Color\(0x([0-9a-f]{8})\)/gi;
  for (const relativePath of relativePaths) {
    const source = read(relativePath);
    for (const match of source.matchAll(pattern)) {
      result.set(match[1], match[2].toUpperCase());
    }
  }
  return result;
}

function callBody(source, marker) {
  const markerIndex = source.indexOf(marker);
  if (markerIndex < 0) throw new Error(`missing Kotlin declaration: ${marker}`);
  const open = source.indexOf("(", markerIndex + marker.length - 1);
  if (open < 0) throw new Error(`missing opening parenthesis after: ${marker}`);
  let depth = 0;
  for (let index = open; index < source.length; index += 1) {
    if (source[index] === "(") depth += 1;
    if (source[index] === ")") {
      depth -= 1;
      if (depth === 0) return source.slice(open + 1, index);
    }
  }
  throw new Error(`unterminated Kotlin declaration: ${marker}`);
}

function assignments(body, constants) {
  const result = {};
  const pattern = /^\s*([A-Za-z_]\w*)\s*=\s*([A-Za-z_][\w.]*)\s*,?\s*$/gm;
  for (const match of body.matchAll(pattern)) {
    const role = match[1];
    const value = constants.get(match[2]);
    if (supportedRoles.has(role) && value) result[role] = value;
  }
  return result;
}

function schemeFromDeclaration(sourcePath, marker, constants) {
  return assignments(callBody(read(sourcePath), marker), constants);
}

function schemesFromPrefix(sourcePath, lightPrefix, darkPrefix) {
  const constants = colorConstants(sourcePath);
  const build = (prefix) => {
    const result = {};
    for (const [name, value] of constants) {
      if (!name.startsWith(prefix)) continue;
      const role = name.slice(prefix.length);
      if (supportedRoles.has(role)) result[role] = value;
    }
    return result;
  };
  return { light: build(lightPrefix), dark: build(darkPrefix) };
}

function schemesFromSuffix(sourcePath) {
  const constants = colorConstants(sourcePath);
  const build = (suffix) => {
    const result = {};
    for (const role of roleOrder) {
      const value = constants.get(`${role}${suffix}`);
      if (value) result[role] = value;
    }
    return result;
  };
  return { light: build("Light"), dark: build("Dark") };
}

function jetsnackSchemes() {
  const colorPath = "Jetsnack/app/src/main/java/com/example/jetsnack/ui/theme/Color.kt";
  const themePath = "Jetsnack/app/src/main/java/com/example/jetsnack/ui/theme/Theme.kt";
  const constants = colorConstants(colorPath);
  const palette = (name) => {
    const body = callBody(read(themePath), `internal val ${name} = JetsnackColors(`);
    const result = {};
    const pattern = /^\s*([A-Za-z_]\w*)\s*=\s*([A-Za-z_]\w*)\s*,?\s*$/gm;
    for (const match of body.matchAll(pattern)) {
      const value = constants.get(match[2]);
      if (value) result[match[1]] = value;
    }
    return result;
  };
  const project = (colors) => ({
    primary: colors.brand,
    onPrimary: colors.textInteractive,
    secondary: colors.brandSecondary,
    onSecondary: colors.textInteractive,
    tertiary: colors.textLink,
    background: colors.uiBackground,
    onBackground: colors.textSecondary,
    surface: colors.uiBackground,
    onSurface: colors.textSecondary,
    surfaceVariant: colors.uiFloated,
    onSurfaceVariant: colors.textHelp,
    surfaceTint: colors.brand,
    outline: colors.uiBorder,
    error: colors.error,
    onError: colors.textInteractive,
  });
  return {
    light: project(palette("LightColorPalette")),
    dark: project(palette("DarkColorPalette")),
  };
}

const samples = {
  jetnews: () => ({
    ...schemesFromPrefix(
      "JetNews/app/src/main/java/com/example/jetnews/ui/theme/Color.kt",
      "md_theme_light_",
      "md_theme_dark_",
    ),
    fonts: {
      display: "Montserrat",
      headline: "Montserrat",
      title: "Montserrat",
      body: "Montserrat",
      label: "Montserrat",
    },
  }),
  jetcaster: () => ({
    ...schemesFromSuffix(
      "Jetcaster/core/designsystem/src/main/java/com/example/jetcaster/designsystem/theme/Color.kt",
    ),
    fonts: {
      display: "Roboto Flex",
      headline: "Montserrat",
      title: "Montserrat",
      body: "Montserrat",
      label: "Montserrat",
    },
  }),
  jetchat: () => {
    const colorPath = "Jetchat/app/src/main/java/com/example/compose/jetchat/theme/Color.kt";
    const themePath = "Jetchat/app/src/main/java/com/example/compose/jetchat/theme/Themes.kt";
    const constants = colorConstants(colorPath);
    return {
      light: schemeFromDeclaration(
        themePath,
        "val JetchatLightColorScheme = lightColorScheme(",
        constants,
      ),
      dark: schemeFromDeclaration(
        themePath,
        "val JetchatDarkColorScheme = darkColorScheme(",
        constants,
      ),
      fonts: {
        display: "Montserrat",
        headline: "Montserrat",
        title: "Montserrat",
        body: "Karla",
        label: "Montserrat",
      },
    };
  },
  jetsnack: () => ({
    ...jetsnackSchemes(),
    fonts: {
      display: "Montserrat",
      headline: "Montserrat",
      title: "Montserrat",
      body: "Karla",
      label: "Montserrat",
    },
  }),
  jetlagged: () => {
    const colorPath = "JetLagged/app/src/main/java/com/example/jetlagged/ui/theme/Color.kt";
    const themePath = "JetLagged/app/src/main/java/com/example/jetlagged/ui/theme/Theme.kt";
    const constants = colorConstants(colorPath);
    return {
      light: schemeFromDeclaration(
        themePath,
        "private val LightColorScheme = lightColorScheme(",
        constants,
      ),
      dark: schemeFromDeclaration(
        themePath,
        "private val DarkColorScheme = darkColorScheme(",
        constants,
      ),
      // MaterialTheme's imported M3 type scale remains the sample's default
      // Typography. Lato is used by separate JetLagged component text styles.
      fonts: {},
    };
  },
  reply: () => ({
    ...schemesFromSuffix(
      "Reply/app/src/main/java/com/example/reply/ui/theme/Color.kt",
    ),
    fonts: {},
  }),
};

function validate(system, theme) {
  for (const mode of ["light", "dark"]) {
    const roles = Object.entries(theme[mode]).filter(([, value]) => value);
    if (roles.length < 4) {
      throw new Error(`${system} ${mode} resolved only ${roles.length} color roles`);
    }
    for (const [role, value] of roles) {
      if (!supportedRoles.has(role)) throw new Error(`${system}: unsupported role ${role}`);
      if (!/^[0-9A-F]{8}$/.test(value)) {
        throw new Error(`${system}: invalid ${role} color ${value}`);
      }
    }
  }
}

function serializeColors(theme) {
  const mode = (tag, scheme) => {
    const roles = roleOrder
      .filter((role) => scheme[role])
      .map((role) => `${role}:${scheme[role]}`)
      .join(",");
    return `${tag}=${roles}`;
  };
  return `scheme:${mode("l", theme.light)};${mode("d", theme.dark)}`;
}

function serializeFonts(fonts) {
  const entries = fontGroupOrder
    .filter((group) => fonts[group])
    .map((group) => `${group}=${fonts[group]}`);
  return entries.length ? `families:${entries.join(",")}` : "";
}

if (process.argv[2] === "--check") {
  for (const [system, load] of Object.entries(samples)) {
    const theme = load();
    validate(system, theme);
    console.log(
      `${system}: ${Object.keys(theme.light).length} light roles, ` +
        `${Object.keys(theme.dark).length} dark roles`,
    );
  }
  process.exit(0);
}

const system = process.argv[2];
const field = process.argv[3] ?? "json";
const load = samples[system];
if (!load) {
  console.error(`unknown system '${system ?? ""}'; expected: ${Object.keys(samples).join(", ")}`);
  process.exit(2);
}

const theme = load();
validate(system, theme);
const output = {
  colors: serializeColors(theme),
  fonts: serializeFonts(theme.fonts),
};
if (field === "colors" || field === "fonts") {
  process.stdout.write(output[field]);
} else if (field === "json") {
  process.stdout.write(`${JSON.stringify(output, null, 2)}\n`);
} else {
  console.error(`unknown field '${field}'; expected colors, fonts, or json`);
  process.exit(2);
}
